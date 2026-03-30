package com.furnituree.furnituree.Controller;

import java.util.List;

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

import com.furnituree.furnituree.model.Product;
import com.furnituree.furnituree.repo.product_repo;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/products")
public class product_controller {
    private final product_repo repo;

    public product_controller(product_repo repo) {
        this.repo = repo;
    }

    // Show all the products

    @GetMapping("/findall")
    public List<Product> getAll() {
        return repo.findAll();
    }

    // Post the product up

    @PostMapping("/addproduct")
    public Product create(@RequestBody Product p) {
        return repo.save(p);
    }

    // Read one
    @GetMapping("/{id}")
    public Product getOneProduct(@PathVariable Long id) {
        return repo.findById(id).orElse(null);
    }

    // Update one product\
    @PutMapping("/update/{id}")
    public Product UpdateProduct(@PathVariable Long id, @RequestBody Product product) {
        Product old = repo.findById(id).orElse(null);

        if (old == null)
            return null;
        old.setProduct_name(product.getProduct_name());
        old.setPrice(product.getPrice());
        old.setDescription(product.getDescription());
        old.setImg(product.getImg());
        old.setQuantity(product.getQuantity());

        return repo.save(old);

    }

    // delete one product
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }

    // find product by product_name
    @CrossOrigin(origins = "*")
    @GetMapping("/search")
    public List<Product> search(@RequestParam String keyword) {
        return repo.findByproductNameContaining(keyword);
    }

}
