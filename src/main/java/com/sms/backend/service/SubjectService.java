package com.sms.backend.service;

import com.sms.backend.dto.SubjectDto;
import com.sms.backend.dto.SubjectRequest;
import com.sms.backend.entity.ClassEntity;
import com.sms.backend.entity.Subject;
import com.sms.backend.entity.Teacher;
import com.sms.backend.exception.BadRequestException;
import com.sms.backend.exception.ResourceNotFoundException;
import com.sms.backend.repository.ClassRepository;
import com.sms.backend.repository.SubjectRepository;
import com.sms.backend.repository.TeacherRepository;
import com.sms.backend.security.AuthUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final ClassRepository classRepository;
    private final TeacherRepository teacherRepository;

    public SubjectService(SubjectRepository subjectRepository, ClassRepository classRepository, TeacherRepository teacherRepository) {
        this.subjectRepository = subjectRepository;
        this.classRepository = classRepository;
        this.teacherRepository = teacherRepository;
    }

    private SubjectDto toDto(Subject s) {
        return new SubjectDto(
                s.getSubjectId(), s.getSubjectName(), s.getSubjectCode(),
                s.getClassEntity().getClassId(), s.getClassEntity().getClassName(),
                s.getTeacher().getTeacherId(), s.getTeacher().getFullName()
        );
    }

    // Admin sees every subject. A Teacher only sees the subjects assigned to them
    // (used to build "My Subjects & Classes" on TeacherDashboard.jsx).
    public List<SubjectDto> getAll() {
        if (AuthUtil.isTeacher()) {
            Teacher me = teacherRepository.findByUser_UserId(AuthUtil.currentUserId()).orElse(null);
            if (me == null) return List.of();
            return subjectRepository.findByTeacher_TeacherId(me.getTeacherId()).stream().map(this::toDto).toList();
        }
        return subjectRepository.findAll().stream().map(this::toDto).toList();
    }

    public SubjectDto getOne(Long id) {
        return toDto(findEntity(id));
    }

    public Subject findEntity(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found."));
    }

    public SubjectDto create(SubjectRequest req) {
        Subject s = new Subject();
        applyFields(s, req);
        return toDto(subjectRepository.save(s));
    }

    public SubjectDto update(Long id, SubjectRequest req) {
        Subject s = findEntity(id);
        applyFields(s, req);
        return toDto(subjectRepository.save(s));
    }

    public void delete(Long id) {
        if (!subjectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Subject not found.");
        }
        subjectRepository.deleteById(id);
    }

    private void applyFields(Subject s, SubjectRequest req) {
        s.setSubjectName(req.getSubjectName());
        s.setSubjectCode(req.getSubjectCode());

        ClassEntity classEntity = classRepository.findById(req.getClassId())
                .orElseThrow(() -> new BadRequestException("The selected class does not exist."));
        s.setClassEntity(classEntity);

        Teacher teacher = teacherRepository.findById(req.getTeacherId())
                .orElseThrow(() -> new BadRequestException("The selected teacher does not exist."));
        s.setTeacher(teacher);
    }
}
