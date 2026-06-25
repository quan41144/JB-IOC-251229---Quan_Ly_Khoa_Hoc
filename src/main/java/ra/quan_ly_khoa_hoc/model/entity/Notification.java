package ra.quan_ly_khoa_hoc.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;
    @Column(name = "message", nullable = false, columnDefinition = "text")
    private String message;
    @Column(name = "type", length = 50)
    private String type;
    @Column(name = "target_url", length = 500)
    private String targetUrl;
    @Column(name = "is_read", nullable = false, columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean isRead = false;
    @Column(name = "created_at", nullable = false, columnDefinition = "timestamp default current_timestamp")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @PrePersist
    public void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
