package com.example.demo.service;

import com.example.demo.model.Course;
import com.example.demo.model.Student;
import com.example.demo.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class StudentServiceIntegrationTest {
    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @Test
    void testStudentEnrollmentAndActiveStudents() {
        // Using sample data from DataLoader
        List<Student> johnList = studentRepository.findByFirstNameIgnoreCaseAndLastNameIgnoreCase("John", "Low");
        List<Student> jasmineList = studentRepository.findByFirstNameIgnoreCaseAndLastNameIgnoreCase("Jasmine", "Davies");

        assertFalse(johnList.isEmpty(), "John Low should exist in sample data");
        assertFalse(jasmineList.isEmpty(), "Jasmine Davies should exist in sample data");

        Student john = johnList.get(0);
        Student jasmine = jasmineList.get(0);

        // F1 – Retrieve Student Enrollments
        List<Course> johnEnrollments = studentService.getStudentEnrollments(john.getId());
        assertEquals(1, johnEnrollments.size()); // Math 101
        System.out.println("F1 SUCCESS: John enrolled in " + johnEnrollments.size() + " course(s).");

        // F2 – List Active Students
        List<Student> activeStudents = studentService.getActiveStudents();
        assertEquals(3, activeStudents.size());

        assertTrue(activeStudents.stream().anyMatch(s -> s.getFirstName().equals("John")));
        assertTrue(activeStudents.stream().anyMatch(s -> s.getFirstName().equals("Jasmine")));
        assertTrue(activeStudents.stream().anyMatch(s -> s.getFirstName().equals("Alice") && s.getLastName().equals("Thomas")));

        System.out.println("F2 SUCCESS: Found " + activeStudents.size() + " active student(s).");
    }

}
