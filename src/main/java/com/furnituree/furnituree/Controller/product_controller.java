package com.furnituree.furnituree.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.furnituree.furnituree.dto.ProductRequest;
import com.furnituree.furnituree.model.Product;
import com.furnituree.furnituree.service.ProductService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/products")
public class product_controller {

    private final ProductService productService;

    public product_controller(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/findall")
    public List<Product> getAll() {
        return productService.getAll();
    }

    @PostMapping("/addproduct")
    public ResponseEntity<Product> create(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(request));
    }

    @GetMapping("/{id}")
    public Product getOneProduct(@PathVariable Long id) {
        return productService.getOneProduct(id);
    }

    @PutMapping("/update/{id}")
    public Product updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return productService.updateProduct(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/search")
    public List<Product> search(@RequestParam String keyword) {
        return productService.search(keyword);
    }

    @GetMapping("/filter")
    public Page<Product> filterProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Long minQuantity,
            @RequestParam(required = false) Long maxQuantity,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "all") String stockStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        return productService.filterProducts(
                keyword, minPrice, maxPrice, minQuantity, maxQuantity, categoryId, stockStatus,
                page, size, sortBy, sortDir);
    }

    @GetMapping("/dashboard")
    public Map<String, Object> getDashboardData() {
        return productService.getDashboardData();
    }

    @GetMapping(value = "/export", produces = "text/csv")
    public ResponseEntity<String> exportProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Long minQuantity,
            @RequestParam(required = false) Long maxQuantity,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "all") String stockStatus,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        String csv = productService.exportProducts(
                keyword, minPrice, maxPrice, minQuantity, maxQuantity, categoryId, stockStatus, sortBy, sortDir);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=product-report.csv")
                .contentType(new MediaType("text", "csv"))
                .body(csv);
    }
}
