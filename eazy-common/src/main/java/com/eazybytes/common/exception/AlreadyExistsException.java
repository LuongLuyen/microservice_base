package com.eazybytes.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class AlreadyExistsException extends RuntimeException {

    public AlreadyExistsException(String entityName, String fieldName, String fieldValue) {
        super(String.format("%s already exists with %s: '%s'", entityName, fieldName, fieldValue));
    }
}
