package com.ai.docMind.exception;

public class DocumentProcessingException extends RuntimeException{

    public DocumentProcessingException() {
        super("Error in processing the document !!!");
    }
    public DocumentProcessingException(String message){
        super(message);
    }

    public DocumentProcessingException(String message, Throwable ex){
        super(message, ex);
    }
}
