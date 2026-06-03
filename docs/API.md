# Furnituree API Documentation

Base URL for local development:

```text
http://127.0.0.1:8080
```

Most protected endpoints require:

```http
Authorization: Bearer <JWT_TOKEN>
```

## Authentication

### Register

```http
POST /auth/register
Content-Type: application/json
```

Example body:

```json
{
  "username": "customer01",
  "password": "123456",
  "phonenumber": 901000001,
  "address": "12 Nguyen Trai, Ho Chi Minh"
}
```

### Login

```http
POST /auth/login
Content-Type: application/json
```

Example body:

```json
{
  "username": "admin",
  "password": "123456"
}
```

Use the returned token for protected requests.

## Products

### Get all products

```http
GET /products/findall
Authorization: Bearer <token>
```

### Get product by ID

```http
GET /products/{id}
Authorization: Bearer <token>
```

### Create product

```http
POST /products/addproduct
Authorization: Bearer <admin_or_manager_token>
Content-Type: application/json
```

Example body:

```json
{
  "product_name": "Modern Oak Wood Desk",
  "price": 450,
  "quantity": 25,
  "description": "Modern desk for office and study spaces",
  "img": "https://example.com/desk.jpg",
  "category": {
    "id": 1
  }
}
```

### Update product

```http
PUT /products/{id}
Authorization: Bearer <admin_or_manager_token>
Content-Type: application/json
```

### Delete product

```http
DELETE /products/{id}
Authorization: Bearer <admin_or_manager_token>
```

### Filter, search, sort, and paginate products

```http
GET /products/filter?keyword=desk&categoryId=1&minPrice=100&maxPrice=1000&stock=IN_STOCK&sortBy=price&direction=asc&page=0&size=10
Authorization: Bearer <token>
```

Supported criteria may include:

| Parameter | Description |
|---|---|
| `keyword` | Search by name or description |
| `categoryId` | Filter by category |
| `minPrice` | Minimum price |
| `maxPrice` | Maximum price |
| `stock` | Stock filter |
| `sortBy` | `id`, `name`, `price`, `quantity` |
| `direction` | `asc` or `desc` |
| `page` | Page number |
| `size` | Page size |

### Dashboard statistics

```http
GET /products/dashboard
Authorization: Bearer <admin_or_manager_token>
```

### Export products to CSV

```http
GET /products/export
Authorization: Bearer <admin_or_manager_token>
```

## Categories

### Get all categories

```http
GET /categories/findall
Authorization: Bearer <token>
```

### Create category

```http
POST /categories/addcategory
Authorization: Bearer <admin_or_manager_token>
Content-Type: application/json
```

Example body:

```json
{
  "name": "Sofa",
  "description": "Comfortable seating for living rooms",
  "imageUrl": "https://example.com/sofa.jpg",
  "status": "ACTIVE"
}
```

### Update category

```http
PUT /categories/{id}
Authorization: Bearer <admin_or_manager_token>
Content-Type: application/json
```

### Delete or deactivate category

```http
DELETE /categories/{id}
Authorization: Bearer <admin_or_manager_token>
```

Business rule:

```text
A category should not be deleted if it still contains active products.
```

## Users

### Get all users

```http
GET /users/findall
Authorization: Bearer <admin_or_manager_token>
```

### Get user profile

```http
GET /users/profile
Authorization: Bearer <token>
```

### Update profile

```http
PUT /users/profile
Authorization: Bearer <token>
Content-Type: application/json
```

### Change password

```http
PUT /users/change-password
Authorization: Bearer <token>
Content-Type: application/json
```

Example body:

```json
{
  "oldPassword": "123456",
  "newPassword": "NewPassword123"
}
```

### Create user by admin

```http
POST /users
Authorization: Bearer <admin_or_manager_token>
Content-Type: application/json
```

### Update user

```http
PUT /users/{id}
Authorization: Bearer <admin_or_manager_token>
Content-Type: application/json
```

### Deactivate user

```http
PATCH /users/{id}/deactivate
Authorization: Bearer <admin_or_manager_token>
```

Soft delete rule:

```text
Users are not physically deleted. The system deactivates accounts to preserve order history and referential integrity.
```

## Cart

### Get current user's cart

```http
GET /cart/getcart
Authorization: Bearer <token>
```

### Add product to cart

```http
POST /cart/addcart
Authorization: Bearer <token>
Content-Type: application/json
```

Example body:

```json
{
  "productId": 1,
  "quantity": 2
}
```

Business rule:

```text
The requested cart quantity must not exceed product stock.
```

### Remove item from cart

```http
DELETE /cart/item
Authorization: Bearer <token>
Content-Type: application/json
```

Example body:

```json
{
  "productId": 1
}
```

### Clear cart

```http
DELETE /cart/{cartId}
Authorization: Bearer <token>
```

## Orders and Checkout

### Checkout

```http
POST /orders/checkout
Authorization: Bearer <token>
Content-Type: application/json
```

Example body:

```json
{
  "shippingName": "Nguyen Van A",
  "shippingPhone": "0901234567",
  "shippingAddress": "123 Nguyen Trai",
  "shippingCity": "Ho Chi Minh",
  "paymentMethod": "COD"
}
```

Supported payment methods:

```text
COD
BANK_TRANSFER
MOCK_CARD
```

Business rules:

```text
- Cart must not be empty.
- Shipping information must be valid.
- Product stock must be sufficient.
- Stock is deducted after successful checkout.
- Cart is cleared after successful checkout.
- A payment record is created with the order.
```

### Get my orders

```http
GET /orders/my-orders
Authorization: Bearer <token>
```

### Get order detail

```http
GET /orders/{id}
Authorization: Bearer <token>
```

Authorization rule:

```text
Users can only view their own orders.
Admin and manager can view all orders.
```

### Cancel my order

```http
PATCH /orders/{id}/cancel
Authorization: Bearer <token>
```

Business rules:

```text
- Users can cancel only their own orders.
- Orders that are SHIPPING or COMPLETED cannot be cancelled.
- Cancelled orders restore product stock.
- Payment status is changed to FAILED for cancelled orders.
```

### Admin / Manager: get all orders

```http
GET /orders/admin/all
Authorization: Bearer <admin_or_manager_token>
```

### Admin / Manager: update order status

```http
PATCH /orders/{id}/status
Authorization: Bearer <admin_or_manager_token>
Content-Type: application/json
```

Example body:

```json
{
  "status": "CONFIRMED"
}
```

Supported order statuses:

```text
PENDING
CONFIRMED
SHIPPING
COMPLETED
CANCELLED
```

Business rules:

```text
- Final orders cannot be changed.
- COMPLETED and CANCELLED are final statuses.
- Orders cannot be moved back to PENDING.
- When an order is COMPLETED, payment is marked as PAID.
```

## Tags

Tags demonstrate a many-to-many relationship between products and tags.

Relationship:

```text
Product n — n Tag
Junction table: product_tags
```

### Get all tags

```http
GET /tags
Authorization: Bearer <token>
```

### Get active tags

```http
GET /tags/active
Authorization: Bearer <token>
```

### Get tag by ID

```http
GET /tags/{id}
Authorization: Bearer <token>
```

### Create tag

```http
POST /tags
Authorization: Bearer <admin_or_manager_token>
Content-Type: application/json
```

Example body:

```json
{
  "name": "Premium",
  "description": "Premium or luxury furniture items",
  "status": "ACTIVE"
}
```

### Update tag

```http
PUT /tags/{id}
Authorization: Bearer <admin_or_manager_token>
Content-Type: application/json
```

### Deactivate tag

```http
PATCH /tags/{id}/deactivate
Authorization: Bearer <admin_or_manager_token>
```

### Assign tag to product

```http
POST /tags/products/{productId}/tags/{tagId}
Authorization: Bearer <admin_or_manager_token>
```

### Remove tag from product

```http
DELETE /tags/products/{productId}/tags/{tagId}
Authorization: Bearer <admin_or_manager_token>
```

### Get tags of a product

```http
GET /tags/products/{productId}
Authorization: Bearer <token>
```

## Chat

If enabled, the chat module uses WebSocket/STOMP.

Typical endpoints:

```text
/ws
/app/chat
/topic/messages
```

Use the frontend chat button to test the feature.

## Common Error Responses

### 400 Bad Request

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Cart is empty"
}
```

### 401 Unauthorized

```text
Missing or invalid JWT token.
```

### 403 Forbidden

```text
The logged-in user does not have permission for this action.
```

### 404 Not Found

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Product not found"
}
```

## API Testing Checklist

```text
1. Login as admin
2. Login as user
3. Get product list
4. Search/filter/sort/paginate products
5. Add product to cart
6. Checkout
7. View my orders
8. Cancel eligible order
9. Login as admin/manager
10. View all orders
11. Update order status
12. Get all tags
13. Assign tag to product
14. Query product_tags in MySQL
```
