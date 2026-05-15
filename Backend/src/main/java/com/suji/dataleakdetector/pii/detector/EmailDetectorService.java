package com.suji.dataleakdetector.pii.detector;

import com.suji.dataleakdetector.pii.model.PiiFinding;
import com.suji.dataleakdetector.pii.model.PiiType;
import com.suji.dataleakdetector.pii.model.RiskLevel;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class EmailDetectorService {
  // Simple and safe email pattern (avoids catastrophic backtracking)
  private static final Pattern EMAIL =
      Pattern.compile("\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}\\b", Pattern.CASE_INSENSITIVE);

  public List<PiiFinding> detect(String text) {
    return PiiRegexUtil.findAll(EMAIL, text, PiiType.EMAIL, RiskLevel.MEDIUM, null);
  }
}

