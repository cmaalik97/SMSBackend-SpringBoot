package com.sms.backend.controller;

import com.sms.backend.dto.FeeDto;
import com.sms.backend.dto.FeeRequest;
import com.sms.backend.service.FeeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fees")
@PreAuthorize("hasAnyRole('Admin','Student')") // Student sees only their own (handled in service)
public class FeeController {

    private final FeeService feeService;

    public FeeController(FeeService feeService) {
        this.feeService = feeService;
    }

    @GetMapping
    public List<FeeDto> getAll() {
        return feeService.getAll();
    }

    @GetMapping("/{id}")
    public FeeDto getOne(@PathVariable Long id) {
        return feeService.getOne(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<FeeDto> create(@Valid @RequestBody FeeRequest request) {
        return ResponseEntity.ok(feeService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    public FeeDto update(@PathVariable Long id, @Valid @RequestBody FeeRequest request) {
        return feeService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        feeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
