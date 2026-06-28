package ra.quan_ly_khoa_hoc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ra.quan_ly_khoa_hoc.model.entity.Enrollment;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    Boolean existsByStudentIdAndCourseId(Integer studentId, Integer courseId);
}
