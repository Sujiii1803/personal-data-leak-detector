package com.suji.dataleakdetector.pii.detector;

import com.suji.dataleakdetector.pii.model.PiiFinding;
import com.suji.dataleakdetector.pii.model.PiiType;
import com.suji.dataleakdetector.pii.model.RiskLevel;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class PiiRegexUtil {
  private PiiRegexUtil() {}

  @FunctionalInterface
  interface PositionValidator {
    boolean isValid(String match, int startIndex, int endIndex);
  }

  static List<PiiFinding> findAll(
      Pattern pattern, String text, PiiType type, RiskLevel riskLevel, Predicate<String> validator) {
    return findAllWithPosition(
        pattern,
        text,
        type,
        riskLevel,
        (match, start, end) -> validator == null || validator.test(match));
  }

  static List<PiiFinding> findAllWithPosition(
      Pattern pattern, String text, PiiType type, RiskLevel riskLevel, PositionValidator validator) {
    List<PiiFinding> out = new ArrayList<>();
    if (text == null || text.isBlank()) return out;

    Matcher matcher = pattern.matcher(text);
    while (matcher.find()) {
      String match = matcher.group();
      if (match == null || match.isBlank()) continue;
      if (validator != null && !validator.isValid(match, matcher.start(), matcher.end())) {
        continue;
      }

      out.add(new PiiFinding(type, match, matcher.start(), matcher.end(), riskLevel));
    }
    return out;
  }

  static boolean hasContextNearby(String text, int start, int end, Pattern contextPattern, int windowSize) {
    if (text == null || contextPattern == null) return false;
    int windowStart = Math.max(0, start - windowSize);
    int windowEnd = Math.min(text.length(), end + windowSize);
    String window = text.substring(windowStart, windowEnd);
    return contextPattern.matcher(window).find();
  }
}
