package be.pxl.demo.repository;

import be.pxl.demo.domain.RefreshToken;
import be.pxl.demo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    void deleteByUser(User user);
    Optional<RefreshToken> findByUuidAndUser(UUID uuid, User user);
}
