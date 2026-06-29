package ra.quan_ly_khoa_hoc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ra.quan_ly_khoa_hoc.model.entity.RoleStatus;
import ra.quan_ly_khoa_hoc.model.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsernameAndIsDeletedFalse(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByIdAndIsDeletedFalse(Integer id);
    Optional<User> findByFullName(String fullName);
    List<User> findByRoleAndIsActiveAndIsDeletedFalse(RoleStatus role, Boolean isActive);
    List<User> findByRoleAndIsDeletedFalse(RoleStatus role);
    List<User> findByIsActiveAndIsDeletedFalse(Boolean isActive);
    List<User> findByIsDeletedFalse();
}
