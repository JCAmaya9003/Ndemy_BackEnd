package org.example.ndemy_backend.repositories;

import org.example.ndemy_backend.models.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {
    List<Course> findByInstructorId(UUID instructorId);

    @Query("""
        SELECT c FROM Course c
        WHERE c.isPublished = true
        AND (:category IS NULL OR :category = '' OR LOWER(TRIM(c.category)) = LOWER(TRIM(:category)))
        AND (:minPrice IS NULL OR c.price >= :minPrice)
        AND (:maxPrice IS NULL OR c.price <= :maxPrice)
        AND (:search IS NULL OR :search = '' OR
            LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%')))
        """)
    Page<Course> findWithFilters(
            @Param("category") String category,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("search") String search,
            Pageable pageable
    );

    // usage in report service
    int countByIsPublishedTrue();
}
