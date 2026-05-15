package com.suji.dataleakdetector.pii.detector;

import com.suji.dataleakdetector.pii.model.PiiFinding;
import com.suji.dataleakdetector.pii.model.PiiType;
import com.suji.dataleakdetector.pii.model.RiskLevel;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class DobDetectorService {
  // Common DOB formats: dd/MM/yyyy, dd-MM-yyyy, yyyy-MM-dd
  private static final Pattern DOB =
      Pattern.compile("\\b(?:\\d{2}[-/]\\d{2}[-/]\\d{4}|\\d{4}-\\d{2}-\\d{2})\\b");

  private static final DateTimeFormatter DMY_SLASH =
      DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
  private static final DateTimeFormatter DMY_DASH =
      DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT);
  private static final DateTimeFormatter YMD_DASH =
      DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);

  public List<PiiFinding> detect(String text) {
    return PiiRegexUtil.findAll(
        DOB,
        text,
        PiiType.DOB,
        RiskLevel.MEDIUM,
        match -> {
          LocalDate date = tryParse(match);
          if (date == null) return false;
          LocalDate now = LocalDate.now();
          if (date.isAfter(now)) return false;
          // Very lenient lower bound (avoid random 0001 dates)
          return date.isAfter(LocalDate.of(1900, 1, 1));
        });
  }

  private LocalDate tryParse(String value) {
    try {
      if (value.contains("/") && value.length() == 10) return LocalDate.parse(value, DMY_SLASH);
      if (value.contains("-") && value.length() == 10) {
        if (Character.isDigit(value.charAt(0)) && Character.isDigit(value.charAt(1))
            && Character.isDigit(value.charAt(2)) && Character.isDigit(value.charAt(3))) {
          return LocalDate.parse(value, YMD_DASH);
        }
        return LocalDate.parse(value, DMY_DASH);
      }
      return null;
    } catch (DateTimeParseException ex) {
      return null;
    }
  }
}

