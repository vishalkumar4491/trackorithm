package com.trackorithm.track.modules.user.entity;

import com.trackorithm.track.common.entity.BaseEntity;
import com.trackorithm.track.common.enums.AuthProvider;
import com.trackorithm.track.common.enums.UserStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    private String name;

    private String username;

    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;

    private String providerId;

    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;
}
