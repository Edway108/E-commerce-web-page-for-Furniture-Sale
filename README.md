# Furnituree — E-commerce Web Page for Furniture Sale

Furnituree is a full-stack web application for selling furniture online. The system supports product browsing, product management, cart operations, checkout, order tracking, user management, role-based access control, dashboard statistics, CSV export, chat, and product tagging.

This project was built for the **Web Application Development** course.

## Team Members

| Nguyễn Phước Thịnh| Backend, database, authentication, order workflow, deployment |

| Trần Lê Minh Quân | Frontend, UI/UX, product catalog, admin dashboard |

| Nguyễn Phước Thịnh, Trần Lê Minh Quân  | Testing, documentation |



## Main Features

### User Features

- Register and login
- View product catalog
- Search, filter, sort, and paginate products
- View product details
- Add products to cart
- Update or remove cart items
- Checkout with shipping information and payment method
- View order history
- Cancel eligible orders
- Manage user profile

### Admin / Manager Features

- Manage products
- Manage categories
- Manage users
- Activate/deactivate users
- View all orders
- Update order status
- View dashboard statistics
- Export product data to CSV
- Manage product tags

### Business Logic

- Prevent checkout with an empty cart
- Validate checkout information
- Prevent purchasing more than available stock
- Deduct stock after successful checkout
- Clear cart after checkout
- Calculate subtotal, shipping fee, and total amount
- Create payment record during checkout
- Mark mock card payments as paid
- Restore stock when an eligible order is cancelled
- Prevent status changes for final orders
- Use soft delete / status-based workflows for critical records such as users and orders

## Technology Stack

### Backend

- Java 17+
- Spring Boot
- Spring Security
- JWT Authentication
- Spring Data JPA / Hibernate
- MySQL
- Maven

### Frontend

- HTML
- CSS
- JavaScript
- Chart.js
- WebSocket / SockJS / STOMP for chat feature

### Database

- MySQL
- JPA/Hibernate generated tables
- Many-to-many relationship with `product_tags` junction table

## Project Structure

```text
E-commerce-web-page-for-Furniture-Sale/
├── src/
│   └── main/
│       ├── java/com/furnituree/furnituree/
│       │   ├── Controller/
│       │   ├── config/
│       │   ├── dto/
│       │   ├── exception/
│       │   ├── model/
│       │   ├── repo/
│       │   └── service/
│       └── resources/
│           └── application.properties
├── font-end/
│   ├── htmlpage/
│   ├── jsLogical/
│   └── style/
├── database/
│   ├── schema.sql
│   ├── seed-data.sql
│   └── seed-tags.sql
├── docs/
│   ├── API.md
│   └── DEPLOYMENT.md
├── pom.xml
├── mvnw
├── mvnw.cmd
├── .env.example
└── README.md
```

## Prerequisites

Install the following software before running the project:

- Java 17 or later
- Maven or Maven Wrapper
- MySQL 8.0 or later
- VS Code / IntelliJ IDEA
- MySQL Workbench
- Live Server extension for frontend testing
- Postman for API testing

## Environment Variables

Create a local `.env` file or configure environment variables in your IDE.

Example `.env.example`:

```env
APP_NAME=furnituree
PORT=8080

DB_URL=jdbc:mysql://localhost:3306/furniture_store
DB_USERNAME=db_username
DB_PASSWORD=db_password

JWT_SECRET=your-super-long-secret-key-at-least-32-characters
JWT_EXPIRATION=86400000
```

Important:

- Commit `.env.example`
- Do **not** commit `.env`
- Do **not** commit real database passwords or production secrets

## Database Setup

### Option 1: Let Hibernate create/update tables

Create the database manually:

```sql
CREATE DATABASE IF NOT EXISTS furniture_store;
```

Then start the Spring Boot application. Hibernate will create/update tables based on the JPA entities.

### Option 2: Import database scripts

If database scripts are provided, import them in this order:

```text
1. database/schema.sql
2. database/seed-data.sql
3. database/seed-tags.sql
```

Using MySQL terminal:

```bash
mysql -u root -p furniture_store < database/schema.sql
mysql -u root -p furniture_store < database/seed-data.sql
mysql -u root -p furniture_store < database/seed-tags.sql
```

On Windows PowerShell:

```powershell
cmd /c "mysql -u root -p furniture_store < database\schema.sql"
cmd /c "mysql -u root -p furniture_store < database\seed-data.sql"
cmd /c "mysql -u root -p furniture_store < database\seed-tags.sql"
```

## Run Backend Locally

From the project root:

```powershell
.\mvnw.cmd spring-boot:run
```

Or on macOS/Linux:

```bash
./mvnw spring-boot:run
```

The backend runs at:

```text
http://127.0.0.1:8080
```

## Run Frontend Locally

Open the project in VS Code.

Use Live Server on:

```text
font-end/htmlpage/mainPage.html
```

Example frontend URL:

```text
http://127.0.0.1:5500/font-end/htmlpage/mainPage.html
```

## Test Accounts

| Role | Username | Password |

| Admin | admin | 123456 |
| Manager | manager | 123456 |
| User | user | 123456 |

Additional seeded accounts may include `customer01`, `customer02`, `customer03`, and `staff01`.

## Core Entity Model

The application contains the following main entities:

- User
- Category
- Product
- Cart
- CartItem
- Order
- OrderItem
- Payment
- Tag

Important relationships:

- Category 1 — n Product
- User 1 — n Order
- Order 1 — n OrderItem
- Product 1 — n OrderItem
- Cart 1 — n CartItem
- Product n — n Tag through `product_tags`

## Advanced Search, Filter, Sort, and Pagination

The product catalog supports:

- Keyword search by product name or description
- Filter by category
- Filter by price range
- Filter by stock status
- Sort by ID, name, price, or quantity
- Sort direction: ascending or descending
- Configurable page size
- Pagination with previous/next and page numbers
- Result statistics, such as `Showing 1-10 of 51 products`

## Order and Checkout Workflow

```text
User adds products to cart
→ User opens checkout page
→ User enters shipping information
→ System validates checkout data
→ System creates Order
→ System creates OrderItems from CartItems
→ System calculates subtotal, shipping fee, total amount
→ System creates Payment
→ System deducts product stock
→ System clears cart
→ User is redirected to My Orders
→ Admin/Manager can update order status
```

Order statuses:

```text
PENDING
CONFIRMED
SHIPPING
COMPLETED
CANCELLED
```

Payment methods:

```text
COD
BANK_TRANSFER
MOCK_CARD
```

Payment statuses:

```text
UNPAID
PAID
FAILED
```

## Screenshot 


01-home-product-catalog.png
<img width="946" height="494" alt="image" src="https://github.com/user-attachments/assets/e8f1bd07-c988-463c-b895-a14804b51f77" />

02-search-filter-pagination.png
<img width="758" height="348" alt="image" src="https://github.com/user-attachments/assets/ae43bee0-16fe-4e58-886a-a673dd03dea2" />

03-product-detail.png

04-login-register.png
05-cart-page.png
06-checkout-page.png
07-my-orders.png
08-admin-dashboard.png
09-admin-product-management.png
10-admin-order-management.png
11-realtime-chat-user-admin.png
12-railway-deployment.png

