package com.eazybytes.accounts.exception;

public class CustomerAlreadyExistsException extends com.eazybytes.common.exception.AlreadyExistsException {

    public CustomerAlreadyExistsException(String message) {
        super("Customer", "mobileNumber", message);
    }
}
