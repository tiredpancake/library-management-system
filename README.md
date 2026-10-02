# Library Management System

A simple full-stack Library Management System for managing members,
books, loans, returns, renewals, reports, and fines.

The project has a React frontend, Spring Boot backend, and PostgreSQL
database.

---

## Technologies

### Backend

- Java 17
- Spring Boot
- Spring Data JPA
- Maven
- REST API

### Frontend

- React
- JavaScript
- Tailwind CSS
- Axios

### Database

- PostgreSQL 16

### Other

- Docker
- Docker Compose
- Git

---

## Main Features

The system has the following main sections:

- Dashboard
- Books
- Members
- Loans
- Reports
- Fines

The main operations are:

- Add and edit members
- Add and edit books
- Borrow books
- Return books
- Renew books
- View loan history
- Search and filter loan history
- Calculate fines
- Pay fines
- Pay fines partially

---

## Members

The Members page is used to manage library members.

Member information includes:

- Membership Number
- Full Name
- National Code
- Birth Date
- Phone
- Address
- Postal Code
- Membership Type
- Status

The system also keeps a history of changes made to members.

---

## Books

The Books page is used to manage books.

Book information includes:

- Book Code
- ISBN
- Title
- Author
- Category
- Publisher
- Publish Year
- Total Copies
- Available Copies
- Price

When a book is borrowed, the available copies decrease.

When a book is returned, the available copies increase.

---

## Loans

The Loans page is used for:

- Borrow
- Return
- Renew
- Check transaction status
- View transaction history

Each transaction has a tracking code.

The transaction types are:

```text
BORROW
RETURN
RENEW
```

Renewals are saved as new transactions and are connected to the previous
transaction using `parent_transaction_id`.

This allows the system to keep the complete loan history.

---

## Borrowing Rules

Before borrowing a book, the backend checks some conditions:

- The member must exist.
- The member must not be blocked.
- The book must exist.
- The book must have available copies.
- The member must not have too many active loans.
- The member must not have an overdue loan above the allowed limit.
- The member's unpaid fines must not be above the allowed limit.
- The member cannot borrow the same book again while it is still an
  active loan for that member.

If everything is valid, the system creates a new loan transaction and
decreases the available copies.

---

## Returns and Renewals

When a book is returned, the return date is saved and the available copy
count is increased.

If the book is returned late, a fine can be created.

For renewal, a new `RENEW` transaction is created instead of changing
the old transaction.

Example:

```text
BORROW
   |
   └── RENEW
          |
          └── RENEW
```

The previous transactions remain in the history.

---

## Reports

The Reports page is used to search and filter loan history.

The available filters include:

- Membership Number
- Book Code
- Transaction Type
- Status
- State
- From Date
- To Date

The available states are:

```text
RETURNED
NOT_RETURNED
OVERDUE
```

The results are paginated so that all transactions do not need to be
loaded at once.

---

## Fines

The Fines page shows the fines in the system.

The table shows:

- ID
- Membership Number
- Loan ID
- Amount
- Paid
- Remaining
- Status
- Created Date

A fine has an original amount and a paid amount.

---

## Authentication

The application uses Spring Security with HTTP Basic Authentication.

The login endpoint is:

```text
POST /api/auth/login
```

Passwords are stored using BCrypt hashing.

The authentication credentials are configured in the application
configuration and can also be provided through Docker environment
variables.

Passwords and authorization headers are not written to application logs.

---

## Event Logging

The backend uses SLF4J/Logback for application logs.

Important events such as login, borrowing, returning, renewal, fine
payment, and errors are logged.

Unexpected errors are also logged with their stack trace.

---

## Database

The project uses PostgreSQL.

The main tables are:

```text
app_user
member
member_history
book
loan_transaction
fine
```

Main relationships:

```text
Member
   |
   v
Loan Transaction
   |
   v
Fine
```

A member can have many loan transactions.

A book can appear in many loan transactions over time.

A loan transaction can have a fine.

Renewal transactions are connected to their previous transaction.

---

## API

Some of the main APIs are:

### Loans

```text
GET  /api/loans/history
GET  /api/loans/status

POST /api/loans/borrow
POST /api/loans/return
POST /api/loans/renew
```

### Fines

```text
GET /api/fines
GET /api/fines/loan/{loanId}
PUT /api/fines/pay
```

Swagger is available at:

```text
http://localhost:8080/swagger-ui/index.html
```

---

## Project Structure

```text
library-management-system/
│
├── backend/
│   └── library-management/
│       ├── src/
│       ├── pom.xml
│       └── mvnw
│
├── frontend/
│   ├── src/
│   │   ├── api/
│   │   ├── components/
│   │   └── pages/
│   ├── package.json
│   └── Dockerfile
│
├── docker-compose.yml
└── README.md
```

---

## Running the Project

### Backend

Go to the backend folder:

```powershell
cd backend/library-management
```

Build the project:

```powershell
.\mvnw clean package -DskipTests
```

Run the backend:

```powershell
.\mvnw spring-boot:run
```

---

### Frontend

Go to the frontend folder:

```powershell
cd frontend
```

Install dependencies:

```powershell
npm install
```

Run the frontend:

```powershell
npm run dev
```

---

## Running with Docker

From the project root:

```powershell
docker compose up --build
```

The application will be available at:

```text
Frontend: http://localhost:3000
Backend:  http://localhost:8080
Swagger:  http://localhost:8080/swagger-ui/index.html
```

To stop the containers:

```powershell
docker compose down
```

---

## PostgreSQL

The database used by the project is:

```text
library_db
```

To connect to PostgreSQL in Docker:

```powershell
docker exec -it library-postgres psql -U library_admin -d library_db
```

---

## Error Handling

The backend validates requests and returns errors when an operation is
not allowed.

Some examples are:

```text
Member not found
Book not found
Book is not available
Member already has this book
Fine not found
Payment amount exceeds unpaid fine amount
No unpaid fine found for this member
Invalid loan history state
```

The frontend displays these errors to the user.

For operations inside modals, such as borrowing and paying a fine, the
error is shown inside the modal.

---
