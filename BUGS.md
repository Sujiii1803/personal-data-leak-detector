# Bug Catalog: Personal Data Leak Detector

This document catalogs security vulnerabilities, accuracy deficiencies, deployment limitations, and architectural bugs identified during the codebase review.

---

## Summary Table

| ID | Severity | Area | Title | Status |
|---|---|---|---|---|
| BUG-01 | Critical | Data Storage / Privacy | Plaintext storage of raw PII and matched values in database | Confirmed |
| BUG-02 | High | Frontend / Privacy | Frontend UI displays raw unmasked sensitive values in table | Confirmed |
| BUG-03 | Medium | Security / Frontend | JWT authentication token stored in browser localStorage | Confirmed |
| BUG-04 | Critical | Dockerfile & Deployment | Dockerfile missing native Tesseract C++ libraries | Confirmed |
| BUG-05 | Medium | Dockerfile & Deployment | Inefficient single-stage Docker build running as root user | Confirmed |
| BUG-06 | High | Detectors / Accuracy | Overly broad PIN code regex flags any 6-digit sequence | Confirmed |
| BUG-07 | High | Detectors / Accuracy | Bank account detector flags any 9–18 digit number without context | Confirmed |
| BUG-08 | Medium | Detectors / Accuracy | Passport detector regex is case-sensitive | Confirmed |
| BUG-09 | Medium | Detectors / Accuracy | Passport detector matches any letter followed by 7 digits | Confirmed |
| BUG-10 | Medium | Detectors / Accuracy | PAN card detector regex is case-sensitive | Confirmed |
| BUG-11 | High | Detectors / Accuracy | Indian phone numbers with spaces or dashes fail detection | Confirmed |
| BUG-12 | Medium | Detectors / Accuracy | Date of birth detector flags any valid calendar date as DOB | Confirmed |
| BUG-13 | High | File Upload & Memory | Unbounded memory consumption during PDF and Word document extraction | Confirmed |
| BUG-14 | Low | File Upload | File type verification relies solely on file extension | Confirmed |
| BUG-15 | Low | Error Handling | Unhandled upload size limit exception and internal OCR errors leaked | Confirmed |
| BUG-16 | Low | File Upload & Storage | Long original filenames trigger unhandled SQL exception on save | Confirmed |
| BUG-17 | Low | Detectors / Accuracy | URL word boundary excludes trailing slashes from redaction | Confirmed |
| BUG-18 | Medium | Testing | Near-total lack of test coverage across services and controllers | Confirmed |
| BUG-19 | Low | File Upload / OCR | Image OCR temp file uses user filename in suffix | Suspected |
| BUG-20 | Low | Detectors / Accuracy | IP address detector does not support IPv6 addresses | Confirmed |

---

## Detailed Bug Reports

### BUG-01
- **Severity**: Critical
- **Area**: Data Storage / Privacy
- **File and lines**:
  - `Backend/src/main/java/com/suji/dataleakdetector/entity/ScanRecord.java`: lines 45–65
  - `Backend/src/main/java/com/suji/dataleakdetector/service/ScanService.java`: lines 108–115, 130–135
- **What is wrong**:
  The application saves the full unredacted text (`originalText`) and every matched sensitive value (`findingsJson`) in plaintext MySQL columns. If the database is compromised, all user-scanned private data (including Aadhaar, PAN, and card numbers) is fully exposed.
- **How to reproduce**:
  Scan any sample text containing an Aadhaar or PAN number, then run `SELECT original_text, findings_json FROM scan_records;` in MySQL to view the unencrypted secrets.
- **Suggested fix**:
  Do not persist `originalText` in the database, or encrypt it using AES-256 via JPA AttributeConverter. Mask `matchedValue` inside `findingsJson` before saving.
- **Which phase should fix it**: Phase 3 (Privacy)

---

### BUG-02
- **Severity**: High
- **Area**: Frontend / Privacy
- **File and lines**:
  - `Frontend/src/pages/ScanResultPage.jsx`: lines 190–192
  - `Backend/src/main/java/com/suji/dataleakdetector/service/ScanService.java`: lines 177–182
- **What is wrong**:
  While the result page shows a redacted preview, the findings table right above it explicitly renders the unredacted `matchedValue` in plain text. Anyone looking at the user's screen or screen-share can see the confidential data the user was attempting to protect.
- **How to reproduce**:
  Scan a sample containing an Aadhaar or card number and open the resulting scan page. The "Matched value" column shows the full plain text.
- **Suggested fix**:
  Mask the matched values in the table by default (e.g., `XXXX-XXXX-1234`) and provide an optional masked toggle button.
- **Which phase should fix it**: Phase 3 (Privacy)

---

### BUG-03
- **Severity**: Medium
- **Area**: Security / Frontend
- **File and lines**:
  - `Frontend/src/app/auth/AuthContext.jsx`: lines 30, 37
  - `Frontend/src/app/api/http.js`: lines 10–14
- **What is wrong**:
  The JWT authentication token is saved in browser `localStorage`. Any Cross-Site Scripting (XSS) vulnerability or malicious browser extension can read `localStorage` and steal the user's session token.
- **How to reproduce**:
  Log in to the dashboard, open the browser developer console, and type `localStorage.getItem('pdl_token')` to see the authentication token.
- **Suggested fix**:
  Migrate auth storage to `HttpOnly`, `Secure`, `SameSite` cookies, or store tokens strictly in application memory with a refresh token mechanism.
- **Which phase should fix it**: Other (Security)

---

### BUG-04
- **Severity**: Critical
- **Area**: Dockerfile & Deployment
- **File and lines**:
  - `Backend/Dockerfile`: lines 1–13
- **What is wrong**:
  The Dockerfile uses `eclipse-temurin:21-jdk` without installing the native Tesseract OCR packages. Tess4J requires native Linux C++ shared libraries (`libtesseract`), so running OCR inside the Linux container on Render will crash with an `UnsatisfiedLinkError`.
- **How to reproduce**:
  Build the container in a clean Linux environment and upload an image file to `POST /api/scan/file`.
- **Suggested fix**:
  Add `apt-get install -y tesseract-ocr libtesseract-dev` in the Dockerfile and configure the Tesseract data path to point to the trained language models.
- **Which phase should fix it**: Phase 4 (OCR + Docker)

---

### BUG-05
- **Severity**: Medium
- **Area**: Dockerfile & Deployment
- **File and lines**:
  - `Backend/Dockerfile`: lines 1–13
- **What is wrong**:
  The Dockerfile builds Maven directly inside the production runtime container, producing an unnecessarily bloated container image (>1GB) with source code and compilers. It also runs the Java process as the root user.
- **How to reproduce**:
  Run `docker run --rm <image_name> whoami`, which outputs `root`.
- **Suggested fix**:
  Implement a multi-stage Docker build separating the Maven build phase from a minimal JRE runtime image, and create a dedicated non-root user.
- **Which phase should fix it**: Phase 4 (OCR + Docker)

---

### BUG-06
- **Severity**: High
- **Area**: Detectors / Accuracy
- **File and lines**:
  - `Backend/src/main/java/com/suji/dataleakdetector/pii/detector/PinCodeDetectorService.java`: line 13
- **What is wrong**:
  The regex `(?<!\d)\d{6}(?!\d)` matches any 6-digit number without verifying postal circle rules. Indian PIN codes never start with `0` (valid range is 110001 to 855126). Random numbers like OTPs (`402918`), invoice IDs, or timestamps are falsely flagged and redacted.
- **How to reproduce**:
  Scan text containing "Your OTP is 492019". The number `492019` is flagged as an Indian PIN Code.
- **Suggested fix**:
  Update regex to `(?<!\d)[1-9]\d{5}(?!\d)` and optionally require context keywords ("PIN", "Postal", "Zip") or known postal zone range checks.
- **Which phase should fix it**: Phase 2 (Accuracy)

---

### BUG-07
- **Severity**: High
- **Area**: Detectors / Accuracy
- **File and lines**:
  - `Backend/src/main/java/com/suji/dataleakdetector/pii/detector/BankAccountDetectorService.java`: lines 13–35
- **What is wrong**:
  The detector treats any sequence of 9 to 18 digits as a Bank Account if it is not repetitive, sequential, or a 10-digit mobile number. Common numbers like Unix millisecond timestamps, order numbers, or tracking IDs are falsely classified as high-risk bank accounts.
- **How to reproduce**:
  Scan text containing "Timestamp: 1715694820123". It is incorrectly flagged as a HIGH-risk Bank Account number.
- **Suggested fix**:
  Require nearby banking context words (such as "account", "a/c", "acct", "savings", "current") or proximity to an IFSC code before flagging numbers.
- **Which phase should fix it**: Phase 2 (Accuracy)

---

### BUG-08
- **Severity**: Medium
- **Area**: Detectors / Accuracy
- **File and lines**:
  - `Backend/src/main/java/com/suji/dataleakdetector/pii/detector/PassportDetectorService.java`: line 13
- **What is wrong**:
  The regex `\b[A-Z]\d{7}\b` is case-sensitive and lacks `Pattern.CASE_INSENSITIVE`. Any passport number written in lowercase (e.g., `z1234567`) or extracted in lowercase by OCR is completely missed.
- **How to reproduce**:
  Scan text containing "Passport: a1234567". The scan returns 0 findings.
- **Suggested fix**:
  Add `Pattern.CASE_INSENSITIVE` to the pattern compilation or normalize input text to uppercase.
- **Which phase should fix it**: Phase 2 (Accuracy)

---

### BUG-09
- **Severity**: Medium
- **Area**: Detectors / Accuracy
- **File and lines**:
  - `Backend/src/main/java/com/suji/dataleakdetector/pii/detector/PassportDetectorService.java`: line 13
- **What is wrong**:
  The pattern matches any single letter followed by 7 digits (`[A-Z]\d{7}`). Common part numbers, order identifiers, or license codes (e.g., `A1029384`) are falsely classified as a HIGH-risk Indian passport.
- **How to reproduce**:
  Scan text containing "Part number: C1029384". It is reported as a HIGH-risk Passport leak.
- **Suggested fix**:
  Check for passport-related context keywords ("passport", "travel document") or validate valid Indian passport booklet series letters.
- **Which phase should fix it**: Phase 2 (Accuracy)

---

### BUG-10
- **Severity**: Medium
- **Area**: Detectors / Accuracy
- **File and lines**:
  - `Backend/src/main/java/com/suji/dataleakdetector/pii/detector/PanDetectorService.java`: line 12
- **What is wrong**:
  The regex `\b[A-Z]{5}\d{4}[A-Z]\b` lacks `Pattern.CASE_INSENSITIVE`. If a PAN number is entered in lowercase (e.g., `abcde1234f`) or scanned in lowercase from an image, it will not be detected.
- **How to reproduce**:
  Scan text containing "My PAN is abcde1234f". The system reports 0 findings.
- **Suggested fix**:
  Compile the regex pattern with `Pattern.CASE_INSENSITIVE`.
- **Which phase should fix it**: Phase 2 (Accuracy)

---

### BUG-11
- **Severity**: High
- **Area**: Detectors / Accuracy
- **File and lines**:
  - `Backend/src/main/java/com/suji/dataleakdetector/pii/detector/PhoneDetectorService.java`: line 14
- **What is wrong**:
  The regex `(?<!\d)(?:\+91[-\s]?)?(?:0)?([6-9]\d{9})(?!\d)` expects 10 uninterrupted digits. In India, phone numbers are almost universally written with spaces or dashes (e.g., `98765 43210` or `+91 98765-43210`), which causes the detector to miss them entirely.
- **How to reproduce**:
  Scan text containing "+91 98765 43210" or "98765-43210". The detector fails to find the phone number.
- **Suggested fix**:
  Allow spaces and hyphens between the digits in the regex pattern, e.g. `(?<!\d)(?:\+91[-\s]?)?(?:0)?([6-9](?:[\s-]?\d){9})(?!\d)`.
- **Which phase should fix it**: Phase 2 (Accuracy)

---

### BUG-12
- **Severity**: Medium
- **Area**: Detectors / Accuracy
- **File and lines**:
  - `Backend/src/main/java/com/suji/dataleakdetector/pii/detector/DobDetectorService.java`: lines 17–41
- **What is wrong**:
  Any valid calendar date between 1900 and today (e.g., `2024-01-15` or `12/05/2023`) is labeled as a Date of Birth (DOB). Document creation dates, news dates, or invoice dates are falsely flagged as personal birth dates.
- **How to reproduce**:
  Scan text containing "Invoice date: 15/08/2023". It is flagged as a Medium-risk DOB finding.
- **Suggested fix**:
  Require birth-related context keywords ("DOB", "birth", "born") or lower the severity when context is absent.
- **Which phase should fix it**: Phase 2 (Accuracy)

---

### BUG-13
- **Severity**: High
- **Area**: File Upload & Memory
- **File and lines**:
  - `Backend/src/main/java/com/suji/dataleakdetector/service/FileParsingService.java`: lines 44–62
  - `Backend/src/main/java/com/suji/dataleakdetector/service/ScanService.java`: line 93
- **What is wrong**:
  `FileParsingService` loads entire PDFs (`PDDocument.load`) and Word documents (`XWPFDocument`) into memory at once without limiting page count. The 200,000 character check occurs only after the entire text is in memory, creating Out-Of-Memory (OOM) risks on free cloud hosting tiers.
- **How to reproduce**:
  Upload a 9MB PDF containing hundreds of pages to `POST /api/scan/file`. Memory usage spikes heavily before any rejection.
- **Suggested fix**:
  Enforce a maximum page count limit (e.g., first 15–20 pages) during PDF extraction and stream-read text rather than loading everything into memory.
- **Which phase should fix it**: Phase 4 (OCR + Docker)

---

### BUG-14
- **Severity**: Low
- **Area**: File Upload
- **File and lines**:
  - `Backend/src/main/java/com/suji/dataleakdetector/service/FileParsingService.java`: lines 27–41
- **What is wrong**:
  The file type is determined solely by checking file extension (`filename.toLowerCase().endsWith(...)`). It does not verify the MIME type or inspect magic bytes (file signature), allowing arbitrary or disguised binaries to be processed.
- **How to reproduce**:
  Upload an executable binary renamed to `document.txt`. The server attempts to parse it as UTF-8 text.
- **Suggested fix**:
  Validate MIME types and magic byte headers before passing files to parser libraries.
- **Which phase should fix it**: Other (File Handling)

---

### BUG-15
- **Severity**: Low
- **Area**: Error Handling
- **File and lines**:
  - `Backend/src/main/java/com/suji/dataleakdetector/exception/GlobalExceptionHandler.java`: lines 99–121
  - `Backend/src/main/java/com/suji/dataleakdetector/service/ImageOcrService.java`: line 28
- **What is wrong**:
  `MaxUploadSizeExceededException` is not caught in `GlobalExceptionHandler`, so files $>10$ MB trigger an unhandled 500 error instead of HTTP 413 Payload Too Large. Additionally, `ImageOcrService` appends raw `e.getMessage()` into `BadRequestException`, leaking internal server file paths to the client.
- **How to reproduce**:
  Upload a file $>10$ MB to `/api/scan/file` and observe the HTTP 500 response.
- **Suggested fix**:
  Add an `@ExceptionHandler(MaxUploadSizeExceededException.class)` returning HTTP 413, and sanitize exception messages from OCR.
- **Which phase should fix it**: Other (Error Handling)

---

### BUG-16
- **Severity**: Low
- **Area**: File Upload & Storage
- **File and lines**:
  - `Backend/src/main/java/com/suji/dataleakdetector/entity/ScanRecord.java`: line 36
  - `Backend/src/main/java/com/suji/dataleakdetector/service/ScanService.java`: lines 54, 105
- **What is wrong**:
  The database column `originalFilename` in `ScanRecord` is defined with `@Column(length = 260)`. If a user uploads a file with a name exceeding 260 characters, MySQL throws a `DataTruncation` exception, resulting in an unexpected HTTP 500 error.
- **How to reproduce**:
  Upload a file with a name longer than 260 characters to `POST /api/scan/file`.
- **Suggested fix**:
  Truncate `originalFilename` to a safe limit (e.g., 255 characters) in `ScanService` before saving to the database.
- **Which phase should fix it**: Other (File Handling)

---

### BUG-17
- **Severity**: Low
- **Area**: Detectors / Accuracy
- **File and lines**:
  - `Backend/src/main/java/com/suji/dataleakdetector/pii/detector/UrlDetectorService.java`: line 13
- **What is wrong**:
  The regex `\b(?:https?://|www\.)[^\s<>"']+\b` ends with a word boundary `\b`. Because a trailing slash `/` is not a word character, the regex matches before the slash, leaving the trailing slash unredacted (e.g., `https://example.com/` becomes `███████████████████/`).
- **How to reproduce**:
  Scan text containing "Visit https://google.com/ today". The trailing slash remains visible.
- **Suggested fix**:
  Adjust URL boundary matching to consume trailing slashes while stripping trailing sentence punctuation like periods.
- **Which phase should fix it**: Phase 2 (Accuracy)

---

### BUG-18
- **Severity**: Medium
- **Area**: Testing
- **File and lines**:
  - `Backend/src/test/java/com/suji/dataleakdetector/pii/PiiDetectionServiceTest.java`: lines 1–84
- **What is wrong**:
  The repository has only 5 unit tests in total, located in a single test file. There is no automated test coverage for authentication, scan history, file parsing, OCR, redaction edge cases, or individual PII detectors.
- **How to reproduce**:
  Run `mvn test` in the `Backend` directory. Only 5 tests execute.
- **Suggested fix**:
  Add unit and integration tests for all individual detector services, `RedactionService`, `AuthService`, and Spring Security filter chains.
- **Which phase should fix it**: Other (Testing)

---

### BUG-19
- **Severity**: Low
- **Area**: File Upload / OCR
- **File and lines**:
  - `Backend/src/main/java/com/suji/dataleakdetector/service/ImageOcrService.java`: lines 18–19
- **What is wrong**:
  `File.createTempFile("ocr_", "_" + originalFilename)` passes user-controlled filenames as the suffix. If the filename includes path separators (`/` or `\`), it may trigger `IllegalArgumentException` on some platforms.
- **How to reproduce**:
  Code reading only (depends on Java runtime OS path validation during temp file creation).
- **Suggested fix**:
  Use a fixed temporary file suffix (e.g. `File.createTempFile("ocr_", ".tmp")`) and do not include user input in the temp file name.
- **Which phase should fix it**: Phase 4 (OCR + Docker)

---

### BUG-20
- **Severity**: Low
- **Area**: Detectors / Accuracy
- **File and lines**:
  - `Backend/src/main/java/com/suji/dataleakdetector/pii/detector/IpAddressDetectorService.java`: lines 12, 16–33
- **What is wrong**:
  `IpAddressDetectorService` only matches IPv4 addresses. IPv6 addresses (e.g., `2001:0db8:85a3:0000:0000:8a2e:0370:7334` or `fe80::1`) are completely ignored.
- **How to reproduce**:
  Scan text containing "Server IPv6: 2001:db8::1". It returns 0 findings.
- **Suggested fix**:
  Add an IPv6 regex pattern and validation logic alongside IPv4.
- **Which phase should fix it**: Phase 2 (Accuracy)
