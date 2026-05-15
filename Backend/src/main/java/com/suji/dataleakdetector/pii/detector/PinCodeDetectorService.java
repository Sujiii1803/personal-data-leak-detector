package com.suji.dataleakdetector.pii.detector;

import com.suji.dataleakdetector.pii.model.PiiFinding;
import com.suji.dataleakdetector.pii.model.PiiType;
import com.suji.dataleakdetector.pii.model.RiskLevel;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class PinCodeDetectorService {
  // Indian PIN code (6 digits)
  private static final Pattern PIN = Pattern.compile("(?<!\\d)\\d{6}(?!\\d)");

  public List<PiiFinding> detect(String text) {
    return PiiRegexUtil.findAll(PIN, text, PiiType.PIN_CODE, RiskLevel.LOW, null);
  }
}

