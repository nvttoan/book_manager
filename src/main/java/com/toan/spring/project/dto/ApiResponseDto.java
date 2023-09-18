package com.toan.spring.project.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ApiResponseDto {
    private String message;
    private Object data;

    public ApiResponseDto(String message, Object data) {
        this.message = message;
        this.data = data;
    }
}
