package com.hiveform.dto.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtoFormList {
    private String id;
    private String shortLink;
    private String title;
    private Long submissionCount;
    private Long expiresAt;
    private Long createdAt;
    private Long updatedAt;
    private Boolean isActive;
    private Boolean isPublic;
} 