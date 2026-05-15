package com.suji.dataleakdetector.controller;

import com.suji.dataleakdetector.dto.ScanHistoryItemResponse;
import com.suji.dataleakdetector.dto.ScanRequest;
import com.suji.dataleakdetector.dto.ScanResultResponse;
import com.suji.dataleakdetector.service.FileParsingService;
import com.suji.dataleakdetector.service.ScanService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/scan")
public class ScanController {
  private final ScanService scanService;
  private final FileParsingService fileParsingService;

  public ScanController(ScanService scanService, FileParsingService fileParsingService) {
    this.scanService = scanService;
    this.fileParsingService = fileParsingService;
  }

  @PostMapping("/text")
  public ResponseEntity<ScanResultResponse> scanText(@Valid @RequestBody ScanRequest request) {
    String email = currentUserEmail();
    return ResponseEntity.ok(scanService.scanText(email, request.getText()));
  }

  @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ScanResultResponse> scanFile(@RequestParam("file") MultipartFile file) {
    String email = currentUserEmail();
    String text = fileParsingService.extractText(file);
    return ResponseEntity.ok(scanService.scanFile(email, file.getOriginalFilename(), text));
  }

  @GetMapping("/history")
  public ResponseEntity<List<ScanHistoryItemResponse>> history() {
    String email = currentUserEmail();
    return ResponseEntity.ok(scanService.history(email));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ScanResultResponse> getById(@PathVariable Long id) {
    String email = currentUserEmail();
    return ResponseEntity.ok(scanService.getById(email, id));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    String email = currentUserEmail();
    scanService.delete(email, id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  private String currentUserEmail() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return auth == null ? null : auth.getName();
  }
}

