package kr.hjun.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_verifications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "email_verification_id")
    private Long id;

    @Column(nullable = false, length = 120)
    private String email;

    @Column(nullable = false, length = 6)
    private String code;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean verified;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 만료 여부를 반환한다.
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
