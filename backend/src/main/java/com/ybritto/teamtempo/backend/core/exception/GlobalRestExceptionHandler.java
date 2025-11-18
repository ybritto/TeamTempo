package com.ybritto.teamtempo.backend.core.exception;

import com.ybritto.teamtempo.backend.gen.model.ProblemDto;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalRestExceptionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(GlobalRestExceptionHandler.class);

  @SuppressWarnings("rawtypes")
  private static final Map<Class, HttpStatus> EXCEPTIONS_MAPPING = new HashMap<>();

  static {

    // Business validations
    EXCEPTIONS_MAPPING.put(InvalidParameterException.class, HttpStatus.BAD_REQUEST);
    EXCEPTIONS_MAPPING.put(NotFoundException.class, HttpStatus.NOT_FOUND);

    // Entity validations
    EXCEPTIONS_MAPPING.put(EntityValidationException.class, HttpStatus.UNPROCESSABLE_ENTITY);

    // Authentication & Authorizations
    EXCEPTIONS_MAPPING.put(BadCredentialsException.class, HttpStatus.UNAUTHORIZED);
    EXCEPTIONS_MAPPING.put(DisabledException.class, HttpStatus.UNAUTHORIZED);
    EXCEPTIONS_MAPPING.put(AccountStatusException.class, HttpStatus.FORBIDDEN);
    EXCEPTIONS_MAPPING.put(SignatureException.class, HttpStatus.FORBIDDEN);
    EXCEPTIONS_MAPPING.put(ExpiredJwtException.class, HttpStatus.INTERNAL_SERVER_ERROR);

  }

  private static HttpStatus resolveStatus(Exception exception) {
    return EXCEPTIONS_MAPPING
        .getOrDefault(exception.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(Exception.class)
  @ResponseBody
  public ResponseEntity<ProblemDto> handleException(Exception ex, NativeWebRequest request) {
    LOGGER.error("Unhandled exception occurred: {} - {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
    return getProblemResponseEntity(ex, resolveStatus(ex));
  }

  private ResponseEntity<ProblemDto> getProblemResponseEntity(Exception exception, HttpStatus httpStatus) {
    ProblemDto problem = new ProblemDto();
    problem.setHttpStatusCode(httpStatus.value());
    problem.setHttpStatusReasonPhrase(httpStatus.getReasonPhrase());
    problem.setTitle(exception.getLocalizedMessage());
    problem.setDetails(exception.getMessage());
    
    // Log based on exception type and HTTP status
    if (httpStatus.is4xxClientError()) {
      LOGGER.warn("Client error ({}): {} - {}", httpStatus.value(), exception.getClass().getSimpleName(), exception.getMessage());
    } else if (httpStatus.is5xxServerError()) {
      LOGGER.error("Server error ({}): {} - {}", httpStatus.value(), exception.getClass().getSimpleName(), exception.getMessage(), exception);
    } else {
      LOGGER.error("Unexpected error ({}): {} - {}", httpStatus.value(), exception.getClass().getSimpleName(), exception.getMessage(), exception);
    }
    
    return ResponseEntity
        .status(httpStatus)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(problem);
  }

}
