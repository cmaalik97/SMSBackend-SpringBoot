package com.sms.backend.service;

import com.sms.backend.dto.ClassDto;
import com.sms.backend.dto.ClassRequest;
import com.sms.backend.entity.ClassEntity;
import com.sms.backend.entity.Teacher;
import com.sms.backend.exception.BadRequestException;
import com.sms.backend.exception.ResourceNotFoundException;
import com.sms.backend.repository.ClassRepository;
import com.sms.backend.repository.TeacherRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassService {

    private final ClassRepository classRepository;
    private final TeacherRepository teacherRepository;

    public ClassService(ClassRepository classRepository, TeacherRepository teacherRepository) {
        this.classRepository = classRepository;
        this.teacherRepository = teacherRepository;
    }

    private ClassDto toDto(ClassEntity c) {
        return new ClassDto(
                c.getClassId(), c.getClassName(), c.getSection(), c.getRoomNo(),
                c.getClassTeacher() != null ? c.getClassTeacher().getTeacherId() : null,
                c.getClassTeacher() != null ? c.getClassTeacher().getFullName() : null
        );
    }

    public List<ClassDto> getAll() {
        return classRepository.findAll().stream().map(this::toDto).toList();
    }

    public ClassDto getOne(Long id) {
        return toDto(findEntity(id));
    }

    public ClassEntity findEntity(Long id) {
        return classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found."));
    }

    public ClassDto create(ClassRequest req) {
        ClassEntity c = new ClassEntity();
        applyFields(c, req);
        return toDto(classRepository.save(c));
    }

    public ClassDto update(Long id, ClassRequest req) {
        ClassEntity c = findEntity(id);
        applyFields(c, req);
        return toDto(classRepository.save(c));
    }

    public void delete(Long id) {
        if (!classRepository.existsById(id)) {
            throw new ResourceNotFoundException("Class not found.");
        }
        classRepository.deleteById(id);
    }

    private void applyFields(ClassEntity c, ClassRequest req) {
        c.setClassName(req.getClassName());
        c.setSection(req.getSection());
        c.setRoomNo(req.getRoomNo());

        if (req.getClassTeacherId() != null) {
            Teacher teacher = teacherRepository.findById(req.getClassTeacherId())
                    .orElseThrow(() -> new BadRequestException("The selected class teacher does not exist."));
            c.setClassTeacher(teacher);
        } else {
            c.setClassTeacher(null);
        }
    }
}
