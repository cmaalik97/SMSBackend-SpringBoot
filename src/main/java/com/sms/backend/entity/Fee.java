package com.sms.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "fees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Fee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feeId;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false, length = 30)
    private String feeType;

    @Column(nullable = false)
    private Double amountDue;

    @Column(nullable = false)
    private Double amountPaid;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false, length = 10)
    private String status;
}
