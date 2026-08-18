package com.ofss.project.repository;

import com.ofss.project.entity.ApplicationDocument;
import com.ofss.project.enums.ApplicationDocumentType;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationDocumentRepository extends JpaRepository<ApplicationDocument, Long> {

	Optional<ApplicationDocument> findByApplication_IdAndDocumentType(Long applicationId,
			ApplicationDocumentType documentType);

	List<ApplicationDocument> findByApplication_IdOrderByUploadedAtAsc(Long applicationId);

	boolean existsByApplication_IdAndDocumentType(Long applicationId, ApplicationDocumentType documentType);
}