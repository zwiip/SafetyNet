package com.safetynet.alerts.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NO_CONTENT)
public class EmptyResourceException extends RuntimeException {
    public EmptyResourceException(String message) {
        super(message);
    }
}
