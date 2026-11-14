# Personal Data Leak Detector

A full-stack cybersecurity web application designed to detect, classify, and redact sensitive Indian Personally Identifiable Information (PII) from raw text, documents, and images.

---

## What the Project Does

- **Multi-Format Ingestion**: Scans unstructured raw text, documents (PDF, DOCX, TXT), and images (PNG, JPG, JPEG via OCR).
- **Comprehensive Indian PII Coverage**: Detects 12 sensitive data types including Aadhaar, PAN, Passport, Credit Cards, Bank Accounts, Phone Numbers, Emails, IFSC Codes, IP Addresses, URLs, Dates of Birth, and PIN Codes.
- **Algorithmic Validation**: Goes beyond regex matching by running mathematical checksums (Verhoeff algorithm for Aadhaar, Luhn algorithm for Credit Cards, calendar checks for DOB).
- **Overlap & Priority Resolution**: Resolves overlapping regex detections using a strict priority and length weighting system (e.g., distinguishing a 12-digit Aadhaar from a 12-digit bank account number).
- **Automated Redaction**: Masks detected sensitive data patterns using block characters (`█`) while preserving non-sensitive context.
- **User Dashboard & History**: Provides user authentication (JWT) and stores scan records, risk breakdowns, and redacted outputs.

---

## Tech Stack

### Backend
- **Language**: Java 17
- **Framework**: Spring Boot 3.5.3 (Spring Web, Spring Security, Spring Data JPA, Spring Validation)
- **Database**: MySQL (Hibernate ORM)
- **Authentication**: Stateless JSON Web Tokens (JJWT 0.12.6)
- **Document & Text Parsing**: Apache PDFBox 2.0.32 (PDF), Apache POI 5.4.1 (DOCX)
- **OCR Engine**: Tess4J 5.14.0 (Tesseract OCR wrapper)
- **Documentation**: Springdoc OpenAPI / Swagger UI 2.8.9

### Frontend
- **Framework**: React 19 + Vite 8
- **Styling**: Tailwind CSS v4
- **Routing**: React Router DOM v7
- **HTTP Client**: Axios
- **UI Components & Icons**: Lucide React, React Hot Toast

### Infrastructure & Deployment
- **Backend Hosting**: Render (Docker containerized)
- **Frontend Hosting**: Vercel
- **Database Hosting**: Railway MySQL

---

## Folder Structure

```text
personal-data-leak-detector/
├── Backend/                            # Spring Boot backend application
│   ├── src/main/java/com/suji/dataleakdetector/
│   │   ├── config/                     # WebMvc CORS and Swagger OpenAPI configuration
│   │   ├── controller/                 # REST controllers handling HTTP requests
│   │   ├── dto/                        # Request and response data transfer objects
│   │   ├── entity/                     # JPA database entities (User, ScanRecord)
│   │   ├── exception/                  # Global error handling and custom API exceptions
│   │   ├── pii/                        # Core PII detection orchestration & redaction
│   │   │   ├── detector/               # Individual PII detector services & regex utilities
│   │   │   └── model/                  # Domain models (findings, risk levels, PII types)
│   │   ├── repository/                 # Spring Data JPA repositories for DB access
│   │   ├── security/                   # Spring Security filter chain & JWT validation filter
│   │   ├── service/                    # Business services (Auth, Scan, FileParsing, ImageOCR)
│   │   └── util/                       # JWT generation and parsing helpers
│   ├── src/main/resources/             # application.properties configuration
│   ├── tessdata/                       # Tesseract trained language models (eng.traineddata)
│   ├── Dockerfile                      # Backend container configuration
│   └── pom.xml                         # Maven dependencies and build settings
├── Frontend/                           # React + Vite frontend application
│   ├── src/
│   │   ├── app/
│   │   │   ├── api/                    # Axios client instance and API endpoint callers
│   │   │   ├── auth/                   # React AuthContext for state & token persistence
│   │   │   ├── providers/              # Root application context providers
│   │   │   ├── routing/                # React Router setup & protected route guards
│   │   │   └── utils/                  # Text formatters and date utilities
│   │   ├── pages/                      # Application view pages (Dashboard, Scan, History, Auth)
│   │   ├── ui/
│   │   │   ├── components/             # Reusable UI controls (Buttons, Cards, Inputs, Spinners)
│   │   │   └── layout/                 # Layout wrappers (Navigation bar, AppShell)
│   │   ├── App.jsx                     # Top-level React component
│   │   └── main.jsx                    # React DOM entry point
│   ├── package.json                    # Frontend dependencies and npm scripts
│   └── vite.config.js                  # Vite build and server settings
├── screenshots/                        # Application preview screenshots
├── LEARNING_NOTES.md                   # Architectural explanations and interview prep
└── README.md                           # Project documentation
```

### Backend Package Breakdown
- `config`: Defines global Cross-Origin Resource Sharing (CORS) rules and Swagger/OpenAPI documentation configurations.
- `controller`: Exposes REST endpoints for user authentication (`AuthController`) and scanning operations (`ScanController`).
- `dto`: Encapsulates JSON request payloads (`LoginRequest`, `ScanRequest`) and structured responses (`ScanResultResponse`).
- `entity`: Represents database tables (`User` for registered accounts, `ScanRecord` for scan audit logs).
- `exception`: Intercepts runtime errors and validation failures, returning structured `ApiError` responses.
- `pii`: Coordinates the detection pipeline (`PiiDetectionService`) and character replacement (`RedactionService`).
- `pii/detector`: Contains 12 specialized detector classes for each PII type, using regex matching and checksum algorithms.
- `pii/model`: Holds data classes and enums (`PiiType`, `RiskLevel`, `PiiFinding`, `PiiScanResult`).
- `repository`: Bridges entity models to MySQL via Spring Data JPA (`UserRepository`, `ScanRecordRepository`).
- `security`: Configures stateless HTTP security, BCrypt password hashing, and token extraction filters.
- `service`: Contains core business logic for authentication, document parsing (PDF/DOCX), OCR, and scan history.
- `util`: Utility logic including `JwtUtil` for signing, parsing, and verifying JWT tokens.

### Frontend Package Breakdown
- `app/api`: Configures Axios interceptors to attach `Authorization: Bearer <token>` and dispatch logout on 401.
- `app/auth`: Maintains authentication state, storing and clearing tokens in `localStorage`.
- `app/routing`: Protects private routes (`/app`, `/app/history`, `/app/scans/:id`) and redirects unauthenticated users.
- `pages`: Implements screens for landing, login, registration, scanning dashboard, scan detail, and scan history.
- `ui/components`: Houses reusable atomic styling primitives (Badge, Button, Card, Fields, Spinner).
- `ui/layout`: Houses `AppLayout` with navigation headers and logout actions.

---

## Request Flow: File Upload to Response

```text
User uploads file in React UI
           │
           ▼
[POST /api/scan/file (Multipart)]
           │
           ▼
1. JwtAuthenticationFilter
   └── Validates Bearer token from Authorization header and sets UserDetails in SecurityContext.
           │
           ▼
2. ScanController & FileParsingService
   └── Examines file extension:
       ├── .txt  -> Reads UTF-8 bytes directly.
       ├── .pdf  -> Extracts text using Apache PDFBox (PDFTextStripper).
       ├── .docx -> Extracts text using Apache POI (XWPFWordExtractor).
       └── .png/.jpg/.jpeg -> Passes file to ImageOcrService (Tess4J/Tesseract OCR).
           │
           ▼
3. PiiDetectionService
   └── Runs extracted text through all 12 detectors:
       ├── Regex matching runs for each pattern.
       ├── Algorithmic validation runs (Verhoeff for Aadhaar, Luhn for Cards, date ranges for DOB).
       ├── Overlaps resolved by priority sorting (Credit Card > Aadhaar > PAN > Passport > ...).
       └── Risk counts computed (High, Medium, Low).
           │
           ▼
4. RedactionService
   └── Sorts matched findings in reverse offset order.
   └── Replaces sensitive character spans with '█' characters.
           │
           ▼
5. ScanService & ScanRecordRepository
   └── Saves new ScanRecord in MySQL database (user reference, metadata, redacted text, findings JSON).
           │
           ▼
6. ScanResultResponse returned to Frontend
   └── React displays risk summary score, redacted text preview, and granular finding chips.
```

---

## API Endpoints

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/auth/register` | Public | Register a new user account with email and password. |
| `POST` | `/api/auth/login` | Public | Authenticate credentials and receive a JWT Bearer token. |
| `POST` | `/api/scan/text` | Authenticated | Scan a raw JSON text string for PII and return redacted output. |
| `POST` | `/api/scan/file` | Authenticated | Upload a document or image file (`multipart/form-data`) to extract, scan, and redact. |
| `GET` | `/api/scan/history` | Authenticated | Retrieve the latest 50 scan summaries for the logged-in user. |
| `GET` | `/api/scan/{id}` | Authenticated | Retrieve full scan details including redacted text and individual findings by ID. |
| `DELETE` | `/api/scan/{id}` | Authenticated | Delete a specific scan record belonging to the authenticated user. |
| `GET` | `/swagger-ui/**` | Public | Interactive OpenAPI documentation and API testing interface. |
| `GET` | `/v3/api-docs/**` | Public | Raw OpenAPI v3 JSON specification. |

---

## How to Run Locally

### Prerequisites
- **Java**: JDK 17 or higher installed.
- **Maven**: Version 3.8+ installed (or use the Maven wrapper if available).
- **Node.js**: Version 18+ and npm installed.
- **MySQL**: MySQL server running locally on port 3306.

### 1. Database Setup
Create a local MySQL database for the application:
```sql
CREATE DATABASE IF NOT EXISTS dataleakdetector;
```

### 2. Backend Setup
Set the required environment variables in your terminal:

**Windows (PowerShell):**
```powershell
$env:SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/dataleakdetector?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
$env:SPRING_DATASOURCE_USERNAME="root"
$env:SPRING_DATASOURCE_PASSWORD="your_mysql_password"
$env:JWT_SECRET="your_very_secret_key_minimum_32_characters_long!"
```

**macOS / Linux (Bash):**
```bash
export SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/dataleakdetector?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
export SPRING_DATASOURCE_USERNAME="root"
export SPRING_DATASOURCE_PASSWORD="your_mysql_password"
export JWT_SECRET="your_very_secret_key_minimum_32_characters_long!"
```

Navigate to the `Backend` directory and start the Spring Boot server:
```bash
cd Backend
mvn clean spring-boot:run
```
The backend will start at `http://localhost:8080`.
Open API Swagger docs will be available at `http://localhost:8080/swagger-ui/index.html`.

### 3. Frontend Setup
Open a new terminal window, navigate to the `Frontend` directory, install dependencies, and start Vite:
```bash
cd Frontend
npm install
npm run dev
```

By default, Vite runs at `http://localhost:5173`.
To configure the backend URL explicitly, create or verify `Frontend/.env`:
```env
VITE_API_BASE_URL=http://localhost:8080
```

### 4. Verification
1. Visit `http://localhost:5173` in your browser.
2. Click **Register** to create a test account.
3. Sign in to view the Dashboard.
4. Paste a sample string with an email or test phone number, or upload a test document to test scanning and redaction.
