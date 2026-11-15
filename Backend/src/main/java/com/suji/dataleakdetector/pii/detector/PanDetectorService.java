package com.suji.dataleakdetector.pii.detector;

import com.suji.dataleakdetector.pii.model.PiiFinding;
import com.suji.dataleakdetector.pii.model.PiiType;
import com.suji.dataleakdetector.pii.model.RiskLevel;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class PanDetectorService {
  // Indian PAN format: 5 letters, 4 digits, 1 letter.
  // The 4th letter must represent a valid entity type:
  // P (Person), C (Company), H (HUF), F (Firm), A (AOP), T (Trust), B (BOI), L (Local), J (Artificial), G (Government).
  // Pattern.CASE_INSENSITIVE allows detecting lowercase entries.
  private static final Pattern PAN = Pattern.compile(
      "\\b[A-Z]{3}[PCHFATBLJG][A-Z]\\d{4}[A-Z]\\b",
      Pattern.CASE_INSENSITIVE);

  public List<PiiFinding> detect(String text) {
    return PiiRegexUtil.findAll(PAN, text, PiiType.PAN, RiskLevel.HIGH, null);
  }
}
