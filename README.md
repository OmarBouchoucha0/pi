# Backend

---

## Prerequisites

Make sure you have the following installed before getting started:

- **Java 17** -
- **Maven** -
- **MariaDB** -

Verify your installs:
```bash
java -version
mvn -version
mysql --version
```

---

## Database Setup

**1. Initialize and start MariaDB:**
```bash
sudo mariadb-install-db --user=mysql --basedir=/usr --datadir=/var/lib/mysql
sudo systemctl enable --now mariadb
sudo mysql_secure_installation
```

**2. Create the database and user:**
```bash
sudo mysql -u root -p
```
```sql
CREATE DATABASE pi_db;
CREATE USER 'user'@'localhost' IDENTIFIED BY '';
GRANT ALL PRIVILEGES ON pi_db.* TO 'user'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

---

## Running the App

```bash
# Development (auto-restarts on changes)
mvn spring-boot:run

# Build a JAR
mvn clean package -DskipTests

# Run the JAR directly
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

The API will be available at `http://localhost:8080`.

---

## Project Structure

```
src/main/java/com/yourname/backend/
├── config/         # Security, WebSocket, CORS configuration
├── controller/     # REST endpoints (@RestController)
├── service/        # Business logic
├── repository/     # Database access (JpaRepository interfaces)
├── model/          # JPA entities (@Entity)
├── dto/            # Request and response shapes
├── security/       # JWT filter, UserDetailsService
└── exception/      # Global error handling (@ControllerAdvice)

src/main/resources/
└── application.properties   # App configuration
```

---

## Key Dependencies

| Dependency | Purpose |
|---|---|
| Spring Web | REST API |
| Spring Data JPA | Database ORM |
| Spring Security | Auth and access control |
| JJWT | JWT token generation and validation |
| MySQL Connector | MariaDB/MySQL driver |
| Spring Mail | Email sending |
| Spring WebSocket | Real-time communication |
| Spring Validation | Request validation |
| Lombok | Boilerplate reduction |

---

## Useful Commands

```bash
# Connect to the database
mysql -u myapp_user -p myapp_db

# Check if the app is running
curl http://localhost:8080/api/users

# Watch logs (if running as a systemd service)
journalctl -f -u backend
```

---

# FrontEnd

---

## Prerequisites

Make sure you have the following installed before getting started:

- **node** -
- **npm** -

Verify your installs:
```bash
node -v
npm -v
```

---

## Database Setup

**1. Initialize and start MariaDB:**
```bash
cd frontend
npm install
```
---

## Running the App

```bash
# Development
ng serve                    # Start dev server with hot reload
ng serve --configuration=production  # Test production build locally

# Building
ng build                    # Development build
ng build --configuration=production  # Optimized production build

# Testing
ng test                     # Run unit tests (Jest)
ng test --watch             # Run tests in watch mode
ng test --coverage          # Generate coverage report
ng e2e                      # Run end-to-end tests (Cypress)

# Code Quality
ng lint                     # Run ESLint
ng lint --fix               # Fix auto-fixable linting errors

# Other
ng generate component [name] # Generate new component
ng generate service [name]   # Generate new service
ng analyze                  # Analyze bundle size
---

## Project Structure

```
src/
├── app/
│   ├── core/                 # Singleton services, guards, interceptors
│   │   ├── services/
│   │   ├── guards/
│   │   └── interceptors/
│   ├── shared/               # Reusable components, pipes, directives
│   │   ├── components/
│   │   ├── pipes/
│   │   └── directives/
│   ├── features/             # Feature modules (lazy-loaded)
│   │   ├── feature-name/
│   │   │   ├── components/
│   │   │   ├── services/
│   │   │   └── feature-name.routes.ts
│   ├── layout/               # Shell components (header, sidebar, footer)
│   └── app.config.ts         # App-level configuration
├── assets/                   # Static assets
├── styles/                   # Global styles & theming
│   ├── _theme.scss           # Material theme customization
│   └── _custom-tailwind.css  # Tailwind custom utilities
└── environments/             # Environment configurations```

---

## Key Dependencies

| Dependency | Purpose |
|---|---|
| tailwind | Css Framework |
| angular material ui | Component Library(offical) |

---

