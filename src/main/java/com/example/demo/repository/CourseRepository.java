package com.example.demo.repository;

import com.example.demo.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByStudents_Id(Long studentId);

    List<Course> findByInstructor_Id(Long instructorId);
}
