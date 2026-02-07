package com.lakesidemutual.customerselfservice.interfaces.configuration;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

/**
 * Configures security for Customer Self Service endpoints only.
 * Important: This chain MUST NOT apply to /policy-management/** (handled by policy module).
 */
@Profile("customerselfservice-security")
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfiguration {

    private static final String[] AUTH_WHITELIST = {
            // Swagger UI v3 (OpenAPI)
            "/v3/api-docs/**",
            "/swagger-ui/**",

            // Actuator
            "/actuator",
            "/actuator/**",

            // Customer Core module (namespaced endpoints)
            "/customer-core",
            "/customer-core/**",

            // Customer Self Service endpoints
            "/customers",
            "/customers/**",
            "/cities",
            "/cities/**",
            "/insurance-quote-requests",
            "/insurance-quote-requests/**",
            "/auth/**",

            // H2 console
            "/console/**"
    };

    /**
     * Paths that belong to the Customer Self Service module.
     * We scope the filter chain to these so it does NOT intercept /policy-management/**.
     */
    private static final String[] CSS_MATCHERS = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/actuator",
            "/actuator/**",
            "/customer-core",
            "/customer-core/**",
            "/customers",
            "/customers/**",
            "/cities",
            "/cities/**",
            "/insurance-quote-requests",
            "/insurance-quote-requests/**",
            "/auth/**",
            "/user",
            "/user/**",
            "/console/**"
    };

    @Autowired
    private UnauthorizedHandler unauthorizedHandler; // keeping it, even if unused

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(new BCryptPasswordEncoder());

        return authenticationManagerBuilder.build();
    }

    @Bean
    public AuthenticationTokenFilter authenticationTokenFilterBean(HttpSecurity http) throws Exception {
        AuthenticationTokenFilter authenticationTokenFilter = new AuthenticationTokenFilter();
        authenticationTokenFilter.setAuthenticationManager(authenticationManager(http));
        return authenticationTokenFilter;
    }

    @Bean
    @Order(2) // policy module can use @Order(1)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            // ✅ CRITICAL: this chain only applies to Customer Self Service related endpoints.
            // It will NOT apply to /policy-management/**, so policy endpoints won't be 401'd here.
            .securityMatcher(CSS_MATCHERS)

            .headers(headers -> headers
                    .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                    .cacheControl(HeadersConfigurer.CacheControlConfig::disable)
            )
            .csrf(AbstractHttpConfigurer::disable)
            .exceptionHandling(exceptionHandling -> exceptionHandling
                    .authenticationEntryPoint((request, response, exception) ->
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"))
                    .accessDeniedHandler((request, response, exception) ->
                            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden"))
            )
            .sessionManagement(sessionManagement -> sessionManagement
                    .sessionCreationPolicy(STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers(AUTH_WHITELIST).permitAll()
                    .anyRequest().authenticated()
            )
            .addFilterBefore(authenticationTokenFilterBean(http), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
        configuration.setExposedHeaders(Arrays.asList("x-auth-token"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
