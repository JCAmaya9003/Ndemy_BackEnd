package org.example.ndemy_backend.repositories;

import org.example.ndemy_backend.models.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CertificateRepository extends JpaRepository<Certificate, UUID> {
    boolean existsByStudentIdAndCourseId(UUID studentId, UUID courseId);

    Optional<Certificate> findByCertificateCode(String certificateCode);

    List<Certificate> findByStudentId(UUID studentId);
}
