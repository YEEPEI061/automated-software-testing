package com.example.demo.service;

import com.example.demo.model.Course;
import com.example.demo.model.Student;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.CourseRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private StudentService studentService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // F1 – Retrieve Student Enrollments
    @Test
    void testGetStudentEnrollments() {
        // Arrange (setup test condition)
        Student student = new Student("Jasmine", "Davies");
        student.setId(1L);

        Course c1 = new Course();
        c1.setId(10L);
        c1.setStudents(Set.of(student));

        Course c2 = new Course();
        c2.setId(20L);
        c2.setStudents(Set.of(student));

        when(studentRepository.existsById(1L)).thenReturn(true);
        when(courseRepository.findByStudents_Id(1L)).thenReturn(List.of(c1,c2));

        // Act (call the business logic - StudentService.java)
        List<Course> result = studentService.getStudentEnrollments(1L);

        // Assert (unit testing - verify the results)
        assertNotNull(result);
        assertEquals(2, result.size());

        // Verify interaction with repository
        verify(studentRepository, times(1)).existsById(1L);
        verify(courseRepository, times(1)).findByStudents_Id(1L);

        System.out.println("F1 SUCCESS: Student enrolled in " + result.size() + " course(s).");
    }


    // F2 – List Active Students
    @Test
    void testGetActiveStudents() {
        // Arrange (setup test condition)
        Student activeStudent = new Student("Jasmine", "Davies");
        activeStudent.setId(1L);

        Student inactiveStudent = new Student("Tom", "Johnson");
        inactiveStudent.setId(2L);

        // Only active student is enrolled in a course
        Course course = new Course();
        course.setStudents(Set.of(activeStudent));

        when(courseRepository.findAll()).thenReturn(List.of(course));
        when(studentRepository.findByIdIn(List.of(1L))).thenReturn(List.of(activeStudent));

        // Act (call the business logic - StudentService.java)
        List<Student> result = studentService.getActiveStudents();

        // Assert (unit testing - verify the results)
        assertEquals(1, result.size());
        assertEquals("Jasmine", result.get(0).getFirstName());

        // Verify interaction with repository
        verify(courseRepository, times(1)).findAll();
        verify(studentRepository, times(1)).findByIdIn(anyList());

        System.out.println("F2 SUCCESS: Active student is " + result.get(0).getFirstName() + " " + result.get(0).getLastName());
    }

}