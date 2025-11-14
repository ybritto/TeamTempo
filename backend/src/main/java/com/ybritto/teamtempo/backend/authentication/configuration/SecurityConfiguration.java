package com.ybritto.teamtempo.backend.authentication.configuration;

import com.ybritto.teamtempo.backend.authentication.entity.SecurityRoleEnum;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfiguration(AuthenticationProvider authenticationProvider, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        String[] allRolesAsString = Arrays.stream(SecurityRoleEnum.values()).map(Enum::name).toArray(String[]::new);

        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login", "auth/signup", "swagger-ui/**", "v3/api-docs/**").permitAll()

                        // KEEP ADMIN ENDPOINTS AT THE BEGINNING. ORDERING MATTERS FOR SPRING SECURITY
                        // I.E: If /teams/sync is moved after /teams/**, the first will take precedence
                        .requestMatchers(HttpMethod.POST, "/teams/sync").hasRole(SecurityRoleEnum.ADMIN.toString())

                        // FEATURE ENDPOINT DEFINITIONS
                        .requestMatchers("/absences/**").hasAnyRole(allRolesAsString)
                        .requestMatchers("/holidays/**").hasAnyRole(allRolesAsString)
                        .requestMatchers(HttpMethod.GET,"/locations/**").hasAnyRole(allRolesAsString)
                        .requestMatchers("/people/**").hasAnyRole(allRolesAsString)
                        .requestMatchers(HttpMethod.GET, "/roles/**").hasAnyRole(allRolesAsString)
                        .requestMatchers("/teams/**").hasAnyRole(allRolesAsString)

                        .anyRequest().authenticated())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }


}
