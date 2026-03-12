package com.example.demo.controller;

import com.example.demo.model.Instructor;
import com.example.demo.service.InstructorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/instructors")
public class InstructorController {

    private final InstructorService instructorService;

    public InstructorController(InstructorService instructorService) {
        this.instructorService = instructorService;
    }

    // F3 – Most active instructor
    @GetMapping("/most-active")
    public Instructor getMostActiveInstructor() {
        return instructorService.getMostActiveInstructor();
    }

    // F4 – Instructors with no enrollments
    @GetMapping("/no-enrollments")
    public List<Instructor> getInstructorsWithNoEnrollments() {
        return instructorService.getInstructorsWithNoEnrollments();
    }
}
