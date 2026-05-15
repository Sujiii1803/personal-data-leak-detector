package com.suji.dataleakdetector.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(
    name = "scan_records",
    indexes = {
      @Index(name = "idx_scan_user_created", columnList = "user_id, createdAt")
    })
public class ScanRecord {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  private ScanType scanType;

  @Column(length = 260)
  private String originalFilename;

  @Column(nullable = false)
  private Instant createdAt = Instant.now();

  @Column(nullable = false)
  private Integer inputLength;

  @Column(nullable = false, columnDefinition = "LONGTEXT")
  private String originalText;

  @Column(nullable = false, columnDefinition = "LONGTEXT")
  private String redactedText;

  @Column(nullable = false)
  private Integer totalFindings;

  @Column(nullable = false)
  private Integer highRiskCount = 0;

  @Column(nullable = false)
  private Integer mediumRiskCount = 0;

  @Column(nullable = false)
  private Integer lowRiskCount = 0;

  @Column(nullable = false, columnDefinition = "LONGTEXT")
  private String findingsJson;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public ScanType getScanType() {
    return scanType;
  }

  public void setScanType(ScanType scanType) {
    this.scanType = scanType;
  }

  public String getOriginalFilename() {
    return originalFilename;
  }

  public void setOriginalFilename(String originalFilename) {
    this.originalFilename = originalFilename;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public Integer getInputLength() {
    return inputLength;
  }

  public void setInputLength(Integer inputLength) {
    this.inputLength = inputLength;
  }

  public String getOriginalText() {
    return originalText;
  }

  public void setOriginalText(String originalText) {
    this.originalText = originalText;
  }

  public String getRedactedText() {
    return redactedText;
  }

  public void setRedactedText(String redactedText) {
    this.redactedText = redactedText;
  }

  public Integer getTotalFindings() {
    return totalFindings;
  }

  public void setTotalFindings(Integer totalFindings) {
    this.totalFindings = totalFindings;
  }

  public Integer getHighRiskCount() {
    return highRiskCount;
  }

  public void setHighRiskCount(Integer highRiskCount) {
    this.highRiskCount = highRiskCount;
  }

  public Integer getMediumRiskCount() {
    return mediumRiskCount;
  }

  public void setMediumRiskCount(Integer mediumRiskCount) {
    this.mediumRiskCount = mediumRiskCount;
  }

  public Integer getLowRiskCount() {
    return lowRiskCount;
  }

  public void setLowRiskCount(Integer lowRiskCount) {
    this.lowRiskCount = lowRiskCount;
  }

  public String getFindingsJson() {
    return findingsJson;
  }

  public void setFindingsJson(String findingsJson) {
    this.findingsJson = findingsJson;
  }
}

