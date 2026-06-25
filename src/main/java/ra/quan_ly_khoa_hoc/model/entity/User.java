package ra.quan_ly_khoa_hoc.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer id;
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;
    @Column(name = "password_hash", nullable = false)
    private String password;
    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;
    @Column(name = "full_name", length = 100, nullable = false)
    private String fullName;
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private RoleStatus role = RoleStatus.STUDENT;
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
    @Column(name = "created_at", nullable = false, columnDefinition = "timestamp default current_timestamp")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @Column(name = "updated_at", nullable = false, columnDefinition = "timestamp default current_timestamp")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
    @OneToMany(mappedBy = "teacher", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Course> courses;
    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Enrollment> enrollments;
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Notification> notifications;
    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Review> reviews;
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (this.createdAt == null) this.createdAt = now;
        if (this.updatedAt == null) this.updatedAt = now;
    }
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
