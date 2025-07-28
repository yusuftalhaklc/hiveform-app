package com.hiveform.dto.question;

import java.util.List;

import lombok.Data;

@Data
public class DtoQuestionDetail {
    private String id;
    private String formId;
    private String title;
    private String description;
    private Integer questionIndex;
    private String imageUrl;
    private String type;
    private Boolean isRequired;
    private List<String> options;
    private Long createdAt;
    private Long updatedAt;
}
