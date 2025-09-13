package com.davidsdk.studentmgmt.enrollment.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Component
public class CourseClient {

    private final WebClient webClient;
    private final String baseUrl;

    public CourseClient(WebClient webClient,
                        @Value("${downstream.courseBaseUrl}") String baseUrl) {
        this.webClient = webClient;
        this.baseUrl = baseUrl;
    }

    /** Throws if course is missing or unreachable */
    public void verifyCourseExists(Long courseId, String bearerToken) {
        webClient.get().uri(baseUrl + "/courses/{id}", courseId)
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()
                .toBodilessEntity()
                .block(Duration.ofSeconds(2));
    }
}
