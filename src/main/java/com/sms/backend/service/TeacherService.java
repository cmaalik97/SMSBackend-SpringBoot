package com.sms.backend.service;

import com.sms.backend.dto.TeacherDto;
import com.sms.backend.dto.TeacherRequest;
import com.sms.backend.entity.Role;
import com.sms.backend.entity.Teacher;
import com.sms.backend.entity.User;
import com.sms.backend.exception.BadRequestException;
import com.sms.backend.exception.ResourceNotFoundException;
import com.sms.backend.repository.TeacherRepository;
import com.sms.backend.repository.UserRepository;
import com.sms.backend.security.AuthUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;

    public TeacherService(TeacherRepository teacherRepository, UserRepository userRepository) {
        this.teacherRepository = teacherRepository;
        this.userRepository = userRepository;
    }

    private TeacherDto toDto(Teacher t) {
        return new TeacherDto(
                t.getTeacherId(), t.getUser().getUserId(), t.getFullName(), t.getEmail(), t.getPhone(),
                t.getQualification(), t.getSalary(), t.getJoiningDate(), t.getAddress()
        );
    }

    // Admin sees every teacher. A Teacher only ever sees their own profile
    // (this is how TeacherDashboard.jsx finds "my info" via getAll()[0]).
    public List<TeacherDto> getAll() {
        if (AuthUtil.isTeacher()) {
            return teacherRepository.findByUser_UserId(AuthUtil.currentUserId())
                    .map(t -> List.of(toDto(t)))
                    .orElse(List.of());
        }
        return teacherRepository.findAll().stream().map(this::toDto).toList();
    }

    public TeacherDto getOne(Long id) {
        return toDto(findEntity(id));
    }

    public Teacher findEntity(Long id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found."));
    }

    public TeacherDto create(TeacherRequest req) {
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new BadRequestException("The selected login account does not exist."));
        if (user.getRole() != Role.Teacher) {
            throw new BadRequestException("The selected account is not a Teacher account.");
        }
        if (teacherRepository.existsByUser_UserId(user.getUserId())) {
            throw new BadRequestException("This login account is already linked to a teacher profile.");
        }

        Teacher teacher = new Teacher();
        teacher.setUser(user);
        applyFields(teacher, req);
        return toDto(teacherRepository.save(teacher));
    }

    public TeacherDto update(Long id, TeacherRequest req) {
        Teacher teacher = findEntity(id);

        if (!teacher.getUser().getUserId().equals(req.getUserId())) {
            User newUser = userRepository.findById(req.getUserId())
                    .orElseThrow(() -> new BadRequestException("The selected login account does not exist."));
            if (newUser.getRole() != Role.Teacher) {
                throw new BadRequestException("The selected account is not a Teacher account.");
            }
            if (teacherRepository.existsByUser_UserId(newUser.getUserId())) {
                throw new BadRequestException("This login account is already linked to a teacher profile.");
            }
            teacher.setUser(newUser);
        }

        applyFields(teacher, req);
        return toDto(teacherRepository.save(teacher));
    }

    public void delete(Long id) {
        if (!teacherRepository.existsById(id)) {
            throw new ResourceNotFoundException("Teacher not found.");
        }
        teacherRepository.deleteById(id);
    }

    private void applyFields(Teacher teacher, TeacherRequest req) {
        teacher.setFullName(req.getFullName());
        teacher.setEmail(req.getEmail());
        teacher.setPhone(req.getPhone());
        teacher.setQualification(req.getQualification());
        teacher.setSalary(req.getSalary());
        teacher.setJoiningDate(req.getJoiningDate());
        teacher.setAddress(req.getAddress());
    }
}
