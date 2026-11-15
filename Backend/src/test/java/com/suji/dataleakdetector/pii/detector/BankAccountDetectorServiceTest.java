package com.suji.dataleakdetector.pii.detector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.suji.dataleakdetector.pii.model.PiiFinding;
import com.suji.dataleakdetector.pii.model.PiiType;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BankAccountDetectorServiceTest {

  private BankAccountDetectorService detector;

  @BeforeEach
  void setUp() {
    detector = new BankAccountDetectorService();
  }

  // --- True Positives (At least 3) ---

  @Test
  void testDetectsAccountNumberWithBankContext() {
    String text = "Please wire payment to bank account 50100234567891 today.";
    List<PiiFinding> findings = detector.detect(text);

    assertEquals(1, findings.size());
    assertEquals("50100234567891", findings.get(0).getMatchedValue());
    assertEquals(PiiType.BANK_ACCOUNT, findings.get(0).getType());
  }

  @Test
  void testDetectsAccountNumberWithAcContext() {
    String text = "Salary credited to A/C 9876543210123 successfully.";
    List<PiiFinding> findings = detector.detect(text);

    assertEquals(1, findings.size());
    assertEquals("9876543210123", findings.get(0).getMatchedValue());
  }

  @Test
  void testDetectsAccountNumberWithAccNoAndIfscContext() {
    String text = "Beneficiary acc no: 204918273645 IFSC: HDFC0001234";
    List<PiiFinding> findings = detector.detect(text);

    assertEquals(1, findings.size());
    assertEquals("204918273645", findings.get(0).getMatchedValue());
  }

  // --- False Positives (At least 3) ---

  @Test
  void testIgnoresTimestampWithoutBankingContext() {
    String text = "Log event recorded at timestamp 1715694820123 in server log.";
    List<PiiFinding> findings = detector.detect(text);

    assertTrue(findings.isEmpty(), "Timestamp should not be flagged as a bank account.");
  }

  @Test
  void testIgnoresOrderIdWithoutBankingContext() {
    String text = "Your package tracking order ID is 987654321012 for delivery.";
    List<PiiFinding> findings = detector.detect(text);

    assertTrue(findings.isEmpty(), "Order ID should not be flagged as a bank account.");
  }

  @Test
  void testIgnoresSequentialAndRepetitiveNumbers() {
    String text = "Test accounts 123456789012 and 111111111111 under account review.";
    List<PiiFinding> findings = detector.detect(text);

    assertTrue(findings.isEmpty(), "Sequential and repetitive numbers should be ignored.");
  }
}
