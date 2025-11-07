# 🏗️ Phase 1: Project Structure & Setup Guide

## 📁 Complete Folder Structure

```
concierge-scheduler/
│
├── src/
│   ├── main/
│   │   ├── java/com/vbforge/concierge/
│   │   │   │
│   │   │   ├── ConciergeSchedulerApplication.java  ✅ (Main class)
│   │   │   │
│   │   │   ├── config/                              📦 Configuration classes
│   │   │   │   ├── JpaAuditingConfig.java          ✅ (Enable auditing)
│   │   │   │   ├── SecurityConfig.java             ⏳ (Phase 4)
│   │   │   │   ├── MapStructConfig.java            ⏳ (Phase 2)
│   │   │   │   └── WebMvcConfig.java               ⏳ (Phase 5)
│   │   │   │
│   │   │   ├── controller/                          🌐 MVC Controllers
│   │   │   │   ├── ScheduleController.java         ⏳ (Phase 5)
│   │   │   │   ├── ConciergeController.java        ⏳ (Phase 5)
│   │   │   │   ├── HistoryController.java          ⏳ (Phase 5)
│   │   │   │   └── StatisticsController.java       ⏳ (Phase 9)
│   │   │   │
│   │   │   ├── dto/                                 📋 Data Transfer Objects
│   │   │   │   ├── ConciergeDto.java               ⏳ (Phase 2)
│   │   │   │   ├── ShiftAssignmentDto.java         ⏳ (Phase 2)
│   │   │   │   ├── MonthScheduleDto.java           ⏳ (Phase 2)
│   │   │   │   └── MonthHistoryDto.java            ⏳ (Phase 2)
│   │   │   │
│   │   │   ├── entity/                              🗄️ JPA Entities
│   │   │   │   ├── BaseEntity.java                 ✅ (Base class)
│   │   │   │   ├── Concierge.java                  ⏳ (Phase 2)
│   │   │   │   ├── ShiftAssignment.java            ⏳ (Phase 2)
│   │   │   │   ├── MonthHistory.java               ⏳ (Phase 2)
│   │   │   │   └── User.java                       ⏳ (Phase 4)
│   │   │   │
│   │   │   ├── enums/                               🏷️ Enumerations
│   │   │   │   ├── ColorType.java                  ⏳ (Phase 2)
│   │   │   │   ├── ShiftType.java                  ⏳ (Phase 2)
│   │   │   │   └── UserRole.java                   ⏳ (Phase 4)
│   │   │   │
│   │   │   ├── exception/                           ⚠️ Custom Exceptions
│   │   │   │   ├── ConciergeSchedulerException.java ✅ (Base exception)
│   │   │   │   ├── ResourceNotFoundException.java   ✅
│   │   │   │   ├── ConciergeNotFoundException.java  ✅
│   │   │   │   ├── ShiftAssignmentNotFoundException.java ✅
│   │   │   │   ├── MonthHistoryNotFoundException.java ✅
│   │   │   │   ├── ShiftConflictException.java      ✅
│   │   │   │   ├── InvalidInputException.java       ✅
│   │   │   │   ├── InvalidDateException.java        ✅
│   │   │   │   ├── ConciergeDuplicateException.java ✅
│   │   │   │   ├── ConciergeInUseException.java     ✅
│   │   │   │   ├── ExportException.java             ✅
│   │   │   │   ├── SnapshotException.java           ✅
│   │   │   │   └── GlobalExceptionHandler.java      ✅
│   │   │   │
│   │   │   ├── mapper/                              🔄 MapStruct Mappers
│   │   │   │   ├── ConciergeMapper.java            ⏳ (Phase 2)
│   │   │   │   ├── ShiftAssignmentMapper.java      ⏳ (Phase 2)
│   │   │   │   └── MonthHistoryMapper.java         ⏳ (Phase 2)
│   │   │   │
│   │   │   ├── repository/                          💾 Spring Data Repositories
│   │   │   │   ├── ConciergeRepository.java        ⏳ (Phase 2)
│   │   │   │   ├── ShiftAssignmentRepository.java  ⏳ (Phase 2)
│   │   │   │   ├── MonthHistoryRepository.java     ⏳ (Phase 2)
│   │   │   │   └── UserRepository.java             ⏳ (Phase 4)
│   │   │   │
│   │   │   ├── security/                            🔒 Security Components
│   │   │   │   ├── CustomUserDetailsService.java   ⏳ (Phase 4)
│   │   │   │   └── PasswordEncoderConfig.java      ⏳ (Phase 4)
│   │   │   │
│   │   │   ├── service/                             💼 Service Interfaces
│   │   │   │   ├── ConciergeService.java           ⏳ (Phase 3)
│   │   │   │   ├── ShiftSchedulingService.java     ⏳ (Phase 3)
│   │   │   │   ├── MonthScheduleService.java       ⏳ (Phase 3)
│   │   │   │   ├── HistoryService.java             ⏳ (Phase 3)
│   │   │   │   ├── ExportService.java              ⏳ (Phase 7)
│   │   │   │   ├── ValidationService.java          ⏳ (Phase 3)
│   │   │   │   └── impl/                            📝 Service Implementations
│   │   │   │       ├── ConciergeServiceImpl.java
│   │   │   │       ├── ShiftSchedulingServiceImpl.java
│   │   │   │       ├── MonthScheduleServiceImpl.java
│   │   │   │       ├── HistoryServiceImpl.java
│   │   │   │       ├── ExportServiceImpl.java
│   │   │   │       └── ValidationServiceImpl.java
│   │   │   │
│   │   │   ├── util/                                🛠️ Utility Classes
│   │   │   │   ├── DateUtils.java                  ⏳ (Phase 2)
│   │   │   │   ├── ColorUtils.java                 ⏳ (Phase 2)
│   │   │   │   └── JsonUtils.java                  ⏳ (Phase 8)
│   │   │   │
│   │   │   └── validation/                          ✔️ Custom Validators
│   │   │       ├── DateValidator.java              ⏳ (Phase 3)
│   │   │       └── ShiftValidator.java             ⏳ (Phase 3)
│   │   │
│   │   └── resources/
│   │       │
│   │       ├── db/migration/                        🗃️ Flyway Migrations
│   │       │   ├── V1__create_concierges_table.sql ⏳ (Phase 2)
│   │       │   ├── V2__create_shift_assignments_table.sql ⏳ (Phase 2)
│   │       │   ├── V3__create_month_history_table.sql ⏳ (Phase 2)
│   │       │   ├── V4__create_users_table.sql      ⏳ (Phase 4)
│   │       │   └── V5__insert_initial_data.sql     ⏳ (Phase 2)
│   │       │
│   │       ├── static/                              🎨 Static Resources
│   │       │   ├── css/
│   │       │   │   ├── main.css                    ⏳ (Phase 5)
│   │       │   │   ├── calendar.css                ⏳ (Phase 5)
│   │       │   │   └── mobile.css                  ⏳ (Phase 6)
│   │       │   │
│   │       │   ├── js/
│   │       │   │   ├── calendar.js                 ⏳ (Phase 5)
│   │       │   │   ├── validation.js               ⏳ (Phase 5)
│   │       │   │   └── mobile.js                   ⏳ (Phase 6)
│   │       │   │
│   │       │   └── images/
│   │       │       └── logo.png                    ⏳ (Phase 5)
│   │       │
│   │       ├── templates/                           📄 Thymeleaf Templates
│   │       │   │
│   │       │   ├── fragments/                       🧩 Reusable Components
│   │       │   │   ├── header.html                 ⏳ (Phase 5)
│   │       │   │   ├── footer.html                 ⏳ (Phase 5)
│   │       │   │   ├── navbar.html                 ⏳ (Phase 5)
│   │       │   │   └── modals.html                 ⏳ (Phase 5)
│   │       │   │
│   │       │   ├── schedule/                        📅 Schedule Pages
│   │       │   │   ├── calendar.html               ⏳ (Phase 5)
│   │       │   │   └── export.html                 ⏳ (Phase 7)
│   │       │   │
│   │       │   ├── concierge/                       👥 Concierge Pages
│   │       │   │   ├── list.html                   ⏳ (Phase 5)
│   │       │   │   ├── form.html                   ⏳ (Phase 5)
│   │       │   │   └── details.html                ⏳ (Phase 5)
│   │       │   │
│   │       │   ├── history/                         🕐 History Pages
│   │       │   │   ├── list.html                   ⏳ (Phase 8)
│   │       │   │   └── view.html                   ⏳ (Phase 8)
│   │       │   │
│   │       │   ├── statistics/                      📊 Statistics Pages
│   │       │   │   └── dashboard.html              ⏳ (Phase 9)
│   │       │   │
│   │       │   ├── auth/                            🔐 Authentication Pages
│   │       │   │   ├── login.html                  ⏳ (Phase 4)
│   │       │   │   └── register.html               ⏳ (Phase 4)
│   │       │   │
│   │       │   ├── error/                           ⚠️ Error Pages
│   │       │   │   ├── 404.html                    ⏳ (Phase 5)
│   │       │   │   ├── 403.html                    ⏳ (Phase 4)
│   │       │   │   └── 500.html                    ⏳ (Phase 5)
│   │       │   │
│   │       │   └── index.html                       🏠 Landing Page
│   │       │
│   │       ├── application.properties               ✅ (Main config)
│   │       ├── application-dev.properties           ✅ (Dev profile)
│   │       ├── application-test.properties          ✅ (Test profile)
│   │       ├── application-prod.properties          ✅ (Prod profile)
│   │       └── logback-spring.xml                   ✅ (Logging config)
│   │
│   └── test/
│       └── java/com/vbforge/concierge/
│           │
│           ├── controller/                          🧪 Controller Tests
│           │   ├── ScheduleControllerTest.java     ⏳ (Phase 5)
│           │   ├── ConciergeControllerTest.java    ⏳ (Phase 5)
│           │   └── HistoryControllerTest.java      ⏳ (Phase 8)
│           │
│           ├── repository/                          🧪 Repository Tests
│           │   ├── ConciergeRepositoryTest.java    ⏳ (Phase 2)
│           │   └── ShiftAssignmentRepositoryTest.java ⏳ (Phase 2)
│           │
│           ├── service/                             🧪 Service Tests
│           │   ├── ConciergeServiceTest.java       ⏳ (Phase 3)
│           │   ├── ShiftSchedulingServiceTest.java ⏳ (Phase 3)
│           │   └── ExportServiceTest.java          ⏳ (Phase 7)
│           │
│           ├── integration/                         🧪 Integration Tests
│           │   └── ScheduleIntegrationTest.java    ⏳ (Phase 10)
│           │
│           └── util/                                🧪 Test Utilities
│               ├── TestDataBuilder.java            ⏳ (Phase 2)
│               └── MockDataProvider.java           ⏳ (Phase 3)
│
├── docker/                                          🐳 Docker Configuration
│   ├── Dockerfile                                   ⏳ (Phase 11)
│   └── docker-compose.yml                           ⏳ (Phase 11)
│
├── docs/                                            📚 Documentation
│   ├── API.md                                       ⏳ (Phase 11)
│   ├── USER_GUIDE.md                                ⏳ (Phase 11)
│   └── DEPLOYMENT.md                                ⏳ (Phase 11)
│
├── .gitignore                                       ✅
├── pom.xml                                          ✅ (Maven config)
└── README.md                                        ✅ (Project overview)
```

---

## 🚀 Setup Instructions

### **Step 1: Create Project Structure**

Create all the folders manually or use this command (Linux/Mac):

```bash
# Create main source folders
mkdir -p src/main/java/com/vbforge/concierge/{config,controller,dto,entity,enums,exception,mapper,repository,security,service/impl,util,validation}

# Create resources folders
mkdir -p src/main/resources/{db/migration,static/{css,js,images},templates/{fragments,schedule,concierge,history,statistics,auth,error}}

# Create test folders
mkdir -p src/test/java/com/vbforge/concierge/{controller,repository,service,integration,util}

# Create additional folders
mkdir -p docker docs logs
```

---

### **Step 2: Setup MySQL Database**

```sql
-- Connect to MySQL
mysql -u root -p

-- Create database
CREATE DATABASE concierge_scheduler_dev
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Create user (optional)
CREATE USER 'concierge_user'@'localhost' IDENTIFIED BY 'concierge_pass';
GRANT ALL PRIVILEGES ON concierge_scheduler_dev.* TO 'concierge_user'@'localhost';
FLUSH PRIVILEGES;
```

---

### **Step 3: Create .gitignore**

```gitignore
# Compiled class files
*.class
target/
out/

# Log files
*.log
logs/

# Package files
*.jar
*.war
*.ear

# IDE files
.idea/
*.iml
.vscode/
.classpath
.project
.settings/

# OS files
.DS_Store
Thumbs.db

# Application properties (with sensitive data)
application-prod.properties

# Test coverage
coverage/

# Maven
.mvn/wrapper/maven-wrapper.jar
```

---

### **Step 4: Initialize Maven Project**

```bash
# Generate project (or use Spring Initializr)
mvn archetype:generate \
  -DgroupId=com.vbforge \
  -DartifactId=concierge-scheduler \
  -DarchetypeArtifactId=maven-archetype-quickstart \
  -DinteractiveMode=false

# Or download from Spring Initializr:
# https://start.spring.io/
```

---

### **Step 5: Copy Configuration Files**

1. Copy `pom.xml` (from artifact above)
2. Copy all `application*.properties` files to `src/main/resources/`
3. Copy `logback-spring.xml` to `src/main/resources/`

---

### **Step 6: Create Java Source Files**

1. Create `ConciergeSchedulerApplication.java` (main class)
2. Create `BaseEntity.java` in entity package
3. Create `JpaAuditingConfig.java` in config package
4. Create all exception classes in exception package
5. Create `GlobalExceptionHandler.java` in exception package

---

### **Step 7: Build & Run**

```bash
# Clean and compile
mvn clean compile

# Run the application
mvn spring-boot:run

# Or run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Access application
# http://localhost:8080
```

---

### **Step 8: Verify Setup**

✅ Application starts without errors
✅ Database connection successful
✅ Flyway migrations ready (will run in Phase 2)
✅ Logging working (check logs/ folder)
✅ Profiles switchable (dev, test, prod)

---

## ✅ Phase 1 Checklist

- [x] pom.xml created with all dependencies
- [x] Application properties (dev, test, prod)
- [x] Base entity with auditing
- [x] Exception hierarchy complete
- [x] Global exception handler
- [x] Logging configuration
- [x] Main application class
- [x] Project structure created
- [ ] MySQL database setup
- [ ] First successful run

---

## 🎯 Next: Phase 2

Create entities, DTOs, mappers, and Flyway migrations!

