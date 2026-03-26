# Rewards Application (Spring Boot)

A Spring Boot REST API that calculates customer reward points based on purchase transactions.

---

## Features

* Calculate reward points per transaction
* Monthly and total rewards per customer
* RESTful API design
* Swagger UI for API testing
* H2 in-memory database
* Transaction management with rollback
* Global exception handling
* Request/Response logging
* Health check endpoint with Spring Boot Actuator

---

## Reward Rules

* **2 points** for every $1 spent over $100
* **1 point** for every $1 spent between $50 and $100

Example:
$120 purchase = (20 × 2) + (50 × 1) = **90 points**

---

## Tech Stack

* Java 17
* Spring Boot 3
* Spring Web / REST
* Spring Data JPA
* H2 Database
* Lombok
* Swagger (OpenAPI)
* Spring Boot Actuator
* Maven

---

##  How to Run

```bash
./mvnw spring-boot:run
```

---

##  Base URL

```text
http://localhost:8080/api/rewards
```

---

## Swagger UI

```text
http://localhost:8080/swagger-ui/index.html
```

---

## H2 Console

```text
http://localhost:8080/h2-console
```

### DB Config:

* JDBC URL: `jdbc:h2:mem:rewardsdb`
* Username: `sa`
* Password: *(empty)*

---

## Health Check

This application exposes a health endpoint using **Spring Boot Actuator**.

### Health URL

```text
http://localhost:8080/actuator/health
```

### Example Response

```json
{
  "status": "UP"
}
```

### Detailed Health Response

If detailed health information is enabled, the response may look like:

```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP"
    },
    "diskSpace": {
      "status": "UP"
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

## API Endpoints

---

### 1. Get All Rewards

```http
GET /api/rewards
```

#### Response

```json
[
  {
    "customer_id": 101,
    "customer_name": "John",
    "monthly_points": [
      {
        "month": "January",
        "points": 115
      }
    ],
    "total_points": 115
  }
]
```

---

### 2. Get Rewards by Customer

```http
GET /api/rewards/{customerId}
```

#### Example

```text
GET /api/rewards/101
```

#### Response

```json
{
  "customer_id": 101,
  "customer_name": "John",
  "monthly_points": [
    {
      "month": "January",
      "points": 115
    }
  ],
  "total_points": 115
}
```

---

### 3. Create Transaction

```http
POST /api/rewards/transactions
```

#### Request

```json
{
  "id": 10,
  "customer_id": 200,
  "customer_name": "Alice",
  "amount": 120,
  "transaction_date": "2026-03-26"
}
```

#### Response (201)

```json
{
  "id": 10,
  "customerId": 200,
  "customerName": "Alice",
  "amount": 120.0,
  "transactionDate": "2026-03-26"
}
```

---

### Error Response

```json
{
  "timestamp": "2026-03-26T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Amount cannot be negative",
  "path": "/api/rewards/transactions"
}
```

---

## Transaction Handling

* Uses `@Transactional`
* Automatically rolls back on failure
* Ensures data consistency

---

## Project Structure

```text
com.charter.rewardsapplication
│
├── controller
├── service
├── repo
├── model
├── exception
├── config
└── filter
```

## Author

**Neranga Urapola**


### 2. Get Rewards by Customer

```http
GET /api/rewards/{customerId}
```

#### Example

```
GET /api/rewards/101
```

#### Response

```json
{
  "customer_id": 101,
  "customer_name": "John",
  "monthly_points": [
    {
      "month": "January",
      "points": 115
    }
  ],
  "total_points": 115
}
```

---

### 3. Create Transaction

```http
POST /api/rewards/transactions
```

#### Request

```json
{
  "id": 10,
  "customer_id": 200,
  "customer_name": "Alice",
  "amount": 120,
  "transaction_date": "2026-03-26"
}
```

####  Response (201)

```json
{
  "id": 10,
  "customerId": 200,
  "customerName": "Alice",
  "amount": 120.0,
  "transactionDate": "2026-03-26"
}
```

---

### Error Response

```json
{
  "timestamp": "2026-03-26T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Amount cannot be negative",
  "path": "/api/rewards/transactions"
}
```

---

## Transaction Handling

* Uses `@Transactional`
* Automatically rolls back on failure
* Ensures data consistency

---


## Project Structure

```
com.charter.rewardsapplication
│
├── controller
├── service
├── repository
├── entity
├── dto
├── exception
├── config
└── filter
```



## Author

**Neranga Urapola**

