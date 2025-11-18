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

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final AuthenticationMapper authenticationMapper;
    private final JwtService jwtService;

    private UserEntity authenticate(LoginUserDto input) {

        UserEntity userEntity = userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User %s not found, please contact your admin", input.getEmail())));

        if (!userEntity.isEnabled()) {
            throw new DisabledException((String.format("User %s is inactive, please contact your admin", input.getEmail())));
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return userEntity;
    }


    public LoginResponseDto login(LoginUserDto loginUserDto) {
        UserEntity authenticatedUser = this.authenticate(loginUserDto);
        return authenticationMapper.mapDto(authenticatedUser, jwtService.generateToken(authenticatedUser), jwtService.getExpirationTime());
    }

    public UserDto signup(RegisterUserDto registerUserDto) {
        UserEntity entity = authenticationMapper.mapEntity(
                registerUserDto, passwordEncoder.encode(registerUserDto.getPassword()));
        return authenticationMapper.mapDto(
                userRepository.save(entity
                        .toBuilder()
                        .enabled(true).build())
        );
    }

    public LogoutResponseDto logout() {
        // Clear the security context
        SecurityContextHolder.clearContext();

        // Create logout response
        LogoutResponseDto logoutResponse = new LogoutResponseDto();
        logoutResponse.setMessage("User successfully logged out");
        logoutResponse.setTimestamp(OffsetDateTime.now());

        return logoutResponse;
    }
}
