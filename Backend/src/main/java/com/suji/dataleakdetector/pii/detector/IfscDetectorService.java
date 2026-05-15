package com.suji.dataleakdetector.pii.detector;

import com.suji.dataleakdetector.pii.model.PiiFinding;
import com.suji.dataleakdetector.pii.model.PiiType;
import com.suji.dataleakdetector.pii.model.RiskLevel;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class IfscDetectorService {
  // IFSC: AAAA0XXXXXX
  private static final Pattern IFSC = Pattern.compile("\\b[A-Z]{4}0[A-Z0-9]{6}\\b");

  public List<PiiFinding> detect(String text) {
    return PiiRegexUtil.findAll(
        IFSC,
        text == null ? null : text.toUpperCase(),
        PiiType.IFSC,
        RiskLevel.MEDIUM,
        null);
  }
}

