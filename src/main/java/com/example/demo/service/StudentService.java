package com.example.demo.service;

import com.example.demo.model.Course;
import com.example.demo.model.Student;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public StudentService(StudentRepository studentRepository, CourseRepository courseRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    // F1 – Retrieve Student Enrollments
    public List<Course> getStudentEnrollments(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new RuntimeException("Student not found");
        }
        return courseRepository.findByStudents_Id(studentId);
    }


    // F2 – List Active Students
    public List<Student> getActiveStudents() {
        List<Course> courses = courseRepository.findAll();

        Set<Long> studentIds = new HashSet<>();
        for (Course course : courses) {
            course.getStudents().forEach(student -> studentIds.add(student.getId()));
        }

        return studentRepository.findByIdIn(studentIds.stream().toList());
    }
}