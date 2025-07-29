package com.hiveform.dto.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtoGetUserFormsRequest {
    private int page = 1;
    private int size = 10;
} 