package com.example.securitycustom.model.dto.request;

import lombok.Data;

/**
 * @author NamTv
 * @since 13/10/2023
 */
@Data
public class SignInRequest {
    private String username;
    private String password;
}
