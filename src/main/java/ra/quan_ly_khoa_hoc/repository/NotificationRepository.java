package ra.quan_ly_khoa_hoc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ra.quan_ly_khoa_hoc.model.entity.Notification;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    @Query("""
        select n from Notification n
        where n.user.id = :currentUserId
        order by n.createdAt desc
""")
    List<Notification> findAllNotificationsByUserId(@Param("currentUserId") Integer currentUserId);
    Optional<Notification> findByIdAndUserId(Integer id, Integer userId);
}
