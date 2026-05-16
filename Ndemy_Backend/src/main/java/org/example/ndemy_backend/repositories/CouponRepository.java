package org.example.ndemy_backend.repositories;

import org.example.ndemy_backend.models.CouponModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CouponRepository extends JpaRepository<CouponModel, UUID> {

    Optional<CouponModel> findByCode(String code);

    boolean existsByCode(String code);
}
