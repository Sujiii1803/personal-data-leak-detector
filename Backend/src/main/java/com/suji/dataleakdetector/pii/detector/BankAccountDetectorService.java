package com.suji.dataleakdetector.pii.detector;

import com.suji.dataleakdetector.pii.model.PiiFinding;
import com.suji.dataleakdetector.pii.model.PiiType;
import com.suji.dataleakdetector.pii.model.RiskLevel;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class BankAccountDetectorService {
  // Matches 9 to 18 digits.
  private static final Pattern ACCOUNT = Pattern.compile("(?<!\\d)\\d{9,18}(?!\\d)");

  // Context words required to avoid flagging random timestamps or order IDs.
  private static final Pattern BANK_CONTEXT =
      Pattern.compile("(?i)(?:\\b(account|acct|ifsc|bank)\\b|\\ba/c\\b|\\bacc\\s*(?:no|num|number)?\\b)");

  // Search window around the matched digits.
  private static final int CONTEXT_WINDOW = 50;

  public List<PiiFinding> detect(String text) {
    return PiiRegexUtil.findAllWithPosition(
        ACCOUNT,
        text,
        PiiType.BANK_ACCOUNT,
        RiskLevel.HIGH,
        (match, start, end) -> isValidBankAccount(match) && hasBankingContext(text, start, end));
  }

  // Checks that the number is not a phone number or sequential/repetitive dummy digits.
  private boolean isValidBankAccount(String number) {
    // Exclude potential Indian phone numbers (10 digits starting with 6-9)
    if (number.length() == 10) {
      char firstDigit = number.charAt(0);
      if (firstDigit >= '6' && firstDigit <= '9') {
        return false;
      }
    }

    // Ignore repetitive numbers like 000000000, 111111111
    if (number.chars().distinct().count() <= 1) {
      return false;
    }

    // Ignore sequential numbers like 1234567890 or 9876543210
    if (isSequential(number)) {
      return false;
    }

    return true;
  }

  private boolean isSequential(String number) {
    if ("01234567890123456789".contains(number) || "98765432109876543210".contains(number)) {
      return true;
    }
    boolean inc = true;
    boolean dec = true;
    for (int i = 1; i < number.length(); i++) {
      int diff = number.charAt(i) - number.charAt(i - 1);
      if (diff != 1) inc = false;
      if (diff != -1) dec = false;
    }
    return inc || dec;
  }

  // Checks if banking keywords appear nearby the matched number.
  private boolean hasBankingContext(String text, int start, int end) {
    return PiiRegexUtil.hasContextNearby(text, start, end, BANK_CONTEXT, CONTEXT_WINDOW);
  }
}
