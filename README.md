# 📚 Library Management System

Welcome to the Library Management System project! This web API application is built using Java Spring Boot and JPA, designed to streamline library operations such as user management, book catalog management, borrowing and returning books, and book reservations. The backend is powered by a MySQL database, and comprehensive unit tests ensure reliability and robustness. This project also features advanced capabilities such as JWT authentication, caching, and automated status updates.

## 🚀 Features

- **User Management**
  - User registration and login
  - User profiles (members and librarians)
  - Roles and permissions
  
- **Book Catalog Management**
  - CRUD operations for books
  - Categories and genres
  - Author management
  
- **Borrowing and Returning Books**
  - Borrow books
  - Return books
  - Track borrowing history
  
- **Reporting and Analytics**
  - Book borrowing reports
  - User activity reports
  - Inventory reports

## 🌟 Advanced Features

### Authentication and Authorization

**JWT tokens for secure authentication and authorization:**

- Stateless, improving scalability.
- Secure storage reduces session hijacking.
- Fine-grained access control.
- Tokens have expiration for enhanced security.

### Caching

**Caffeine cache provider for efficient caching:**

- High performance with low latency.
- Supports asynchronous operations.
- Efficient memory management with weak/soft references.
- Provides detailed performance metrics.

### Testing

**Unit tests for controllers, services, and repositories using Mockito and JUnit:**

- Isolated testing with mock objects.
- Verifies method behavior and interactions.
- Supports diverse test scenarios.
- Readable and maintainable test structure.

### API Documentation

**Spring OpenAPI (Swagger) for comprehensive API documentation:**

- Interactive and user-friendly interface.
- Auto-generates documentation from code.
- Standardized and consistent format.
- Supports API versioning.
- Generates client SDKs for easier integration.

### Scheduled Tasks

**Automated status updates for overdue borrows:**

- Reduces manual intervention.
- Ensures consistent status updates.
- Scalable for high-traffic applications.

## 🗄️ Database Schema

### User
- **id**: Primary key
- **firstname**: Unique identifier for user
- **lastname**: Unique identifier for user
- **email**: User email address
- **password**: Encrypted user password
- **created_at**: Timestamp of user creation

### Role
- **id**: Primary key
- **name**: Role name (e.g., MEMBER, ADMIN, LIBRARIAN)

### Book
- **id**: Primary key
- **title**: Book title
- **isbn**: Unique identifier for book
- **category_id**: Foreign key to Category
- **publication_year**: Year of publication

### Category
- **id**: Primary key
- **name**: Category name (e.g., Fiction, Non-Fiction)

### Author
- **id**: Primary key
- **name**: Author name

### Borrow
- **id**: Primary key
- **user_id**: Foreign key to User
- **book_id**: Foreign key to Book
- **borrow_date**: Timestamp of borrowing
- **return_date**: Timestamp of returning
- **status**: Borrow status (e.g., Borrowed, Returned, Overdue)

## 📡 API Endpoints

### User Management

| HTTP Method | Endpoint                  | Description                 | Roles                |
|-------------|---------------------------|-----------------------------|----------------------|
| POST        | /api/users/register/admin       | Register a new admin         | Public               |
| POST        | /api/users/register/member       | Register a new member         | Public               |
| POST        | /api/users/register/librarian       | Register a new librarian         | Public               |
| POST        | /api/users/login          | Authenticate a user         | Public               |
| GET         | /api/users/profile        | Get user profile            | MEMBER, ADMIN, LIBRARIAN        |
| PUT         | /api/users/profil        | Update user profile         | MEMBER, ADMIN, LIBRARIAN        |
| Post        | /api/users/profil-image        | Set user profile image         | MEMBER, ADMIN, LIBRARIAN        |

### Book Management

| HTTP Method | Endpoint                  | Description                 | Roles                |
|-------------|---------------------------|-----------------------------|----------------------|
| GET         | /api/books                | Get all books               | MEMBER, ADMIN, LIBRARIAN        |
| GET         | /api/books/{id}           | Get book by ID              | MEMBER, ADMIN, LIBRARIAN        |
| POST        | /api/books                | Create a new book           | ADMIN, LIBRARIAN                |
| PUT         | /api/books/{id}           | Update a book               | ADMIN, LIBRARIAN                |
| DELETE      | /api/books/{id}           | Delete a book               | ADMIN, LIBRARIAN                |
| Post        | /api/books/{id}/images    | Set book images             | ADMIN, LIBRARIAN                |
### Category Management

| HTTP Method | Endpoint                  | Description                 | Roles                |
|-------------|---------------------------|-----------------------------|----------------------|
| GET         | /api/categories           | Get all categories          | MEMBER, ADMIN, LIBRARIAN        |
| GET         | /api/categories/{id}      | Get category by ID          | MEMBER, ADMIN, LIBRARIAN        |
| POST        | /api/categories           | Create a new category       | ADMIN, LIBRARIAN                |
| PUT         | /api/categories/{id}      | Update a category           | ADMIN, LIBRARIAN                |
| DELETE      | /api/categories/{id}      | Delete a category           | ADMIN, LIBRARIAN                |

### Author Management

| HTTP Method | Endpoint                  | Description                 | Roles                |
|-------------|---------------------------|-----------------------------|----------------------|
| GET         | /api/authors              | Get all authors             | MEMBER, ADMIN, LIBRARIAN        |
| GET         | /api/authors/{id}         | Get author by ID            | MEMBER, ADMIN, LIBRARIAN        |
| POST        | /api/authors              | Create a new author         | ADMIN, LIBRARIAN                |
| PUT         | /api/authors/{id}         | Update an author            | ADMIN, LIBRARIAN                |
| DELETE      | /api/authors/{id}         | Delete an author            | ADMIN, LIBRARIAN                |

### Borrow Management

| HTTP Method | Endpoint                  | Description                 | Roles                |
|-------------|---------------------------|-----------------------------|----------------------|
| POST        | /api/borrows              | Borrow a book               | MEMBER               |
| GET         | /api/borrows              | Get all borrows             | ADMIN, LIBRARIAN                |
| GET         | /api/borrows/{id}         | Get borrow by ID            | ADMIN, LIBRARIAN                |
| GET         | /api/borrows/user/{userId}| Get all borrows for a specific user            | ADMIN, LIBRARIAN                |
| Patch       | /api/borrows/{id}         | Update borrow status        | ADMIN, LIBRARIAN                |
| DELETE      | /api/borrows/{id}         | Delete a borrow             | ADMIN, LIBRARIAN                |

## 🔄 Entity Relationships

### User and Role
- **Relationship:** Many-to-One
- **Description:** A user has one role, and a role can be assigned to multiple users.

### Book and Author
- **Relationship:** Many-to-Many
- **Description:** A book can have multiple authors, and an author can write multiple books.

### Book and Category
- **Relationship:** Many-to-One
- **Description:** A book belongs to a single category, but a category can have multiple books.

### User and Borrow
- **Relationship:** One-to-Many
- **Description:** A user can borrow multiple books, but a borrow record belongs to a single user.

### Book and Borrow
- **Relationship:** Many-to-One
- **Description:** A borrow record refers to a single book, but a book can have multiple borrow records.

## 🛠️ Getting Started

### Prerequisites

- Java 11 or later
- Maven
- MySQL Server

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/Sameeh-hussein/LMS.git
   cd LMS
   ```

2. **Install dependencies**
   ```bash
   mvn clean install
   ```

### Database Configuration

1. **Create a MySQL database**
   ```sql
   CREATE DATABASE lmsdb;
   ```

2. **Update `application.properties`**
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/lmsdb
   spring.datasource.username=your-username
   spring.datasource.password=your-password
   spring.jpa.hibernate.ddl-auto=update
   ```

### Running the Application

1. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

2. **Access the API**
   - The API will be available at `http://localhost:8080/swagger-ui/index.html`.

### Running Tests

1. **Run unit tests**
   ```bash
   mvn test
   ```
   
## 📚 Resources

Here are some resources that I found particularly useful:

- [Guide to Spring Boot](https://www.youtube.com/watch?v=Nv2DERaMx-4&t=20333s&pp=ygUhdGhlIHVsdGltYXRlIGd1aWRlIHRvIHNwcmluZyBib290)
- [Spring Security 6 - JWT Authentication and Authorisation](https://youtu.be/KxqlJblhzfI?si=poESThtLtJpD0aEz)
- [API Testing in Spring Boot](https://medium.com/@mbanaee61/api-testing-in-spring-boot-2a6d69e5c3ce)
- [Spring Boot Custom Validation](https://medium.com/@bereketberhe27/spring-boot-custom-validation-7af89a64f805)
- [Spring Boot Cache](https://medium.com/@geonikpal/spring-boot-cache-18ab22a09f42)
- [Documenting a Spring REST API Using OpenAPI 3.0](https://www.baeldung.com/spring-rest-openapi-documentation)
