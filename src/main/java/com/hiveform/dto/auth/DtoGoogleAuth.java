package com.hiveform.dto.auth;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DtoGoogleAuth {

    @NotBlank(message = "Code is required")
    private String code;

    @NotBlank(message = "State is required")
    private String state;
}
