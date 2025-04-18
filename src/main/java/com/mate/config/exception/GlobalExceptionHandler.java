package com.mate.config.exception;

import com.mate.config.exception.custom.AuthedException;
import com.mate.config.exception.custom.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.text.MessageFormat;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    public void handleNotFound(NoHandlerFoundException ex, HttpServletRequest request, HttpServletResponse response) {
        int httpStatus = HttpStatus.NOT_FOUND.value();
        String message = "해당 경로를 찾을 수 없습니다.";
        ExceptionResponse exceptionResponse = new ExceptionResponse(httpStatus, message);
        exceptionResponse.createExceptionResponse(request, response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public void handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex, HttpServletRequest request, HttpServletResponse response) {
        int httpStatus = HttpStatus.METHOD_NOT_ALLOWED.value();
        String message = "해당 요청을 처리 할 수 없습니다.";
        ExceptionResponse exceptionResponse = new ExceptionResponse(httpStatus, message);
        exceptionResponse.createExceptionResponse(request, response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public void handlerMissingServletRequestParameterException(MissingServletRequestParameterException ex, HttpServletRequest request, HttpServletResponse response) {
        int httpStatus = HttpStatus.BAD_REQUEST.value();

        String parameterName = ex.getParameterName();
        String message = MessageFormat.format("{0}의 값이 누락되었습니다.", parameterName);

        ExceptionResponse exceptionResponse = new ExceptionResponse(httpStatus, message);
        exceptionResponse.createExceptionResponse(request, response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public void handlerMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request, HttpServletResponse response) {
        int httpStatus = HttpStatus.BAD_REQUEST.value();
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        List<String> message = fieldErrors.stream()
                .map(error -> error.getField() + ", " + error.getDefaultMessage())
                .toList();

        ExceptionResponse exceptionResponse = new ExceptionResponse(httpStatus, message.toString());
        exceptionResponse.createExceptionResponse(request, response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public void handlerDataIntegrityViolationException(DataIntegrityViolationException ex, HttpServletRequest request, HttpServletResponse response) {
        int httpStatus = HttpStatus.INTERNAL_SERVER_ERROR.value();
        String message = ex.getMessage();

        ExceptionResponse exceptionResponse = new ExceptionResponse(httpStatus, message);
        exceptionResponse.createExceptionResponse(request, response);
    }

    @ExceptionHandler(NotFoundException.class)
    public void handlerNotFoundMemberException(NotFoundException ex, HttpServletRequest request, HttpServletResponse response) {
        int httpStatus = HttpStatus.NOT_FOUND.value();
        String message = ex.getMessage();

        ExceptionResponse exceptionResponse = new ExceptionResponse(httpStatus, message);
        exceptionResponse.createExceptionResponse(request, response);
    }

    @ExceptionHandler(AuthedException.class)
    public void handlerAuthException(AuthedException ex, HttpServletRequest request, HttpServletResponse response) {
        int httpStatus = HttpStatus.FORBIDDEN.value();
        String message = ex.getMessage();

        ExceptionResponse exceptionResponse = new ExceptionResponse(httpStatus, message);
        exceptionResponse.createExceptionResponse(request, response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public void handlerHttpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request, HttpServletResponse response) {
        int httpStatus = HttpStatus.BAD_REQUEST.value();
        String message = ex.getMessage();

        ExceptionResponse exceptionResponse = new ExceptionResponse(httpStatus, message);
        exceptionResponse.createExceptionResponse(request, response);
    }

    @ExceptionHandler(Exception.class)
    public void handleException(Exception ex, HttpServletRequest request, HttpServletResponse response){
        int httpStatus = HttpStatus.INTERNAL_SERVER_ERROR.value();
        String message = ex.getMessage();

        ExceptionResponse exceptionResponse = new ExceptionResponse(httpStatus, message);
        exceptionResponse.createExceptionResponse(request, response);
    }
}
