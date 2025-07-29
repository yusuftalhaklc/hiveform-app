package com.hiveform.dto.mail;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MailRequest implements Serializable {
    private String to;
    private String subject;
    private String text;
    private String from;
} 