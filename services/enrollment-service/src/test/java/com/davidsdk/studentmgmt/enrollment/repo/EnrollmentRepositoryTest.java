package com.davidsdk.studentmgmt.enrollment.repo;

import com.davidsdk.studentmgmt.enrollment.domain.Enrollment;
import com.davidsdk.studentmgmt.enrollment.domain.EnrollmentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EnrollmentRepositoryTest {

    @Autowired
    private EnrollmentRepository repo;

    @Test
    void saveAndQueryByStudent() {
        var e1 = Enrollment.builder()
                .studentId(1L).courseId(100L)
                .status(EnrollmentStatus.ENROLLED).createdAt(Instant.now())
                .build();
        var e2 = Enrollment.builder()
                .studentId(1L).courseId(101L)
                .status(EnrollmentStatus.ENROLLED).createdAt(Instant.now())
                .build();
        repo.saveAll(List.of(e1, e2));

        var found = repo.findByStudentId(1L);
        assertThat(found).hasSize(2);
        assertThat(repo.findByStudentIdAndCourseId(1L, 100L)).isPresent();
    }
}
