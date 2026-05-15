package com.suji.dataleakdetector.pii.detector;

import com.suji.dataleakdetector.pii.model.PiiFinding;
import com.suji.dataleakdetector.pii.model.PiiType;
import com.suji.dataleakdetector.pii.model.RiskLevel;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class PassportDetectorService {
  // Example: A1234567
  private static final Pattern PASSPORT = Pattern.compile("\\b[A-Z]\\d{7}\\b");

  public List<PiiFinding> detect(String text) {
    return PiiRegexUtil.findAll(PASSPORT, text, PiiType.PASSPORT, RiskLevel.HIGH, null);
  }
}

