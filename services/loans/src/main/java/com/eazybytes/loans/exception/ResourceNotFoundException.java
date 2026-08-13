package com.eazybytes.loans.exception;

public class ResourceNotFoundException extends com.eazybytes.common.exception.ResourceNotFoundException {

    public ResourceNotFoundException(String resourceName, String fieldName, String fieldValue) {
        super(resourceName, fieldName, fieldValue);
    }
}
