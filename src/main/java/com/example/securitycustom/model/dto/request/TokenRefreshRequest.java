package com.example.securitycustom.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author NamTv
 * @since 13/10/2023
 */
@Data
public class TokenRefreshRequest {
    @NotBlank
    private String refreshToken;
}
