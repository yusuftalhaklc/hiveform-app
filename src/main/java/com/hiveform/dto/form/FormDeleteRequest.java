package com.hiveform.dto.form;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormDeleteRequest {
    private String formId;
    private String userId;
}
