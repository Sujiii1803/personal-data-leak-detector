package com.suji.dataleakdetector.pii;

import com.suji.dataleakdetector.pii.detector.AadhaarDetectorService;
import com.suji.dataleakdetector.pii.detector.BankAccountDetectorService;
import com.suji.dataleakdetector.pii.detector.CreditCardDetectorService;
import com.suji.dataleakdetector.pii.detector.DobDetectorService;
import com.suji.dataleakdetector.pii.detector.EmailDetectorService;
import com.suji.dataleakdetector.pii.detector.IfscDetectorService;
import com.suji.dataleakdetector.pii.detector.IpAddressDetectorService;
import com.suji.dataleakdetector.pii.detector.PanDetectorService;
import com.suji.dataleakdetector.pii.detector.PassportDetectorService;
import com.suji.dataleakdetector.pii.detector.PhoneDetectorService;
import com.suji.dataleakdetector.pii.detector.PinCodeDetectorService;
import com.suji.dataleakdetector.pii.detector.UrlDetectorService;
import com.suji.dataleakdetector.pii.model.PiiFinding;
import com.suji.dataleakdetector.pii.model.PiiScanResult;
import com.suji.dataleakdetector.pii.model.PiiType;
import com.suji.dataleakdetector.pii.model.RiskLevel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class PiiDetectionService {
  private final EmailDetectorService emailDetectorService;
  private final PhoneDetectorService phoneDetectorService;
  private final AadhaarDetectorService aadhaarDetectorService;
  private final PanDetectorService panDetectorService;
  private final PassportDetectorService passportDetectorService;
  private final CreditCardDetectorService creditCardDetectorService;
  private final BankAccountDetectorService bankAccountDetectorService;
  private final IfscDetectorService ifscDetectorService;
  private final IpAddressDetectorService ipAddressDetectorService;
  private final DobDetectorService dobDetectorService;
  private final UrlDetectorService urlDetectorService;
  private final PinCodeDetectorService pinCodeDetectorService;

  public PiiDetectionService(
      EmailDetectorService emailDetectorService,
      PhoneDetectorService phoneDetectorService,
      AadhaarDetectorService aadhaarDetectorService,
      PanDetectorService panDetectorService,
      PassportDetectorService passportDetectorService,
      CreditCardDetectorService creditCardDetectorService,
      BankAccountDetectorService bankAccountDetectorService,
      IfscDetectorService ifscDetectorService,
      IpAddressDetectorService ipAddressDetectorService,
      DobDetectorService dobDetectorService,
      UrlDetectorService urlDetectorService,
      PinCodeDetectorService pinCodeDetectorService) {
    this.emailDetectorService = emailDetectorService;
    this.phoneDetectorService = phoneDetectorService;
    this.aadhaarDetectorService = aadhaarDetectorService;
    this.panDetectorService = panDetectorService;
    this.passportDetectorService = passportDetectorService;
    this.creditCardDetectorService = creditCardDetectorService;
    this.bankAccountDetectorService = bankAccountDetectorService;
    this.ifscDetectorService = ifscDetectorService;
    this.ipAddressDetectorService = ipAddressDetectorService;
    this.dobDetectorService = dobDetectorService;
    this.urlDetectorService = urlDetectorService;
    this.pinCodeDetectorService = pinCodeDetectorService;
  }

  private static final Map<PiiType, Integer> TYPE_PRIORITY = Map.ofEntries(
      Map.entry(PiiType.CREDIT_CARD, 100),
      Map.entry(PiiType.AADHAAR, 90),
      Map.entry(PiiType.PAN, 80),
      Map.entry(PiiType.PASSPORT, 70),
      Map.entry(PiiType.EMAIL, 60),
      Map.entry(PiiType.PHONE, 50),
      Map.entry(PiiType.DOB, 40),
      Map.entry(PiiType.BANK_ACCOUNT, 30),
      Map.entry(PiiType.IFSC, 20),
      Map.entry(PiiType.URL, 10),
      Map.entry(PiiType.IP_ADDRESS, 10),
      Map.entry(PiiType.PIN_CODE, 5)
  );

  private int getPriority(PiiType type) {
      return TYPE_PRIORITY.getOrDefault(type, 0);
  }

  public PiiScanResult scanText(String text) {
    String safe = text == null ? "" : text;
    List<PiiFinding> findings = new ArrayList<>();

    findings.addAll(aadhaarDetectorService.detect(safe));
    findings.addAll(panDetectorService.detect(safe));
    findings.addAll(passportDetectorService.detect(safe));
    findings.addAll(creditCardDetectorService.detect(safe));
    findings.addAll(bankAccountDetectorService.detect(safe));

    findings.addAll(emailDetectorService.detect(safe));
    findings.addAll(phoneDetectorService.detect(safe));
    findings.addAll(dobDetectorService.detect(safe));
    findings.addAll(ifscDetectorService.detect(safe));

    findings.addAll(ipAddressDetectorService.detect(safe));
    findings.addAll(urlDetectorService.detect(safe));
    findings.addAll(pinCodeDetectorService.detect(safe));

    // Deduplicate and resolve overlaps greedy approach:
    // Sort by Priority (desc), Length (desc), StartIndex (asc)
    findings.sort(
        Comparator.<PiiFinding>comparingInt(f -> -getPriority(f.getType()))
            .thenComparingInt(f -> -(f.getEndIndex() - f.getStartIndex()))
            .thenComparingInt(PiiFinding::getStartIndex));

    List<PiiFinding> deduplicated = new ArrayList<>();
    for (PiiFinding current : findings) {
      boolean overlap = false;
      for (PiiFinding existing : deduplicated) {
        if (Math.max(current.getStartIndex(), existing.getStartIndex()) < Math.min(current.getEndIndex(), existing.getEndIndex())) {
          overlap = true;
          break;
        }
      }
      if (!overlap) {
        deduplicated.add(current);
      }
    }

    // Stable ordering for clients and redaction (earlier matches first)
    deduplicated.sort(
        Comparator.comparingInt(PiiFinding::getStartIndex)
            .thenComparingInt(PiiFinding::getEndIndex)
            .thenComparing(f -> f.getType().name()));

    PiiScanResult result = new PiiScanResult();
    result.setInputLength(safe.length());
    result.setFindings(deduplicated);
    result.setTotalFindings(deduplicated.size());

    int high = 0, medium = 0, low = 0;
    for (PiiFinding f : deduplicated) {
      if (f.getRiskLevel() == RiskLevel.HIGH) high++;
      else if (f.getRiskLevel() == RiskLevel.MEDIUM) medium++;
      else if (f.getRiskLevel() == RiskLevel.LOW) low++;
    }
    result.setHighRiskCount(high);
    result.setMediumRiskCount(medium);
    result.setLowRiskCount(low);
    return result;
  }
}

