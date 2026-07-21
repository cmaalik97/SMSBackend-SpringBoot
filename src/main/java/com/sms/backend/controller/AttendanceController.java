package com.sms.backend.controller;

import com.sms.backend.dto.AttendanceDto;
import com.sms.backend.dto.AttendanceRequest;
import com.sms.backend.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@PreAuthorize("hasAnyRole('Admin','Teacher','Student')") // Student sees only their own (handled in service)
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping
    public List<AttendanceDto> getAll() {
        return attendanceService.getAll();
    }

    @GetMapping("/{id}")
    public AttendanceDto getOne(@PathVariable Long id) {
        return attendanceService.getOne(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('Admin','Teacher')")
    public ResponseEntity<AttendanceDto> create(@Valid @RequestBody AttendanceRequest request) {
        return ResponseEntity.ok(attendanceService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('Admin','Teacher')")
    public AttendanceDto update(@PathVariable Long id, @Valid @RequestBody AttendanceRequest request) {
        return attendanceService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('Admin','Teacher')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        attendanceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
