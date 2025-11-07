# 🏢 Concierge Shift Scheduler  
_A full-stack Spring Boot application for managing concierge work shifts with exports, statistics, and responsive UI._

---

![Java](https://img.shields.io/badge/Java-17-blue?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?logo=springboot)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-UI%20Template-green?logo=thymeleaf)
![MySQL](https://img.shields.io/badge/MySQL-Database-blue?logo=mysql)
![Build](https://img.shields.io/badge/Build-Maven-orange?logo=apachemaven)
![License](https://img.shields.io/badge/License-MIT-lightgrey)
![Status](https://img.shields.io/badge/Status-In%20Development-yellow)

---

## 📖 Overview  
The **Concierge Shift Scheduler** is a Spring Boot 3.x web application that allows administrators to manage concierge working shifts, automate scheduling, view history snapshots, and generate reports in multiple export formats.  
It includes **role-based security**, a **responsive mobile design**, and a **modular structure** for scalability.

---

## 🚀 Key Features  

✅ **Multi-format Export:** CSV, PDF, JSON, TXT, HTML  
✅ **Mobile-first responsive design**  
✅ **Monthly history snapshots & restore**  
✅ **Role-based access control (Admin/Concierge)**  
✅ **Statistics dashboard with visual charts**  
✅ **Flyway migrations for database versioning**  
✅ **Fully tested services and controllers (80%+ coverage)**  

---

## 🗺️ Final Phase Plan

### **Phase 1: Foundation & Project Setup ⚙️**
**Duration:** 1–2 days  
**Goal:** Setup project structure, dependencies, and core configs.

**Includes:**
- Initialize Spring Boot 3.x project  
- Configure MySQL + H2 (test)  
- Setup Lombok, MapStruct, Flyway  
- Add exception handling, logging (SLF4J + Logback)  
- Implement base audit entities and profiles  

**Deliverables:**
- ✅ Running Spring Boot app  
- ✅ Database connectivity  
- ✅ Exception handling + logging configured  

---

### **Phase 2: Domain Model & Core Entities 🏗️**
**Duration:** 2 days  
**Goal:** Define domain model with validation and migrations.

**Entities:**
- `BaseEntity` (audited abstract class)  
- `Concierge` (color, name, active flag)  
- `ShiftAssignment` (shift per date + concierge)  
- `MonthHistory` (snapshots in JSON)  
- Enums: `ColorType`, `ShiftType`, `UserRole`

**Deliverables:**
- ✅ Entities, DTOs, MapStruct mappers  
- ✅ Flyway schema migration  
- ✅ Repository tests with H2  

---

### **Phase 3: Business Logic & Services 💼**
**Duration:** 3–4 days  
**Goal:** Implement all core business logic with validation.

**Core Services:**
- `ConciergeService`  
- `ShiftSchedulingService`  
- `MonthScheduleService`  
- `HistoryService`  
- `ExportService`  
- `ValidationService`

**Deliverables:**
- ✅ Service layer implemented  
- ✅ Unit + integration tests  
- ✅ Validation + transactional logic  

---

### **Phase 4: Security Layer 🔒**
**Duration:** 1–2 days  
**Goal:** Add authentication & authorization.

**Includes:**
- Spring Security + BCrypt  
- `User` entity with roles (ADMIN, CONCIERGE)  
- Role-based permissions  
- Login/logout pages + CSRF protection  

**Deliverables:**
- ✅ Working login/logout  
- ✅ Role-based access  

---

### **Phase 5: Web Layer (Desktop First) 🌐**
**Duration:** 3 days  
**Goal:** MVC controllers + Thymeleaf views.

**Controllers:**
- `ScheduleController`  
- `ConciergeController`  
- `HistoryController`  
- `StatisticsController`

**Deliverables:**
- ✅ Desktop-ready UI  
- ✅ Form validation + flash messages  
- ✅ Controller tests passing  

---

### **Phase 6: Responsive Mobile Design 📱**
**Duration:** 2–3 days  
**Goal:** Optimize for mobile & tablet users.

**Optimizations:**
- List-style calendar view on mobile  
- Touch-friendly navigation  
- Responsive forms & layouts  

**Deliverables:**
- ✅ Fully responsive across devices  
- ✅ Touch gestures & adaptive layout  

---

### **Phase 7: Export Functionality 📤**
**Duration:** 2 days  
**Goal:** Implement all export formats.

| Format | Library | Description |
|--------|----------|-------------|
| CSV | OpenCSV | UTF-8 table export |
| PDF | iText7 / Flying Saucer | Printable monthly calendar |
| JSON | Jackson | Structured month snapshot |
| TXT | Built-in | Simple readable layout |
| HTML | Built-in | Standalone printable file |

**Deliverables:**
- ✅ All exports working  
- ✅ Download endpoints & UI integrated  

---

### **Phase 8: History & Snapshot System 🕐**
**Duration:** 2 days  
**Goal:** Implement historical snapshots & restore.

**Includes:**
- Auto monthly snapshot  
- Manual snapshot trigger  
- JSON serialization  
- Month-to-month comparison  

**Deliverables:**
- ✅ Snapshot system operational  
- ✅ Restore & comparison features  

---

### **Phase 9: Statistics & Reports 📊**
**Duration:** 1–2 days  
**Goal:** Build analytics dashboard.

**Metrics:**
- Shifts per concierge  
- Most active days  
- Monthly comparison  

**Deliverables:**
- ✅ Chart-based dashboard (Chart.js)  
- ✅ Exportable reports  

---

### **Phase 10: Testing & Quality Assurance ✅**
**Duration:** 2–3 days  
**Goal:** Comprehensive testing.

**Tests:**
- Unit (80%+ coverage)  
- Integration (H2 DB)  
- Web (MockMvc)  
- E2E (Selenium optional)  
- Security & performance  

**Deliverables:**
- ✅ All tests passing  
- ✅ No critical bugs  

---

### **Phase 11: Deployment & Documentation 🚀**
**Duration:** 2 days  
**Goal:** Production-ready delivery.

**Includes:**
- Docker + Compose setup  
- Production database config  
- CI/CD (GitHub Actions optional)  
- Full documentation  

**Deliverables:**
- ✅ Docker image ready  
- ✅ Deployment & user docs complete  

---

## ⚙️ Dependencies
```xml
<!-- Core Spring Boot -->
spring-boot-starter-web
spring-boot-starter-thymeleaf
spring-boot-starter-data-jpa
spring-boot-starter-validation
spring-boot-starter-security

<!-- Database -->
mysql-connector-java
h2database (test scope)

<!-- Utilities -->
lombok
mapstruct + mapstruct-processor
flyway-core

<!-- Export Libraries -->
opencsv
itext7 or flying-saucer
jackson-databind

<!-- Testing -->
spring-boot-starter-test
junit-jupiter
mockito-core
```

---

## 📦 Project Structure
```
concierge-scheduler/
├── src/
│   ├── main/java/com/vladproduction/concierge/
│   │   ├── config/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── enums/
│   │   ├── exception/
│   │   ├── mapper/
│   │   ├── repository/
│   │   ├── security/
│   │   ├── service/
│   │   │   └── impl/
│   │   ├── util/
│   │   └── validation/
│   ├── resources/
│   │   ├── db/migration/
│   │   ├── static/{css,js,images}/
│   │   ├── templates/{fragments,schedule,concierge,history,statistics,auth}/
│   │   ├── application.properties
│   │   ├── application-dev.properties
│   │   └── application-prod.properties
│   └── test/java/com/vladproduction/concierge/
│       ├── controller/
│       ├── repository/
│       ├── service/
│       └── integration/
├── docker/
│   ├── Dockerfile
│   └── docker-compose.yml
├── docs/
│   ├── API.md
│   ├── USER_GUIDE.md
│   └── DEPLOYMENT.md
├── pom.xml
└── README.md
```

---

## 🕒 Estimated Timeline (~4–5 Weeks)

| Phase | Duration | Focus |
|-------|-----------|--------|
| 1–3 | 1 week | Foundation |
| 4–6 | 1.5 weeks | Web & Mobile |
| 7–9 | 1 week | Features |
| 10–11 | 1 week | Testing & Deployment |

---

## 💡 Future Enhancements
- Google Calendar API integration  
- Shift notifications (email/SMS)  
- Dark mode toggle  
- REST API for mobile apps  

---

## 🧑‍💻 Author
**vbforge**  
_Java Developer | Spring Boot | Software Architecture_

📫 **Contact:** [GitHub](https://github.com/vbforge) • [LinkedIn](https://linkedin.com/in/vlad-bogdantsev-7897662b2)
