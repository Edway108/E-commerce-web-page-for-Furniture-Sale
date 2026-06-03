package com.furnituree.furnituree.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.furnituree.furnituree.dto.CheckoutRequest;
import com.furnituree.furnituree.dto.OrderStatusUpdateRequest;
import com.furnituree.furnituree.model.Order;
import com.furnituree.furnituree.service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/orders")
public class order_controller {

    private final OrderService orderService;

    public order_controller(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<Order> checkout(@Valid @RequestBody CheckoutRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.checkout(request));
    }

    @GetMapping("/my-orders")
    public List<Order> getMyOrders() {
        return orderService.getMyOrders();
    }

    @GetMapping("/{id}")
    public Order getOrderDetail(@PathVariable Long id) {
        return orderService.getOrderDetail(id);
    }

    @GetMapping("/admin/all")
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @PatchMapping("/{id}/status")
    public Order updateStatus(@PathVariable Long id, @Valid @RequestBody OrderStatusUpdateRequest request) {
        return orderService.updateStatus(id, request.getStatus());
    }

    @PatchMapping("/{id}/cancel")
    public Order cancelMyOrder(@PathVariable Long id) {
        return orderService.cancelMyOrder(id);
    }
}
