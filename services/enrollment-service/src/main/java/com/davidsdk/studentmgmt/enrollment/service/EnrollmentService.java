package com.davidsdk.studentmgmt.enrollment.service;

import com.davidsdk.studentmgmt.enrollment.domain.Enrollment;

public interface EnrollmentService {
    Enrollment enroll(Long studentId, Long courseId, String bearerToken);
    void cancel(Long id);
}
