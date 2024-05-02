package pl.tiguarces.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import pl.tiguarces.JwtFilter;
import pl.tiguarces.JwtUtils;
import pl.tiguarces.filter.AuthenticateFilter;
import pl.tiguarces.service.UserDetailsServiceImpl;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class SecurityConfig {
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtils jwtUtils;

    private static final String[] NOT_SECURED_ENDPOINTS = { "/api/auth/**", "/api/book/fetch/**", "/api/category/fetch/**",
                                                            "/api/basket/**", "/api/author/**", "/api/publisher/**",
                                                            "/api/reaction/get-all/**"};

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.csrf(AbstractHttpConfigurer::disable)
                           .userDetailsService(userDetailsService)
                           .cors(withDefaults())
                           .authorizeHttpRequests(auth -> auth.requestMatchers(NOT_SECURED_ENDPOINTS).permitAll()
                                                              .anyRequest().authenticated())
                           .sessionManagement(configurer -> configurer.sessionCreationPolicy(STATELESS))
                           .addFilterBefore(new JwtFilter(jwtUtils), UsernamePasswordAuthenticationFilter.class)
                           .addFilterAfter(new AuthenticateFilter(userDetailsService, jwtUtils), JwtFilter.class)
                           .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
                          configuration.setAllowedOrigins(List.of("http://localhost:4200"));
                          configuration.setAllowedMethods(List.of("*"));
                          configuration.setAllowedHeaders(List.of("*"));
                          configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                                        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(final HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                   .build();
    }
}
