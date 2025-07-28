package com.hiveform.dto.question;

import java.util.List;

import lombok.Data;

@Data
public class DtoQuestionIU {
    private String title;
    private String description;
    private Integer questionIndex;
    private String imageUrl;
    private String type;
    private Boolean isRequired;
    private List<String> options;
}
