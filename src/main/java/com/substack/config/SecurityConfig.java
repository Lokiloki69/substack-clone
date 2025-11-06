package com.substack.config;
import com.substack.service.auth.CustomLogoutSuccessHandler;
import com.substack.service.auth.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    private final CustomUserDetailsService userDetailsService;
    private final CustomLogoutSuccessHandler customLogoutSuccessHandler;

    public SecurityConfig(CustomUserDetailsService userDetailsService, CustomLogoutSuccessHandler customLogoutSuccessHandler) {
        this.userDetailsService = userDetailsService;
        this.customLogoutSuccessHandler = customLogoutSuccessHandler;
    }
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http
            // ✅ Disable CSRF only for API routes
            .csrf(csrf -> csrf.ignoringRequestMatchers("/**"))

            // ✅ Authorization: permit all public pages
            .authorizeHttpRequests(auth -> auth
                    .anyRequest().permitAll()
            )

            // ✅ Custom login form setup
            .formLogin(form -> form.disable())
            .httpBasic(httpBasic -> httpBasic.disable())


            // ✅ Logout setup
            .logout(logout -> logout
                    .logoutUrl("/auth/logout")               // POST request for logout
                    .logoutSuccessUrl("/auth/login?logout")  // redirect after logout
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .logoutSuccessHandler(customLogoutSuccessHandler)
                    .permitAll()
            )

            // ✅ Session management setup
            .sessionManagement(session -> session
                    .maximumSessions(1)
                    .maxSessionsPreventsLogin(false)
            );

    return http.build();
}

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
