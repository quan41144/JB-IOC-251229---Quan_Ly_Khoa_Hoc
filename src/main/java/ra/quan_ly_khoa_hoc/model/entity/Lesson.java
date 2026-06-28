package ra.quan_ly_khoa_hoc.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "lessons")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lesson_id")
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Course course;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "content_url", length = 500)
    private String contentURL;
    @Column(name = "text_content", columnDefinition = "text")
    private String textContent;
    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;
    @Column(name = "is_published", nullable = false, columnDefinition = "boolean")
    @Builder.Default
    private Boolean isPublished = false;
    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<LessonProgress> lessonProgress;
    @PrePersist
    protected void onCreate()
    {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) this.createdAt = now;
        if (updatedAt == null) this.updatedAt = now;
    }
    @PreUpdate
    protected void onUpdate()
    {
        this.updatedAt = LocalDateTime.now();
    }
}
