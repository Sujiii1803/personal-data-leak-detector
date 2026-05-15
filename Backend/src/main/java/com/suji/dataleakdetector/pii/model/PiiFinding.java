package com.suji.dataleakdetector.pii.model;

public class PiiFinding {
  private PiiType type;
  private String matchedValue;
  private int startIndex;
  private int endIndex;
  private RiskLevel riskLevel;

  public PiiFinding() {}

  public PiiFinding(
      PiiType type, String matchedValue, int startIndex, int endIndex, RiskLevel riskLevel) {
    this.type = type;
    this.matchedValue = matchedValue;
    this.startIndex = startIndex;
    this.endIndex = endIndex;
    this.riskLevel = riskLevel;
  }

  public PiiType getType() {
    return type;
  }

  public void setType(PiiType type) {
    this.type = type;
  }

  public String getMatchedValue() {
    return matchedValue;
  }

  public void setMatchedValue(String matchedValue) {
    this.matchedValue = matchedValue;
  }

  public int getStartIndex() {
    return startIndex;
  }

  public void setStartIndex(int startIndex) {
    this.startIndex = startIndex;
  }

  public int getEndIndex() {
    return endIndex;
  }

  public void setEndIndex(int endIndex) {
    this.endIndex = endIndex;
  }

  public RiskLevel getRiskLevel() {
    return riskLevel;
  }

  public void setRiskLevel(RiskLevel riskLevel) {
    this.riskLevel = riskLevel;
  }
}

