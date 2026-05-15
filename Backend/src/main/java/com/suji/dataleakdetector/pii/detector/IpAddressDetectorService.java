package com.suji.dataleakdetector.pii.detector;

import com.suji.dataleakdetector.pii.model.PiiFinding;
import com.suji.dataleakdetector.pii.model.PiiType;
import com.suji.dataleakdetector.pii.model.RiskLevel;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class IpAddressDetectorService {
  private static final Pattern IPV4 = Pattern.compile("(?<!\\d)(?:\\d{1,3}\\.){3}\\d{1,3}(?!\\d)");

  public List<PiiFinding> detect(String text) {
    return PiiRegexUtil.findAll(
        IPV4,
        text,
        PiiType.IP_ADDRESS,
        RiskLevel.LOW,
        match -> {
          String[] parts = match.split("\\.");
          if (parts.length != 4) return false;
          for (String p : parts) {
            try {
              int v = Integer.parseInt(p);
              if (v < 0 || v > 255) return false;
            } catch (NumberFormatException e) {
              return false;
            }
          }
          return true;
        });
  }
}

