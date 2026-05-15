package com.suji.dataleakdetector.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.suji.dataleakdetector.dto.PiiFindingResponse;
import com.suji.dataleakdetector.dto.ScanHistoryItemResponse;
import com.suji.dataleakdetector.dto.ScanResultResponse;
import com.suji.dataleakdetector.entity.ScanRecord;
import com.suji.dataleakdetector.entity.ScanType;
import com.suji.dataleakdetector.entity.User;
import com.suji.dataleakdetector.exception.BadRequestException;
import com.suji.dataleakdetector.exception.ConflictException;
import com.suji.dataleakdetector.pii.PiiDetectionService;
import com.suji.dataleakdetector.pii.RedactionService;
import com.suji.dataleakdetector.pii.model.PiiFinding;
import com.suji.dataleakdetector.pii.model.PiiScanResult;
import com.suji.dataleakdetector.repository.ScanRecordRepository;
import com.suji.dataleakdetector.repository.UserRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScanService {
  private final PiiDetectionService piiDetectionService;
  private final RedactionService redactionService;
  private final ScanRecordRepository scanRecordRepository;
  private final UserRepository userRepository;
  private final ObjectMapper objectMapper;

  public ScanService(
      PiiDetectionService piiDetectionService,
      RedactionService redactionService,
      ScanRecordRepository scanRecordRepository,
      UserRepository userRepository,
      ObjectMapper objectMapper) {
    this.piiDetectionService = piiDetectionService;
    this.redactionService = redactionService;
    this.scanRecordRepository = scanRecordRepository;
    this.userRepository = userRepository;
    this.objectMapper = objectMapper;
  }

  @Transactional
  public ScanResultResponse scanText(String userEmail, String text) {
    return doScan(userEmail, ScanType.TEXT, null, text);
  }

  @Transactional
  public ScanResultResponse scanFile(String userEmail, String originalFilename, String text) {
    return doScan(userEmail, ScanType.FILE, originalFilename, text);
  }

  @Transactional(readOnly = true)
  public List<ScanHistoryItemResponse> history(String userEmail) {
    User user = getUserOrThrow(userEmail);
    List<ScanRecord> records = scanRecordRepository.findTop50ByUserIdOrderByCreatedAtDesc(user.getId());
    List<ScanHistoryItemResponse> out = new ArrayList<>();
    for (ScanRecord r : records) {
      out.add(toHistoryItem(r));
    }
    return out;
  }

  @Transactional(readOnly = true)
  public ScanResultResponse getById(String userEmail, Long id) {
    if (id == null) throw new BadRequestException("id is required");
    User user = getUserOrThrow(userEmail);
    ScanRecord r =
        scanRecordRepository
            .findByIdAndUserId(id, user.getId())
            .orElseThrow(() -> new BadRequestException("Scan not found"));
    return toResult(r, true);
  }

  @Transactional
  public void delete(String userEmail, Long id) {
    if (id == null) throw new BadRequestException("id is required");
    User user = getUserOrThrow(userEmail);
    if (!scanRecordRepository.existsByIdAndUserId(id, user.getId())) {
      throw new BadRequestException("Scan not found");
    }
    scanRecordRepository.deleteByIdAndUserId(id, user.getId());
  }

  private ScanResultResponse doScan(
      String userEmail, ScanType scanType, String originalFilename, String text) {
    if (text == null) text = "";
    if (text.isBlank()) throw new BadRequestException("Text is required");
    if (text.length() > 200000) throw new BadRequestException("Text too large");

    User user = getUserOrThrow(userEmail);

    PiiScanResult scan = piiDetectionService.scanText(text);
    String redacted = redactionService.redact(text, scan.getFindings());

    String findingsJson = toFindingsJson(scan.getFindings());

    ScanRecord record = new ScanRecord();
    record.setUser(user);
    record.setScanType(scanType);
    record.setOriginalFilename(originalFilename);
    record.setCreatedAt(Instant.now());
    record.setInputLength(scan.getInputLength());
    record.setOriginalText(text);
    record.setRedactedText(redacted);
    record.setTotalFindings(scan.getTotalFindings());
    record.setHighRiskCount(scan.getHighRiskCount());
    record.setMediumRiskCount(scan.getMediumRiskCount());
    record.setLowRiskCount(scan.getLowRiskCount());
    record.setFindingsJson(findingsJson);

    ScanRecord saved = scanRecordRepository.save(record);
    return toResult(saved, true);
  }

  private User getUserOrThrow(String email) {
    if (email == null || email.isBlank()) {
      throw new UsernameNotFoundException("User not found");
    }
    return userRepository
        .findByEmail(email.trim().toLowerCase())
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
  }

  private String toFindingsJson(List<PiiFinding> findings) {
    try {
      return objectMapper.writeValueAsString(findings);
    } catch (JsonProcessingException e) {
      throw new ConflictException("Failed to serialize findings");
    }
  }

  private ScanHistoryItemResponse toHistoryItem(ScanRecord r) {
    ScanHistoryItemResponse out = new ScanHistoryItemResponse();
    out.setId(r.getId());
    out.setScannedAt(r.getCreatedAt());
    out.setScanType(r.getScanType().name());
    out.setOriginalFilename(r.getOriginalFilename());
    out.setInputLength(r.getInputLength() == null ? 0 : r.getInputLength());
    out.setTotalFindings(r.getTotalFindings() == null ? 0 : r.getTotalFindings());
    out.setHighRiskCount(r.getHighRiskCount() == null ? 0 : r.getHighRiskCount());
    out.setMediumRiskCount(r.getMediumRiskCount() == null ? 0 : r.getMediumRiskCount());
    out.setLowRiskCount(r.getLowRiskCount() == null ? 0 : r.getLowRiskCount());
    return out;
  }

  private ScanResultResponse toResult(ScanRecord r, boolean includeFindings) {
    ScanResultResponse out = new ScanResultResponse();
    out.setId(r.getId());
    out.setScannedAt(r.getCreatedAt());
    out.setScanType(r.getScanType().name());
    out.setOriginalFilename(r.getOriginalFilename());
    out.setInputLength(r.getInputLength() == null ? 0 : r.getInputLength());
    out.setTotalFindings(r.getTotalFindings() == null ? 0 : r.getTotalFindings());
    out.setHighRiskCount(r.getHighRiskCount() == null ? 0 : r.getHighRiskCount());
    out.setMediumRiskCount(r.getMediumRiskCount() == null ? 0 : r.getMediumRiskCount());
    out.setLowRiskCount(r.getLowRiskCount() == null ? 0 : r.getLowRiskCount());
    out.setRedactedText(r.getRedactedText());

    if (includeFindings) {
      out.setFindings(parseFindings(r.getFindingsJson()));
    }
    return out;
  }

  private List<PiiFindingResponse> parseFindings(String json) {
    if (json == null || json.isBlank()) return List.of();
    try {
      PiiFinding[] arr = objectMapper.readValue(json, PiiFinding[].class);
      List<PiiFindingResponse> out = new ArrayList<>();
      for (PiiFinding f : arr) {
        PiiFindingResponse dto = new PiiFindingResponse();
        dto.setType(f.getType());
        dto.setMatchedValue(f.getMatchedValue());
        dto.setStartIndex(f.getStartIndex());
        dto.setEndIndex(f.getEndIndex());
        dto.setRiskLevel(f.getRiskLevel());
        out.add(dto);
      }
      return out;
    } catch (Exception ex) {
      // If older records exist with different structure, just hide findings rather than failing.
      return List.of();
    }
  }
}

