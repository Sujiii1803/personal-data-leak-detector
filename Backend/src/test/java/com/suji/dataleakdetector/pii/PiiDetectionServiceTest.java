package com.suji.dataleakdetector.pii;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.suji.dataleakdetector.pii.detector.*;
import com.suji.dataleakdetector.pii.model.PiiScanResult;
import com.suji.dataleakdetector.pii.model.PiiType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PiiDetectionServiceTest {

  private PiiDetectionService piiDetectionService;

  @BeforeEach
  void setUp() {
    piiDetectionService = new PiiDetectionService(
        new EmailDetectorService(),
        new PhoneDetectorService(),
        new AadhaarDetectorService(),
        new PanDetectorService(),
        new PassportDetectorService(),
        new CreditCardDetectorService(),
        new BankAccountDetectorService(),
        new IfscDetectorService(),
        new IpAddressDetectorService(),
        new DobDetectorService(),
        new UrlDetectorService(),
        new PinCodeDetectorService()
    );
  }

  @Test
  void testPhoneNumberNotDetectedAsBankAccount() {
    String text = "Contact me at 9876543210.";
    PiiScanResult result = piiDetectionService.scanText(text);

    assertEquals(1, result.getTotalFindings());
    assertEquals(PiiType.PHONE, result.getFindings().get(0).getType());
  }

  @Test
  void testRandom12DigitNotDetectedAsAadhaar() {
    String text = "Account: 837492104561";
    // This is a random 12-digit number. It should fail Verhoeff check.
    // It should be detected as a BANK_ACCOUNT instead.
    PiiScanResult result = piiDetectionService.scanText(text);

    assertEquals(1, result.getTotalFindings());
    assertEquals(PiiType.BANK_ACCOUNT, result.getFindings().get(0).getType());
  }

  @Test
  void testValidAadhaarDetectedAndDeduplicated() {
    // 737834215280 is a sample valid Aadhaar number (passes Verhoeff).
    // Note: this is just a mathematically valid number according to Verhoeff algorithm.
    String text = "Aadhaar: 737834215280";
    PiiScanResult result = piiDetectionService.scanText(text);

    assertEquals(1, result.getTotalFindings());
    assertEquals(PiiType.AADHAAR, result.getFindings().get(0).getType());
  }

  @Test
  void testTrivialBankAccountNumbersIgnored() {
    String text = "Invalid: 0000000000 and 1234567890";
    PiiScanResult result = piiDetectionService.scanText(text);

    // 0000000000 is ignored by bank account and phone (distinct chars > 1)
    // 1234567890 is sequential, ignored by bank account. Phone ignores because it doesn't start with 6-9.
    assertEquals(0, result.getTotalFindings());
  }

  @Test
  void testOverlapResolution() {
    // A 12-digit number "737834215280" could match both AADHAAR and BANK_ACCOUNT.
    // Since AADHAAR has priority 90 and BANK_ACCOUNT 30, AADHAAR wins.
    String overlapText = "737834215280";
    PiiScanResult result = piiDetectionService.scanText(overlapText);
    
    assertEquals(1, result.getTotalFindings());
    assertEquals(PiiType.AADHAAR, result.getFindings().get(0).getType());
  }
}
