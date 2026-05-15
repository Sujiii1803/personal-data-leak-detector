package com.suji.dataleakdetector.pii;

import com.suji.dataleakdetector.pii.model.PiiFinding;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RedactionService {
  private static final char REDACTION_CHAR = '█';

  public String redact(String text, List<PiiFinding> findings) {
    if (text == null || text.isEmpty()) return text == null ? "" : text;
    if (findings == null || findings.isEmpty()) return text;

    List<PiiFinding> sorted = new ArrayList<>(findings);
    sorted.sort(Comparator.comparingInt(PiiFinding::getStartIndex).reversed());

    StringBuilder sb = new StringBuilder(text);
    for (PiiFinding f : sorted) {
      int start = f.getStartIndex();
      int end = f.getEndIndex();
      if (start < 0 || end < 0 || start >= end) continue;
      if (start >= sb.length()) continue;
      if (end > sb.length()) end = sb.length();

      int len = end - start;
      sb.replace(start, end, repeat(REDACTION_CHAR, len));
    }
    return sb.toString();
  }

  private String repeat(char c, int count) {
    if (count <= 0) return "";
    return String.valueOf(c).repeat(count);
  }
}

