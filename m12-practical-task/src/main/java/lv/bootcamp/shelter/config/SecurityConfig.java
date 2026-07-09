package lv.bootcamp.shelter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Baseline Spring Security setup for the shelter starter project.
 *
 * <ul>
 *   <li>Two in-memory demo accounts, with deliberately separate roles:
 *       {@code user}/{@code user123} (ROLE_USER only) and
 *       {@code admin}/{@code admin123} (ROLE_ADMIN only).</li>
 *   <li>Anyone (including anonymous visitors) can browse the animal pages
 *       and read the API (GET).</li>
 *   <li>Only ROLE_ADMIN can create animals ({@code POST /animals},
 *       {@code POST /api/v1/animals}).</li>
 *   <li>Only ROLE_USER can adopt an animal ({@code POST /api/v1/animals/{id}/adopt},
 *       {@code POST /animals/{id}/adopt});
 *       admins are deliberately excluded since they don't have ROLE_USER.</li>
 *   <li>Only ROLE_ADMIN can list adopted animals ({@code GET /api/v1/animals/adopted}) -
 *       a read-only endpoint, handy for testing role-based authorization without
 *       any side effects.</li>
 *   <li>Only ROLE_ADMIN sees the "adopted by {userId} on {date}" note -
 *       enforced in {@code AnimalService#toResponse}, not here, since it's a
 *       field-level (not URL-level) restriction.</li>
 *   <li>Uses a custom Thymeleaf login page served at {@code /login}.</li>
 * </ul>
 */
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("user123"))
                .roles("USER")
                .build();

        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // The JSON API is exercised directly with HTTP Basic (curl/Postman/Swagger),
                // which never carries an ambient browser session cookie, so CSRF tokens
                // aren't needed there. Browser-facing pages/forms keep CSRF protection.
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET,
                                "/", "/*.html", "/css/**", "/js/**", "/images/**", "/favicon.ico",
                                "/login", "/swagger-ui/**", "/v1/api-docs/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/animals/types").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/animals/adopted").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/animals", "/api/v1/animals/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/animals/new").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/animals", "/animals/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/animals/*/adopt").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/api/v1/animals").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/animals/*/adopt").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/animals").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/animals", true)
                        .permitAll())
                .httpBasic(Customizer.withDefaults())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll());

        return http.build();
    }
}
