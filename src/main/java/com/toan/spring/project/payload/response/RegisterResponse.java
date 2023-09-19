package com.toan.spring.project.payload.response;

import lombok.Data;

@Data
public class RegisterResponse {
    private int code;
    private String message;

    public RegisterResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
