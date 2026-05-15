package com.suji.dataleakdetector.pii.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class PiiScanResult {
  private Instant scannedAt = Instant.now();
  private int inputLength;
  private int totalFindings;
  private int highRiskCount;
  private int mediumRiskCount;
  private int lowRiskCount;
  private List<PiiFinding> findings = new ArrayList<>();

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

  public int getHighRiskCount() {
    return highRiskCount;
  }

  public void setHighRiskCount(int highRiskCount) {
    this.highRiskCount = highRiskCount;
  }

  public int getMediumRiskCount() {
    return mediumRiskCount;
  }

  public void setMediumRiskCount(int mediumRiskCount) {
    this.mediumRiskCount = mediumRiskCount;
  }

  public int getLowRiskCount() {
    return lowRiskCount;
  }

  public void setLowRiskCount(int lowRiskCount) {
    this.lowRiskCount = lowRiskCount;
  }

  public List<PiiFinding> getFindings() {
    return findings;
  }

  public void setFindings(List<PiiFinding> findings) {
    this.findings = findings;
  }
}

