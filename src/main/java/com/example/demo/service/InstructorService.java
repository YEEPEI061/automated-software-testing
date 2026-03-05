package com.example.demo.service;

import com.example.demo.model.Course;
import com.example.demo.model.Instructor;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.InstructorRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class InstructorService {

    private final InstructorRepository instructorRepository;
    private final CourseRepository courseRepository;

    public InstructorService(InstructorRepository instructorRepository, CourseRepository courseRepository) {
        this.instructorRepository = instructorRepository;
        this.courseRepository = courseRepository;
    }

    // F3 – Identify Most Active Instructor
    public Instructor getMostActiveInstructor() {
        List<Instructor> instructors = instructorRepository.findAll();

        return instructors.stream()
                .max(Comparator.comparingLong(this::countEnrollments))
                .orElseThrow(() -> new RuntimeException("No instructors found"));
    }

    private long countEnrollments(Instructor instructor) {
        List<Course> courses = courseRepository.findByInstructor_Id(instructor.getId());

        long total = 0;
        for (Course course : courses) {
            total += course.getStudents().size();
        }

        return total;
    }

    // F4 – List Instructors with No Enrollments
    public List<Instructor> getInstructorsWithNoEnrollments() {
        List<Instructor> result = new ArrayList<>();
        List<Instructor> instructors = instructorRepository.findAll();

        for (Instructor instructor : instructors) {
            if (countEnrollments(instructor) == 0) {
                result.add(instructor);
            }
        }

        return result;
    }
}