package com.davidsdk.studentmgmt.enrollment.support;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * In tests, let everyone in (no auth). We override the app bean by using the same name: "filterChain".
 */
@TestConfiguration
public class TestSecurityConfig {

    @Bean(name = "filterChain")
    public SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(a -> a.anyRequest().permitAll());
        return http.build();
    }
}
