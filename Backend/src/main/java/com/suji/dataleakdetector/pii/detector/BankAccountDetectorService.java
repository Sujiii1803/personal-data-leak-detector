package com.suji.dataleakdetector.pii.detector;

import com.suji.dataleakdetector.pii.model.PiiFinding;
import com.suji.dataleakdetector.pii.model.PiiType;
import com.suji.dataleakdetector.pii.model.RiskLevel;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class BankAccountDetectorService {
  // Heuristic: 9-18 digit sequences (bank account numbers vary widely).
  private static final Pattern ACCOUNT = Pattern.compile("(?<!\\d)\\d{9,18}(?!\\d)");

  public List<PiiFinding> detect(String text) {
    return PiiRegexUtil.findAll(ACCOUNT, text, PiiType.BANK_ACCOUNT, RiskLevel.HIGH, this::isValidBankAccount);
  }

  private boolean isValidBankAccount(String number) {
    // Exclude potential Indian phone numbers
    if (number.length() == 10) {
      char firstDigit = number.charAt(0);
      if (firstDigit >= '6' && firstDigit <= '9') {
        return false; 
      }
    }
    // Ignore trivial repetitive numbers like 000000000, 111111111
    if (number.chars().distinct().count() <= 1) {
      return false;
    }
    // Ignore sequential numbers like 123456789
    if ("0123456789".contains(number) || "1234567890".contains(number) || "9876543210".contains(number)) {
      return false;
    }
    return true;
  }
}

