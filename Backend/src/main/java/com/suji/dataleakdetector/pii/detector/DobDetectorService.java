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
  // Supports dd/MM/yyyy, dd-MM-yyyy, dd.MM.yyyy, and yyyy-MM-dd formats.
  private static final Pattern DOB =
      Pattern.compile("\\b(?:\\d{2}[-/.]\\d{2}[-/.]\\d{4}|\\d{4}-\\d{2}-\\d{2})\\b");

  // Context words required to prevent flagging invoice dates, meeting dates, or logs.
  private static final Pattern DOB_CONTEXT =
      Pattern.compile("(?i)(?:\\b(dob|born|birth)\\b|\\bdate\\s*of\\s*birth\\b)");

  private static final int CONTEXT_WINDOW = 50;

  // Strict formatters (ResolverStyle.STRICT rejects invalid dates like 31/02/2000).
  private static final DateTimeFormatter DMY_SLASH =
      DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
  private static final DateTimeFormatter DMY_DASH =
      DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT);
  private static final DateTimeFormatter DMY_DOT =
      DateTimeFormatter.ofPattern("dd.MM.uuuu").withResolverStyle(ResolverStyle.STRICT);
  private static final DateTimeFormatter YMD_DASH =
      DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);

  public List<PiiFinding> detect(String text) {
    return PiiRegexUtil.findAllWithPosition(
        DOB,
        text,
        PiiType.DOB,
        RiskLevel.MEDIUM,
        (match, start, end) -> isValidDate(match) && hasBirthContext(text, start, end));
  }

  // Parses the date strictly and ensures it falls between 1900 and today.
  private boolean isValidDate(String match) {
    LocalDate date = tryParse(match);
    if (date == null) return false;

    LocalDate now = LocalDate.now();
    if (date.isAfter(now)) return false;

    return date.isAfter(LocalDate.of(1900, 1, 1));
  }

  // Checks for birth-related context words within the nearby text window.
  private boolean hasBirthContext(String text, int start, int end) {
    return PiiRegexUtil.hasContextNearby(text, start, end, DOB_CONTEXT, CONTEXT_WINDOW);
  }

  private LocalDate tryParse(String value) {
    if (value == null || value.length() != 10) return null;
    try {
      if (value.contains("/")) return LocalDate.parse(value, DMY_SLASH);
      if (value.contains(".")) return LocalDate.parse(value, DMY_DOT);
      if (value.contains("-")) {
        if (Character.isDigit(value.charAt(0))
            && Character.isDigit(value.charAt(1))
            && Character.isDigit(value.charAt(2))
            && Character.isDigit(value.charAt(3))) {
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
