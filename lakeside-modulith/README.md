# 🏦 Lakeside Mutual – Modular Monolith

## 📖 Overview

Lakeside Mutual was migrated from a microservices architecture to a modular monolith application built using Spring Boot and Spring Modulith principles.  
It models an insurance system with clearly defined business modules that collaborate within a single deployable application.

Unlike microservices, all modules run in the same process while maintaining strict boundaries, ensuring high cohesion and low coupling.

## 🧩 Architecture (Modular Monolith)

The system is structured into independent application modules, each representing a business capability.

### Core Modules

- **Customer Core (`customercore`)**  
  Owns customer master data and provides a public API for accessing customer information.

- **Customer Management (`customermanagement`)**  
  Handles customer-related operations using the Customer Core API.

- **Customer Self-Service (`customerselfservice`)**  
  Manages customer-facing workflows such as insurance quote requests.

- **Policy Management (`policymanagement`)**  
  Handles quotes and the policy lifecycle.

- **Risk Management (`riskmanagement`)**  
  Maintains projections and risk analysis based on policy data.

## 🚀 Prerequisites

- Docker
- Docker Compose
- Node.js (for `risk-management-client`)
- npm

## ▶️ Running the Application

From the root folder of the project, run:

```bash
docker compose build
docker compose up
```

## 📊 Running the Risk Management Client 
Navigate to the client directory: 
Run: 
```bash
npm install  
.\riskmanager.bat run C:\Users\MariaS\Desktop\report.csv 
```

This client: 
- Processes policy-related data 
- Generates a risk analysis report 
- Writes results to the specified CSV file