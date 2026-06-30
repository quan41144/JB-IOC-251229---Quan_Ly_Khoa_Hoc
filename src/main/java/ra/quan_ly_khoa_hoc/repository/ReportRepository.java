package ra.quan_ly_khoa_hoc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ra.quan_ly_khoa_hoc.model.entity.Course;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Course, Integer> {
    @Query("""
        select c from Course c
        left join c.enrollments e
        where c.isDeleted = false and (c.status = ra.quan_ly_khoa_hoc.model.entity.CourseStatus.PUBLISHED or c.status = ra.quan_ly_khoa_hoc.model.entity.CourseStatus.ARCHIVED)
        group by c.id
        order by count(e.id) desc
""")
    List<Course> findTopCoursesByEnrollment();

}
