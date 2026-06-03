-- Seed tags and product_tags after Spring Boot creates the tables.
-- Run this after the application starts successfully once.

INSERT IGNORE INTO tags (name, description, status, created_at, updated_at) VALUES
('Living Room', 'Furniture commonly used in living room spaces', 'ACTIVE', NOW(), NOW()),
('Bedroom', 'Furniture commonly used in bedrooms', 'ACTIVE', NOW(), NOW()),
('Office', 'Furniture for work and study spaces', 'ACTIVE', NOW(), NOW()),
('Storage', 'Storage-focused furniture and organizers', 'ACTIVE', NOW(), NOW()),
('Lighting', 'Lamps and lighting products', 'ACTIVE', NOW(), NOW()),
('Decor', 'Decorative furniture and accessories', 'ACTIVE', NOW(), NOW()),
('Outdoor', 'Outdoor and patio furniture', 'ACTIVE', NOW(), NOW()),
('Wood', 'Products made with wood materials', 'ACTIVE', NOW(), NOW()),
('Premium', 'Premium or luxury furniture items', 'ACTIVE', NOW(), NOW()),
('Compact', 'Small-space and compact furniture', 'ACTIVE', NOW(), NOW());

INSERT IGNORE INTO product_tags (product_id, tag_id)
SELECT p.id, t.id FROM product p JOIN tags t ON t.name = 'Living Room'
WHERE p.product_name LIKE '%Sofa%' OR p.product_name LIKE '%TV Stand%' OR p.product_name LIKE '%Coffee Table%';

INSERT IGNORE INTO product_tags (product_id, tag_id)
SELECT p.id, t.id FROM product p JOIN tags t ON t.name = 'Bedroom'
WHERE p.product_name LIKE '%Bed%' OR p.product_name LIKE '%Nightstand%' OR p.product_name LIKE '%Wardrobe%';

INSERT IGNORE INTO product_tags (product_id, tag_id)
SELECT p.id, t.id FROM product p JOIN tags t ON t.name = 'Office'
WHERE p.product_name LIKE '%Office%' OR p.product_name LIKE '%Desk%' OR p.product_name LIKE '%Writing Desk%';

INSERT IGNORE INTO product_tags (product_id, tag_id)
SELECT p.id, t.id FROM product p JOIN tags t ON t.name = 'Storage'
WHERE p.product_name LIKE '%Storage%' OR p.product_name LIKE '%Bookshelf%' OR p.product_name LIKE '%Cabinet%' OR p.product_name LIKE '%Wardrobe%';

INSERT IGNORE INTO product_tags (product_id, tag_id)
SELECT p.id, t.id FROM product p JOIN tags t ON t.name = 'Lighting'
WHERE p.product_name LIKE '%Lamp%';

INSERT IGNORE INTO product_tags (product_id, tag_id)
SELECT p.id, t.id FROM product p JOIN tags t ON t.name = 'Decor'
WHERE p.product_name LIKE '%Mirror%' OR p.product_name LIKE '%Rug%';

INSERT IGNORE INTO product_tags (product_id, tag_id)
SELECT p.id, t.id FROM product p JOIN tags t ON t.name = 'Outdoor'
WHERE p.product_name LIKE '%Outdoor%' OR p.product_name LIKE '%Patio%';

INSERT IGNORE INTO product_tags (product_id, tag_id)
SELECT p.id, t.id FROM product p JOIN tags t ON t.name = 'Wood'
WHERE p.product_name LIKE '%Wood%' OR p.product_name LIKE '%Oak%' OR p.product_name LIKE '%Walnut%' OR p.product_name LIKE '%Ash%';

INSERT IGNORE INTO product_tags (product_id, tag_id)
SELECT p.id, t.id FROM product p JOIN tags t ON t.name = 'Premium'
WHERE p.product_name LIKE '%Premium%' OR p.product_name LIKE '%Luxury%';

INSERT IGNORE INTO product_tags (product_id, tag_id)
SELECT p.id, t.id FROM product p JOIN tags t ON t.name = 'Compact'
WHERE p.product_name LIKE '%Compact%' OR p.price < 300;
