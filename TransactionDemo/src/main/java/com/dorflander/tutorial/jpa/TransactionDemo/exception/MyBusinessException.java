package com.dorflander.tutorial.jpa.TransactionDemo.exception;

public class MyBusinessException extends RuntimeException {

    public MyBusinessException(String msg) {
        super(msg);
    }
}
