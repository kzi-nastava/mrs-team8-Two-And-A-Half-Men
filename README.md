# Two And A Half Men

> University Project

## Team Members

| Name | Index |
|------|-------|
| Mihajlo Milojević | SV57/2023 |
| Stefan Ilić | SV12/2023 |
| Petar Popović | SV17/2023 |

---

## Project Structure

```
├── BACKEND/        # Spring Boot REST API
│   └── db.sh       # Script for starting the Docker database
├── WEB/            # Angular web application
└── MOBILE/         # Mobile application
    └── release/    # Built APK file
```

---

## Getting Started

### Prerequisites

- Java 17+
- Maven
- Node.js & npm
- Angular CLI
- Docker

---

### 1. Database

Start the Docker database container by running the provided script:

```bash
cd BACKEND
chmod +x db.sh
./db.sh
```

---

### 2. Backend

Navigate to the `BACKEND` folder and run the Spring Boot application:

```bash
cd BACKEND
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080` by default.

---

### 3. Web Application

Navigate to the `WEB` folder, install dependencies, and start the Angular dev server:

```bash
cd WEB
npm install
ng serve
```

The web app will be available at `http://localhost:4200`.

---

### 4. Mobile Application

A pre-built APK is available for Android devices:

```
MOBILE/release/
```

Transfer the APK to your Android device and install it. You may need to enable **Install from unknown sources** in your device settings.

---
