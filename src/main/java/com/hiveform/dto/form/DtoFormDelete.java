package com.hiveform.dto.form;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtoFormDelete {
    private String formId;
    private String userId;
}
