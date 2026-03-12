package com.example.demo.controller;

import com.example.demo.model.Course;
import com.example.demo.model.Student;
import com.example.demo.service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.example.demo.repository.StudentRepository;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;
    private final StudentRepository studentRepository;

    public StudentController(StudentService studentService, StudentRepository studentRepository) {
        this.studentService = studentService;
        this.studentRepository = studentRepository;
    }

    // F1 – Retrieve Student Enrollments
    @GetMapping("/search")
    public List<Course> getEnrollmentsByName(
            @RequestParam String firstName,
            @RequestParam String lastName) {

        String trimmedFirst = firstName.trim();
        String trimmedLast = lastName.trim();

        if (trimmedFirst.isBlank() || trimmedLast.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please enter both first name and last name.");
        }

        List<Student> students = studentRepository.findByFirstNameIgnoreCaseAndLastNameIgnoreCase(trimmedFirst, trimmedLast);

        if (students.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Student \"" + firstName + " " + lastName + "\" not found."
            );
        }

        Student student = students.get(0);

        return studentService.getStudentEnrollments(student.getId());
    }

}
