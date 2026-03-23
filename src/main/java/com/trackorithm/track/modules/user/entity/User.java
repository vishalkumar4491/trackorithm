package com.trackorithm.track.modules.user.entity;

import com.trackorithm.track.common.entity.BaseEntity;
import com.trackorithm.track.common.enums.AuthProvider;
import com.trackorithm.track.common.enums.Role;
import com.trackorithm.track.common.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "users",
        indexes = {
                @Index(name = "idx_users_email", columnList = "email")
        })
@Getter
@Setter
public class User extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    private String name;

    private String username;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "auth_provider", columnDefinition = "auth_provider_enum")
    private AuthProvider authProvider;

    @Column(name = "provider_id")
    private String providerId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", columnDefinition = "user_status_enum")
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role = Role.USER;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;
}
