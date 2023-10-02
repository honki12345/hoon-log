package me.honki12345.hoonlog.error;

import lombok.extern.slf4j.Slf4j;
import me.honki12345.hoonlog.error.exception.CustomBaseException;
import me.honki12345.hoonlog.error.exception.DuplicateUserAccountException;
import me.honki12345.hoonlog.error.exception.ForbiddenException;
import me.honki12345.hoonlog.error.exception.JwtException;
import me.honki12345.hoonlog.error.exception.LoginErrorException;
import me.honki12345.hoonlog.error.exception.LogoutErrorException;
import me.honki12345.hoonlog.error.exception.NotFoundException;
import me.honki12345.hoonlog.error.exception.RoleNotFoundException;
import me.honki12345.hoonlog.error.exception.UserAccountNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.StringJoiner;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    public static final String DELIMITER = ", ";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
        MethodArgumentNotValidException exception) {
        String message = createValidationExceptionMessage(exception);
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, message);
        return new ResponseEntity<>(errorResponse, ErrorCode.INVALID_INPUT_VALUE.getStatus());
    }

    @ExceptionHandler(DuplicateUserAccountException.class)
    public ResponseEntity<ErrorResponse> handleDuplicationException(
        DuplicateUserAccountException exception) {
        return createResponseEntityByException(exception);
    }

    @ExceptionHandler(UserAccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserAccountNotFoundException(
        UserAccountNotFoundException exception) {
        return createResponseEntityByException(exception);
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ErrorResponse> roleNotFoundException(RoleNotFoundException exception) {
        return createResponseEntityByException(exception);
    }

    @ExceptionHandler(LoginErrorException.class)
    public ResponseEntity<ErrorResponse> loginErrorException(LoginErrorException exception) {
        return createResponseEntityByException(exception);
    }

    @ExceptionHandler(LogoutErrorException.class)
    public ResponseEntity<ErrorResponse> logoutErrorException(LogoutErrorException exception) {
        return createResponseEntityByException(exception);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> notFoundException(NotFoundException exception) {
        return createResponseEntityByException(exception);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> jwtException(JwtException exception) {
        return createResponseEntityByException(exception);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> forbiddenException(ForbiddenException exception) {
        return createResponseEntityByException(exception);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity exceptionHandler(Exception exception) {
        log.warn("Exception 발생: {}, {}", exception, exception.getMessage());
        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private static ResponseEntity<ErrorResponse> createResponseEntityByException(
        CustomBaseException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        String message = ex.getMessage();
        ErrorResponse errorResponse = ErrorResponse.of(errorCode, message);
        return new ResponseEntity<>(errorResponse,
            errorCode.getStatus());
    }

    private String createValidationExceptionMessage(MethodArgumentNotValidException ex) {
        StringJoiner sj = new StringJoiner(DELIMITER);
        BindingResult bindingResult = ex.getBindingResult();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            sj.add(fieldError.getDefaultMessage());
        }
        return sj.toString();
    }
}
