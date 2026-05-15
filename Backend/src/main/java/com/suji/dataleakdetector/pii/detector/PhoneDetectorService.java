package com.suji.dataleakdetector.pii.detector;

import com.suji.dataleakdetector.pii.model.PiiFinding;
import com.suji.dataleakdetector.pii.model.PiiType;
import com.suji.dataleakdetector.pii.model.RiskLevel;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class PhoneDetectorService {
  // Supports +91 and 10-digit Indian mobile numbers (starting 6-9)
  private static final Pattern PHONE =
      Pattern.compile("(?<!\\d)(?:\\+91[-\\s]?)?(?:0)?([6-9]\\d{9})(?!\\d)");

  public List<PiiFinding> detect(String text) {
    return PiiRegexUtil.findAll(
        PHONE,
        text,
        PiiType.PHONE,
        RiskLevel.MEDIUM,
        match -> {
          String digits = match.replaceAll("\\D", "");
          if (digits.startsWith("91") && digits.length() == 12) digits = digits.substring(2);
          if (digits.startsWith("0") && digits.length() == 11) digits = digits.substring(1);
          return digits.length() == 10 && digits.charAt(0) >= '6' && digits.charAt(0) <= '9' && digits.chars().distinct().count() > 1;
        });
  }
}

