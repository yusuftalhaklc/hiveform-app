package com.hiveform.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DtoGoogleUserInfo {
    private String id;
    private String email;
    
    @JsonProperty("verified_email")
    private boolean verifiedEmail;
    
    private String name;
    
    @JsonProperty("given_name")
    private String givenName;
    
    @JsonProperty("family_name")
    private String familyName;
    
    private String picture;
    
    private String locale;
} 