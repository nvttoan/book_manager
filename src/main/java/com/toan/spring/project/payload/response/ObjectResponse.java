package com.toan.spring.project.payload.response;

import lombok.Data;

@Data
public class ObjectResponse {
    private int code;
    private Object message;

    public ObjectResponse(int code, Object message) {
        this.code = code;
        this.message = message;
    }
}
