package com.example.get_learning_server.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
public class ExceptionResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Date timestamp;
    private Boolean success;
    private String data;
    private int statusCode;
    private String status;
    private String description;

    public ExceptionResponse(String message, String description, HttpStatus httpStatus) {
        timestamp = new Date();
        success = false;
        statusCode = httpStatus.value();
        status = httpStatus.getReasonPhrase();
        this.data = message;
        this.description = description;
    }
}
