package ra.quan_ly_khoa_hoc.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ra.quan_ly_khoa_hoc.repository.InvalidateTokenRepository;

import java.time.LocalDateTime;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class TokenCleanupScheduler {
    private final InvalidateTokenRepository invalidateTokenRepository;
    @Scheduled(fixedRate = 3600000)
    public void cleanupExpiredTokens() {
        invalidateTokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }
}
