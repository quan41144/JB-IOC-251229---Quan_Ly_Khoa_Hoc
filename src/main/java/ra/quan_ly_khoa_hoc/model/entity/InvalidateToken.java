package ra.quan_ly_khoa_hoc.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "invalidate_token")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class InvalidateToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invalidate_token_id")
    private Long id;
    @Column(name = "token", nullable = false, columnDefinition = "text", unique = true)
    private String token;
    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;
}
