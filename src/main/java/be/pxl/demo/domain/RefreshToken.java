package be.pxl.demo.domain;

import jakarta.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class RefreshToken {
    @Id
    @Column(nullable = false, updatable = false)
    private UUID uuid;

    @Column(nullable = false)
    private Instant expirationTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public RefreshToken(UUID uuid, String token, Instant expirationTime, User user) {
        this.uuid = uuid;
        this.expirationTime = expirationTime;
        this.user = user;
    }

    public RefreshToken() {
    }

    public Instant getExpirationTime() {
        return expirationTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RefreshToken that = (RefreshToken) o;
        return uuid != null && uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid != null ? uuid.hashCode() : 0;
    }
}
