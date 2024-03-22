package com.dorflander.tutorial.jpa.TransactionDemo.exception;

public class MyOtherBusinessException extends RuntimeException {

    public MyOtherBusinessException(String msg) {
        super(msg);
    }
}
