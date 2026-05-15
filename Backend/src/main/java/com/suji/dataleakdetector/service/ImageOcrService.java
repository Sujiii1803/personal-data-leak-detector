package com.suji.dataleakdetector.service;

import com.suji.dataleakdetector.exception.BadRequestException;
import java.io.File;
import java.io.IOException;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageOcrService {

  public String extractTextFromImage(MultipartFile file) {
    File tempFile = null;
    try {
      String originalFilename = file.getOriginalFilename() == null ? "image.png" : file.getOriginalFilename();
      tempFile = File.createTempFile("ocr_", "_" + originalFilename);
      file.transferTo(tempFile);

      ITesseract tesseract = new Tesseract();
      tesseract.setDatapath("tessdata");
      tesseract.setLanguage("eng");

      return tesseract.doOCR(tempFile);
    } catch (IOException | TesseractException e) {
      throw new BadRequestException("Failed to perform OCR on image: " + e.getMessage());
    } finally {
      if (tempFile != null && tempFile.exists()) {
        tempFile.delete();
      }
    }
  }
}
