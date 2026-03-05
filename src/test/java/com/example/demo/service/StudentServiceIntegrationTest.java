package com.example.demo.service;

import com.example.demo.model.Course;
import com.example.demo.model.Student;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class StudentServiceIntegrationTest {
    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Test
    void testStudentEnrollmentAndActiveStudents() {

        // Create student
        Student s1 = studentRepository.save(new Student("John","Low"));
        Student s2 = studentRepository.save(new Student("Inactive","Guy"));

        // Create course and enroll s1
        Course c1 = new Course();
        c1.setStudents(Set.of(s1));
        courseRepository.save(c1);

        // F1 – Retrieve Student Enrollments
        List<Course> enrollments = studentService.getStudentEnrollments(s1.getId());
        assertEquals(1, enrollments.size());

        System.out.println("SUCCESS: Student enrolled in " + enrollments.size() + " course(s).");

        // F2 – List Active Students
        List<Student> activeStudents = studentService.getActiveStudents();
        assertEquals(1, activeStudents.size());
        assertEquals("John", activeStudents.get(0).getFirstName());

        System.out.println("SUCCESS: Found " + activeStudents.size() + " active student(s).");
    }

}
