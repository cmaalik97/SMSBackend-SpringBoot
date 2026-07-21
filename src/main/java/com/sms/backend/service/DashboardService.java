package com.sms.backend.service;

import com.sms.backend.dto.DashboardSummaryDto;
import com.sms.backend.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DashboardService {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ClassRepository classRepository;
    private final SubjectRepository subjectRepository;
    private final AttendanceRepository attendanceRepository;
    private final FeeRepository feeRepository;
    private final ResultRepository resultRepository;
    private final UserRepository userRepository;

    public DashboardService(StudentRepository studentRepository, TeacherRepository teacherRepository,
                             ClassRepository classRepository, SubjectRepository subjectRepository,
                             AttendanceRepository attendanceRepository, FeeRepository feeRepository,
                             ResultRepository resultRepository, UserRepository userRepository) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.classRepository = classRepository;
        this.subjectRepository = subjectRepository;
        this.attendanceRepository = attendanceRepository;
        this.feeRepository = feeRepository;
        this.resultRepository = resultRepository;
        this.userRepository = userRepository;
    }

    public DashboardSummaryDto getSummary() {
        double feesCollected = feeRepository.findAll().stream().mapToDouble(f -> f.getAmountPaid()).sum();
        double feesDue = feeRepository.findAll().stream()
                .mapToDouble(f -> Math.max(0, f.getAmountDue() - f.getAmountPaid())).sum();

        LocalDate today = LocalDate.now();
        List<com.sms.backend.entity.Attendance> todayRecords = attendanceRepository.findAll().stream()
                .filter(a -> a.getAttendanceDate().equals(today))
                .toList();
        int attendanceRateToday = todayRecords.isEmpty() ? 0 :
                (int) Math.round(100.0 * todayRecords.stream().filter(a -> "Present".equals(a.getStatus())).count() / todayRecords.size());

        return new DashboardSummaryDto(
                studentRepository.count(),
                teacherRepository.count(),
                feesCollected,
                feesDue,
                attendanceRateToday,
                classRepository.count(),
                subjectRepository.count(),
                resultRepository.count(),
                userRepository.count()
        );
    }
}
