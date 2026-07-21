package com.sms.backend.service;

import com.sms.backend.dto.ResultDto;
import com.sms.backend.dto.ResultRequest;
import com.sms.backend.entity.Result;
import com.sms.backend.entity.Student;
import com.sms.backend.entity.Subject;
import com.sms.backend.exception.BadRequestException;
import com.sms.backend.exception.ResourceNotFoundException;
import com.sms.backend.repository.ResultRepository;
import com.sms.backend.repository.StudentRepository;
import com.sms.backend.repository.SubjectRepository;
import com.sms.backend.security.AuthUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ResultService {

    private static final Map<String, Double> MAX_MARKS = Map.of(
            "Quiz", 10.0, "Midterm", 40.0, "Final", 100.0
    );

    private final ResultRepository resultRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;

    public ResultService(ResultRepository resultRepository, StudentRepository studentRepository, SubjectRepository subjectRepository) {
        this.resultRepository = resultRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
    }

    private ResultDto toDto(Result r) {
        return new ResultDto(
                r.getResultId(), r.getStudent().getStudentId(), r.getStudent().getFullName(),
                r.getSubject().getSubjectId(), r.getSubject().getSubjectName(),
                r.getExamType(), r.getMarks(), r.getMaxMarks(), r.getGrade(), r.getExamDate()
        );
    }

    // Admin sees every result. A Student only ever sees their own.
    public List<ResultDto> getAll() {
        if (AuthUtil.isStudent()) {
            Student me = studentRepository.findByUser_UserId(AuthUtil.currentUserId()).orElse(null);
            if (me == null) return List.of();
            return resultRepository.findByStudent_StudentId(me.getStudentId()).stream().map(this::toDto).toList();
        }
        return resultRepository.findAll().stream().map(this::toDto).toList();
    }

    public ResultDto getOne(Long id) {
        return toDto(findEntity(id));
    }

    public Result findEntity(Long id) {
        return resultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Result not found."));
    }

    public ResultDto create(ResultRequest req) {
        Student student = studentRepository.findById(req.getStudentId())
                .orElseThrow(() -> new BadRequestException("The selected student does not exist."));
        Subject subject = subjectRepository.findById(req.getSubjectId())
                .orElseThrow(() -> new BadRequestException("The selected subject does not exist."));

        Result r = new Result();
        r.setStudent(student);
        r.setSubject(subject);
        applyFields(r, req);
        return toDto(resultRepository.save(r));
    }

    public ResultDto update(Long id, ResultRequest req) {
        Result r = findEntity(id);
        Student student = studentRepository.findById(req.getStudentId())
                .orElseThrow(() -> new BadRequestException("The selected student does not exist."));
        Subject subject = subjectRepository.findById(req.getSubjectId())
                .orElseThrow(() -> new BadRequestException("The selected subject does not exist."));
        r.setStudent(student);
        r.setSubject(subject);
        applyFields(r, req);
        return toDto(resultRepository.save(r));
    }

    public void delete(Long id) {
        if (!resultRepository.existsById(id)) {
            throw new ResourceNotFoundException("Result not found.");
        }
        resultRepository.deleteById(id);
    }

    // maxMarks and grade are always derived here from examType/marks - the
    // client's values (if any) are ignored so results can't be tampered with.
    private void applyFields(Result r, ResultRequest req) {
        Double maxMarks = MAX_MARKS.get(req.getExamType());
        if (maxMarks == null) {
            throw new BadRequestException("Exam type must be Quiz, Midterm or Final.");
        }
        if (req.getMarks() > maxMarks) {
            throw new BadRequestException("Marks (" + req.getMarks() + ") cannot exceed Max Marks (" + maxMarks + ").");
        }

        r.setExamType(req.getExamType());
        r.setMarks(req.getMarks());
        r.setMaxMarks(maxMarks);
        r.setExamDate(req.getExamDate());
        r.setGrade(computeGrade(req.getMarks(), maxMarks));
    }

    private String computeGrade(double marks, double maxMarks) {
        double pct = (marks / maxMarks) * 100;
        if (pct >= 90) return "A";
        if (pct >= 75) return "B";
        if (pct >= 60) return "C";
        if (pct >= 45) return "D";
        return "F";
    }
}
