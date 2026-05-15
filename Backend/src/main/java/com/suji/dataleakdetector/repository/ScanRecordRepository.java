package com.suji.dataleakdetector.repository;

import com.suji.dataleakdetector.entity.ScanRecord;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScanRecordRepository extends JpaRepository<ScanRecord, Long> {
  List<ScanRecord> findTop50ByUserIdOrderByCreatedAtDesc(Long userId);

  Optional<ScanRecord> findByIdAndUserId(Long id, Long userId);

  boolean existsByIdAndUserId(Long id, Long userId);

  void deleteByIdAndUserId(Long id, Long userId);
}

