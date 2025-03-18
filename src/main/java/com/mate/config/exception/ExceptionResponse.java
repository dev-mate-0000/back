package com.mate.config.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Getter
public class ExceptionResponse {
    private final String timestamp = LocalDateTime.now().toString();
    private final int status;
    private final String error;
    private String path;

    public ExceptionResponse(int httpStatus, String message) {
        this.status = httpStatus;
        this.error = message;
    }

    public void createExceptionResponse(HttpServletRequest request, HttpServletResponse response) {
        this.path = request.getRequestURI();
        log.error("Path: {}, Status: {}, Exception: {}", path, status, error);

        final ObjectMapper mapper = new ObjectMapper();
        try {
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write(mapper.writeValueAsString(this));
            response.setStatus(status);
        } catch (IOException e) {
            log.error("응답 작성 실패: {}", e.getMessage(), e);
        }
    }
}