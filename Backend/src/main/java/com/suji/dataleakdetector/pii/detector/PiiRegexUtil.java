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

  static List<PiiFinding> findAll(
      Pattern pattern, String text, PiiType type, RiskLevel riskLevel, Predicate<String> validator) {
    List<PiiFinding> out = new ArrayList<>();
    if (text == null || text.isBlank()) return out;

    Matcher matcher = pattern.matcher(text);
    while (matcher.find()) {
      String match = matcher.group();
      if (match == null || match.isBlank()) continue;
      if (validator != null && !validator.test(match)) continue;

      out.add(new PiiFinding(type, match, matcher.start(), matcher.end(), riskLevel));
    }
    return out;
  }
}

