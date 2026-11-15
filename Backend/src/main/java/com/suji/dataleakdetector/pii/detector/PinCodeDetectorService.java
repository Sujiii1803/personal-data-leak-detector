package com.suji.dataleakdetector.pii.detector;

import com.suji.dataleakdetector.pii.model.PiiFinding;
import com.suji.dataleakdetector.pii.model.PiiType;
import com.suji.dataleakdetector.pii.model.RiskLevel;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class PinCodeDetectorService {
  // Indian PIN codes are 6 digits and start with 1-9 (never 0).
  private static final Pattern PIN = Pattern.compile("(?<!\\d)[1-9]\\d{5}(?!\\d)");

  // Context words required to prevent flagging lone 6-digit OTPs or order IDs.
  private static final Pattern PIN_CONTEXT = Pattern.compile(
      "(?i)(?:\\b(pin|pincode|postal|address|delhi|mumbai|bangalore|bengaluru|chennai|kolkata|hyderabad|pune|ahmedabad|jaipur|karnataka|maharashtra|kerala|gujarat)\\b|\\bpin\\s*code\\b|\\btamil\\s*nadu\\b)");

  // Search window size around the matched digits.
  private static final int CONTEXT_WINDOW = 50;

  public List<PiiFinding> detect(String text) {
    return PiiRegexUtil.findAllWithPosition(
        PIN,
        text,
        PiiType.PIN_CODE,
        RiskLevel.LOW,
        (match, start, end) -> hasPostalContext(text, start, end));
  }

  // Checks if postal or address context words appear nearby.
  private boolean hasPostalContext(String text, int start, int end) {
    return PiiRegexUtil.hasContextNearby(text, start, end, PIN_CONTEXT, CONTEXT_WINDOW);
  }
}
