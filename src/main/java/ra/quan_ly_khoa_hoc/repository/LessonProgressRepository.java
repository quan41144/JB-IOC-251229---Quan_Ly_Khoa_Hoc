package ra.quan_ly_khoa_hoc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ra.quan_ly_khoa_hoc.model.entity.LessonProgress;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, Integer> {
    List<LessonProgress> findByEnrollmentId(Integer enrollmentId);
    Optional<LessonProgress> findByEnrollmentIdAndLessonId(Integer enrollmentId, Integer lessonId);
    void deleteByLessonId(Integer lessonId);
    @Query("""
        select count(lp) from LessonProgress lp
        where lp.enrollment.id = :enrollment_id
""")
    Long countALLLessonProgressesByEnrollmentId(@Param("enrollment_id") Integer enrollmentId);
    @Query("""
        select count(lp) from LessonProgress lp
        where lp.enrollment.id = :enrollment_id and lp.isCompleted = true
""")
    Long countAllLessonProgressesCompletedByEnrollmentId(@Param("enrollment_id") Integer enrollmentId);
}
