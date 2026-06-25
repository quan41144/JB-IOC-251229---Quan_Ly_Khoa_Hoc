package ra.quan_ly_khoa_hoc.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews", uniqueConstraints = {@UniqueConstraint(name = "uk_course_student", columnNames = {"course_id", "student_id"})})
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Course course;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User student;
    @Column(name = "rating", nullable = false, columnDefinition = "int check (rating >= 1 and rating <= 5)")
    private Integer rating;
    @Column(name = "comment", columnDefinition = "text")
    private String comment;
    @Column(name = "created_at", nullable = false, columnDefinition = "timestamp default current_timestamp")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @Column(name = "updated_at", nullable = false, columnDefinition = "timestamp default current_timestamp")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) this.createdAt = now;
        if (updatedAt == null) this.updatedAt = now;
    }
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
