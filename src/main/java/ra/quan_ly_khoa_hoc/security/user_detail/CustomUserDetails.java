package ra.quan_ly_khoa_hoc.security.user_detail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ra.quan_ly_khoa_hoc.model.entity.RoleStatus;
import ra.quan_ly_khoa_hoc.model.entity.User;

import java.util.Collection;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CustomUserDetails implements UserDetails {
    private User user;
    private Collection<? extends GrantedAuthority> authorities;

    @Override
    public @Nullable String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getIsActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getIsActive();
    }

    public Integer getUserId() {
        return user.getId();
    }

    public RoleStatus getRole() {
        return user.getRole();
    }
}
