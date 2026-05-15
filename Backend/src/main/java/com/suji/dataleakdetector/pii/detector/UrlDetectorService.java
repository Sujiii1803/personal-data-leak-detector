package com.suji.dataleakdetector.pii.detector;

import com.suji.dataleakdetector.pii.model.PiiFinding;
import com.suji.dataleakdetector.pii.model.PiiType;
import com.suji.dataleakdetector.pii.model.RiskLevel;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class UrlDetectorService {
  private static final Pattern URL =
      Pattern.compile("\\b(?:https?://|www\\.)[^\\s<>\"']+\\b", Pattern.CASE_INSENSITIVE);

  public List<PiiFinding> detect(String text) {
    return PiiRegexUtil.findAll(URL, text, PiiType.URL, RiskLevel.LOW, null);
  }
}

