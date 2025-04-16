# 🧪 Order Management Demo Project

A microservices-based demo project demonstrating an order and payment workflow.  
Focus areas include inter-service communication patterns (sync & async) and RabbitMQ as the primary message broker for decoupled event handling.

⚙️ **Tech Stack**: Java 21, Maven, Spring Boot, Spring Data JPA, Spring Cloud Config, RabbitMQ, PostgreSQL, Redis, Lombok, MapStruct, Jackson (FasterXML)

---

## 📦 Microservices Overview

| Service Name                         | Description                                                                              |
|--------------------------------------|------------------------------------------------------------------------------------------|
| **Config Server**                    | Provides centralized configuration for all microservices.                                |
| **Order Service**                    | Manages order creation, order-related data, and the order validation process.            |
| **Accounting Service**               | Handles verified order costs and redirects them to the payment and billing services.     |
| **Product Service**                  | Manages product stock availability and updates stock data.                               |
| **Discount Service**                 | Validates and applies discount codes to orders.                                          |
| **Billing Service** (Planned)        | Manages the billing process for verified and completed orders.                           |
| **Payment Service** (Planned)        | Provides the payment processing for validated orders.                                    |

A common module is used to gather shared tools such as Lombok, MapStruct, Jackson (FasterXML), and Spring AMQP for managing RabbitMQ schemas.

## 📬 RabbitMQ Schema 
Current exhanges and queues:

| Exchange Name              | Type   | Queue Name                  | Publisher Service | Consumer Service   | Routing Key        | Purpose                                      |
|----------------------------|--------|-----------------------------|-------------------|--------------------|--------------------|----------------------------------------------|
|x.order.validation          | Fanout | q.stock.validation.stock    | order-service	    | product-service    | N/A                | Publishes order stock validation request     |
|x.order.validation          | Fanout | q.stock.validation.discount | order-service     | discount-service   | N/A                | Publishes discount validation request        |
|x.validation.response       | Fanout | q.validation.response       | order-service	    | accounting-service | N/A                | Publishes combined validation results        |
|x.validation.response.wait  | Fanout | q.validation.response.wait  | N/A 			       | order-service      | N/A                | Retry queue for delayed validation responses |
|dlx.validation.response     | DLX    | q.order.rejected            | N/A 			       | not determined     | N/A                | Dead-letter queue for failed validations     |
|x.accounting.price          | Direct | q.accounting.price          | N/A			       | not determined     | q.accounting.price | Publishes validated order to be priced       |

📝 Notes :
- x.validation.response.wait is the dead-letter exchange (DLX) target of q.validation.response. When the retry count is exceeded, messages are forwarded to q.order.rejected via the dlx.validation.response.

## 🔄 Workflow
### 1. Order Acceptance 
The order-service accepts an order via REST API. the Order Request includes a discount code (optional) and a cart with one or more items (product IDs and requested quantities)
   - The discount code is sent to the discount-service for validation.
   - The cart is sent to the product-service to check product stock availability.
     
### 2. Validation
discount-service and product-service validates the order and both services return their validation results to the order-service.
   - Both services forward validation results to order-service.
   - The discount-service validates the discount code.
   - The product-service validates the stock availability.
     
### 3. Validation Handling 
order-service collects validation results forwards the order record to accounting-service if validations succeed
   - Collects both validation responses using the Order ID as a correlation key.
   - Validation results are temporarily stored in an in-memory cache until both are received.
   - If both discount and stock validation succeed, forwards the order record to accounting-service in order to determine and calculate prices ant total cost.
   - If either validation fails, the order is rejected immediately.
     
### 4. Accounting and Pricing
accounting-service determines prices of products in the cart and calculate total cost.
   - Retrieves product prices via a REST API call.
   - Retrieves the discount amount via a REST API call.
   - Calculates total prices and applies discount
   - Forwards priced order to payment-service and billing service (Planned)
     
### 5. Payment Processing (Planned)
The payment-service will handle the actual payment process based on the chosen payment method.

### 6. Billing (Planned)
The billing-service will store the final priced order and payment details, which can be queried via an API.

