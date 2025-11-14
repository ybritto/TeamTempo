package com.ybritto.teamtempo.backend.authentication.controller;

import com.ybritto.teamtempo.backend.authentication.service.AuthenticationService;
import com.ybritto.teamtempo.backend.gen.api.AuthApi;
import com.ybritto.teamtempo.backend.gen.model.LoginResponseDto;
import com.ybritto.teamtempo.backend.gen.model.LoginUserDto;
import com.ybritto.teamtempo.backend.gen.model.RegisterUserDto;
import com.ybritto.teamtempo.backend.gen.model.UserDto;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AuthenticationController implements AuthApi {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    AuthenticationService authenticationService;

    @Override
    public ResponseEntity<LoginResponseDto> login(LoginUserDto loginUserDto) {
        logger.info("GET /auth/login - Logging in the app");
        LoginResponseDto login = authenticationService.login(loginUserDto);
        logger.info("GET /auth/login - Login executed successfully");
        return ResponseEntity.ok(login);
    }

    @Override
    public ResponseEntity<UserDto> signup(RegisterUserDto registerUserDto) {
        logger.info("GET /auth/signup - Registering {} in the app",  registerUserDto.getFullName());
        UserDto userDto = authenticationService.signup(registerUserDto);
        logger.info("GET /auth/signup - signup for {} executed successfully", userDto.getEmail());
        return ResponseEntity.ok(userDto);
    }
}
