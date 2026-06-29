package ra.quan_ly_khoa_hoc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ra.quan_ly_khoa_hoc.model.entity.InvalidateToken;

import java.time.LocalDateTime;

@Repository
public interface InvalidateTokenRepository extends JpaRepository<InvalidateToken, Long> {
    Boolean existsByToken(String token);
    @Query("delete from InvalidateToken it where it.expiryDate <= :now")
    @Modifying
    @Transactional
    void deleteExpiredTokens(@Param("now") LocalDateTime now);
}
