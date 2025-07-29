package com.hiveform.dto.form;

import java.util.List;

import com.hiveform.dto.question.QuestionDetailResponse;
import com.hiveform.dto.user.UserInfoResponse;

import lombok.Data;
import lombok.NoArgsConstructor;    
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormDetailResponse {
    private String id;
    private String shortLink;
    private String title;
    private String description;
    private String bannerImageUrl;
    private Boolean isActive;
    private Boolean isPublic;
    private Long expiresAt;
    private Long createdAt;
    private Long updatedAt;
    private List<QuestionDetailResponse> questions;
    private UserInfoResponse createdBy;
} 