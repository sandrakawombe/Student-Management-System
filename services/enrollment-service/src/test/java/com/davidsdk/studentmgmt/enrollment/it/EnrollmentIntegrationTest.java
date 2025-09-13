package com.davidsdk.studentmgmt.enrollment.it;

import com.davidsdk.studentmgmt.enrollment.EnrollmentServiceApplication;
import com.davidsdk.studentmgmt.enrollment.client.CourseClient;
import com.davidsdk.studentmgmt.enrollment.client.StudentClient;
import com.davidsdk.studentmgmt.enrollment.domain.Enrollment;
import com.davidsdk.studentmgmt.enrollment.repo.EnrollmentRepository;
import com.davidsdk.studentmgmt.enrollment.support.TestSecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.willDoNothing;

@SpringBootTest(classes = EnrollmentServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestSecurityConfig.class) // use the "guard is off" test security
class EnrollmentIntegrationTest {

    @LocalServerPort int port;

    @Autowired TestRestTemplate rest;
    @Autowired EnrollmentRepository repo;

    // Pretend neighbors (toy phones)
    @MockBean StudentClient studentClient;
    @MockBean CourseClient courseClient;

    @BeforeEach
    void stubDownstream() {
        willDoNothing().given(studentClient).verifyStudentExists(anyLong(), anyString());
        willDoNothing().given(courseClient).verifyCourseExists(anyLong(), anyString());
    }

    @Test
    void createEnrollment_persists() {
        var headers = new HttpHeaders();
        headers.setBearerAuth("anything"); // guard is off anyway
        headers.setContentType(MediaType.APPLICATION_JSON);
        var body = """
          {"studentId":1,"courseId":2}
        """;

        var res = rest.exchange(
                "http://localhost:" + port + "/enrollments",
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                Enrollment.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(repo.findByStudentIdAndCourseId(1L, 2L)).isPresent();
    }
}
