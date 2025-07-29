package com.hiveform.dto.question;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionDeleteRequest {
    
    @JsonIgnore
    private String questionId;
    
    @JsonIgnore
    private String userId;
} 