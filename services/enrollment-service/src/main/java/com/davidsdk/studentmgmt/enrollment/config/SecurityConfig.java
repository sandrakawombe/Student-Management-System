package com.davidsdk.studentmgmt.enrollment.config;

import com.davidsdk.studentmgmt.enrollment.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .anyRequest().authenticated()
            );
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        var cfg = new CorsConfiguration();
        cfg.addAllowedOriginPattern("*");
        cfg.addAllowedHeader("*");
        cfg.addAllowedMethod("*");
        var src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", cfg);
        return new CorsFilter(src);
    }
}

@Component
@RequiredArgsConstructor
class JwtAuthFilter extends org.springframework.web.filter.OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, jakarta.servlet.ServletException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Claims claims = jwtUtil.parse(token);
                String username = claims.getSubject();

                // NOTE the wildcard here:
                Collection<? extends GrantedAuthority> authorities = extractAuthorities(claims.get("roles"));

                var principal = User.withUsername(username)
                        .password("") // not used
                        .authorities(authorities)
                        .build();

                var auth = new UsernamePasswordAuthenticationToken(principal, token, principal.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (Exception ignored) { }
        }
        chain.doFilter(request, response);
    }

    /**
     * Accepts roles in multiple shapes and returns a wildcard collection type
     * so List<SimpleGrantedAuthority> matches Collection<? extends GrantedAuthority>.
     */
    @SuppressWarnings("unchecked")
    private Collection<? extends GrantedAuthority> extractAuthorities(Object rolesClaim) {
        List<String> rolesAsStrings = new ArrayList<>();

        if (rolesClaim == null) {
            // no roles
        } else if (rolesClaim instanceof List<?> list) {
            for (Object o : list) {
                if (o != null) rolesAsStrings.add(String.valueOf(o).trim());
            }
        } else if (rolesClaim instanceof String s) {
            if (!s.isBlank()) {
                rolesAsStrings.addAll(
                    Arrays.stream(s.split(","))
                          .map(String::trim)
                          .filter(str -> !str.isEmpty())
                          .toList()
                );
            }
        } else if (rolesClaim instanceof String[] arr) {
            rolesAsStrings.addAll(Arrays.stream(arr)
                    .filter(x -> x != null && !x.isBlank())
                    .map(String::trim)
                    .toList());
        } else if (rolesClaim instanceof Object[] arr) {
            rolesAsStrings.addAll(Arrays.stream(arr)
                    .filter(x -> x != null)
                    .map(String::valueOf)
                    .map(String::trim)
                    .filter(s2 -> !s2.isEmpty())
                    .toList());
        }

        // Build SimpleGrantedAuthority list and return it as Collection<? extends GrantedAuthority>
        return rolesAsStrings.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }
}
