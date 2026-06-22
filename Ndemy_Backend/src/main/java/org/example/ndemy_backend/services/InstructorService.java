package org.example.ndemy_backend.services;

import org.example.ndemy_backend.dto.response.CourseStatsResponse;

import java.util.List;
import java.util.UUID;

public interface InstructorService {
    List<CourseStatsResponse> getInstructorCourses(UUID instructorId);
}
