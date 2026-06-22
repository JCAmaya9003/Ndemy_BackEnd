package org.example.ndemy_backend.repositories;

import org.example.ndemy_backend.models.Course;
import org.example.ndemy_backend.models.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {
    boolean existsByStudentIdAndCourseId(UUID studentId, UUID courseId);

    Optional<Enrollment> findByStudentIdAndCourseIdAndIsActiveTrue(UUID studentId, UUID courseId);

    List<Enrollment> findByStudentIdAndIsActiveTrue(UUID studentId);

    boolean existsByStudentIdAndCourseIdAndIsActiveTrue(UUID studentId, UUID courseId);

    // usage in report service
    int countByIsActiveTrue();

    @Query("""
        SELECT e.course.id, e.course.title, e.course.instructor.name, COUNT(e)
        FROM Enrollment e
        WHERE e.isActive = true
        GROUP BY e.course.id, e.course.title, e.course.instructor.name
        ORDER BY COUNT(e) DESC
        LIMIT 5
        """)
    List<Object[]> findTop5CoursesByEnrollments();

    int countByCourseIdAndIsActiveTrue(UUID courseId);
    List<Enrollment> findByCourseIdAndIsActiveTrue(UUID courseId);
}
