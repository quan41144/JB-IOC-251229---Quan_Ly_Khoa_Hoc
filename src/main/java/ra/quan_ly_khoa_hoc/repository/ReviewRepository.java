package ra.quan_ly_khoa_hoc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ra.quan_ly_khoa_hoc.model.entity.Review;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    Optional<Review> findByCourseIdAndStudentId(Integer courseId, Integer studentId);
    List<Review> findReviewsByCourseId(Integer courseId);
}
