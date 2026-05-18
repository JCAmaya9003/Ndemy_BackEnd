package org.example.ndemy_backend.repositories;

import org.example.ndemy_backend.models.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CouponRepository extends JpaRepository<Coupon, UUID> {

    Optional<Coupon> findByCode(String code);

    boolean existsByCode(String code);
}
