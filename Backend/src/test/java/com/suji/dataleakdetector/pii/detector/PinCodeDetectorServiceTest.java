package com.suji.dataleakdetector.pii.detector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.suji.dataleakdetector.pii.model.PiiFinding;
import com.suji.dataleakdetector.pii.model.PiiType;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PinCodeDetectorServiceTest {

  private PinCodeDetectorService detector;

  @BeforeEach
  void setUp() {
    detector = new PinCodeDetectorService();
  }

  // --- True Positives (At least 3) ---

  @Test
  void testDetectsPinWithCityAndPinKeyword() {
    String text = "Delivery address: Flat 402, MG Road, Bangalore PIN: 560001.";
    List<PiiFinding> findings = detector.detect(text);

    assertEquals(1, findings.size());
    assertEquals("560001", findings.get(0).getMatchedValue());
    assertEquals(PiiType.PIN_CODE, findings.get(0).getType());
  }

  @Test
  void testDetectsPinWithPostalContext() {
    String text = "Please send courier to postal code 110001.";
    List<PiiFinding> findings = detector.detect(text);

    assertEquals(1, findings.size());
    assertEquals("110001", findings.get(0).getMatchedValue());
  }

  @Test
  void testDetectsPinWithPincodeAndMumbaiContext() {
    String text = "Branch office located in Mumbai, pincode 400001.";
    List<PiiFinding> findings = detector.detect(text);

    assertEquals(1, findings.size());
    assertEquals("400001", findings.get(0).getMatchedValue());
  }

  // --- False Positives (At least 3) ---

  @Test
  void testIgnoresLoneOtp() {
    String text = "Your one-time password (OTP) is 492019 for online verification.";
    List<PiiFinding> findings = detector.detect(text);

    assertTrue(findings.isEmpty(), "Lone 6-digit OTP should not be flagged as a PIN code.");
  }

  @Test
  void testIgnoresNumbersStartingWithZero() {
    String text = "Postal verification code: 012345.";
    List<PiiFinding> findings = detector.detect(text);

    assertTrue(findings.isEmpty(), "PIN codes starting with 0 are invalid in India.");
  }

  @Test
  void testIgnoresInvoiceNumberWithoutPostalContext() {
    String text = "Invoice #839201 was generated successfully today.";
    List<PiiFinding> findings = detector.detect(text);

    assertTrue(findings.isEmpty(), "Plain 6-digit invoice numbers should not be flagged.");
  }
}
