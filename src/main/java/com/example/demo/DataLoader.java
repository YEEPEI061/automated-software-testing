package com.example.demo;

import com.example.demo.model.Course;
import com.example.demo.model.Instructor;
import com.example.demo.model.Student;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.InstructorRepository;
import com.example.demo.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Profile("!test")
public class DataLoader implements CommandLineRunner {

    private final StudentRepository studentRepository;
    private final InstructorRepository instructorRepository;
    private final CourseRepository courseRepository;

    public DataLoader(StudentRepository studentRepository,
                      InstructorRepository instructorRepository,
                      CourseRepository courseRepository) {
        this.studentRepository = studentRepository;
        this.instructorRepository = instructorRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Create instructors
        Instructor i1 = instructorRepository.save(new Instructor("Stephen", "Lee"));
        Instructor i2 = instructorRepository.save(new Instructor("Jenny", "Wong"));

        // Create students
        Student s1 = studentRepository.save(new Student("John", "Low"));
        Student s2 = studentRepository.save(new Student("Alice", "Tan"));

        // Create courses and enroll students
        Course c1 = new Course("Math 101", i1);
        c1.setStudents(Set.of(s1, s2));
        courseRepository.save(c1);

        Course c2 = new Course("Science 101", i2);
        c2.setStudents(Set.of(s2));
        courseRepository.save(c2);

        System.out.println("Sample data loaded successfully!");
    }
}
