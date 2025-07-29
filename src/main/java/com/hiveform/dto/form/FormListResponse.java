package com.hiveform.dto.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormListResponse {
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