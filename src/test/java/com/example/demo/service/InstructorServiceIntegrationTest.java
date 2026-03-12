package com.example.demo.service;

import com.example.demo.model.Course;
import com.example.demo.model.Instructor;
import com.example.demo.model.Student;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.InstructorRepository;
import com.example.demo.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class InstructorServiceIntegrationTest {

    @Autowired
    private InstructorService instructorService;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Test
    void testInstructorActivityScenarios() {

        // Create instructor
        Instructor i1 = instructorRepository.save(new Instructor("Stephen","Lee"));
        Instructor i2 = instructorRepository.save(new Instructor("Jenny","Wong"));

        // Create student
        Student s1 = studentRepository.save(new Student("Jane","Tan"));

        // Create a course, assign it to instructor i1, and enroll student s1
        Course c1 = new Course();
        c1.setInstructor(i1);
        c1.setStudents(Set.of(s1));
        courseRepository.save(c1);

        // F3 – Identify Most Active Instructor
        Instructor mostActive = instructorService.getMostActiveInstructor();
        assertEquals(i1.getId(), mostActive.getId());

        System.out.println("SUCCESS: Most active instructor is " + mostActive.getFirstName() + " " + mostActive.getLastName());

        // F4 – List Instructors with No Enrollments
        List<Instructor> noEnrollment = instructorService.getInstructorsWithNoEnrollments();
        assertEquals(1, noEnrollment.size());
        assertEquals(i2.getId(), noEnrollment.get(0).getId());

        System.out.println("SUCCESS: Found " + noEnrollment.size() + " instructor(s) with no enrollments -> " + noEnrollment.get(0).getFirstName() + " " + noEnrollment.get(0).getLastName());
    }
}
