package com.sms.backend.service;

import com.sms.backend.dto.AttendanceDto;
import com.sms.backend.dto.AttendanceRequest;
import com.sms.backend.entity.Attendance;
import com.sms.backend.entity.ClassEntity;
import com.sms.backend.entity.Student;
import com.sms.backend.exception.BadRequestException;
import com.sms.backend.exception.ResourceNotFoundException;
import com.sms.backend.repository.AttendanceRepository;
import com.sms.backend.repository.ClassRepository;
import com.sms.backend.repository.StudentRepository;
import com.sms.backend.security.AuthUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final ClassRepository classRepository;

    public AttendanceService(AttendanceRepository attendanceRepository, StudentRepository studentRepository, ClassRepository classRepository) {
        this.attendanceRepository = attendanceRepository;
        this.studentRepository = studentRepository;
        this.classRepository = classRepository;
    }

    private AttendanceDto toDto(Attendance a) {
        return new AttendanceDto(
                a.getAttendanceId(), a.getStudent().getStudentId(), a.getStudent().getFullName(),
                a.getClassEntity().getClassId(), a.getClassEntity().getClassName(),
                a.getAttendanceDate(), a.getStatus()
        );
    }

    // Admin and Teacher see every record (needed to mark attendance for any class).
    // A Student only ever sees their own attendance history.
    public List<AttendanceDto> getAll() {
        if (AuthUtil.isStudent()) {
            Student me = studentRepository.findByUser_UserId(AuthUtil.currentUserId()).orElse(null);
            if (me == null) return List.of();
            return attendanceRepository.findByStudent_StudentId(me.getStudentId()).stream().map(this::toDto).toList();
        }
        return attendanceRepository.findAll().stream().map(this::toDto).toList();
    }

    public AttendanceDto getOne(Long id) {
        return toDto(findEntity(id));
    }

    public Attendance findEntity(Long id) {
        return attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found."));
    }

    public AttendanceDto create(AttendanceRequest req) {
        validateStatus(req.getStatus());

        attendanceRepository.findByStudent_StudentIdAndAttendanceDate(req.getStudentId(), req.getAttendanceDate())
                .ifPresent(a -> { throw new BadRequestException("This student is already marked for this date."); });

        Student student = studentRepository.findById(req.getStudentId())
                .orElseThrow(() -> new BadRequestException("The selected student does not exist."));
        ClassEntity classEntity = classRepository.findById(req.getClassId())
                .orElseThrow(() -> new BadRequestException("The selected class does not exist."));

        Attendance a = new Attendance();
        a.setStudent(student);
        a.setClassEntity(classEntity);
        a.setAttendanceDate(req.getAttendanceDate());
        a.setStatus(req.getStatus());

        return toDto(attendanceRepository.save(a));
    }

    public AttendanceDto update(Long id, AttendanceRequest req) {
        validateStatus(req.getStatus());
        Attendance a = findEntity(id);

        Student student = studentRepository.findById(req.getStudentId())
                .orElseThrow(() -> new BadRequestException("The selected student does not exist."));
        ClassEntity classEntity = classRepository.findById(req.getClassId())
                .orElseThrow(() -> new BadRequestException("The selected class does not exist."));

        a.setStudent(student);
        a.setClassEntity(classEntity);
        a.setAttendanceDate(req.getAttendanceDate());
        a.setStatus(req.getStatus());

        return toDto(attendanceRepository.save(a));
    }

    public void delete(Long id) {
        if (!attendanceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Attendance record not found.");
        }
        attendanceRepository.deleteById(id);
    }

    private void validateStatus(String status) {
        if (!List.of("Present", "Absent", "Late").contains(status)) {
            throw new BadRequestException("Status must be Present, Absent or Late.");
        }
    }
}
