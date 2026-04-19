package com.example.smartcampusapi.model;

public class ErrorMessage {

    private String errorMessage;
    private int errorCode;
    private String documentation;

    public ErrorMessage() {}

    public ErrorMessage(String message, int code, String doc) {
        this.errorMessage = message;
        this.errorCode = code;
        this.documentation = doc;
    }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public int getErrorCode() { return errorCode; }
    public void setErrorCode(int errorCode) { this.errorCode = errorCode; }

    public String getDocumentation() { return documentation; }
    public void setDocumentation(String documentation) { this.documentation = documentation; }
}