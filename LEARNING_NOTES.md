# Learning Notes: Personal Data Leak Detector

A beginner-friendly guide to understanding how this full-stack application works under the hood, along with key software engineering interview concepts.

---

## Project Overview

### What the Application Does
- **Purpose**: It acts as a safety scanner for documents and text, finding sensitive personal details before they are accidentally shared or leaked.
- **Inputs**: It accepts direct text, text files (.txt), office documents (.docx), PDFs (.pdf), and pictures (.png, .jpg) containing text.
- **Outputs**: It provides a sanitized version of the input where private data is replaced with black block characters (`█`), along with a detailed list of what was found.

### Frontend Architecture (React + Vite)
- **Role**: The frontend provides a visual web dashboard where users can register, log in, paste text, upload files, and view previous scan reports.
- **API Communication**: It uses Axios to send HTTP requests to the Spring Boot backend, automatically attaching the user's security token to every request.
- **Routing & Protection**: React Router organizes the pages and uses a wrapper to block unauthenticated visitors from accessing internal dashboard screens.

### User Authentication & Security (JWT)
- **Sign In Flow**: When a user registers or logs in with an email and password, the backend verifies their credentials and issues a JSON Web Token (JWT).
- **Stateless Verification**: The backend does not store active session records in memory; it verifies the JWT signature on each incoming request to identify the user.
- **Password Safety**: User passwords are encrypted using BCrypt hashing before being saved, ensuring plain passwords are never stored in the database.

### Text Extraction & OCR
- **Document Parsing**: When a PDF or Word document arrives, libraries (Apache PDFBox and Apache POI) extract the raw text content from the file structure.
- **Optical Character Recognition (OCR)**: For images, the app uses Tess4J (a Java wrapper for the Tesseract engine) to convert visual characters into computer text.
- **Unified Pipeline**: Once text is extracted from any supported file format, it passes through the exact same scanning steps as standard typed text.

### PII Detection Engine
- **Pattern Matching**: Specialized detector services search the text using regular expressions designed specifically for Indian identifiers (like PAN cards and phone numbers).
- **Algorithmic Validation**: To eliminate false alarms, detectors use checksum algorithms—such as the Verhoeff algorithm for 12-digit Aadhaar cards and Luhn for credit cards.
- **Risk Categorization**: Every detected item is labeled with a risk tier (High for Aadhaar/Cards/Bank Accounts, Medium for Email/Phone/DOB, Low for URLs/IPs/PIN codes).

### Overlap & Priority Resolution
- **The Challenge**: A single sequence of digits might match multiple patterns simultaneously (for example, a 12-digit number could look like both an Aadhaar card and a bank account).
- **Priority Sorting**: The system assigns higher priority weights to more sensitive types (Credit Card = 100, Aadhaar = 90, PAN = 80, Bank Account = 30).
- **Deduplication**: When two matches occupy overlapping positions in the text, the detector with the highest priority and longest match is kept, discarding the duplicate.

### Redaction Strategy
- **Character Replacement**: Detected sensitive characters are swapped with solid block characters (`█`), hiding the actual confidential information.
- **Offset Preservation**: The redaction process sorts findings from the end of the text to the beginning, preventing character offset shifts from corrupting earlier replacements.
- **Safe Sharing**: The resulting redacted text retains the sentence structure and context, making it safe to export or share publicly.

### Database Storage (MySQL + Spring Data JPA)
- **Scan Records**: The backend records metadata for each scan, including timestamps, input character counts, risk summaries, and the redacted output.
- **User Ownership**: Every scan record is linked to the specific user account ID that created it, ensuring users can only view or delete their own scan history.
- **History Retention**: The history endpoint retrieves the most recent 50 scans for quick review on the dashboard.

---

## 5 Architecture Interview Questions & Answers

### 1. Why use JWT (JSON Web Tokens) instead of traditional server-side sessions?
- **Answer**: Traditional sessions require the server to store session IDs in memory or a database, which consumes server memory and makes horizontal scaling harder across multiple instances. JWTs are stateless and self-contained; the user's identity is cryptographically signed inside the token, allowing any server instance to authenticate requests instantly without a database lookup.

### 2. Why does the system combine regular expressions with checksum algorithms (like Verhoeff and Luhn)?
- **Answer**: Regular expressions only check the format and shape of a string (such as verifying that a number has 12 digits), which leads to many false positives on random numbers. Checksum algorithms mathematically verify the internal check digits embedded in legitimate Aadhaar and credit card numbers, ensuring only authentic identifiers are flagged.

### 3. How does the application handle overlapping PII detections?
- **Answer**: The application uses a greedy conflict resolution strategy based on predefined priority rankings (e.g., Credit Card > Aadhaar > Bank Account) and match length. When two detectors claim the same character indices, the engine keeps the higher-priority detection and discards the overlapping lower-priority match, preventing double-counting and corrupted redactions.

### 4. Why must redaction be applied in reverse index order (from highest start index to lowest)?
- **Answer**: If you modify characters at the beginning of a string, all character positions downstream change if the replacement text has a different length than the original match. By replacing matched spans in reverse order (starting from the end of the text moving toward the front), earlier string offsets remain completely accurate and unchanged.

### 5. How does the backend process different file formats (PDF, Word, Images) without duplicating detection logic?
- **Answer**: The architecture uses an adapter pattern inside `FileParsingService` to separate file extraction from PII detection. File parsing services convert any file (via PDFBox, POI, or Tesseract OCR) into a plain Java `String` first, allowing the core `PiiDetectionService` to remain completely agnostic of the original file format.
