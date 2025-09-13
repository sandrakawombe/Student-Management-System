package com.davidsdk.studentmgmt.enrollment.service;

import com.davidsdk.studentmgmt.enrollment.client.CourseClient;
import com.davidsdk.studentmgmt.enrollment.client.StudentClient;
import com.davidsdk.studentmgmt.enrollment.domain.Enrollment;
import com.davidsdk.studentmgmt.enrollment.domain.EnrollmentStatus;
import com.davidsdk.studentmgmt.enrollment.repo.EnrollmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class EnrollmentServiceImplTest {

    private EnrollmentRepository repo;
    private StudentClient studentClient;
    private CourseClient courseClient;

    private EnrollmentServiceImpl service;

    @BeforeEach
    void setUp() {
        repo = mock(EnrollmentRepository.class);
        studentClient = mock(StudentClient.class);
        courseClient = mock(CourseClient.class);

        // Service now depends on the two clients (Option A)
        service = new EnrollmentServiceImpl(repo, studentClient, courseClient);
    }

    @Test
    void enroll_createsWhenNotExists_andCallsDownstream() {
        Long studentId = 10L, courseId = 200L;

        // Repo says there is no existing enrollment
        when(repo.findByStudentIdAndCourseId(studentId, courseId))
                .thenReturn(Optional.empty());

        // Save returns entity with generated id
        when(repo.save(any(Enrollment.class))).thenAnswer(inv -> {
            Enrollment e = inv.getArgument(0);
            e.setId(1L);
            return e;
        });

        // Mock other services’ verification calls (no exception -> OK)
        doNothing().when(studentClient).verifyStudentExists(eq(studentId), eq("Bearer xyz"));
        doNothing().when(courseClient).verifyCourseExists(eq(courseId), eq("Bearer xyz"));

        var saved = service.enroll(studentId, courseId, "Bearer xyz");

        assertThat(saved.getId()).isEqualTo(1L);
        assertThat(saved.getStatus()).isEqualTo(EnrollmentStatus.ENROLLED);

        // Ensure we persisted with createdAt set
        var captor = ArgumentCaptor.forClass(Enrollment.class);
        verify(repo).save(captor.capture());
        assertThat(captor.getValue().getCreatedAt()).isNotNull();

        // Ensure we actually asked the two downstream services
        verify(studentClient).verifyStudentExists(studentId, "Bearer xyz");
        verify(courseClient).verifyCourseExists(courseId, "Bearer xyz");
    }

    @Test
    void enroll_returnsExistingIfDuplicate_andSkipsDownstream() {
        Long studentId = 10L, courseId = 200L;

        var existing = Enrollment.builder()
                .id(5L).studentId(studentId).courseId(courseId)
                .status(EnrollmentStatus.ENROLLED).createdAt(Instant.now())
                .build();

        // Repo says it already exists
        when(repo.findByStudentIdAndCourseId(studentId, courseId))
                .thenReturn(Optional.of(existing));

        var result = service.enroll(studentId, courseId, "Bearer xyz");

        assertThat(result.getId()).isEqualTo(5L);

        // No new save and no downstream calls
        verify(repo, never()).save(any());
        verifyNoInteractions(studentClient, courseClient);
    }

    @Test
    void cancel_updatesStatusToCancelled() {
        var existing = Enrollment.builder()
                .id(7L).studentId(10L).courseId(200L)
                .status(EnrollmentStatus.ENROLLED).createdAt(Instant.now())
                .build();

        when(repo.findById(7L)).thenReturn(Optional.of(existing));

        service.cancel(7L);

        assertThat(existing.getStatus()).isEqualTo(EnrollmentStatus.CANCELLED);
        verify(repo).save(existing);
    }
}
