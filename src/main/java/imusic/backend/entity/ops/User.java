package imusic.backend.entity.ops;

import jakarta.persistence.*;
import lombok.*;
import imusic.backend.entity.ref.Role;
import imusic.backend.entity.ref.UserStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "users", schema = "ops")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(name = "full_name")
    private String fullName;
    private String email;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "role_id")
    private Role role;
    @Column(name = "avatar_path")
    private String avatarPath;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "status_id")
    private UserStatus status;
    @Column(name = "is_blocked")
    private boolean isBlocked = false;
    @Column(name = "is_deleted")
    private boolean isDeleted = false;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
