package com.example.securitycustom.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

/**
 * @author NamTv
 * @since 13/10/2023
 */
@Data
@AllArgsConstructor
public class SignupRequest {
    private String username;
    private String email;
    private String password;
    private Set<String> roles;
}
