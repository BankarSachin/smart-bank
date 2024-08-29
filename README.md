# SmartBank : A Modern Banking Application with Microservices Architecture
This microservices based application is designed to perfrom below things

1. **Account Creation**: Users should be able to create a new account by providing 

2.  **Balance Enquiry**: Users should be able to check their account balance.

3.  **Deposit**: Users should be able to deposit money into their account.

4.  **Withdrawal**: Users should be able to withdraw money from their account.

5.  **Transaction History**: Users should be able to view their transaction history.

6.  **Notifications**: Users should receive notifications via email for every transaction made on their account.

7.  **Transfer** : Fund transfer between accounts


# Architecture
![image](https://github.com/user-attachments/assets/77ab5d76-c8ec-433c-bc0e-405568bc563a)

- Gateway Design pattern and Service Discovery Pattern used
- Gateway automatically Load Balance traffic and routes with help of Service Registry

# Technology Stack and Tools
- Java 17
- Spring Boot - 3.3.2
- Spring Cloud - 2023.0.3
- PostgreSQL – 16
- Spring Data JPA
- Spring Security
- Open Feign
- ThymeLeaf
- Junit5 and Mockito
- Maven
- JWT
- Zipkin
- Netflix Eureka Service Registry
- Postman
- Jmeter
- Docker Desktop - 4.31.1 (153621)

# DB Schema Design
- Used Double Entry Database Schema for ledger /transaction maintenance
![image](https://github.com/user-attachments/assets/f915a63f-6882-4b34-8727-e365fec6d31e)


