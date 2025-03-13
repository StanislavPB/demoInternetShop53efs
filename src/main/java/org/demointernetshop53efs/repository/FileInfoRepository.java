package org.demointernetshop53efs.repository;

import org.demointernetshop53efs.entity.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileInfoRepository extends JpaRepository<FileInfo, Long> {
}
