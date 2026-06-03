package com.furnituree.furnituree.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.furnituree.furnituree.dto.ProductRequest;
import com.furnituree.furnituree.exception.BadRequestException;
import com.furnituree.furnituree.exception.ResourceNotFoundException;
import com.furnituree.furnituree.model.Category;
import com.furnituree.furnituree.model.Product;
import com.furnituree.furnituree.repo.category_repo;
import com.furnituree.furnituree.repo.product_repo;

import jakarta.persistence.criteria.Predicate;

@Service
public class ProductService {

    private final product_repo productRepo;
    private final category_repo categoryRepo;

    public ProductService(product_repo productRepo, category_repo categoryRepo) {
        this.productRepo = productRepo;
        this.categoryRepo = categoryRepo;
    }

    public List<Product> getAll() {
        return productRepo.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    @Transactional
    public Product create(ProductRequest request) {
        Product product = new Product();
        applyProductRequest(product, request);
        return productRepo.save(product);
    }

    public Product getOneProduct(Long id) {
        return findProduct(id);
    }

    @Transactional
    public Product updateProduct(Long id, ProductRequest request) {
        Product product = findProduct(id);
        applyProductRequest(product, request);
        return productRepo.save(product);
    }

    @Transactional
    public void delete(Long id) {
        Product product = findProduct(id);
        productRepo.delete(product);
    }

    public List<Product> search(String keyword) {
        return productRepo.findByproductNameContaining(keyword);
    }

    public Page<Product> filterProducts(
            String keyword,
            Double minPrice,
            Double maxPrice,
            Long minQuantity,
            Long maxQuantity,
            Long categoryId,
            String stockStatus,
            int page,
            int size,
            String sortBy,
            String sortDir) {

        validateFilter(minPrice, maxPrice, minQuantity, maxQuantity);

        int safePage = Math.max(page, 0);
        int safeSize = normalizePageSize(size);
        Pageable pageable = PageRequest.of(safePage, safeSize, buildSort(sortBy, sortDir));

        return productRepo.findAll(
                buildProductSpecification(keyword, minPrice, maxPrice, minQuantity, maxQuantity, categoryId, stockStatus),
                pageable);
    }

    public Map<String, Object> getDashboardData() {
        List<Product> products = productRepo.findAll(Sort.by(Sort.Direction.ASC, "id"));

        long totalProducts = products.size();
        long totalStock = products.stream()
                .mapToLong(p -> p.getQuantity() == null ? 0 : p.getQuantity())
                .sum();
        long outOfStock = products.stream()
                .filter(p -> p.getQuantity() == null || p.getQuantity() == 0)
                .count();
        long lowStock = products.stream()
                .filter(p -> p.getQuantity() != null && p.getQuantity() > 0 && p.getQuantity() <= 5)
                .count();
        long available = products.stream()
                .filter(p -> p.getQuantity() != null && p.getQuantity() > 5)
                .count();
        double inventoryValue = products.stream()
                .mapToDouble(p -> p.getPrice() * (p.getQuantity() == null ? 0 : p.getQuantity()))
                .sum();

        List<Map<String, Object>> topStockProducts = products.stream()
                .sorted((a, b) -> Long.compare(
                        b.getQuantity() == null ? 0 : b.getQuantity(),
                        a.getQuantity() == null ? 0 : a.getQuantity()))
                .limit(7)
                .map(this::toProductSummary)
                .collect(Collectors.toList());

        List<Map<String, Object>> highestValueProducts = products.stream()
                .sorted((a, b) -> Double.compare(
                        b.getPrice() * (b.getQuantity() == null ? 0 : b.getQuantity()),
                        a.getPrice() * (a.getQuantity() == null ? 0 : a.getQuantity())))
                .limit(7)
                .map(this::toProductSummary)
                .collect(Collectors.toList());

        Map<String, Long> categorySummary = products.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getCategory() == null ? "Uncategorized" : p.getCategory().getName(),
                        LinkedHashMap::new,
                        Collectors.counting()));

        Map<String, Object> stockSummary = new LinkedHashMap<>();
        stockSummary.put("outOfStock", outOfStock);
        stockSummary.put("lowStock", lowStock);
        stockSummary.put("available", available);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalProducts", totalProducts);
        result.put("totalStock", totalStock);
        result.put("outOfStock", outOfStock);
        result.put("lowStock", lowStock);
        result.put("available", available);
        result.put("inventoryValue", inventoryValue);
        result.put("stockSummary", stockSummary);
        result.put("categorySummary", categorySummary);
        result.put("topStockProducts", topStockProducts);
        result.put("highestValueProducts", highestValueProducts);
        return result;
    }

    public String exportProducts(
            String keyword,
            Double minPrice,
            Double maxPrice,
            Long minQuantity,
            Long maxQuantity,
            Long categoryId,
            String stockStatus,
            String sortBy,
            String sortDir) {

        validateFilter(minPrice, maxPrice, minQuantity, maxQuantity);

        List<Product> products = productRepo.findAll(
                buildProductSpecification(keyword, minPrice, maxPrice, minQuantity, maxQuantity, categoryId, stockStatus),
                buildSort(sortBy, sortDir));

        StringBuilder csv = new StringBuilder();
        csv.append("ID,Name,Category,Price,Quantity,Inventory Value,Description,Image URL\n");
        for (Product p : products) {
            long quantity = p.getQuantity() == null ? 0 : p.getQuantity();
            double inventoryValue = p.getPrice() * quantity;
            csv.append(p.getId()).append(',')
                    .append(csvValue(p.getProduct_name())).append(',')
                    .append(csvValue(categoryName(p))).append(',')
                    .append(p.getPrice()).append(',')
                    .append(quantity).append(',')
                    .append(inventoryValue).append(',')
                    .append(csvValue(p.getDescription())).append(',')
                    .append(csvValue(p.getImg()))
                    .append('\n');
        }

        return csv.toString();
    }

    private Product findProduct(Long id) {
        return productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id));
    }

    private void applyProductRequest(Product product, ProductRequest request) {
        product.setProduct_name(request.getProductName().trim());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setDescription(blankToNull(request.getDescription()));
        product.setImg(blankToNull(request.getImg()));

        Category category = categoryRepo.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + request.getCategoryId()));
        product.setCategory(category);
    }

    private Specification<Product> buildProductSpecification(
            String keyword,
            Double minPrice,
            Double maxPrice,
            Long minQuantity,
            Long maxQuantity,
            Long categoryId,
            String stockStatus) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.trim().isEmpty()) {
                String pattern = "%" + keyword.trim().toLowerCase() + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("productName")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.join("category", jakarta.persistence.criteria.JoinType.LEFT).<String>get("name")), pattern)));
            }

            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.<Double>get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.<Double>get("price"), maxPrice));
            }
            if (minQuantity != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.<Long>get("quantity"), minQuantity));
            }
            if (maxQuantity != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.<Long>get("quantity"), maxQuantity));
            }
            if (categoryId != null) {
                predicates.add(criteriaBuilder.equal(root.get("category").get("id"), categoryId));
            }

            if (stockStatus != null) {
                switch (stockStatus) {
                    case "inStock" -> predicates.add(criteriaBuilder.greaterThan(root.<Long>get("quantity"), 0L));
                    case "outOfStock" -> predicates.add(criteriaBuilder.or(
                            criteriaBuilder.isNull(root.get("quantity")),
                            criteriaBuilder.equal(root.<Long>get("quantity"), 0L)));
                    case "lowStock" -> predicates.add(criteriaBuilder.and(
                            criteriaBuilder.greaterThan(root.<Long>get("quantity"), 0L),
                            criteriaBuilder.lessThanOrEqualTo(root.<Long>get("quantity"), 5L)));
                    default -> {
                    }
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void validateFilter(Double minPrice, Double maxPrice, Long minQuantity, Long maxQuantity) {
        if (minPrice != null && minPrice < 0) {
            throw new BadRequestException("Minimum price cannot be negative");
        }
        if (maxPrice != null && maxPrice < 0) {
            throw new BadRequestException("Maximum price cannot be negative");
        }
        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            throw new BadRequestException("Minimum price cannot be greater than maximum price");
        }
        if (minQuantity != null && minQuantity < 0) {
            throw new BadRequestException("Minimum quantity cannot be negative");
        }
        if (maxQuantity != null && maxQuantity < 0) {
            throw new BadRequestException("Maximum quantity cannot be negative");
        }
        if (minQuantity != null && maxQuantity != null && minQuantity > maxQuantity) {
            throw new BadRequestException("Minimum quantity cannot be greater than maximum quantity");
        }
    }

    private Sort buildSort(String sortBy, String sortDir) {
        String safeSortBy = switch (sortBy) {
            case "productName", "price", "quantity", "id" -> sortBy;
            default -> "id";
        };
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, safeSortBy);
    }

    private int normalizePageSize(int size) {
        return switch (size) {
            case 10, 20, 50, 100 -> size;
            default -> 10;
        };
    }

    private Map<String, Object> toProductSummary(Product p) {
        Map<String, Object> row = new LinkedHashMap<>();
        long quantity = p.getQuantity() == null ? 0 : p.getQuantity();
        row.put("id", p.getId());
        row.put("name", p.getProduct_name());
        row.put("category", categoryName(p));
        row.put("price", p.getPrice());
        row.put("quantity", quantity);
        row.put("inventoryValue", p.getPrice() * quantity);
        return row;
    }

    private String categoryName(Product p) {
        return p.getCategory() == null ? "Uncategorized" : p.getCategory().getName();
    }

    private String csvValue(String value) {
        if (value == null) {
            return "\"\"";
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private String blankToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}
