# Personal Data Leak Detector

AI-powered full-stack cybersecurity web application that detects, classifies, and redacts sensitive personal information from text, uploaded documents, and images using OCR and regex-based PII detection.

---

## Live Demo

### Frontend
https://personal-data-leak-detector.vercel.app

### Backend API
https://personal-data-leak-detector-backend.onrender.com

### Swagger Documentation
https://personal-data-leak-detector-backend.onrender.com/swagger-ui/index.html

---

## Features

- JWT Authentication & Authorization
- OCR-based Image Scanning
- Aadhaar/PAN/Passport Detection
- PDF/DOCX/TXT File Parsing
- Risk Classification Engine
- Automatic Sensitive Data Redaction
- Scan History Dashboard
- Protected APIs with Spring Security
- Responsive Cybersecurity UI
- Dockerized Backend Deployment
- Cloud Deployment with Vercel + Render

---

## Supported PII Detection

- Email Addresses
- Indian Phone Numbers
- Aadhaar Numbers
- PAN Numbers
- Passport Numbers
- Credit Card Numbers
- Bank Account Numbers
- IFSC Codes
- IP Addresses
- URLs
- PIN Codes
- Date of Birth (DOB)

---

## Tech Stack

### Frontend
- React JS
- Vite
- Tailwind CSS
- Axios
- React Router DOM

### Backend
- Spring Boot
- Java
- Spring Security
- JWT Authentication
- MySQL
- Swagger/OpenAPI

### OCR & File Processing
- Tess4J (Tesseract OCR)
- Apache PDFBox
- Apache POI

### Deployment
- Frontend: Vercel
- Backend: Render
- Database: Railway MySQL
- Containerization: Docker

---

## Screenshots

### Landing Page
![Landing](screenshots/landing-page.png)

### Dashboard
![Dashboard](screenshots/dashboard.png)

### Scan Results
![Scan Results](screenshots/scan-results.png)

### OCR Scan
![OCR](screenshots/ocr-scan.png)

### Redacted Output
![Redacted](screenshots/redacted-output.png)

### Scan History
![History](screenshots/scan-history.png)

### Swagger API
![Swagger](screenshots/swagger-api.png)

---

## Architecture Overview

```text
Frontend (React + Vercel)
        ↓
Backend REST API (Spring Boot + Render)
        ↓
MySQL Database (Railway)