package com.suji.dataleakdetector.pii.detector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.suji.dataleakdetector.pii.model.PiiFinding;
import com.suji.dataleakdetector.pii.model.PiiType;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PanDetectorServiceTest {

  private PanDetectorService detector;

  @BeforeEach
  void setUp() {
    detector = new PanDetectorService();
  }

  // --- True Positives (At least 3) ---

  @Test
  void testDetectsIndividualPanUppercase() {
    String text = "Taxpayer PAN: ABCPE1234F submitted.";
    List<PiiFinding> findings = detector.detect(text);

    assertEquals(1, findings.size());
    assertEquals("ABCPE1234F", findings.get(0).getMatchedValue());
    assertEquals(PiiType.PAN, findings.get(0).getType());
  }

  @Test
  void testDetectsCompanyPanLowercase() {
    String text = "Corporate tax filing under pan xyzcd5678g.";
    List<PiiFinding> findings = detector.detect(text);

    assertEquals(1, findings.size());
    assertEquals("xyzcd5678g", findings.get(0).getMatchedValue());
  }

  @Test
  void testDetectsTrustPan() {
    String text = "Charity trust registered under MNOTK9999Z.";
    List<PiiFinding> findings = detector.detect(text);

    assertEquals(1, findings.size());
    assertEquals("MNOTK9999Z", findings.get(0).getMatchedValue());
  }

  // --- False Positives (At least 3) ---

  @Test
  void testIgnoresInvalidFourthEntityLetter() {
    String text = "Invalid card number ABCZE1234F.";
    List<PiiFinding> findings = detector.detect(text);

    assertTrue(findings.isEmpty(), "4th character 'Z' is not a valid Indian tax entity type.");
  }

  @Test
  void testIgnoresInvalidDigitCount() {
    String text = "Code ABCPE123F has only 3 digits.";
    List<PiiFinding> findings = detector.detect(text);

    assertTrue(findings.isEmpty(), "PAN must contain exactly 4 digits.");
  }

  @Test
  void testIgnoresRandomAlphanumericWord() {
    String text = "Reference code ABCDEF12345.";
    List<PiiFinding> findings = detector.detect(text);

    assertTrue(findings.isEmpty(), "Random alphanumeric strings should not match PAN structure.");
  }
}
