package com.example.securitycustom.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;

/**
 * @author NamTv
 * @since 13/10/2023
 */
@Data
@Entity(name = "refreshtoken")
public class RefreshToken {
    @Id
    @NotBlank
    @NotNull
    @UuidGenerator(style = UuidGenerator.Style.AUTO)
    @GeneratedValue(generator = "UUID")
    private String id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Instant expiryDate;
}
