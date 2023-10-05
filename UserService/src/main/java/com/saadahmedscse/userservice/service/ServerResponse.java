package com.saadahmedscse.userservice.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ServerResponse {

    public static ResponseEntity<?> internalServerError(Exception e) {
        return getResponse(false, e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static ResponseEntity<?> ok(String message) {
        return getResponse(true, message, HttpStatus.OK);
    }

    public static ResponseEntity<?> created(String message) {
        return getResponse(true, message, HttpStatus.CREATED);
    }

    public static ResponseEntity<?> badRequest(String message) {
        return getResponse(false, message, HttpStatus.BAD_REQUEST);
    }

    public static <T> ResponseEntity<T> body(T body) {
        return ResponseEntity.ok().body(body);
    }

    public static ResponseEntity<?> ok() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public static ResponseEntity<?> created() {
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    public static ResponseEntity<?> internalServerError() {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static ResponseEntity<?> badRequest() {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    private static ResponseEntity<ApiResponse> getResponse(boolean status, String message, HttpStatus httpStatus) {
        return new ResponseEntity<>(new ApiResponse(status, message), httpStatus);
    }

    private record ApiResponse(boolean status, String message) {}
}
