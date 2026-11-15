package com.suji.dataleakdetector.pii.detector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.suji.dataleakdetector.pii.model.PiiFinding;
import com.suji.dataleakdetector.pii.model.PiiType;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DobDetectorServiceTest {

  private DobDetectorService detector;

  @BeforeEach
  void setUp() {
    detector = new DobDetectorService();
  }

  // --- True Positives (At least 3) ---

  @Test
  void testDetectsDobWithSlashFormat() {
    String text = "Candidate details - DOB: 15/08/1995 for verification.";
    List<PiiFinding> findings = detector.detect(text);

    assertEquals(1, findings.size());
    assertEquals("15/08/1995", findings.get(0).getMatchedValue());
    assertEquals(PiiType.DOB, findings.get(0).getType());
  }

  @Test
  void testDetectsDobWithDashFormat() {
    String text = "Applicant date of birth: 24-11-2001 in records.";
    List<PiiFinding> findings = detector.detect(text);

    assertEquals(1, findings.size());
    assertEquals("24-11-2001", findings.get(0).getMatchedValue());
  }

  @Test
  void testDetectsDobWithDotFormatAndBornKeyword() {
    String text = "Patient was born on 05.10.1988 in hospital.";
    List<PiiFinding> findings = detector.detect(text);

    assertEquals(1, findings.size());
    assertEquals("05.10.1988", findings.get(0).getMatchedValue());
  }

  // --- False Positives (At least 3) ---

  @Test
  void testIgnoresCalendarDateWithoutBirthContext() {
    String text = "Team meeting scheduled on 15/08/2023 at 10 AM.";
    List<PiiFinding> findings = detector.detect(text);

    assertTrue(findings.isEmpty(), "General calendar dates without birth context should not be flagged.");
  }

  @Test
  void testIgnoresImpossibleCalendarDate() {
    String text = "DOB: 31/02/2000 entered by user.";
    List<PiiFinding> findings = detector.detect(text);

    assertTrue(findings.isEmpty(), "February 31 is an invalid date and must be rejected.");
  }

  @Test
  void testIgnoresFutureDate() {
    String text = "Date of birth: 15/08/2099";
    List<PiiFinding> findings = detector.detect(text);

    assertTrue(findings.isEmpty(), "Future dates cannot be a valid date of birth.");
  }
}
