package com.sms.backend.repository;

import com.sms.backend.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByStudent_StudentId(Long studentId);
    Optional<Attendance> findByStudent_StudentIdAndAttendanceDate(Long studentId, LocalDate date);
}
