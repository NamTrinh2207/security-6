package com.example.securitycustom.model.dto.response;

import lombok.Data;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * @author NamTv
 * @since 13/10/2023
 */
@Data
@ToString
public class JwtResponse {
    private String id;
    private String username;
    private String email;
    private List<String> roles;
    private String type = "Bearer";
    private String accessToken;
    private String refreshToken;

    public JwtResponse(String id, String username, String email, List<String> roles, String accessToken, String refreshToken) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
