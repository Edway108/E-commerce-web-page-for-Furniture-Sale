package com.furnituree.furnituree.config;

import java.util.List;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.furnituree.furnituree.model.Category;
import com.furnituree.furnituree.model.Product;
import com.furnituree.furnituree.model.User;
import com.furnituree.furnituree.repo.category_repo;
import com.furnituree.furnituree.repo.product_repo;
import com.furnituree.furnituree.repo.user_repo;

@Configuration
public class DataSeeder {

    private static final int TARGET_PRODUCT_COUNT = 120;

    @Bean
    CommandLineRunner initDatabase(product_repo productRepo,
            category_repo categoryRepo,
            user_repo userRepo,
            PasswordEncoder encoder) {
        return args -> {
            seedUsers(userRepo, encoder);
            List<Category> categories = seedCategories(categoryRepo);

            // Update old seeded products that used broken/random image URLs.
            normalizeExistingProductImages(productRepo);

            seedProducts(productRepo, categories);
        };
    }

    private void seedUsers(user_repo userRepo, PasswordEncoder encoder) {
        createUserIfMissing(userRepo, encoder, "admin", "123456", "admin",
                123456789, "Admin address", true);

        createUserIfMissing(userRepo, encoder, "user", "123456", "user",
                987654321, "User address", true);

        createUserIfMissing(userRepo, encoder, "manager", "123456", "manager",
                123123123, "Manager address", true);

        createUserIfMissing(userRepo, encoder, "customer01", "123456", "user",
                901000001, "12 Nguyen Trai, Ho Chi Minh", true);

        createUserIfMissing(userRepo, encoder, "customer02", "123456", "user",
                901000002, "25 Le Loi, Ho Chi Minh", true);

        createUserIfMissing(userRepo, encoder, "customer03", "123456", "user",
                901000003, "88 Tran Phu, Da Nang", true);

        createUserIfMissing(userRepo, encoder, "customer04", "123456", "user",
                901000004, "19 Hai Ba Trung, Ha Noi", true);

        createUserIfMissing(userRepo, encoder, "customer05", "123456", "user",
                901000005, "45 Pasteur, Ho Chi Minh", true);

        createUserIfMissing(userRepo, encoder, "staff01", "123456", "user",
                902000001, "Warehouse A, Thu Duc", false);

        createUserIfMissing(userRepo, encoder, "staff02", "123456", "user",
                902000002, "Showroom District 1", true);
    }

    private void createUserIfMissing(user_repo userRepo,
            PasswordEncoder encoder,
            String username,
            String password,
            String role,
            int phone,
            String address,
            boolean active) {

        if (userRepo.findByUsername(username) != null) {
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(encoder.encode(password));
        user.setRole(role);
        user.setActive(active);
        user.setAddress(address);
        user.setPhonenumber(phone);

        userRepo.save(user);
    }

    private List<Category> seedCategories(category_repo categoryRepo) {
        String[][] categoryData = {
                { "Sofa", "Comfortable seating for living rooms", imageForCategory("sofa") },
                { "Table", "Dining, coffee and side tables", imageForCategory("table") },
                { "Chair", "Accent, office and dining chairs", imageForCategory("chair") },
                { "Bed", "Bedroom furniture and bed frames", imageForCategory("bed") },
                { "Storage", "Wardrobes, cabinets and shelves", imageForCategory("storage") },
                { "Lighting", "Lamps and decorative lighting", imageForCategory("lighting") },
                { "Office", "Desks, office chairs and work-from-home furniture", imageForCategory("office") },
                { "Outdoor", "Balcony, garden and patio furniture", imageForCategory("outdoor") },
                { "Decor", "Mirrors, rugs and decorative accessories", imageForCategory("decor") },
                { "Dining", "Dining tables, dining chairs and bar stools", imageForCategory("dining") }
        };

        for (String[] row : categoryData) {
            Category category = categoryRepo.findByNameIgnoreCase(row[0]).orElse(null);

            if (category == null) {
                category = new Category();
                category.setName(row[0]);
            }

            category.setDescription(row[1]);
            category.setImageUrl(row[2]);
            category.setStatus("ACTIVE");
            categoryRepo.save(category);
        }

        return categoryRepo.findAll();
    }

    private void seedProducts(product_repo productRepo, List<Category> categories) {
        long existingCount = productRepo.count();

        if (existingCount >= TARGET_PRODUCT_COUNT) {
            return;
        }

        String[] productTypes = {
                "Sofa", "Coffee Table", "Dining Table", "Office Chair", "Lounge Chair",
                "Queen Bed", "Wardrobe", "Bookshelf", "Storage Cabinet", "TV Stand",
                "Desk Lamp", "Floor Lamp", "Writing Desk", "Patio Chair", "Outdoor Table",
                "Mirror", "Rug", "Bar Stool", "Side Table", "Nightstand"
        };

        String[] materials = {
                "Oak Wood", "Walnut", "Ash Wood", "Rattan", "Fabric",
                "Leather", "Metal", "Glass", "Marble", "Bamboo"
        };

        String[] styles = {
                "Modern", "Minimalist", "Scandinavian", "Industrial", "Classic",
                "Luxury", "Compact", "Premium", "Urban", "Natural"
        };

        Random rand = new Random(2026);

        for (int i = (int) existingCount + 1; i <= TARGET_PRODUCT_COUNT; i++) {
            String type = productTypes[(i - 1) % productTypes.length];
            String material = materials[rand.nextInt(materials.length)];
            String style = styles[rand.nextInt(styles.length)];

            Product product = new Product();
            product.setProduct_name(style + " " + material + " " + type + " " + i);
            product.setPrice(80 + rand.nextInt(1420));
            product.setQuantity((long) rand.nextInt(101));
            product.setDescription(buildDescription(style, material, type, i));
            product.setImg(imageForProduct(type, i));
            product.setCategory(matchCategory(type, categories));

            productRepo.save(product);
        }
    }

    private void normalizeExistingProductImages(product_repo productRepo) {
        List<Product> products = productRepo.findAll();

        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            String type = detectProductType(product.getProduct_name());
            product.setImg(imageForProduct(type, i + 1));
            productRepo.save(product);
        }
    }

    private String buildDescription(String style, String material, String type, int index) {
        return style + " " + type.toLowerCase()
                + " made with " + material.toLowerCase()
                + ". Suitable for home, office, and showroom spaces. Sample item #" + index
                + " for realistic catalog testing.";
    }

    private Category matchCategory(String productName, List<Category> categories) {
        String target = switch (productName) {
            case "Sofa", "Lounge Chair" -> "Sofa";
            case "Coffee Table", "Dining Table", "Side Table", "Outdoor Table" -> "Table";
            case "Office Chair", "Patio Chair", "Bar Stool" -> "Chair";
            case "Queen Bed", "Nightstand" -> "Bed";
            case "Wardrobe", "Bookshelf", "Storage Cabinet", "TV Stand" -> "Storage";
            case "Desk Lamp", "Floor Lamp" -> "Lighting";
            case "Writing Desk" -> "Office";
            case "Mirror", "Rug" -> "Decor";
            default -> "Storage";
        };

        return categories.stream()
                .filter(category -> category.getName().equalsIgnoreCase(target))
                .findFirst()
                .orElse(categories.isEmpty() ? null : categories.get(0));
    }

    private String detectProductType(String productName) {
        if (productName == null) {
            return "Furniture";
        }

        String name = productName.toLowerCase();

        if (name.contains("coffee table")) return "Coffee Table";
        if (name.contains("dining table")) return "Dining Table";
        if (name.contains("outdoor table")) return "Outdoor Table";
        if (name.contains("side table")) return "Side Table";
        if (name.contains("office chair")) return "Office Chair";
        if (name.contains("lounge chair")) return "Lounge Chair";
        if (name.contains("patio chair")) return "Patio Chair";
        if (name.contains("bar stool")) return "Bar Stool";
        if (name.contains("queen bed")) return "Queen Bed";
        if (name.contains("nightstand")) return "Nightstand";
        if (name.contains("storage cabinet")) return "Storage Cabinet";
        if (name.contains("tv stand")) return "TV Stand";
        if (name.contains("desk lamp")) return "Desk Lamp";
        if (name.contains("floor lamp")) return "Floor Lamp";
        if (name.contains("writing desk")) return "Writing Desk";

        if (name.contains("sofa")) return "Sofa";
        if (name.contains("table")) return "Coffee Table";
        if (name.contains("chair")) return "Office Chair";
        if (name.contains("bed")) return "Queen Bed";
        if (name.contains("wardrobe")) return "Wardrobe";
        if (name.contains("bookshelf") || name.contains("shelf")) return "Bookshelf";
        if (name.contains("cabinet")) return "Storage Cabinet";
        if (name.contains("lamp")) return "Desk Lamp";
        if (name.contains("desk")) return "Writing Desk";
        if (name.contains("mirror")) return "Mirror";
        if (name.contains("rug")) return "Rug";

        return "Furniture";
    }

    private String imageForProduct(String type, int index) {
        String[] images = switch (type) {
            case "Sofa", "Lounge Chair" -> new String[] {
                    "https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=600&h=400&fit=crop",
                    "https://images.unsplash.com/photo-1540574163026-643ea20ade25?w=600&h=400&fit=crop",
                    "https://images.unsplash.com/photo-1493663284031-b7e3aefcae8e?w=600&h=400&fit=crop"
            };

            case "Coffee Table", "Side Table" -> new String[] {
                    "https://images.unsplash.com/photo-1532372320572-cda25653a26d?w=600&h=400&fit=crop",
                    "https://images.unsplash.com/photo-1567016432779-094069958ea5?w=600&h=400&fit=crop",
                    "https://images.unsplash.com/photo-1616486029423-aaa4789e8c9a?w=600&h=400&fit=crop"
            };

            case "Dining Table", "Outdoor Table" -> new String[] {
                    "https://images.unsplash.com/photo-1615873968403-89e068629265?w=600&h=400&fit=crop",
                    "https://images.unsplash.com/photo-1615066390971-03e4e1c36ddf?w=600&h=400&fit=crop",
                    "https://images.unsplash.com/photo-1600210491892-03d54c0aaf87?w=600&h=400&fit=crop"
            };

            case "Office Chair", "Patio Chair", "Bar Stool" -> new String[] {
                    "https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=600&h=400&fit=crop",
                    "https://images.unsplash.com/photo-1598300042247-d088f8ab3a91?w=600&h=400&fit=crop",
                    "https://images.unsplash.com/photo-1503602642458-232111445657?w=600&h=400&fit=crop"
            };

            case "Queen Bed", "Nightstand" -> new String[] {
                    "https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?w=600&h=400&fit=crop",
                    "https://images.unsplash.com/photo-1617325247661-675ab4b64ae2?w=600&h=400&fit=crop",
                    "https://images.unsplash.com/photo-1615874694520-474822394e73?w=600&h=400&fit=crop"
            };

            case "Wardrobe", "Storage Cabinet" -> new String[] {
                    "https://images.unsplash.com/photo-1595428774223-ef52624120d2?w=600&h=400&fit=crop",
                    "https://images.unsplash.com/photo-1616627561839-074385245ff6?w=600&h=400&fit=crop",
                    "https://images.unsplash.com/photo-1618220179428-22790b461013?w=600&h=400&fit=crop"
            };

            case "Bookshelf" -> new String[] {
                    "https://images.unsplash.com/photo-1521587760476-6c12a4b040da?w=600&h=400&fit=crop",
                    "https://images.unsplash.com/photo-1594620302200-9a762244a156?w=600&h=400&fit=crop",
                    "https://images.unsplash.com/photo-1519682337058-a94d519337bc?w=600&h=400&fit=crop"
            };

            case "TV Stand" -> new String[] {
                    "https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?w=600&h=400&fit=crop",
                    "https://images.unsplash.com/photo-1461151304267-38535e780c79?w=600&h=400&fit=crop",
                    "https://images.unsplash.com/photo-1601944179066-29786cb9d32a?w=600&h=400&fit=crop"
            };

            case "Desk Lamp", "Floor Lamp" -> new String[] {
                    "https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=600&h=400&fit=crop",
                    "https://images.unsplash.com/photo-1513506003901-1e6a229e2d15?w=600&h=400&fit=crop",
                    "https://images.unsplash.com/photo-1543198126-a8ad8e47fb22?w=600&h=400&fit=crop"
            };

            case "Writing Desk" -> new String[] {
                    "https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?w=600&h=400&fit=crop",
                    "https://images.unsplash.com/photo-1497366754035-f200968a6e72?w=600&h=400&fit=crop",
                    "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=600&h=400&fit=crop"
            };

            case "Mirror" -> new String[] {
                    "https://images.unsplash.com/photo-1618220179428-22790b461013?w=600&h=400&fit=crop",
                    "https://images.unsplash.com/photo-1600489000022-c2086d79f9d4?w=600&h=400&fit=crop"
            };

            case "Rug" -> new String[] {
                    "https://images.unsplash.com/photo-1600166898405-da9535204843?w=600&h=400&fit=crop",
                    "https://images.unsplash.com/photo-1600566753190-17f0baa2a6c3?w=600&h=400&fit=crop"
            };

            default -> new String[] {
                    "https://images.unsplash.com/photo-1618221195710-dd6b41faaea6?w=600&h=400&fit=crop",
                    "https://images.unsplash.com/photo-1616486338812-3dadae4b4ace?w=600&h=400&fit=crop"
            };
        };

        return images[index % images.length];
    }

    private String imageForCategory(String keyword) {
        return switch (keyword) {
            case "sofa" ->
                    "https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=600&h=400&fit=crop";

            case "table", "dining" ->
                    "https://images.unsplash.com/photo-1615873968403-89e068629265?w=600&h=400&fit=crop";

            case "chair" ->
                    "https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=600&h=400&fit=crop";

            case "bed" ->
                    "https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?w=600&h=400&fit=crop";

            case "storage" ->
                    "https://images.unsplash.com/photo-1595428774223-ef52624120d2?w=600&h=400&fit=crop";

            case "lighting" ->
                    "https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=600&h=400&fit=crop";

            case "office" ->
                    "https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?w=600&h=400&fit=crop";

            case "outdoor" ->
                    "https://images.unsplash.com/photo-1600210491892-03d54c0aaf87?w=600&h=400&fit=crop";

            case "decor" ->
                    "https://images.unsplash.com/photo-1618220179428-22790b461013?w=600&h=400&fit=crop";

            default ->
                    "https://images.unsplash.com/photo-1618221195710-dd6b41faaea6?w=600&h=400&fit=crop";
        };
    }
}
