package com.hiveform.dto.question;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
public class DtoQuestionDelete {
    
    @JsonIgnore
    private String questionId;
    
    @JsonIgnore
    private String userId;
} 