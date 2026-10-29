package com.schoolchan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
<<<<<<< HEAD
                        .requestMatchers("/staff/login", "/css/**", "/media/**", "/error").permitAll()
                        .requestMatchers("/staff/**").authenticated()
=======
                        .requestMatchers("/staff/login", "/css/**", "/js/**", "/img/**", "/media/**", "/error").permitAll()
                        .requestMatchers("/student/login", "/student/enroll", "/student/logout").permitAll()
                        // The moderation policy is a policy, not a routine action:
                        // only an admin changes how the board treats posts.
                        .requestMatchers("/staff/settings/moderation-policy").hasRole("ADMIN")
                        // Staff areas need a staff role. A signed-in student is not staff.
                        .requestMatchers("/staff/**").hasAnyRole("ADMIN", "MODERATOR")
>>>>>>> d5851d3 (Initial commit)
                        .anyRequest().permitAll())
                .formLogin(form -> form
                        .loginPage("/staff/login")
                        .loginProcessingUrl("/staff/login")
                        .defaultSuccessUrl("/staff/dashboard", true)
<<<<<<< HEAD
=======
                        .successHandler((request, response, authentication) -> {
                            // One identity at a time: a staff sign-in clears any
                            // student session in the same browser.
                            var session = request.getSession(false);
                            if (session != null) {
                                session.removeAttribute("sc-identity");
                                session.setAttribute("sc-identity", "staff");
                            }
                            response.sendRedirect(request.getContextPath() + "/staff/dashboard");
                        })
>>>>>>> d5851d3 (Initial commit)
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/staff/logout")
                        .logoutSuccessUrl("/")
                        .permitAll())
                .csrf(csrf -> csrf.ignoringRequestMatchers("/staff/api/**"));
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
