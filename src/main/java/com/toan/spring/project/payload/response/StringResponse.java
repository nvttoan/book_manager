package com.toan.spring.project.payload.response;

import lombok.Data;

@Data
public class StringResponse {
    private int code;
    private String message;

    public StringResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
