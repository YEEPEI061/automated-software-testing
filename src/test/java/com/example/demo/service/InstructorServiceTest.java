package com.example.demo.service;

import com.example.demo.model.Course;
import com.example.demo.model.Instructor;
import com.example.demo.model.Student;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.InstructorRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class InstructorServiceTest {

    @Mock
    private InstructorRepository instructorRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private InstructorService instructorService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // F3 – Identify Most Active Instructor
    @Test
    void testGetMostActiveInstructor() {
        // Arrange (setup test condition)
        Instructor i1 = new Instructor("Amelia", "Taylor");
        i1.setId(1L);

        Instructor i2 = new Instructor("Stephen", "Lee");
        i2.setId(2L);

        Student s1 = new Student("Jasmine", "Davies");
        s1.setId(1L);

        Course c1 = new Course();
        c1.setInstructor(i1);
        c1.setStudents(Set.of(s1));

        Course c2 = new Course();
        c2.setInstructor(i2);
        c2.setStudents(Set.of(s1, new Student("Alice","Evans")));

        when(instructorRepository.findAll()).thenReturn(List.of(i1, i2));
        when(courseRepository.findByInstructor_Id(1L)).thenReturn(List.of(c1));
        when(courseRepository.findByInstructor_Id(2L)).thenReturn(List.of(c2));

        // Act (call business logic - InstructorService.java)
        Instructor result = instructorService.getMostActiveInstructor();

        // Assert (unit testing - verify the results)
        assertEquals(2L, result.getId());

        // Verify interaction with repository
        verify(instructorRepository, times(1)).findAll();
        verify(courseRepository, times(1)).findByInstructor_Id(1L);
        verify(courseRepository, times(1)).findByInstructor_Id(2L);

        System.out.println("F3 SUCCESS: Most active instructor = " + result.getFirstName() + " " + result.getLastName());
    }

    // F4 – List Instructors with No Enrollments
    @Test
    void testGetInstructorsWithNoEnrollments() {
        // Arrange (setup test condition)
        Instructor i1 = new Instructor("Oliver", "Smith");
        i1.setId(1L);

        Instructor i2 = new Instructor("Amelia", "Taylor");
        i2.setId(2L);

        Course c2 = new Course();
        c2.setInstructor(i2);
        c2.setStudents(Set.of(new Student("Alice", "Evans")));

        when(instructorRepository.findAll()).thenReturn(List.of(i1, i2));
        when(courseRepository.findByInstructor_Id(1L)).thenReturn(List.of()); // no enrollments
        when(courseRepository.findByInstructor_Id(2L)).thenReturn(List.of(c2)); // has enrollments

        // Act (call business logic - InstructorService.java)
        List<Instructor> result = instructorService.getInstructorsWithNoEnrollments();

        // Assert (unit testing - verify the results)
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());

        // Verify interaction with repository
        verify(instructorRepository, times(1)).findAll();
        verify(courseRepository, times(1)).findByInstructor_Id(1L);
        verify(courseRepository, times(1)).findByInstructor_Id(2L);

        System.out.println("F4 SUCCESS: Instructor with no enrollments = " + result.get(0).getFirstName() + " " + result.get(0).getLastName());
    }

}