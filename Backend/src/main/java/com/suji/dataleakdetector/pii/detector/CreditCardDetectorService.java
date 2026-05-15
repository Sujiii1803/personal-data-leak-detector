package com.suji.dataleakdetector.pii.detector;

import com.suji.dataleakdetector.pii.model.PiiFinding;
import com.suji.dataleakdetector.pii.model.PiiType;
import com.suji.dataleakdetector.pii.model.RiskLevel;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class CreditCardDetectorService {
  // Matches 13-19 digits, allowing spaces/dashes.
  private static final Pattern CARD =
      Pattern.compile("(?<!\\d)(?:\\d[ -]?){13,19}(?!\\d)");

  public List<PiiFinding> detect(String text) {
    return PiiRegexUtil.findAll(
        CARD,
        text,
        PiiType.CREDIT_CARD,
        RiskLevel.HIGH,
        match -> {
          String digits = match.replaceAll("\\D", "");
          if (digits.length() < 13 || digits.length() > 19) return false;
          return isValidLuhn(digits);
        });
  }

  // Luhn checksum validation for credit cards.
  private boolean isValidLuhn(String digits) {
    int sum = 0;
    boolean alternate = false;
    for (int i = digits.length() - 1; i >= 0; i--) {
      int n = digits.charAt(i) - '0';
      if (n < 0 || n > 9) return false;
      if (alternate) {
        n *= 2;
        if (n > 9) n -= 9;
      }
      sum += n;
      alternate = !alternate;
    }
    return sum % 10 == 0;
  }
}

