package org.example.ndemy_backend.repositories;

import org.example.ndemy_backend.models.User;
import org.example.ndemy_backend.models.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    // --- Búsquedas básicas ---
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByRole(Role role);

    // --- Filtros por estado ---
    List<User> findByIsActiveTrue();

    List<User> findByIsLockedTrue();

    List<User> findByRole(Role role);

    List<User> findByIsActiveTrueAndRole(Role role);

    // --- Seguridad: manejo de intentos fallidos ---
    @Modifying
    @Query("UPDATE User u SET u.failedLoginAttempts = u.failedLoginAttempts + 1 WHERE u.email = :email")
    void incrementFailedLoginAttempts(@Param("email") String email);

    @Modifying
    @Query("UPDATE User u SET u.failedLoginAttempts = 0, u.isLocked = false WHERE u.email = :email")
    void resetFailedLoginAttempts(@Param("email") String email);

    @Modifying
    @Query("UPDATE User u SET u.isLocked = true WHERE u.email = :email")
    void lockAccount(@Param("email") String email);

    @Modifying
    @Query("UPDATE User u SET u.isActive = false WHERE u.id = :id")
    void deactivateUser(@Param("id") UUID id);
}
