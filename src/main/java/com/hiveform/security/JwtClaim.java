package com.hiveform.security;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtClaim {
    private String userId;
    private String email;
    private String fullname;
    private String role;
    private Long exp;
    private String iss;
    private Long iat;
}
