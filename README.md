# Library Management System

A full-stack Library Management System developed for managing the main operations of a library, including members, books, borrowing, returning, renewing, transaction history, reports, and fines.

The project was developed as a practical full-stack application with a React frontend, Spring Boot backend, and PostgreSQL database.

The main idea was to keep the system simple enough to understand and maintain while still covering the main rules that exist in a real library management workflow.

---

## Table of Contents

- [Overview](#overview)
- [Main Features](#main-features)
- [Technologies](#technologies)
- [Authentication and Security](#authentication-and-security)
- [Event Logging](#event-logging)
- [System Architecture](#system-architecture)
- [Project Structure](#project-structure)
- [Backend Structure](#backend-structure)
- [Frontend Structure](#frontend-structure)
- [Database Design](#database-design)
- [Database Tables](#database-tables)
- [Relationships](#relationships)
- [Members](#members)
- [Member History](#member-history)
- [Books](#books)
- [Loans](#loans)
- [Borrowing](#borrowing)
- [Returning](#returning)
- [Renewals](#renewals)
- [Current Loans](#current-loans)
- [Loan History](#loan-history)
- [Reports](#reports)
- [Fines](#fines)
- [Fine Calculation](#fine-calculation)
- [Partial Fine Payment](#partial-fine-payment)
- [Fine Payment Validation](#fine-payment-validation)
- [Validation and Error Handling](#validation-and-error-handling)
- [Business Rules](#business-rules)
- [REST API](#rest-api)
- [Pagination](#pagination)
- [Dynamic Filtering](#dynamic-filtering)
- [Frontend UI](#frontend-ui)
- [Running the Backend](#running-the-backend)
- [Running the Frontend](#running-the-frontend)
- [Running with Docker](#running-with-docker)
- [PostgreSQL](#postgresql)
- [Development Workflow](#development-workflow)
- [Example Workflows](#example-workflows)
- [Loan Transaction Flow](#loan-transaction-flow)
- [Fine Flow](#fine-flow)
- [Current Status](#current-status)
- [Future Improvements](#future-improvements)
- [Author](#author)

---

# Overview

The Library Management System is a web-based application for managing books, members, loans, renewals, returns, and fines.

The system has two main parts:

```text
Frontend
React + JavaScript + Tailwind CSS
        |
        | REST API
        v
Backend
Spring Boot + Java
        |
        | JPA
        v
Database
PostgreSQL
```

The frontend is responsible for the user interface and sending requests to the backend.

The backend contains the main business logic and communicates with PostgreSQL through Spring Data JPA.

The database stores members, books, loan transactions, member history, users, and fines.

---

# Main Features

The main sections of the application are:

- Dashboard
- Books
- Members
- Loans
- Reports
- Fines

The main operations supported by the system are:

- Add and manage members
- Add and manage books
- Borrow books
- Return books
- Renew loans
- Track loan transactions
- View loan history
- Filter loan history
- Check loan status
- Calculate overdue fines
- Record fine payments
- Support partial fine payments
- Track paid and remaining fine amounts
- Prevent payments above the unpaid fine amount
- Keep member change history
- Maintain parent-child relationships between loan and renewal transactions

---

# Technologies

## Backend

- Java 17
- Spring Boot
- Spring Data JPA
- Spring Web
- Maven
- REST API
- Lombok

## Frontend

- React
- JavaScript
- Tailwind CSS
- Axios
- Lucide React

## Database

- PostgreSQL 16

## Development Environment

- Windows
- Docker
- Docker Desktop
- Git
- GitHub
- Visual Studio Code / IntelliJ IDEA

---

# Authentication and Security

All application APIs require HTTP Basic Authentication, except the login endpoint and the public Swagger/OpenAPI documentation endpoints.

The application uses Spring Security with BCrypt password hashing. The initial application user is configured through:

```text
security.basic.username
security.basic.password
```

The same values can be supplied as environment variables in Docker Compose. The password is never written to application logs.

The frontend stores the Base64-encoded Basic Authentication credential in `sessionStorage` for the current browser session and sends it with authenticated API requests. Basic Authentication should only be used over HTTPS outside a local development environment.

---

# Event Logging

Important application operations are recorded through SLF4J/Logback application logs. Events include authentication, member and book operations, borrow, return, renewal, fine creation, fine payment, loan-history searches, and dashboard access.

Example event format:

```text
EVENT=LOAN_BORROW_SUCCESS actor=admin loanId=15 memberId=3 bookId=7 trackingCode=...
```

Credentials, passwords, and Authorization headers are not logged. Expected business errors are logged with their operation and request context. Unexpected exceptions are logged with the full stack trace while the client receives only a generic internal-server-error message.

---

# System Architecture

The application follows a simple layered architecture.

```text
                         USER
                           |
                           v
                    React Frontend
                           |
                           | HTTP / REST
                           v
                    Spring Boot API
                           |
              +------------+------------+
              |                         |
              v                         v
          Controllers                Services
                                        |
                                        v
                                  Repositories
                                        |
                                        v
                                   PostgreSQL
```

The frontend does not directly access the database.

All database operations are handled by the backend.

---

# Project Structure

The project is divided into frontend and backend directories.

```text
library-management-system/
│
├── backend/
│   └── library-management/
│       │
│       ├── src/
│       │   ├── main/
│       │   │   ├── java/
│       │   │   │   └── com/
│       │   │   │       └── library/
│       │   │   │           └── library_management/
│       │   │
│       │   │   └── resources/
│       │   │
│       │   └── test/
│       │
│       ├── pom.xml
│       ├── mvnw
│       └── mvnw.cmd
│
├── frontend/
│   │
│   ├── src/
│   │   ├── api/
│   │   ├── components/
│   │   ├── pages/
│   │   └── ...
│   │
│   ├── package.json
│   ├── Dockerfile
│   └── ...
│
└── README.md
```

---

# Backend Structure

The backend is organized into several layers.

```text
controller/
service/
service/impl/
repository/
repository/specification/
entity/
dto/
config/
exception/
```

## Controller

Controllers expose REST endpoints to the frontend.

Examples:

```text
LoanController
LoanHistoryController
FineController
```

## Service

Service interfaces define the main application operations.

Examples:

```text
LoanService
LoanHistoryService
FineService
```

## Service Implementation

The actual business logic is implemented in classes such as:

```text
LoanServiceImpl
LoanHistoryServiceImpl
FineServiceImpl
```

## Repository

Repositories are responsible for database access through Spring Data JPA.

Examples include:

```text
LoanTransactionRepository
FineRepository
MemberRepository
BookRepository
```

## Specification

Dynamic filtering for loan history and reports is implemented using Spring Data JPA Specifications.

The main specification class is:

```text
LoanSpecification
```

---

# Frontend Structure

The frontend is implemented using React.

The main directories are:

```text
src/
├── api/
├── components/
├── pages/
└── ...
```

## API

API files contain functions used to communicate with the backend.

For example:

```text
loanApi.js
fineApi.js
```

The loan API contains functions for:

```text
getLoanHistory()
borrowBook()
returnBook()
renewLoan()
getLoanStatus()
```

The fine API provides functions for retrieving fines and submitting fine payments.

## Pages

The main pages include:

```text
Dashboard
Books
Members
Loans
Fines
Reports
```

## Components

Reusable components are used for forms and modal windows.

Examples include modal forms for:

- Borrowing a book
- Paying a fine
- Viewing transaction details

---

# Database Design

The application uses PostgreSQL.

The main database tables are:

```text
app_user
member
member_history
book
loan_transaction
fine
```

The database is designed around the main entities of the library system.

---

# Database Tables

## APP_USER

Stores application users.

Main fields:

```text
id
username
password_hash
created_at
updated_at
```

The username is unique.

Users can also be referenced by records that need information about who created or changed them.

---

## MEMBER

Stores library member information.

Main fields:

```text
id
membership_number
full_name
national_code
birth_date
phone
address
postal_code
membership_type
status
created_by
created_at
updated_at
```

Important unique fields include:

```text
membership_number
national_code
```

---

## MEMBER_HISTORY

Stores changes made to member information.

Main fields:

```text
id
member_id
changed_by
field_name
old_value
new_value
changed_at
```

For example, if a member's phone number changes:

```text
field_name = phone
old_value  = old phone number
new_value  = new phone number
```

This allows changes to member information to be tracked.

---

## BOOK

Stores book information.

Main fields include:

```text
id
book_code
isbn
title
author
category
publisher
publish_year
total_copies
available_copies
price
created_by
created_at
updated_at
```

Important unique fields include:

```text
book_code
isbn
```

The system keeps both:

```text
total_copies
available_copies
```

so it can determine whether a book can currently be borrowed.

---

## LOAN_TRANSACTION

This is the main table for borrowing-related operations.

Main fields include:

```text
id
tracking_code
member_id
book_id
request_date
due_date
return_date
status
type
parent_transaction_id
created_by
created_at
updated_at
renew_count
```

The transaction type can be:

```text
BORROW
RETURN
RENEW
```

The transaction status is used to describe the result/state of the transaction.

The `parent_transaction_id` is used to connect a renewal transaction to the previous loan transaction.

---

## FINE

Stores fines associated with loan transactions.

Main fields include:

```text
id
loan_transaction_id
amount
paid_amount
status
created_at
paid_at
```

The important difference between `amount` and `paid_amount` is:

```text
amount
    =
total fine

paid_amount
    =
amount already paid
```

The remaining amount is:

```text
remaining_amount =
amount - paid_amount
```

---

# Relationships

The main database relationships are:

```text
APP_USER
   |
   +-------------------+
   |                   |
   v                   v
MEMBER                BOOK
   |                   |
   |                   |
   +--------+----------+
            |
            v
     LOAN_TRANSACTION
            |
            +----------------+
            |                |
            v                v
          FINE       parent_transaction
```

A member can have multiple loan transactions.

A book can appear in multiple loan transactions over time.

A loan transaction can have an associated fine.

A loan transaction can also have a parent transaction when it is a renewal.

---

# Members

The Members page is used to manage library members.

A member can be created with information such as:

```text
Membership Number
Full Name
National Code
Birth Date
Phone
Address
Postal Code
Membership Type
Status
```

The system validates required information and unique values.

Member information can also be edited.

Changes to member information can be stored in `MEMBER_HISTORY`.

---

# Member History

Member history is used to keep track of changes made to member records.

The history records:

```text
Member
Changed Field
Old Value
New Value
Changed By
Changed At
```

For example:

```text
Member: 3735632761

Field:
phone

Old:
0912xxxxxxx

New:
0935xxxxxxx
```

---

# Books

The Books page is used to manage books.

A book contains:

```text
Book Code
ISBN
Title
Author
Category
Publisher
Publish Year
Total Copies
Available Copies
Price
```

When a book is borrowed, the number of available copies is updated.

When a book is returned, the available copy count is increased again.

The system therefore separates:

```text
Total Copies
```

from:

```text
Available Copies
```

---

# Loans

The Loans page is used for the main loan operations.

The main transaction types are:

```text
BORROW
RETURN
RENEW
```

The page also allows the user to:

- Check transaction status
- Borrow a book
- Return a book
- Renew a loan
- View transaction details
- Review transaction history

---

# Borrowing

The borrowing workflow starts with:

```text
Membership Number
Book Code
```

The backend then checks the required conditions before creating the transaction.

The general flow is:

```text
User enters member number
          |
          v
User enters book code
          |
          v
Backend finds member
          |
          v
Backend finds book
          |
          v
Check book availability
          |
          v
Create BORROW transaction
          |
          v
Set request date
          |
          v
Set due date
          |
          v
Update available copies
          |
          v
Return tracking code
```

A tracking code is associated with each transaction.

---

# Returning

Returning a book creates a return transaction.

The return operation includes:

```text
Find current loan
        |
        v
Check transaction
        |
        v
Record return date
        |
        v
Update book availability
        |
        v
Create RETURN transaction
        |
        v
Check overdue condition
        |
        v
Calculate fine if required
```

The previous borrowing transaction remains in the transaction history.

---

# Renewals

A renewal does not simply overwrite the original loan.

Instead, a new transaction is created.

For example:

```text
BORROW
   |
   └── RENEW
```

If it is renewed again:

```text
BORROW
   |
   └── RENEW
          |
          └── RENEW
```

The relationship is maintained using:

```text
parent_transaction_id
```

This makes it possible to keep the history of the complete loan process.

---

# Current Loans

A transaction is considered current when it represents the active loan and has not already been returned or followed by another transaction.

The system uses this information to determine whether actions such as:

```text
Return
Renew
```

should still be available.

For example, after a renewal:

```text
BORROW
```

is no longer the current transaction.

The new:

```text
RENEW
```

transaction becomes the current transaction.

Similarly, after a return, the active loan is no longer available for another return or renewal.

---

# Loan History

The application keeps the complete history of loan transactions.

The history includes information such as:

```text
Tracking Code
Member
Book
Type
Status
Request Date
Due Date
Return Date
Renew Count
```

The system does not delete the previous transaction when a return or renewal occurs.

This is important because the system needs to preserve the history of what happened.

---

# Loan History Filtering

Loan history can be filtered dynamically.

Available filters include:

```text
Membership Number
Book Code
Transaction Type
Transaction Status
State
From Date
To Date
```

The transaction type can be:

```text
BORROW
RETURN
RENEW
```

The state filter supports:

```text
RETURNED
NOT_RETURNED
OVERDUE
```

---

# Returned State

A transaction can be considered returned when a return date exists.

Conceptually:

```text
returnDate != null
```

This state is used when filtering transaction history.

---

# Not Returned State

A transaction can be considered not returned when:

```text
status = SUCCESS
```

and the transaction represents an active borrowing or renewal, with:

```text
returnDate = null
```

and there is no later child transaction associated with it.

This prevents old transactions from being shown as current after a renewal.

---

# Overdue State

An active transaction can be considered overdue when its due date has passed.

The basic condition is:

```text
dueDate < current date/time
```

while the transaction has not been returned and does not have a later child transaction.

---

# Reports

The Reports page is separate from the main Loans page.

The Loans page is mainly for current loan operations.

The Reports page is intended for reviewing historical transactions and filtering them.

The report page includes filters for:

```text
Membership Number
Book Code
Type
Status
From
To
```

The report table displays information such as:

```text
Membership
Book Code
Type
Request Date
Status
```

The report uses pagination.

---

# Pagination

Loan history and reports use pagination.

The frontend sends:

```text
page
size
```

to the backend.

For example:

```text
page = 0
size = 10
```

The backend returns a Spring Data `Page` containing:

```text
content
totalPages
totalElements
current page information
```

This prevents the application from loading all historical transactions at once.

---

# Dynamic Filtering

Dynamic filtering is implemented using Spring Data JPA Specifications.

The main specification class is:

```text
LoanSpecification
```

The specifications include:

```text
hasMember()
hasBook()
hasType()
hasStatus()
hasState()
dateFrom()
dateTo()
```

The filters are combined dynamically.

For example:

```text
Member
   AND
Book
   AND
Type
   AND
Status
   AND
Date Range
```

Filters that are not provided are ignored.

This allows the same endpoint to support different combinations of search criteria.

---

# Fines

The Fines page is used to display and manage fines.

The table contains information such as:

```text
ID
Membership Number
Loan ID
Amount
Paid
Remaining
Status
Created
```

For example:

```text
ID                  1
Membership Number   3735632761
Loan ID             17
Amount              120000
Paid                25000
Remaining           95000
Status              PARTIALLY_PAID
Created             2026-10-01
```

This makes it possible to identify which member the fine belongs to without having to manually look up the loan transaction first.

---

# Fine Calculation

A fine is associated with a loan transaction.

The fine is calculated when an overdue return is processed according to the fine rules configured in the application.

The general concept is:

```text
Late Days
    |
    v
Fine Calculation
    |
    v
Fine Record
```

The resulting fine is stored in the `FINE` table.

---

# Fine Amount and Remaining Amount

The system stores the original fine amount separately from the amount already paid.

For example:

```text
Total Fine:
120000

Paid:
25000
```

The remaining amount is:

```text
120000 - 25000 = 95000
```

Therefore:

```text
Amount      = 120000
Paid        = 25000
Remaining   = 95000
Status      = PARTIALLY_PAID
```

---

# Partial Fine Payment

The system supports partial payments.

A member does not have to pay the entire fine in one payment.

For example:

```text
Initial Fine
120000
```

First payment:

```text
25000
```

Result:

```text
Amount       120000
Paid          25000
Remaining     95000
Status        PARTIALLY_PAID
```

A later payment can be made for the remaining amount.

For example:

```text
Second Payment
95000
```

Final result:

```text
Amount       120000
Paid         120000
Remaining         0
Status           PAID
```

---

# Fine Payment Validation

The backend checks the total unpaid fine amount before accepting a payment.

The important rule is:

```text
Payment Amount <= Total Unpaid Fine Amount
```

For example, if the member has:

```text
Unpaid Fine = 95000
```

then:

```text
Payment = 50000
```

is valid.

The remaining amount becomes:

```text
45000
```

But:

```text
Payment = 100000
```

is not allowed because:

```text
100000 > 95000
```

The backend returns an error instead of allowing the payment.

The error message used by the backend is:

```text
Payment amount exceeds unpaid fine amount
```

---

# Fine Payment Across Multiple Fines

The payment logic is based on the member's total unpaid fine amount.

If a member has multiple unpaid fines, the payment can be applied to unpaid fines until the requested payment amount is consumed.

The system updates:

```text
paid_amount
status
paid_at
```

for the affected fine records.

A fine is:

```text
PARTIALLY_PAID
```

when only part of its amount has been paid.

A fine becomes:

```text
PAID
```

when its full amount has been paid.

---

# Fine Status

The fine status represents the payment state.

The main states used by the application include:

```text
UNPAID
PARTIALLY_PAID
PAID
```

Conceptually:

```text
UNPAID
   |
   | partial payment
   v
PARTIALLY_PAID
   |
   | remaining amount paid
   v
PAID
```

---

# Validation and Error Handling

The application performs validation on both the frontend and backend.

Examples of possible errors include:

```text
Member not found
Book not found
Book is not available
Fine not found
Payment amount exceeds unpaid fine amount
No unpaid fine found for this member
Invalid loan history state
```

The frontend displays API errors to the user instead of silently ignoring them.

For modal operations such as borrowing and fine payment, validation errors are displayed inside the modal so that the user can see the reason for the failure.

---

# Business Rules

The main business rules implemented in the system are:

1. Membership numbers must be unique.
2. National codes must be unique.
3. Book codes must be unique.
4. ISBN values must be unique.
5. A book cannot be borrowed when there are no available copies.
6. Borrowing a book decreases the available copy count.
7. Returning a book increases the available copy count.
8. Borrowing creates a loan transaction.
9. Returning creates a return transaction.
10. Renewing creates a new renewal transaction.
11. Renewal transactions are linked to their previous transaction.
12. Previous transactions remain in the history.
13. A transaction that has already been followed by another transaction should not remain the current transaction.
14. A returned transaction should not remain available for another return operation.
15. A returned transaction should not remain available for renewal.
16. Overdue transactions can generate fines.
17. A fine can be paid partially.
18. The system stores the amount already paid separately from the original fine amount.
19. Remaining fine amount is calculated from the original amount and the paid amount.
20. A fully paid fine is marked as `PAID`.
21. A partially paid fine is marked as `PARTIALLY_PAID`.
22. A payment cannot exceed the member's total unpaid fine amount.
23. Loan history can be filtered by member.
24. Loan history can be filtered by book.
25. Loan history can be filtered by transaction type.
26. Loan history can be filtered by status.
27. Loan history can be filtered by state.
28. Loan history can be filtered by date range.
29. Loan history is paginated.
30. Member changes can be recorded in member history.

---

# REST API

## Authentication

The login endpoint validates the configured application credentials:

```text
POST /api/auth/login
```

Request body:

```json
{
  "username": "admin",
  "password": "test123"
}
```

After successful login, the frontend uses HTTP Basic Authentication for subsequent API requests. In Swagger UI, use the `Authorize` button and enter the same username and password.

---

The backend exposes REST APIs for communication with the React frontend.

## Loan APIs

```text
GET  /api/loans/history
GET  /api/loans/status

POST /api/loans/borrow
POST /api/loans/return
POST /api/loans/renew
```

---

## Loan History API

The loan history endpoint is:

```text
GET /api/loans/history
```

Optional query parameters include:

```text
membershipNumber
bookCode
type
status
state
from
to
page
size
```

Example:

```text
GET /api/loans/history?membershipNumber=3735632761&page=0&size=10
```

Another example:

```text
GET /api/loans/history?type=RENEW&status=SUCCESS&page=0&size=10
```

---

## Loan Status API

The status endpoint is:

```text
GET /api/loans/status
```

It uses the transaction tracking code.

Example:

```text
GET /api/loans/status?trackingCode=<tracking-code>
```

---

## Borrow API

The borrowing endpoint is:

```text
POST /api/loans/borrow
```

The request contains the information required to identify the member and book.

The backend checks the required conditions and creates the transaction.

---

## Return API

The return endpoint is:

```text
POST /api/loans/return
```

The request identifies the loan that should be returned.

The backend records the return and updates the related book availability.

If the loan is overdue, the fine logic is also applied.

---

## Renew API

The renewal endpoint is:

```text
POST /api/loans/renew
```

The backend creates a new renewal transaction and links it to the previous transaction.

---

# Fine APIs

The main fine endpoints include:

```text
GET /api/fines
GET /api/fines/loan/{loanId}
PUT /api/fines/pay
```

---

## Get All Fines

```text
GET /api/fines
```

Returns the fines available in the system.

---

## Get Fine by Loan

```text
GET /api/fines/loan/{loanId}
```

Returns the fine associated with a specific loan transaction.

---

## Pay Fine

```text
PUT /api/fines/pay
```

The payment request contains:

```text
membershipNumber
amount
```

The backend checks the member and the unpaid fine amount before applying the payment.

---

# Frontend API Layer

The frontend separates API requests from page components.

For example, the loan API contains functions similar to:

```javascript
getLoanHistory()
borrowBook()
returnBook()
renewLoan()
getLoanStatus()
```

The fine API provides functions for:

```javascript
getFines()
payFine()
```

Axios is used as the HTTP client.

---

# Frontend UI

The frontend provides a web interface for the main operations.

The UI contains:

```text
Sidebar Navigation
Dashboard
Books
Members
Loans
Fines
Reports
```

---

# Loans UI

The Loans page contains:

```text
Loans
Manage borrowing, returns, renewals, and transaction status.
```

Main actions include:

```text
Check Status
Borrow Book
```

The transaction table contains:

```text
Tracking Code
Member
Book
Type
Status
Request Date
Actions
```

Depending on the transaction state, actions such as:

```text
View
Return
Renew
```

are enabled or disabled.

---

# Transaction Table

The transaction history table can contain many columns.

To prevent the table from expanding outside the page, the table area uses horizontal scrolling.

The intended behavior is:

```text
+---------------------------------------------+
| Transaction History                          |
|                                             |
| +-----------------------------------------+ |
| | Tracking | Member | Book | ... | Action | |
| |-----------------------------------------| |
| | ...                                      | |
| +-----------------------------------------+ |
|        <---- horizontal scroll ---->        |
+---------------------------------------------+
```

The horizontal scrollbar belongs to the table container rather than the entire browser page.

---

# Reports UI

The Reports page contains:

```text
Reports
Analyze library activity
```

The filters include:

```text
Membership Number
Book Code
Type
From
To
```

The report table contains:

```text
Membership
Book Code
Type
Request Date
Status
```

Pagination controls are available for navigating through the history.

---

# Fines UI

The Fines page displays fine records.

The main table contains:

```text
ID
Membership Number
Loan ID
Amount
Paid
Remaining
Status
Created
```

The page also contains:

```text
Pay Fine
```

which opens the fine payment form.

The payment form accepts:

```text
Membership Number
Payment Amount
```

Validation errors are shown in the payment modal.

---

# Error Display in Modals

For operations such as borrowing and paying fines, the UI should show errors close to the form that caused the error.

For example, when borrowing fails:

```text
Borrow Book

Borrow failed
Member not found

Membership Number
[...................]

Book Code
[...................]

Cancel        Borrow
```

Similarly, if a fine payment exceeds the unpaid amount:

```text
Pay Fine

Payment amount exceeds unpaid fine amount
```

---

# Running the Backend

Go to the backend directory:

```powershell
cd backend/library-management
```

Build the project:

```powershell
.\mvnw clean package
```

To build without running tests:

```powershell
.\mvnw clean package -DskipTests
```

If the build is successful, Maven creates the compiled application under:

```text
target/
```

---

# Running the Frontend

Go to the frontend directory:

```powershell
cd frontend
```

Install the dependencies:

```powershell
npm install
```

Start the development server:

```powershell
npm run dev
```

The frontend will then be available through the development server URL shown by Vite.

---

# Running with Docker

The complete application can be built and started from the project root with one command:

```powershell
docker compose up --build
```

This starts:

```text
PostgreSQL
Spring Boot Backend
React + Nginx Frontend
```

PostgreSQL has a health check, so the backend waits for the database to become ready before starting.

The application is then available at:

```text
Frontend: http://localhost:3000
Backend:  http://localhost:8080
Swagger:  http://localhost:8080/swagger-ui/index.html
```

To stop the application:

```powershell
docker compose down
```

To remove the database volume as well during a clean local reset:

```powershell
docker compose down -v
```

---

# PostgreSQL

The application uses PostgreSQL as the database.

The database used by the project is:

```text
library_db
```

The main database user used in the Docker environment is configured separately from the application configuration.

The database contains:

```text
app_user
book
fine
loan_transaction
member
member_history
```

To inspect the database from the PostgreSQL container, use the PostgreSQL user configured for the container.

Example:

```powershell
docker exec -it library-postgres psql -U library_admin -d library_db
```

Inside `psql`, the following command can be used to list the tables:

```sql
\dt
```

---

# Database Inspection

Useful PostgreSQL commands during development include:

```sql
\dt
```

to list tables.

To inspect the structure of a table:

```sql
\d table_name
```

For example:

```sql
\d loan_transaction
```

To view loan transactions:

```sql
SELECT *
FROM loan_transaction;
```

To view fines:

```sql
SELECT *
FROM fine;
```

---

# Development Workflow

The general development workflow is:

```text
1. Start PostgreSQL
        |
        v
2. Start Spring Boot backend
        |
        v
3. Start React frontend
        |
        v
4. Open the web application
        |
        v
5. Perform library operations
        |
        v
6. Backend validates the operation
        |
        v
7. PostgreSQL stores the result
        |
        v
8. Frontend updates the UI
```

---

# Example Workflows

## Add a Member

```text
Open Members
      |
      v
Add Member
      |
      v
Enter member information
      |
      v
Validate fields
      |
      v
Send request to backend
      |
      v
Store member in PostgreSQL
      |
      v
Show member in Members page
```

---

# Add a Book

```text
Open Books
      |
      v
Add Book
      |
      v
Enter book information
      |
      v
Validate book data
      |
      v
Store book
      |
      v
Book becomes available for borrowing
```

---

# Borrow a Book

```text
Member Number
      |
      v
Book Code
      |
      v
Check Member
      |
      v
Check Book
      |
      v
Check Available Copies
      |
      v
Create BORROW Transaction
      |
      v
Decrease Available Copies
      |
      v
Set Due Date
      |
      v
Return Tracking Code
```

---

# Renew a Book

```text
Current Loan
      |
      v
Check Renewal Conditions
      |
      v
Create RENEW Transaction
      |
      v
Link to Parent Transaction
      |
      v
Update Current Transaction
      |
      v
New Due Date
```

The original transaction is kept in the history.

---

# Return a Book

```text
Current Loan
      |
      v
Return Book
      |
      v
Set Return Date
      |
      v
Increase Available Copies
      |
      v
Create RETURN Transaction
      |
      v
Check Overdue
      |
      +---- No ----> Complete
      |
      +---- Yes ---> Calculate Fine
                         |
                         v
                    Store Fine
```

---

# Loan Transaction Flow

A complete loan lifecycle can look like:

```text
BORROW
   |
   | renewal
   v
RENEW
   |
   | renewal
   v
RENEW
   |
   | return
   v
RETURN
```

The system stores these as separate transaction records.

This means the database can preserve the complete sequence of operations.

---

# Current Transaction Logic

The application checks whether a transaction is still current.

A transaction is not considered current if:

- It has already been returned.
- It is a `RETURN` transaction.
- It has a child transaction caused by a renewal or another follow-up operation.

This is important because an old BORROW record should not continue to display active actions after a renewal.

For example:

```text
BORROW  -> current
```

After renewal:

```text
BORROW  -> not current
RENEW   -> current
```

After return:

```text
BORROW  -> not current
RENEW   -> not current
RETURN  -> completed
```

---

# Fine Flow

The general fine flow is:

```text
Loan
 |
 v
Due Date
 |
 v
Book Returned
 |
 +------------------+
 |                  |
 v                  v
On Time           Late
 |                  |
 v                  v
No Fine          Calculate Fine
                    |
                    v
                Create Fine
                    |
                    v
               Unpaid Fine
                    |
                    v
             Partial Payment
                    |
                    v
             Update Paid Amount
                    |
             +------+------+
             |             |
             v             v
      Remaining > 0    Remaining = 0
             |             |
             v             v
     PARTIALLY_PAID       PAID
```

---

# Fine Payment Example

Initial state:

```text
Amount      = 120000
Paid        = 0
Remaining   = 120000
Status      = UNPAID
```

After paying 25000:

```text
Amount      = 120000
Paid        = 25000
Remaining   = 95000
Status      = PARTIALLY_PAID
```

Trying to pay 100000:

```text
Expected:
Payment rejected

Reason:
100000 > 95000
```

Paying 95000:

```text
Amount      = 120000
Paid        = 120000
Remaining   = 0
Status      = PAID
```

---

# Example Loan Test

Initial book state:

```text
Total Copies     = 3
Available Copies = 3
```

After borrowing one copy:

```text
Total Copies     = 3
Available Copies = 2
```

After returning the book:

```text
Total Copies     = 3
Available Copies = 3
```

---

# Example Renewal Test

Initial transaction:

```text
ID = 15
TYPE = BORROW
STATUS = SUCCESS
```

After renewal:

```text
ID = 16
TYPE = RENEW
STATUS = SUCCESS
parent_transaction_id = 15
```

The new renewal transaction becomes the current transaction.

The old transaction remains in the history.

---

# API Error Handling

The frontend handles errors returned by the backend.

The general flow is:

```text
Frontend Request
      |
      v
Backend
      |
      +---- Success ----> Response
      |
      +---- Error ------> Error Response
                              |
                              v
                         Frontend
                              |
                              v
                         Show Message
```

This is especially important for operations such as:

```text
Borrow
Return
Renew
Pay Fine
```

---

# Configuration

Backend configuration is stored under:

```text
backend/library-management/src/main/resources/
```

The application configuration contains the database connection and other Spring Boot settings required to run the backend.

Environment-specific values should not be hard-coded into source code when the project is deployed in a different environment.

---

# Build Verification

The backend can be checked with:

```powershell
.\mvnw clean package -DskipTests
```

A successful build should end with:

```text
BUILD SUCCESS
```

If compilation fails, Maven reports the exact Java file and line where the problem occurred.

For example:

```text
Compilation failure
```

should be fixed before deploying the backend.

---

# Docker Development

A typical Docker-based development setup is:

```text
                 Docker Desktop
                      |
          +-----------+-----------+
          |                       |
          v                       v
   PostgreSQL Container      Frontend Container
          |                       |
          |                       |
          +----------+------------+
                     |
                     v
              Spring Boot Backend
```

The exact container names and environment variables depend on the Docker configuration used by the project.

---

# Frontend Production Build

The frontend Dockerfile uses a multi-stage build.

The general structure is:

```text
Node
 |
 | npm install
 | npm build
 v
React production build
 |
 v
Nginx
 |
 v
Production frontend
```

The resulting frontend is served using Nginx.

---

# Git Workflow

The project is maintained using Git.

Typical workflow:

```powershell
git status
```

Check changed files:

```powershell
git diff
```

Add changes:

```powershell
git add .
```

Commit:

```powershell
git commit -m "Update library management system"
```

Push:

```powershell
git push
```

---

# Development Notes

The project is being developed incrementally.

The main focus has been to keep the business logic in the backend and keep the frontend responsible mainly for displaying data and sending user requests.

For example:

```text
Frontend
    |
    | "Pay 95,000"
    v
Backend
    |
    | Check unpaid amount
    | Check member
    | Apply payment
    | Update fine
    v
Database
```

This prevents important business rules from depending only on frontend validation.

---

# Important Design Decisions

## Separate Loan Transactions

Borrow, return, and renewal operations are represented as transactions rather than simply changing one loan record.

This allows the system to preserve history.

---

## Parent Transaction

Renewals use:

```text
parent_transaction_id
```

to connect the new transaction to the previous one.

This allows a sequence such as:

```text
BORROW
   |
   +--> RENEW
          |
          +--> RENEW
```

to be stored in the database.

---

## Fine Payment Tracking

The system does not overwrite the original fine amount when a payment is made.

Instead:

```text
amount
paid_amount
```

are kept separately.

This makes it possible to calculate:

```text
remaining_amount =
amount - paid_amount
```

and support partial payments.

---

# Current Status

The project currently contains the main functionality required for the library management workflow.

Implemented areas include:

```text
[✓] Project structure
[✓] Spring Boot backend
[✓] React frontend
[✓] PostgreSQL database
[✓] Member management
[✓] Member history
[✓] Book management
[✓] Book availability
[✓] Borrowing
[✓] Returning
[✓] Renewals
[✓] Tracking codes
[✓] Loan status
[✓] Current loan logic
[✓] Loan history
[✓] Dynamic loan filters
[✓] Pagination
[✓] Reports
[✓] Fine management
[✓] Partial fine payment
[✓] Full fine payment
[✓] Paid amount tracking
[✓] Remaining amount tracking
[✓] Fine payment validation
[✓] Error handling
[✓] Generic internal-error protection
[✓] HTTP Basic Authentication
[✓] Configurable initial credentials
[✓] Application event logging
[✓] Loan-history state filtering
[✓] Docker Compose one-command build/start
```

The project is still open for UI improvements, additional validation, testing, and additional reporting features.

---

# Future Improvements

Possible improvements for future versions include:

- Role-based authorization
- More detailed dashboard statistics
- Advanced report generation
- CSV/Excel report export
- More advanced book search
- More advanced member search
- Better fine reporting
- Automated tests for loan workflows
- Automated tests for fine payment
- Improved validation messages
- Better mobile responsiveness
- API documentation
- More detailed system documentation

---

# Testing Scenarios

Some important scenarios for testing the application are listed below.

## Member

```text
1. Create a member
2. Try to create another member with the same membership number
3. Edit member information
4. Check member history
```

## Book

```text
1. Create a book
2. Set total copies
3. Check available copies
4. Borrow a copy
5. Check available copies again
6. Return the book
7. Check available copies again
```

## Loan

```text
1. Borrow a book
2. Check the transaction
3. Renew the book
4. Check parent transaction
5. Try to renew the old transaction
6. Return the current transaction
7. Check transaction history
```

## Fine

```text
1. Create an overdue return
2. Check generated fine
3. Pay part of the fine
4. Check paid amount
5. Check remaining amount
6. Pay the remaining amount
7. Check PAID status
8. Try to pay more than the remaining amount
9. Confirm that the invalid payment is rejected
```

---

# Example Fine Test

Initial state:

```text
Amount      = 120000
Paid        = 0
Remaining   = 120000
Status      = UNPAID
```

After paying 25000:

```text
Amount      = 120000
Paid        = 25000
Remaining   = 95000
Status      = PARTIALLY_PAID
```

Trying to pay 100000:

```text
Expected:
Payment rejected

Reason:
100000 > 95000
```

Paying 95000:

```text
Amount      = 120000
Paid        = 120000
Remaining   = 0
Status      = PAID
```

---

# Example Loan Test

Initial book state:

```text
Total Copies     = 3
Available Copies = 3
```

After borrowing one copy:

```text
Total Copies     = 3
Available Copies = 2
```

After returning the book:

```text
Total Copies     = 3
Available Copies = 3
```

---

# Example Renewal Test

Initial transaction:

```text
ID = 15
TYPE = BORROW
STATUS = SUCCESS
```

After renewal:

```text
ID = 16
TYPE = RENEW
STATUS = SUCCESS
parent_transaction_id = 15
```

The new renewal transaction becomes the current transaction.

The old transaction remains in the history.

---

# Troubleshooting

## Backend does not compile

Run:

```powershell
.\mvnw clean package -DskipTests
```

Read the first compilation error in the Maven output.

Do not focus on the final `BUILD FAILURE` message; the useful information is normally the Java file and line shown above it.

---

## PostgreSQL connection problem

First check running containers:

```powershell
docker ps
```

Then check the PostgreSQL container.

Example:

```powershell
docker exec -it library-postgres psql -U library_admin -d library_db
```

Inside PostgreSQL:

```sql
\dt
```

The expected main tables are:

```text
app_user
book
fine
loan_transaction
member
member_history
```

---

## Frontend Docker Build Problem

Build from the frontend directory:

```powershell
docker build -t library-frontend .
```

If Docker cannot pull images such as:

```text
node:22-alpine
nginx:alpine
```

check the Docker connection and network access to Docker Hub.

---

# Summary

The Library Management System provides a complete workflow for managing the basic operations of a library.

The main workflow is:

```text
MEMBER
   |
   v
BOOK
   |
   v
BORROW
   |
   +----> RENEW
   |          |
   |          +----> RENEW
   |
   v
RETURN
   |
   +---- No overdue ----> Complete
   |
   +---- Overdue -------> FINE
                              |
                              v
                         PAYMENT
                              |
                   +----------+----------+
                   |                     |
                   v                     v
             PARTIALLY_PAID            PAID
```

The system keeps the history of transactions instead of replacing previous records, supports partial fine payments, validates payments against unpaid amounts, and provides separate pages for current loan operations, historical reports, and fine management.

---

# Author

Developed as a full-stack Library Management System project.
