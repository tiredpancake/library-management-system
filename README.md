# Library Management System

A full-stack Library Management System developed to manage members, books, borrowing transactions, returns, renewals, loan history, reports, and fines.

The project is designed as a simple and practical library system where the main library operations can be handled through a web interface.

---

## Technologies

### Backend

- Java 17
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Maven
- REST API

### Frontend

- React
- JavaScript
- Tailwind CSS
- Axios
- Lucide React

### Database

- PostgreSQL 16

### Deployment / Environment

- Docker
- Docker Compose

---

## Main Features

The system currently includes the following main sections:

- Dashboard
- Books
- Members
- Loans
- Reports
- Fines

---

# Members

The Members section is used to manage library members.

Each member contains information such as:

- Membership Number
- Full Name
- National Code
- Birth Date
- Phone
- Address
- Postal Code
- Membership Type
- Member Status
- Created Date
- Updated Date

The system also keeps a history of changes made to member information.

For example, if a member's phone number is changed, the previous and new values can be recorded in the member history.

---

# Books

The Books section is used to add and manage books in the library.

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
- Created Date
- Updated Date

The system checks the number of available copies when a borrowing transaction is created.

A book cannot be borrowed when there are no available copies.

---

# Loans

The Loans section is used to manage borrowing operations.

The supported transaction types are:

- `BORROW`
- `RETURN`
- `RENEW`

Each loan transaction contains information such as:

- Tracking Code
- Member
- Book
- Transaction Type
- Status
- Request Date
- Due Date
- Return Date
- Renewal Count

The system keeps the transaction history instead of replacing previous transactions.

For example, a normal borrowing operation can be followed by a renewal:

```text
BORROW
   |
   └── RENEW
```
