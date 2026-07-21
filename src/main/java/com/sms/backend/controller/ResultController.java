package com.sms.backend.controller;

import com.sms.backend.dto.ResultDto;
import com.sms.backend.dto.ResultRequest;
import com.sms.backend.service.ResultService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/results")
@PreAuthorize("hasAnyRole('Admin','Student')") // Student sees only their own (handled in service)
public class ResultController {

    private final ResultService resultService;

    public ResultController(ResultService resultService) {
        this.resultService = resultService;
    }

    @GetMapping
    public List<ResultDto> getAll() {
        return resultService.getAll();
    }

    @GetMapping("/{id}")
    public ResultDto getOne(@PathVariable Long id) {
        return resultService.getOne(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<ResultDto> create(@Valid @RequestBody ResultRequest request) {
        return ResponseEntity.ok(resultService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResultDto update(@PathVariable Long id, @Valid @RequestBody ResultRequest request) {
        return resultService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        resultService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
