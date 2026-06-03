package com.furnituree.furnituree.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.furnituree.furnituree.dto.CheckoutRequest;
import com.furnituree.furnituree.exception.BadRequestException;
import com.furnituree.furnituree.exception.ResourceNotFoundException;
import com.furnituree.furnituree.model.Cart;
import com.furnituree.furnituree.model.CartItem;
import com.furnituree.furnituree.model.Order;
import com.furnituree.furnituree.model.OrderItem;
import com.furnituree.furnituree.model.OrderStatus;
import com.furnituree.furnituree.model.Payment;
import com.furnituree.furnituree.model.PaymentMethod;
import com.furnituree.furnituree.model.PaymentStatus;
import com.furnituree.furnituree.model.Product;
import com.furnituree.furnituree.model.User;
import com.furnituree.furnituree.repo.cartItem_repo;
import com.furnituree.furnituree.repo.cart_repo;
import com.furnituree.furnituree.repo.order_repo;
import com.furnituree.furnituree.repo.product_repo;
import com.furnituree.furnituree.repo.user_repo;

@Service
public class OrderService {

    private static final double FREE_SHIPPING_THRESHOLD = 1_000.0;
    private static final double STANDARD_SHIPPING_FEE = 50.0;

    private final order_repo orderRepo;
    private final user_repo userRepo;
    private final cart_repo cartRepo;
    private final cartItem_repo cartItemRepo;
    private final product_repo productRepo;

    public OrderService(order_repo orderRepo,
            user_repo userRepo,
            cart_repo cartRepo,
            cartItem_repo cartItemRepo,
            product_repo productRepo) {
        this.orderRepo = orderRepo;
        this.userRepo = userRepo;
        this.cartRepo = cartRepo;
        this.cartItemRepo = cartItemRepo;
        this.productRepo = productRepo;
    }

    @Transactional
    public Order checkout(CheckoutRequest request) {
        validateCheckoutRequest(request);
        
        User user = getCurrentUser();
        Cart cart = cartRepo.findByUser(user);
        if (cart == null || cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new BadRequestException("Cannot checkout because cart is empty");
        }

        List<CartItem> cartItems = new ArrayList<>(cart.getCartItems());
        double subtotal = 0;

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setShippingName(clean(request.getShippingName()));
        order.setShippingPhone(clean(request.getShippingPhone()));
        order.setShippingAddress(clean(request.getShippingAddress()));
        order.setShippingCity(clean(request.getShippingCity()));

        for (CartItem cartItem : cartItems) {
            Product product = productRepo.findById(cartItem.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            long requestedQuantity = cartItem.getQuantity();
            long stock = product.getQuantity() == null ? 0 : product.getQuantity();

            if (requestedQuantity <= 0) {
                throw new BadRequestException("Invalid cart item quantity");
            }
            if (stock < requestedQuantity) {
                throw new BadRequestException("Not enough stock for product: " + product.getProduct_name());
            }

            double price = product.getPrice();
            double lineTotal = price * requestedQuantity;
            subtotal += lineTotal;

            product.setQuantity(stock - requestedQuantity);
            productRepo.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setProductName(product.getProduct_name());
            orderItem.setQuantity(requestedQuantity);
            orderItem.setPrice(price);
            orderItem.setLineTotal(lineTotal);
            order.getOrderItems().add(orderItem);
        }

        double shippingFee = calculateShippingFee(subtotal);
        double totalAmount = subtotal + shippingFee;

        order.setSubtotal(subtotal);
        order.setShippingFee(shippingFee);
        order.setTotalAmount(totalAmount);

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setMethod(request.getPaymentMethod());
        payment.setAmount(totalAmount);
        if (request.getPaymentMethod() == PaymentMethod.MOCK_CARD) {
            payment.setStatus(PaymentStatus.PAID);
            payment.setPaidAt(LocalDateTime.now());
        } else {
            payment.setStatus(PaymentStatus.UNPAID);
        }
        order.setPayment(payment);

        Order savedOrder = orderRepo.save(order);

        cartItemRepo.deleteAll(cartItems);
        cart.getCartItems().clear();
        cartRepo.save(cart);

        return savedOrder;
    }

    public List<Order> getMyOrders() {
        return orderRepo.findByUserOrderByCreatedAtDesc(getCurrentUser());
    }

    public List<Order> getAllOrders() {
        User user = getCurrentUser();
        requireAdminOrManager(user);
        return orderRepo.findAllByOrderByCreatedAtDesc();
    }

    public Order getOrderDetail(Long id) {
        Order order = findOrder(id);
        User user = getCurrentUser();
        if (!isAdminOrManager(user) && !order.getUser().getUser_Id().equals(user.getUser_Id())) {
            throw new BadRequestException("You are not allowed to view this order");
        }
        return order;
    }

    @Transactional
    public Order updateStatus(Long id, OrderStatus newStatus) {
        User user = getCurrentUser();
        requireAdminOrManager(user);

        Order order = findOrder(id);
        validateStatusTransition(order.getStatus(), newStatus);
        order.setStatus(newStatus);

        if (newStatus == OrderStatus.COMPLETED && order.getPayment() != null) {
            order.getPayment().setStatus(PaymentStatus.PAID);
            order.getPayment().setPaidAt(LocalDateTime.now());
        }

        return orderRepo.save(order);
    }

    @Transactional
    public Order cancelMyOrder(Long id) {
        User user = getCurrentUser();
        Order order = findOrder(id);

        if (!order.getUser().getUser_Id().equals(user.getUser_Id())) {
            throw new BadRequestException("You are not allowed to cancel this order");
        }
        if (order.getStatus() == OrderStatus.SHIPPING || order.getStatus() == OrderStatus.COMPLETED) {
            throw new BadRequestException("Cannot cancel an order that is already shipping or completed");
        }
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException("Order is already cancelled");
        }

        restoreStock(order);
        order.setStatus(OrderStatus.CANCELLED);
        if (order.getPayment() != null) {
            order.getPayment().setStatus(PaymentStatus.FAILED);
        }
        return orderRepo.save(order);
    }

    private void restoreStock(Order order) {
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            long currentStock = product.getQuantity() == null ? 0 : product.getQuantity();
            product.setQuantity(currentStock + item.getQuantity());
            productRepo.save(product);
        }
    }

    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        if (currentStatus == OrderStatus.CANCELLED || currentStatus == OrderStatus.COMPLETED) {
            throw new BadRequestException("Cannot change status of a final order");
        }
        if (newStatus == OrderStatus.PENDING) {
            throw new BadRequestException("Cannot move order back to PENDING");
        }
    }

    private Order findOrder(Long id) {
        return orderRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + id));
    }

    private double calculateShippingFee(double subtotal) {
        return subtotal >= FREE_SHIPPING_THRESHOLD ? 0 : STANDARD_SHIPPING_FEE;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new BadRequestException("User is not authenticated");
        }

        User user = userRepo.findByUsername(authentication.getName());
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        return user;
    }

    private void requireAdminOrManager(User user) {
        if (!isAdminOrManager(user)) {
            throw new BadRequestException("Only admin or manager can perform this action");
        }
    }

    private boolean isAdminOrManager(User user) {
        String role = user.getRole() == null ? "" : user.getRole().trim().toLowerCase();
        return role.equals("admin") || role.equals("manager");
    }
    private void validateCheckoutRequest(CheckoutRequest request) {
    if (request == null) {
        throw new BadRequestException("Checkout information is required");
    }

    String name = clean(request.getShippingName());
    String phone = clean(request.getShippingPhone());
    String address = clean(request.getShippingAddress());
    String city = clean(request.getShippingCity());

    if (name == null || !name.matches("^[A-Za-zÀ-ỹ\\s]{2,50}$")) {
        throw new BadRequestException(
                "Full name must be 2-50 letters and cannot contain numbers or special characters");
    }

    if (phone == null || !phone.matches("^0\\d{9}$")) {
        throw new BadRequestException(
                "Phone number must start with 0 and contain exactly 10 digits");
    }

    if (address == null || !address.matches("^[A-Za-zÀ-ỹ0-9\\s,./-]{5,120}$")) {
        throw new BadRequestException(
                "Address must be 5-120 characters and cannot contain invalid symbols");
    }

    if (city == null || !city.matches("^[A-Za-zÀ-ỹ\\s]{2,50}$")) {
        throw new BadRequestException(
                "City must be 2-50 letters and cannot contain numbers or special characters");
    }

    if (request.getPaymentMethod() == null) {
        throw new BadRequestException("Payment method is required");
    }
}
    private String clean(String value) {
        return value == null ? null : value.trim();
    }
}
