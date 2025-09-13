package com.davidsdk.studentmgmt.enrollment.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Component
public class StudentClient {

    private final WebClient webClient;
    private final String baseUrl;

    public StudentClient(WebClient webClient,
                         @Value("${downstream.studentBaseUrl}") String baseUrl) {
        this.webClient = webClient;
        this.baseUrl = baseUrl;
    }

    /** Throws if student is missing or unreachable */
    public void verifyStudentExists(Long studentId, String bearerToken) {
        webClient.get().uri(baseUrl + "/students/{id}", studentId)
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()
                .toBodilessEntity()
                .block(Duration.ofSeconds(2));
    }
}
