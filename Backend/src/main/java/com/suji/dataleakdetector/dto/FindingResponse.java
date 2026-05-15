package com.suji.dataleakdetector.dto;

public class FindingResponse {
  private String type;
  private String value;
  private boolean valid;

  public FindingResponse() {}

  public FindingResponse(String type, String value, boolean valid) {
    this.type = type;
    this.value = value;
    this.valid = valid;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public boolean isValid() {
    return valid;
  }

  public void setValid(boolean valid) {
    this.valid = valid;
  }
}

