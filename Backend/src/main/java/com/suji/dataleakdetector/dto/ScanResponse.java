package com.suji.dataleakdetector.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ScanResponse {
  private Instant scannedAt = Instant.now();
  private int inputLength;
  private int totalFindings;
  private List<FindingResponse> findings = new ArrayList<>();

  public Instant getScannedAt() {
    return scannedAt;
  }

  public void setScannedAt(Instant scannedAt) {
    this.scannedAt = scannedAt;
  }

  public int getInputLength() {
    return inputLength;
  }

  public void setInputLength(int inputLength) {
    this.inputLength = inputLength;
  }

  public int getTotalFindings() {
    return totalFindings;
  }

  public void setTotalFindings(int totalFindings) {
    this.totalFindings = totalFindings;
  }

  public List<FindingResponse> getFindings() {
    return findings;
  }

  public void setFindings(List<FindingResponse> findings) {
    this.findings = findings;
  }
}

