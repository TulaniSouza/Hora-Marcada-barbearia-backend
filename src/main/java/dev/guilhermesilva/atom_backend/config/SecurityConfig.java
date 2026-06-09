package dev.guilhermesilva.atom_backend.config;

import dev.guilhermesilva.atom_backend.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    private static final String[] SWAGGER_WHITELIST = {
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/v3/api-docs",
            "/webjars/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth

                        // Swagger
                        .requestMatchers(SWAGGER_WHITELIST).permitAll()

                        // Barber auth
                        .requestMatchers("/api/auth/**").permitAll()

                        // Customer auth
                        .requestMatchers("/api/customers/auth/**").permitAll()

                        // Public route
                        .requestMatchers(HttpMethod.GET, "/api/service-types/active").permitAll()

                        // Customer routes
                        .requestMatchers(HttpMethod.GET, "/api/barbers").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.GET, "/api/appointments/available-times").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.POST, "/api/appointments").hasRole("CUSTOMER")

                        // Barber routes
                        .requestMatchers(HttpMethod.GET, "/api/barber/schedule").hasRole("BARBER")
                        .requestMatchers(HttpMethod.POST, "/api/service-types").hasRole("BARBER")
                        .requestMatchers(HttpMethod.PUT, "/api/service-types/**").hasRole("BARBER")
                        .requestMatchers(HttpMethod.DELETE, "/api/service-types/**").hasRole("BARBER")
                        .requestMatchers(HttpMethod.GET, "/api/service-types").hasRole("BARBER")
                        .requestMatchers(HttpMethod.GET, "/api/service-types/**").hasRole("BARBER")
                        .requestMatchers(HttpMethod.GET, "/api/appointments").hasRole("BARBER")
                        .requestMatchers(HttpMethod.GET, "/api/appointments/date").hasRole("BARBER")
                        .requestMatchers(HttpMethod.PATCH, "/api/appointments/**").hasRole("BARBER")

                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider =
                new DaoAuthenticationProvider(userDetailsService);

        authenticationProvider.setPasswordEncoder(passwordEncoder());

        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}