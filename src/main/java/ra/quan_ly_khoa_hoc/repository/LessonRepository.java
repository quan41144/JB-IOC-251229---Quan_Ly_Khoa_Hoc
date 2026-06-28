package ra.quan_ly_khoa_hoc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ra.quan_ly_khoa_hoc.model.entity.Lesson;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Integer> {
    List<Lesson> findByCourseIdAndIsPublishedTrueOrderByOrderIndex(Integer courseId);
    List<Lesson> findByCourseIdOrderByOrderIndex(Integer courseId);
    Optional<Lesson> findLessonByIdAndIsPublishedTrue(Integer lessonId);
    Boolean existsByCourseIdAndOrderIndexAndIdNot(Integer courseId, Integer orderIndex, Integer lessonId);
    Boolean existsByCourseIdAndOrderIndex(Integer courseId, Integer orderIndex);
}
