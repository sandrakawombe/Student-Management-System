package com.davidsdk.studentmgmt.enrollment.service;

import com.davidsdk.studentmgmt.enrollment.client.CourseClient;
import com.davidsdk.studentmgmt.enrollment.client.StudentClient;
import com.davidsdk.studentmgmt.enrollment.domain.Enrollment;
import com.davidsdk.studentmgmt.enrollment.domain.EnrollmentStatus;
import com.davidsdk.studentmgmt.enrollment.repo.EnrollmentRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository repo;
    private final StudentClient studentClient;
    private final CourseClient courseClient;

    public EnrollmentServiceImpl(EnrollmentRepository repo,
                                 StudentClient studentClient,
                                 CourseClient courseClient) {
        this.repo = repo;
        this.studentClient = studentClient;
        this.courseClient = courseClient;
    }

    @Override
    public Enrollment enroll(Long studentId, Long courseId, String bearerToken) {
        var existing = repo.findByStudentIdAndCourseId(studentId, courseId);
        if (existing.isPresent()) return existing.get();

        // Ask the other shops via our walkie-talkies
        studentClient.verifyStudentExists(studentId, bearerToken);
        courseClient.verifyCourseExists(courseId, bearerToken);

        var saved = repo.save(Enrollment.builder()
                .studentId(studentId)
                .courseId(courseId)
                .status(EnrollmentStatus.ENROLLED)
                .createdAt(Instant.now())
                .build());

        return saved;
    }

    @Override
    public void cancel(Long id) {
        var e = repo.findById(id).orElseThrow();
        e.setStatus(EnrollmentStatus.CANCELLED);
        repo.save(e);
    }
}
