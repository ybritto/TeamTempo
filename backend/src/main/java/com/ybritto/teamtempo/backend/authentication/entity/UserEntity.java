package com.ybritto.teamtempo.backend.authentication.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Builder(toBuilder = true) // toBuilder is true to facilitate object copy/transformation to perform IT tests
@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode(of = {"uuid"})
@Entity
@Table(
        name = "app_user",
        uniqueConstraints = {
                @UniqueConstraint(name = "app_user_uuid_unique", columnNames = {"uuid"}),
                @UniqueConstraint(name = "app_user_email_unique", columnNames = {"email"})
        })
public class UserEntity implements UserDetails {

        @Id
        @Column(name = "key_id")
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userSeqGen")
        @SequenceGenerator(name = "userSeqGen", sequenceName = "app_user_key_id_seq", allocationSize = 1)
        private Long id;

        @Column(name = "uuid", unique = true, nullable = false, updatable = false)
        private UUID uuid;

        @Column(nullable = false, name = "full_name", length = 200)
        @NotNull(message = "Name can not be null")
        @NotEmpty(message = "Name can not be empty")
        @Size(min = 1, max = 200)
        private String fullName;

        @Column(name = "email", length = 1000, nullable = false, unique = true)
        @Email(message = "Email should be valid")
        @Size(max = 1000)
        @NotNull(message = "Email can not be null")
        @NotEmpty(message = "Email can not be empty")
        private String email;

        @Column(name = "password", nullable = false, length = 255)
        @NotNull(message = "Password can not be null")
        @NotEmpty(message = "Password can not be empty")
        @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
        private String password;

        @Column(name = "enabled", nullable = false)
        @NotNull(message = "Enabled status can not be null")
        private boolean enabled = true;

        @Enumerated(EnumType.STRING)
        @Column(name = "role", nullable = false)
        private SecurityRoleEnum role = SecurityRoleEnum.USER; // Default role

        @Column(name = "created_at", nullable = false)
        private LocalDateTime createdAt;

        @Column(name = "updated_at", nullable = false)
        private LocalDateTime updatedAt;

        @PrePersist
        private void prePersist() {
                if (this.uuid == null) {
                        this.uuid = UUID.randomUUID();
                }
                if (this.createdAt == null) {
                        this.createdAt = LocalDateTime.now();
                }
                if (this.updatedAt == null) {
                        this.updatedAt = LocalDateTime.now();
                }
                if (this.role == null) {
                        this.role = SecurityRoleEnum.USER;
                }
        }

        @PreUpdate
        private void preUpdate() {
                this.updatedAt = LocalDateTime.now();
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
                return List.of();
        }

        @Override
        public String getUsername() {
                return this.getEmail();
        }

        @Override
        public boolean isAccountNonExpired() {
                return UserDetails.super.isAccountNonExpired();
        }

        @Override
        public boolean isAccountNonLocked() {
                return UserDetails.super.isAccountNonLocked();
        }

        @Override
        public boolean isCredentialsNonExpired() {
                return UserDetails.super.isCredentialsNonExpired();
        }

        @Override
        public boolean isEnabled() {
                return UserDetails.super.isEnabled();
        }
}
