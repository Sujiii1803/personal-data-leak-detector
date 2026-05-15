package com.suji.dataleakdetector.dto;

import java.time.Instant;

public class ScanHistoryItemResponse {
  private Long id;
  private Instant scannedAt;
  private String scanType;
  private String originalFilename;
  private int inputLength;
  private int totalFindings;
  private int highRiskCount;
  private int mediumRiskCount;
  private int lowRiskCount;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Instant getScannedAt() {
    return scannedAt;
  }

  public void setScannedAt(Instant scannedAt) {
    this.scannedAt = scannedAt;
  }

  public String getScanType() {
    return scanType;
  }

  public void setScanType(String scanType) {
    this.scanType = scanType;
  }

  public String getOriginalFilename() {
    return originalFilename;
  }

  public void setOriginalFilename(String originalFilename) {
    this.originalFilename = originalFilename;
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
}

