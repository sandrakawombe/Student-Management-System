package com.davidsdk.studentmgmt.enrollment.controller;

import com.davidsdk.studentmgmt.enrollment.domain.Enrollment;
import com.davidsdk.studentmgmt.enrollment.domain.EnrollmentStatus;
import com.davidsdk.studentmgmt.enrollment.service.EnrollmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = EnrollmentController.class)
@AutoConfigureMockMvc(addFilters = false) // turn off security filters for this slice test
class EnrollmentControllerTest {

    @Autowired MockMvc mvc;

    // Controller talks to service -> we mock it
    @MockBean EnrollmentService enrollmentService;

    @Test
    void create_returns201() throws Exception {
        var saved = Enrollment.builder()
                .id(99L).studentId(1L).courseId(2L)
                .status(EnrollmentStatus.ENROLLED).createdAt(Instant.now())
                .build();

        given(enrollmentService.enroll(eq(1L), eq(2L), anyString())).willReturn(saved);

        mvc.perform(post("/enrollments")
                .header(HttpHeaders.AUTHORIZATION, "Bearer test")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"studentId\":1,\"courseId\":2}"))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.id").value(99));
    }

    @Test
    void listByStudent_returns200() throws Exception {
        mvc.perform(get("/enrollments?studentId=1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer test"))
           .andExpect(status().isOk());
    }
}
