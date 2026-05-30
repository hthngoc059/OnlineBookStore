# Online BookStore - Online Book Management System

## I. INTRODUCTION
Online BookStore is an e-commerce web application that allows users to purchase books online, featuring book browsing, searching, filtering by genre, shopping cart, checkout, book reviews, and an admin management panel.

## II. TARGET USERS
- Guest: Who visit the website without logging in or creating an account can browse and search books
- Users: Customers who buy books, review books, collect books, and receive notifications
- Administrators: Manage books, orders, users, and view statistical reports

## III. KEY FEATURES 
### 1. User Features
- Register / Login
- Browse & search books
- View book details with star ratings
- Add to cart / wishlist
- Checkout (COD / Banking)
- Order history & cancel orders
- Write reviews 
- Manage addresses (add, edit, delete, set default)
- Change password & update profile
- Apply discount codes
- Receive notifications

### 2. Admin Features
- Dashboard with statistics
- Book management (add, edit, delete, update)
- Order management (confirm, update status)
- User management (view, delete)
- Discount management (CRUD, toggle)
- Send notifications
- Revenue reports (line chart, doughnut chart)

## IV. TECHNOLOGY STACK
- **Backend:** Java 17, Spring Boot 3.3.0, Spring Security, Hibernate/JPA
- **Frontend:** JSP, JSTL, HTML5/CSS3, JavaScript, Chart.js
- **Database:** MySQL 8.0
- **Server:** Apache Tomcat 10.1.52
- **Tools:** Maven, Git, NetBeans, Visual Studio Code

## V. PROJECT STRUCTURE
```
OnlineBookStore/
├── src/
│   ├── main/
│   │   ├── java/com/student/onlinebookstore/
│   │   │   ├── config/          # Spring & Security configuration
│   │   │   ├── controller/      # Servlets & Spring Controllers
│   │   │   ├── dao/             # Data Access Objects
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── exception/       # Custom exceptions
│   │   │   ├── model/           # JPA Entity models
│   │   │   ├── service/         # Business logic layer
│   │   │   └── util/            # Utility classes
│   │   ├── resources/
│   │   │   └── application.properties  # Application configuration
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   └── views/       # JSP files
│   │       │       ├── admin/   # Admin pages
│   │       │       ├── about.jsp, contact.jsp
│   │       │       ├── address.jsp, address-management.jsp
│   │       │       ├── book-detail.jsp, books.jsp
│   │       │       ├── cart.jsp
│   │       │       ├── checkout.jsp
│   │       │       ├── home.jsp
│   │       │       ├── notification.jsp
│   │       │       ├── order-detail.jsp, order-history.jsp
│   │       │       ├── profile.jsp
│   │       │       ├── search.jsp
│   │       │       └── wishlist.jsp
│   │       ├── css/             # Stylesheets
│   │       │   └── admin.css
│   │       │   ├── style.css
│   │       ├── fonts/           # Arimo fonts
│   │       ├── images/          # Logo
│   │       └── js/              # JavaScript files
│   └── test/                    # Unit tests
├── pom.xml                      # Maven dependencies
├── bookstore_management.sql     # SQL script
├── bookstore_management_sample.sql     # Sample data script
└── README.md                    # Project documentation
```

## VI. DATABASE SCHEMA
Main tables: `users`, `books`, `genres`, `book_genre`, `addresses`, `carts`, `cart_items`, `orders`, `order_items`, `payments`, `reviews`, `wishlists`, `discounts`, `notifications`

Tables use `ON DELETE CASCADE` for data integrity.

## VII. INSTALLATION & SETUP
### 1. System Requirements
- JDK 17 or higher
- Apache Tomcat 10.1.x
- MySQL 8.0
- Maven 3.9+

### 2. Setup Steps
#### **2.1 Clone the repository**
```
git clone https://github.com/hthngoc059/OnlineBookStore.git
cd OnlineBookStore
```

#### **2.2 Create database**
```
CREATE DATABASE bookstore_management;
USE bookstore_management;
-- Run the table creation scripts from the project structure
```

#### **2.3 Configure database connection**
Edit `src/main/resources/application.properties`:
```
spring.datasource.url=jdbc:mysql://localhost:3306/bookstore_management?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password
```

#### **2.4 Build the project**
```
mvn clean package
```

#### **2.5 Deploy to Tomcat**
Copy `target/OnlineBookStore-1.0-SNAPSHOT.war` to Tomcat's `webapps` directory

#### **2.6 Run the application**
Access: `http://localhost:8080/OnlineBookStore/`

## VIII. KNOWN ISSUES
- Admin Panel:
    - Search by type and dropdown cannot work simultaneously
    - Cannot send notifications to individual users when order status is updated by admin
- Customer Panel: 
    - Review system does not verify whether the user has actually purchased the book before allowing them to write a review
    - Banking payment method is only a simulation (not integrated with real payment gateway)


## IX. CONTACT
- GitHub: [hthngoc059/OnlineBookStore](https://github.com/hthngoc059/OnlineBookStore)
- Authors:
    - Nguyễn Huỳnh Anh Thư 
    - Hứa Thụy Hồng Ngọc 

## X. LISENCE
This project is developed for educational and research purposes only.

---
_Last Updated: May 2026_

