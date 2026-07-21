package com.sms.backend.service;

import com.sms.backend.dto.StudentDto;
import com.sms.backend.dto.StudentRequest;
import com.sms.backend.entity.ClassEntity;
import com.sms.backend.entity.Role;
import com.sms.backend.entity.Student;
import com.sms.backend.entity.User;
import com.sms.backend.exception.BadRequestException;
import com.sms.backend.exception.ResourceNotFoundException;
import com.sms.backend.repository.ClassRepository;
import com.sms.backend.repository.StudentRepository;
import com.sms.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final ClassRepository classRepository;

    public StudentService(StudentRepository studentRepository, UserRepository userRepository, ClassRepository classRepository) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.classRepository = classRepository;
    }

    private StudentDto toDto(Student s) {
        return new StudentDto(
                s.getStudentId(), s.getUser().getUserId(), s.getFullName(), s.getEmail(), s.getPhone(),
                s.getDob(), s.getGender(), s.getClassEntity().getClassId(), s.getClassEntity().getClassName(),
                s.getParentName(), s.getParentPhone(), s.getAddress()
        );
    }

    public List<StudentDto> getAll() {
        return studentRepository.findAll().stream().map(this::toDto).toList();
    }

    public StudentDto getOne(Long id) {
        return toDto(findEntity(id));
    }

    public Student findEntity(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found."));
    }

    public StudentDto create(StudentRequest req) {
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new BadRequestException("The selected login account does not exist."));
        if (user.getRole() != Role.Student) {
            throw new BadRequestException("The selected account is not a Student account.");
        }
        if (studentRepository.existsByUser_UserId(user.getUserId())) {
            throw new BadRequestException("This login account is already linked to a student profile.");
        }
        ClassEntity classEntity = classRepository.findById(req.getClassId())
                .orElseThrow(() -> new BadRequestException("The selected class does not exist."));

        Student student = new Student();
        applyRequest(student, req, user, classEntity);
        return toDto(studentRepository.save(student));
    }

    public StudentDto update(Long id, StudentRequest req) {
        Student student = findEntity(id);
        ClassEntity classEntity = classRepository.findById(req.getClassId())
                .orElseThrow(() -> new BadRequestException("The selected class does not exist."));

        // Allow re-linking to a different account only if that account isn't already used elsewhere.
        if (!student.getUser().getUserId().equals(req.getUserId())) {
            User newUser = userRepository.findById(req.getUserId())
                    .orElseThrow(() -> new BadRequestException("The selected login account does not exist."));
            if (newUser.getRole() != Role.Student) {
                throw new BadRequestException("The selected account is not a Student account.");
            }
            if (studentRepository.existsByUser_UserId(newUser.getUserId())) {
                throw new BadRequestException("This login account is already linked to a student profile.");
            }
            student.setUser(newUser);
        }

        student.setFullName(req.getFullName());
        student.setEmail(req.getEmail());
        student.setPhone(req.getPhone());
        student.setDob(req.getDob());
        student.setGender(req.getGender());
        student.setClassEntity(classEntity);
        student.setParentName(req.getParentName());
        student.setParentPhone(req.getParentPhone());
        student.setAddress(req.getAddress());

        return toDto(studentRepository.save(student));
    }

    public void delete(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student not found.");
        }
        studentRepository.deleteById(id);
    }

    private void applyRequest(Student student, StudentRequest req, User user, ClassEntity classEntity) {
        student.setUser(user);
        student.setFullName(req.getFullName());
        student.setEmail(req.getEmail());
        student.setPhone(req.getPhone());
        student.setDob(req.getDob());
        student.setGender(req.getGender());
        student.setClassEntity(classEntity);
        student.setParentName(req.getParentName());
        student.setParentPhone(req.getParentPhone());
        student.setAddress(req.getAddress());
    }
}
