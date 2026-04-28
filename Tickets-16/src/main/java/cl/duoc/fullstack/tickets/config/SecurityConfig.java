package cl.duoc.fullstack.tickets.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.GET, "/tickets/by-id/*/history", "/tickets/by-id/*/audit")
                .hasRole("ADMIN")
            .requestMatchers(HttpMethod.GET, "/tickets", "/tickets/by-id/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/tickets").hasAnyRole("USER", "AGENT", "ADMIN")
            .requestMatchers(HttpMethod.PUT, "/tickets/by-id/**").hasAnyRole("USER", "AGENT", "ADMIN")
            .requestMatchers(HttpMethod.PATCH, "/tickets/by-id/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/tickets/by-id/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.GET, "/users", "/users/by-id/**").permitAll()
            .requestMatchers("/users/**").hasRole("ADMIN")
            .anyRequest().authenticated()
        )
        .httpBasic(Customizer.withDefaults());

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
