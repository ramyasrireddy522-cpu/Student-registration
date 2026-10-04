package com.example.studentregistration;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/students")
public class StudentController {
    private final StudentRepository repo;

    public StudentController(StudentRepository repo) {
        this.repo = repo;
    }

    private String check(Student s, Long id) {
        if (s.getName() == null || s.getName().isBlank()
                || s.getEmail() == null || s.getEmail().isBlank()
                || s.getCourse() == null || s.getCourse().isBlank()) {
            return "All fields are required";
        }
        if (!s.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            return "Enter a valid email";
        }
        for (Student other : repo.findAll()) {
            if (other.getEmail().equalsIgnoreCase(s.getEmail())
                    && !other.getId().equals(id)) {
                return "This email is already registered";
            }
        }
        return null;
    }

    @PostMapping
    public ResponseEntity<?> register(@RequestBody Student s) {
        String err = check(s, null);
        if (err != null) return ResponseEntity.badRequest().body(Map.of("error", err));
        return ResponseEntity.ok(repo.save(s));
    }

    @GetMapping
    public List<Student> all() {
        return repo.findAll();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Student s) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        String err = check(s, id);
        if (err != null) return ResponseEntity.badRequest().body(Map.of("error", err));
        s.setId(id);
        return ResponseEntity.ok(repo.save(s));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.ok(Map.of("deleted", id));
    }
}