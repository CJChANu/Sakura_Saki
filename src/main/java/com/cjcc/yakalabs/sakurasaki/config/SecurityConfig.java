package com.cjcc.yakalabs.sakurasaki.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Role-based redirect after login:
     *   ADMIN  → /admin/dashboard
     *   STAFF  → /staff/dashboard
     *   USER   → / (homepage)
     */
    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            boolean isStaff = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_STAFF"));

            if (isAdmin) {
                response.sendRedirect("/admin/dashboard");
            } else if (isStaff) {
                response.sendRedirect("/staff/dashboard");
            } else {
                response.sendRedirect("/");
            }
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/register", "/login", "/css/**", "/js/**", "/images/**", "/videos/**", "/our-services", "/our-packages").permitAll()
                        .requestMatchers("/staff", "/reviews/**", "/api/staff/**").permitAll()
                        .requestMatchers("/services", "/services/**", "/packages", "/packages/**").hasRole("ADMIN")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/staff/dashboard/**", "/staff/appointments/**").hasAnyRole("STAFF", "ADMIN")
                        .requestMatchers("/profile/**").authenticated()
                        .requestMatchers("/booking/**", "/my-appointments/**").hasRole("USER")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/do-login")
                        .successHandler(successHandler())
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}