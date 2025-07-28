package com.hiveform.dto.form;

import java.time.LocalDateTime;
import java.util.List;

import com.hiveform.dto.question.DtoQuestionIU;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
public class DtoFormIU {
    private String title;
    private String description;
    private String bannerImageUrl;
    private Boolean isActive;
    private Boolean isPublic;
    private LocalDateTime expiresAt;
    private List<DtoQuestionIU> questions;

    @JsonIgnore
    private String userId;

    @JsonIgnore
    private String formId; 
}
