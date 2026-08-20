package com.january.demo.exception;

public class TransactionTypeMismatchException extends RuntimeException {

    public TransactionTypeMismatchException(String message) {
        super(message);
    }
}