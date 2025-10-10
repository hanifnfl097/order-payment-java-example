# 🧾 Order & Payment — Java Backend Mini Project

A **Spring Boot microservices** project built to demonstrate clean backend practices:
- Two independent services: **Orders** & **Payments**
- Each with its own **PostgreSQL** database and **Flyway** migrations
- **REST communication**, **Java Streams**, and **Native SQL** for data processing
- Fully **containerized** with **Docker Compose**

---

## 🧰 Tech Stack
| Layer | Technology |
|-------|-------------|
| Language | Java 17 |
| Framework | Spring Boot 3 |
| Database | PostgreSQL |
| ORM / Migration | Spring Data JPA + Flyway |
| Build Tool | Maven |
| Containerization | Docker & Docker Compose |
| Data Processing | Java Streams API |

---

## 🏗 Architecture

```
docker-compose.yml
├── orders-service   → port 8081
│   ├─ users, products, orders, order_items
│   └─ REST → payments-service
├── payments-service → port 8082
│   ├─ payments
│   └─ SQL aggregation endpoints
└── PostgreSQL (x2)  → ports 5433, 5434
```

---

## 🚀 Quick Start

### Run with Docker
```bash
docker compose up --build
```

Access:
- Orders Service → [http://localhost:8081](http://localhost:8081)
- Payments Service → [http://localhost:8082](http://localhost:8082)

Stop & clean:
```bash
docker compose down -v
```

---

## 💻 Run without Docker (optional)

1. Create local PostgreSQL DBs:
   ```sql
   CREATE USER orders WITH PASSWORD 'orders';
   CREATE DATABASE orders OWNER orders;
   CREATE USER payments WITH PASSWORD 'payments';
   CREATE DATABASE payments OWNER payments;
   ```

2. Run:
    - **payments-service** → port 8082
    - **orders-service** → port 8081 with `PAYMENTS_BASE_URL=http://localhost:8082`

---

## 📬 Example APIs

| Service | Endpoint | Description |
|----------|-----------|--------------|
| Orders | `/api/orders/{id}/with-payment` | Order + payment info |
| Orders | `/api/users/{id}/orders/history` | SQL join + aggregation |
| Orders | `/api/reports/top-products?limit=5` | SQL group by |
| Orders | `/api/orders/stream/large?minTotal=100` | Java Streams filter |
| Payments | `/api/payments/stats/amounts` | SQL sum, avg, count |

---

## 🧩 Features Checklist
- ✅ Spring IoC & DI
- ✅ Java Streams in-memory filtering
- ✅ Native SQL joins & group-by queries
- ✅ Microservice-to-microservice REST calls
- ✅ Dockerized with Compose
- ✅ Separate databases per service
- ✅ Clean code structure & RESTful API

---

## 👨‍💻 Author
**Hanif Naufal**  
📦 [GitHub Repository](https://github.com/hanifnfl097/order-payment-java-example)

---

🟢 **Run everything easily:**
```bash
docker compose up --build
```
then visit  
👉 [http://localhost:8081/api/users](http://localhost:8081/api/users)  
👉 [http://localhost:8082/api/payments](http://localhost:8082/api/payments)
