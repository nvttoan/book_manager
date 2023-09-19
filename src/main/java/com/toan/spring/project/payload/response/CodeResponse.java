package com.toan.spring.project.payload.response;

import lombok.Data;

@Data
public class CodeResponse {
    private int code;
    private String message;

    public CodeResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
