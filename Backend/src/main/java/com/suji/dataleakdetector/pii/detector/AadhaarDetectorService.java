package com.suji.dataleakdetector.pii.detector;

import com.suji.dataleakdetector.pii.model.PiiFinding;
import com.suji.dataleakdetector.pii.model.PiiType;
import com.suji.dataleakdetector.pii.model.RiskLevel;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class AadhaarDetectorService {
  private static final Pattern AADHAAR = Pattern.compile("\\b\\d{4}[\\s-]\\d{4}[\\s-]\\d{4}\\b|\\b\\d{12}\\b");

  private static final int[][] d = new int[][] {
      {0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
      {1, 2, 3, 4, 0, 6, 7, 8, 9, 5},
      {2, 3, 4, 0, 1, 7, 8, 9, 5, 6},
      {3, 4, 0, 1, 2, 8, 9, 5, 6, 7},
      {4, 0, 1, 2, 3, 9, 5, 6, 7, 8},
      {5, 9, 8, 7, 6, 0, 4, 3, 2, 1},
      {6, 5, 9, 8, 7, 1, 0, 4, 3, 2},
      {7, 6, 5, 9, 8, 2, 1, 0, 4, 3},
      {8, 7, 6, 5, 9, 3, 2, 1, 0, 4},
      {9, 8, 7, 6, 5, 4, 3, 2, 1, 0}
  };
  
  private static final int[][] p = new int[][] {
      {0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
      {1, 5, 7, 6, 2, 8, 3, 0, 9, 4},
      {5, 8, 0, 3, 7, 9, 6, 1, 4, 2},
      {8, 9, 1, 6, 0, 4, 3, 5, 2, 7},
      {9, 4, 5, 3, 1, 2, 6, 8, 7, 0},
      {4, 2, 8, 6, 5, 7, 3, 9, 0, 1},
      {2, 7, 9, 3, 8, 0, 6, 4, 1, 5},
      {7, 0, 4, 6, 9, 1, 3, 2, 5, 8}
  };

  public List<PiiFinding> detect(String text) {
    return PiiRegexUtil.findAll(AADHAAR, text, PiiType.AADHAAR, RiskLevel.HIGH, this::isValidVerhoeff);
  }

  private boolean isValidVerhoeff(String number) {
    String cleanNum = number.replaceAll("\\D", "");
    if (cleanNum.length() != 12) return false;
    
    // Aadhaar numbers should not start with 0 or 1
    if (cleanNum.charAt(0) == '0' || cleanNum.charAt(0) == '1') return false;

    int c = 0;
    int[] myArray = new int[cleanNum.length()];
    for (int i = 0; i < cleanNum.length(); i++) {
        myArray[i] = cleanNum.charAt(i) - '0';
    }
    for (int i = 0; i < myArray.length; i++) {
        c = d[c][p[(i % 8)][myArray[myArray.length - i - 1]]];
    }
    return c == 0;
  }
}

