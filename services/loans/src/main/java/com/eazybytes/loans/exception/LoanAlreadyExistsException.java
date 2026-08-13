package com.eazybytes.loans.exception;

public class LoanAlreadyExistsException extends com.eazybytes.common.exception.AlreadyExistsException {

    public LoanAlreadyExistsException(String message) {
        super("Loan", "loanNumber", message);
    }
}
