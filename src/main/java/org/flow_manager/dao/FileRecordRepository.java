package org.flow_manager.dao;

import org.flow_manager.model.FileRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRecordRepository extends JpaRepository<FileRecord, Long> {

}
