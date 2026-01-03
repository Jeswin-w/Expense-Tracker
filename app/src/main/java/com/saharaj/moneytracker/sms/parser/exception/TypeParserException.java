package com.saharaj.moneytracker.sms.parser.exception;

public class TypeParserException extends RuntimeException{
    public TypeParserException(String message) {
        super(message);
    }

    public TypeParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public TypeParserException(Throwable cause) {
        super(cause);
    }
}
