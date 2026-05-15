package com.suji.dataleakdetector.dto;

import com.suji.dataleakdetector.pii.model.PiiType;
import com.suji.dataleakdetector.pii.model.RiskLevel;

public class PiiFindingResponse {
  private PiiType type;
  private String matchedValue;
  private int startIndex;
  private int endIndex;
  private RiskLevel riskLevel;

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

