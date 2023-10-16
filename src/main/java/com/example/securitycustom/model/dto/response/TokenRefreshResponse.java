package com.example.securitycustom.model.dto.response;

import lombok.Data;

/**
 * @author NamTv
 * @since 13/10/2023
 */
@Data
public class TokenRefreshResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";

    public TokenRefreshResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
