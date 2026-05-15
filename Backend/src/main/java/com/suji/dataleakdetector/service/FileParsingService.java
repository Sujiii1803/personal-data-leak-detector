package com.suji.dataleakdetector.service;

import com.suji.dataleakdetector.exception.BadRequestException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileParsingService {

  private final ImageOcrService imageOcrService;

  public FileParsingService(ImageOcrService imageOcrService) {
    this.imageOcrService = imageOcrService;
  }

  public String extractText(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new BadRequestException("File is required");
    }

    String filename = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
    String lower = filename.toLowerCase();

    try {
      if (lower.endsWith(".pdf")) return extractPdf(file);
      if (lower.endsWith(".docx")) return extractDocx(file);
      if (lower.endsWith(".txt")) return extractTxt(file);
      if (lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
        return imageOcrService.extractTextFromImage(file);
      }
    } catch (IOException ex) {
      throw new BadRequestException("Failed to read file");
    }

    throw new BadRequestException("Unsupported file type. Supported formats: PDF, DOCX, TXT, PNG, JPG, JPEG.");
  }

  private String extractPdf(MultipartFile file) throws IOException {
    try (PDDocument doc = PDDocument.load(file.getInputStream())) {
      PDFTextStripper stripper = new PDFTextStripper();
      return stripper.getText(doc);
    }
  }

  private String extractDocx(MultipartFile file) throws IOException {
    try (XWPFDocument doc = new XWPFDocument(file.getInputStream())) {
      try (XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {
        return extractor.getText();
      }
    }
  }

  private String extractTxt(MultipartFile file) throws IOException {
    byte[] bytes = file.getBytes();
    return new String(bytes, StandardCharsets.UTF_8);
  }
}

