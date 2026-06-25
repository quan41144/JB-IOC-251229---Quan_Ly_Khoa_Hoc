package ra.quan_ly_khoa_hoc.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "lesson_progress", uniqueConstraints = {@UniqueConstraint(name = "uk_enrollment_lesson", columnNames = {"enrollment_id", "lesson_id"})})
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LessonProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "progress_id")
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Enrollment enrollment;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Lesson lesson;
    @Column(name = "is_completed", nullable = false, columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean isCompleted = false;
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    @Column(name = "last_accessed_at", nullable = false, columnDefinition = "timestamp default current_timestamp")
    @Builder.Default
    private LocalDateTime lastAccessedAt = LocalDateTime.now();
    @PrePersist
    private void onCreate() {
        if (this.lastAccessedAt == null) this.lastAccessedAt = LocalDateTime.now();
    }
    @PreUpdate
    private void onUpdate() {
        this.lastAccessedAt = LocalDateTime.now();
    }
}
