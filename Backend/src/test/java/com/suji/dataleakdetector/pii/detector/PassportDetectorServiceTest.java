package com.suji.dataleakdetector.pii.detector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.suji.dataleakdetector.pii.model.PiiFinding;
import com.suji.dataleakdetector.pii.model.PiiType;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PassportDetectorServiceTest {

  private PassportDetectorService detector;

  @BeforeEach
  void setUp() {
    detector = new PassportDetectorService();
  }

  // --- True Positives (At least 3) ---

  @Test
  void testDetectsPassportUppercase() {
    String text = "Passport No: A1234567 verified at airport.";
    List<PiiFinding> findings = detector.detect(text);

    assertEquals(1, findings.size());
    assertEquals("A1234567", findings.get(0).getMatchedValue());
    assertEquals(PiiType.PASSPORT, findings.get(0).getType());
  }

  @Test
  void testDetectsPassportLowercase() {
    String text = "Immigration clearance for passport z9876543.";
    List<PiiFinding> findings = detector.detect(text);

    assertEquals(1, findings.size());
    assertEquals("z9876543", findings.get(0).getMatchedValue());
  }

  @Test
  void testDetectsPassportWithLetterK() {
    String text = "Document details: K5544332 registered.";
    List<PiiFinding> findings = detector.detect(text);

    assertEquals(1, findings.size());
    assertEquals("K5544332", findings.get(0).getMatchedValue());
  }

  // --- False Positives (At least 3) ---

  @Test
  void testIgnoresAllDigitsWithoutLetter() {
    String text = "Order tracking code 12345678.";
    List<PiiFinding> findings = detector.detect(text);

    assertTrue(findings.isEmpty(), "Passports require 1 letter followed by 7 digits.");
  }

  @Test
  void testIgnoresMultipleLeadingLetters() {
    String text = "Vehicle registration prefix AB123456.";
    List<PiiFinding> findings = detector.detect(text);

    assertTrue(findings.isEmpty(), "Passports cannot have two leading letters.");
  }

  @Test
  void testIgnoresShortDigitSequence() {
    String text = "Short reference code A123456.";
    List<PiiFinding> findings = detector.detect(text);

    assertTrue(findings.isEmpty(), "Passport must have exactly 7 digits after the letter.");
  }
}
