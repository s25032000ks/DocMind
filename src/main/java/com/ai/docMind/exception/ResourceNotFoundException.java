package com.ai.docMind.exception;

public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException() {
        super("Resource you are looking is not found !!!");
    }
    public ResourceNotFoundException(String message){
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable ex){
        super(message, ex);
    }
}
