package com.ybritto.teamtempo.backend.authentication.service;

import com.ybritto.teamtempo.backend.authentication.entity.UserEntity;
import com.ybritto.teamtempo.backend.authentication.mapper.AuthenticationMapper;
import com.ybritto.teamtempo.backend.authentication.repository.UserRepository;
import com.ybritto.teamtempo.backend.gen.model.LoginResponseDto;
import com.ybritto.teamtempo.backend.gen.model.LoginUserDto;
import com.ybritto.teamtempo.backend.gen.model.LogoutResponseDto;
import com.ybritto.teamtempo.backend.gen.model.RegisterUserDto;
import com.ybritto.teamtempo.backend.gen.model.UserDto;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@AllArgsConstructor
public class AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final AuthenticationMapper authenticationMapper;
    private final JwtService jwtService;

    private UserEntity authenticate(LoginUserDto input) {
        logger.debug("Attempting to authenticate user with email: {}", input.getEmail());

        UserEntity userEntity = userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> {
                    logger.warn("Authentication failed: User not found with email: {}", input.getEmail());
                    return new UsernameNotFoundException(String.format("User %s not found, please contact your admin", input.getEmail()));
                });

        if (!userEntity.isEnabled()) {
            logger.warn("Authentication failed: User is disabled with email: {}", input.getEmail());
            throw new DisabledException((String.format("User %s is inactive, please contact your admin", input.getEmail())));
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            input.getEmail(),
                            input.getPassword()
                    )
            );
            logger.debug("Password authentication successful for user: {}", input.getEmail());
        } catch (Exception e) {
            logger.error("Authentication failed for user: {} - {}", input.getEmail(), e.getMessage());
            throw e;
        }

        return userEntity;
    }


    public LoginResponseDto login(LoginUserDto loginUserDto) {
        logger.info("Authenticating user with email: {}", loginUserDto.getEmail());
        UserEntity authenticatedUser = this.authenticate(loginUserDto);
        LoginResponseDto response = authenticationMapper.mapDto(authenticatedUser, jwtService.generateToken(authenticatedUser), jwtService.getExpirationTime());
        logger.info("User {} authenticated successfully", authenticatedUser.getEmail());
        return response;
    }

    public UserDto signup(RegisterUserDto registerUserDto) {
        logger.info("Registering new user: {} with email: {}", registerUserDto.getName(), registerUserDto.getEmail());
        UserEntity entity = authenticationMapper.mapEntity(
                registerUserDto, passwordEncoder.encode(registerUserDto.getPassword()));
        UserDto userDto = authenticationMapper.mapDto(
                userRepository.save(entity
                        .toBuilder()
                        .enabled(true).build())
        );
        logger.info("User {} registered successfully with email: {}", userDto.getName(), userDto.getEmail());
        return userDto;
    }

    public LogoutResponseDto logout() {
        logger.info("Logging out user");
        // Clear the security context
        SecurityContextHolder.clearContext();

        // Create logout response
        LogoutResponseDto logoutResponse = new LogoutResponseDto();
        logoutResponse.setMessage("User successfully logged out");
        logoutResponse.setTimestamp(OffsetDateTime.now());

        logger.info("User logged out successfully");
        return logoutResponse;
    }
}
