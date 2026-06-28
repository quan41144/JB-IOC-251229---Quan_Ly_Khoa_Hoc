package ra.quan_ly_khoa_hoc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ra.quan_ly_khoa_hoc.model.entity.Course;
import ra.quan_ly_khoa_hoc.model.entity.CourseStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {
    List<Course> findCoursesByStatusAndIsDeletedFalse(CourseStatus status);
    List<Course> findAllByIsDeletedFalse();
    @Query("""
        select c from Course c
        join Enrollment e on e.course.id = c.id
        where c.status = 'ARCHIVED'
        and c.isDeleted = false
        and e.student.id = :studentId
""")
    List<Course> findArchivedCoursesByStudentId(@Param("studentId") Integer studentId);
    List<Course> findByTeacherIdAndIsDeletedFalse(Integer teacherId);
    Optional<Course> findByIdAndIsDeletedFalse(Integer id);
}
