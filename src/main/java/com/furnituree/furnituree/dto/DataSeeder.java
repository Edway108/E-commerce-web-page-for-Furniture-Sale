// package com.furnituree.furnituree.dto;

// import java.util.Random;

// import org.springframework.boot.CommandLineRunner;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// import com.furnituree.furnituree.model.Product;
// import com.furnituree.furnituree.repo.product_repo;

// @Configuration
// public class DataSeeder {

// @Bean
// CommandLineRunner initDatabase(product_repo repo) {
// return args -> {

// if (repo.count() > 22) {
// System.out.println("Data already seeded!");
// return;
// }

// String[] names = {
// "Sofa", "Table", "Chair", "Bed", "Wardrobe",
// "Desk", "Lamp", "Shelf", "Cabinet", "TV Stand"
// };

// Random rand = new Random();

// for (int i = 1; i <= 50; i++) {
// Product p = new Product();

// p.setProduct_name(names[rand.nextInt(names.length)] + " " + i);
// p.setPrice(100 + rand.nextInt(900)); // 100 -> 1000
// p.setQuantity((long) (1 + rand.nextInt(50)));
// p.setDescription("High quality furniture item number " + i);
// p.setImg("https://picsum.photos/200?random=" + i);

// repo.save(p);
// }

// System.out.println("Seeded 50 products!");
// };
// }
// }