package com.ybritto.teamtempo.backend.authentication.configuration;

import com.ybritto.teamtempo.backend.authentication.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String requestUri = request.getRequestURI();

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.debug("No Bearer token found in request to: {}", requestUri);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            logger.debug("Processing JWT authentication for request: {}", requestUri);
            
            final String userEmail = jwtService.extractUsername(jwt);
            logger.debug("Extracted user email from token: {}", userEmail);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (userEmail != null && authentication == null) {
                logger.debug("Loading user details for: {}", userEmail);
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.info("User {} authenticated successfully for request: {}", userEmail, requestUri);
                } else {
                    logger.warn("Invalid JWT token for user: {} in request: {}", userEmail, requestUri);
                }
            } else if (authentication != null) {
                logger.debug("User already authenticated: {} for request: {}", userEmail, requestUri);
            }

            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            logger.error("JWT authentication failed for request: {}", requestUri, exception);
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }

    }
}
