package com.sms.backend.controller;

import com.sms.backend.dto.ClassDto;
import com.sms.backend.dto.ClassRequest;
import com.sms.backend.service.ClassService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
@PreAuthorize("hasAnyRole('Admin','Teacher','Student')") // everyone needs this for dropdowns/labels
public class ClassController {

    private final ClassService classService;

    public ClassController(ClassService classService) {
        this.classService = classService;
    }

    @GetMapping
    public List<ClassDto> getAll() {
        return classService.getAll();
    }

    @GetMapping("/{id}")
    public ClassDto getOne(@PathVariable Long id) {
        return classService.getOne(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<ClassDto> create(@Valid @RequestBody ClassRequest request) {
        return ResponseEntity.ok(classService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ClassDto update(@PathVariable Long id, @Valid @RequestBody ClassRequest request) {
        return classService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        classService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
