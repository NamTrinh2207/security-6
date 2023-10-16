package com.example.securitycustom.model.dto.response;

import lombok.Data;

import java.util.Date;

/**
 * @author NamTv
 * @since 13/10/2023
 */
@Data
public class ErrorMessage {
    private int status;
    private Date timestamp;
    private String message;
    private String description;

    public ErrorMessage(int status, Date timestamp, String message, String description) {
        this.status = status;
        this.timestamp = timestamp;
        this.message = message;
        this.description = description;
    }
}
