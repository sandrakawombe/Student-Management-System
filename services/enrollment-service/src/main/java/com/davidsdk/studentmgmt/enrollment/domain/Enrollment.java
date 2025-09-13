package com.davidsdk.studentmgmt.enrollment.domain;

import jakarta.persistence.*;
import lombok.*;
import jdk.jfr.DataAmount;
import java.time.Instant;


@Builder
@NoArgsConstructor @AllArgsConstructor
@Setter @Getter
@Entity
@Table(name = "enrollments",
       uniqueConstraints = @UniqueConstraint(columnNames = {"studentId", "courseId"}))
//@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Enrollment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long studentId;
    private Long courseId;

    @Enumerated(EnumType.STRING)
   // @Builder.Default
    private EnrollmentStatus status = EnrollmentStatus.ENROLLED;

   // @Builder.Default
    private Instant createdAt = Instant.now();

//    public Enrollment(Long id, Long studentId, Long courseId, EnrollmentStatus status, Instant createdAt) {
//        this.id = id;
//        this.studentId = studentId;
//        this.courseId = courseId;
//        this.status = status;
//        this.createdAt = createdAt;
//    }
//
//    public Enrollment() {
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public Long getStudentId() {
//        return studentId;
//    }
//
//    public void setStudentId(Long studentId) {
//        this.studentId = studentId;
//    }
//
//    public Long getCourseId() {
//        return courseId;
//    }
//
//    public void setCourseId(Long courseId) {
//        this.courseId = courseId;
//    }
//
//    public EnrollmentStatus getStatus() {
//        return status;
//    }
//
//    public void setStatus(EnrollmentStatus status) {
//        this.status = status;
//    }
//
//    public Instant getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setCreatedAt(Instant createdAt) {
//        this.createdAt = createdAt;
//    }
}
