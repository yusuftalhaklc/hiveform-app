package com.hiveform.dto.form;

import java.util.List;

import com.hiveform.dto.question.DtoQuestionDetail;
import com.hiveform.dto.user.DtoUserInfo;

import lombok.Data;

@Data
public class DtoFormDetail {
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
    private List<DtoQuestionDetail> questions;
    private DtoUserInfo createdBy;
}
