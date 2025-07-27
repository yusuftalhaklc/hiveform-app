package com.hiveform.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailDto implements Serializable {
    private String to;
    private String subject;
    private String text;
    private String from;
}
