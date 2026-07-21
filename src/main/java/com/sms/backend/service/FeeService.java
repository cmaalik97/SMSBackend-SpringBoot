package com.sms.backend.service;

import com.sms.backend.dto.FeeDto;
import com.sms.backend.dto.FeeRequest;
import com.sms.backend.entity.Fee;
import com.sms.backend.entity.Student;
import com.sms.backend.exception.BadRequestException;
import com.sms.backend.exception.ResourceNotFoundException;
import com.sms.backend.repository.FeeRepository;
import com.sms.backend.repository.StudentRepository;
import com.sms.backend.security.AuthUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeeService {

    private final FeeRepository feeRepository;
    private final StudentRepository studentRepository;

    public FeeService(FeeRepository feeRepository, StudentRepository studentRepository) {
        this.feeRepository = feeRepository;
        this.studentRepository = studentRepository;
    }

    private FeeDto toDto(Fee f) {
        return new FeeDto(
                f.getFeeId(), f.getStudent().getStudentId(), f.getStudent().getFullName(),
                f.getFeeType(), f.getAmountDue(), f.getAmountPaid(), f.getDueDate(), f.getStatus()
        );
    }

    // Admin sees every fee record. A Student only ever sees their own.
    public List<FeeDto> getAll() {
        if (AuthUtil.isStudent()) {
            Student me = studentRepository.findByUser_UserId(AuthUtil.currentUserId()).orElse(null);
            if (me == null) return List.of();
            return feeRepository.findByStudent_StudentId(me.getStudentId()).stream().map(this::toDto).toList();
        }
        return feeRepository.findAll().stream().map(this::toDto).toList();
    }

    public FeeDto getOne(Long id) {
        return toDto(findEntity(id));
    }

    public Fee findEntity(Long id) {
        return feeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fee record not found."));
    }

    public FeeDto create(FeeRequest req) {
        Student student = studentRepository.findById(req.getStudentId())
                .orElseThrow(() -> new BadRequestException("The selected student does not exist."));

        Fee fee = new Fee();
        fee.setStudent(student);
        applyFields(fee, req);
        return toDto(feeRepository.save(fee));
    }

    public FeeDto update(Long id, FeeRequest req) {
        Fee fee = findEntity(id);
        Student student = studentRepository.findById(req.getStudentId())
                .orElseThrow(() -> new BadRequestException("The selected student does not exist."));
        fee.setStudent(student);
        applyFields(fee, req);
        return toDto(feeRepository.save(fee));
    }

    public void delete(Long id) {
        if (!feeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Fee record not found.");
        }
        feeRepository.deleteById(id);
    }

    // The frontend never sends `status` - it is always derived here so a
    // client can never fake a "Paid" record.
    private void applyFields(Fee fee, FeeRequest req) {
        if (req.getAmountPaid() > req.getAmountDue()) {
            throw new BadRequestException("Amount paid cannot exceed amount due.");
        }
        fee.setFeeType(req.getFeeType());
        fee.setAmountDue(req.getAmountDue());
        fee.setAmountPaid(req.getAmountPaid());
        fee.setDueDate(req.getDueDate());
        fee.setStatus(computeStatus(req.getAmountDue(), req.getAmountPaid()));
    }

    private String computeStatus(double due, double paid) {
        if (paid <= 0) return "Unpaid";
        if (paid < due) return "Partial";
        return "Paid";
    }
}
