package com.example.demo.service;

import com.example.demo.model.Instructor;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.InstructorRepository;
import com.example.demo.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class InstructorServiceIntegrationTest {

    @Autowired
    private InstructorService instructorService;

    @Autowired
    private InstructorRepository instructorRepository;

    @Test
    void testInstructorActivityUsingSampleData() {

        // Fetch sample instructors
        List<Instructor> instructors = instructorRepository.findAll();
        assertFalse(instructors.isEmpty(), "Sample instructors should exist");

        Instructor stephen = instructors.stream()
                .filter(i -> i.getFirstName().equalsIgnoreCase("Stephen") && i.getLastName().equalsIgnoreCase("Lee"))
                .findFirst().orElseThrow();

        Instructor oliver = instructors.stream()
                .filter(i -> i.getFirstName().equalsIgnoreCase("Oliver") && i.getLastName().equalsIgnoreCase("Smith"))
                .findFirst().orElseThrow();

        // F3 – Identify Most Active Instructor
        Instructor mostActive = instructorService.getMostActiveInstructor();
        assertEquals(stephen.getId(), mostActive.getId());
        System.out.println("F3 SUCCESS: Most active instructor is " + mostActive.getFirstName() + " " + mostActive.getLastName());

        // F4 – List Instructors with No Enrollments
        List<Instructor> noEnrollment = instructorService.getInstructorsWithNoEnrollments();
        assertEquals(1, noEnrollment.size());
        assertEquals(oliver.getId(), noEnrollment.get(0).getId());
        System.out.println("F4 SUCCESS: Instructor with no enrollments -> " + noEnrollment.get(0).getFirstName() + " " + noEnrollment.get(0).getLastName());
    }
}
