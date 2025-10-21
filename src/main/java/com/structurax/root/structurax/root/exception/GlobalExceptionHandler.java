package com.structurax.root.structurax.root.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handle type conversion errors (e.g., "undefined" -> Integer)
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String parameterName = ex.getName();
        String parameterValue = ex.getValue() != null ? ex.getValue().toString() : "null";
        String expectedType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";

        logger.warn("Type mismatch for parameter '{}': received '{}', expected {}",
                   parameterName, parameterValue, expectedType);

        String errorMessage;
        if ("undefined".equals(parameterValue) || "null".equals(parameterValue)) {
            errorMessage = String.format("Parameter '%s' is required and cannot be undefined or null", parameterName);
        } else {
            errorMessage = String.format("Invalid value '%s' for parameter '%s'. Expected a valid %s",
                                       parameterValue, parameterName, expectedType);
        }

        return ResponseEntity.badRequest()
                .body(Map.of(
                    "error", errorMessage,
                    "parameter", parameterName,
                    "receivedValue", parameterValue,
                    "expectedType", expectedType
                ));
    }

    /**
     * Handle missing static resources - usually indicates incorrect API path
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<?> handleNoResourceFound(NoResourceFoundException ex) {
        String requestedPath = ex.getResourcePath();
        logger.warn("No static resource found: {}", requestedPath);

        // Check if this looks like an API call missing the /api prefix
        if (requestedPath != null && (requestedPath.startsWith("supplier/") ||
                                     requestedPath.startsWith("admin/") ||
                                     requestedPath.startsWith("user/") ||
                                     requestedPath.startsWith("designer/") ||
                                     requestedPath.startsWith("qs/") ||
                                     requestedPath.startsWith("quotation/"))) {
            String suggestedPath = "/api/" + requestedPath;
            logger.warn("API path missing '/api' prefix. Requested: '{}', should be: '{}'",
                       requestedPath, suggestedPath);

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                        "error", "API endpoint not found",
                        "requestedPath", requestedPath,
                        "suggestion", "Did you mean: " + suggestedPath + "? All API endpoints require the /api prefix."
                    ));
        }

        return ResponseEntity.notFound().build();
    }

    /**
     * Handle 404 errors
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<?> handleNoHandlerFound(NoHandlerFoundException ex) {
        logger.warn("No handler found for {} {}", ex.getHttpMethod(), ex.getRequestURL());

        return ResponseEntity.notFound().build();
    }

    /**
     * Handle general exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneral(Exception ex) {
        logger.error("Unexpected error: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "An unexpected error occurred: " + ex.getMessage()));
    }
}
