package com.sms.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "classes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClassEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long classId;

    @Column(nullable = false, length = 60)
    private String className;

    @Column(nullable = false, length = 10)
    private String section;

    @Column(nullable = false, length = 30)
    private String roomNo;

    @ManyToOne
    @JoinColumn(name = "class_teacher_id")
    private Teacher classTeacher;
}
