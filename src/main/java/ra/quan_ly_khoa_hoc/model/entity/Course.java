package ra.quan_ly_khoa_hoc.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "courses")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Integer id;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "description", columnDefinition = "text")
    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User teacher;
    @Column(name = "price", nullable = false, columnDefinition = "numeric(10,2) default 0.00")
    @Builder.Default
    private BigDecimal price = BigDecimal.ZERO;
    @Column(name = "duration_hours")
    private Integer duration;
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CourseStatus status = CourseStatus.DRAFT;
    @Column(name = "created_at", nullable = false, columnDefinition = "timestamp default current_timestamp")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @Column(name = "updated_at", nullable = false, columnDefinition = "timestamp default current_timestamp")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Lesson> lessons;
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Enrollment> enrollments;
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
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
