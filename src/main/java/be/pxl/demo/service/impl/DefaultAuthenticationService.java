package be.pxl.demo.service.impl;

import be.pxl.demo.api.dto.LoginDto;
import be.pxl.demo.api.dto.RegisterDto;
import be.pxl.demo.api.dto.TokenDto;
import be.pxl.demo.config.JwtUtilities;
import be.pxl.demo.domain.RefreshToken;
import be.pxl.demo.domain.Role;
import be.pxl.demo.domain.User;
import be.pxl.demo.exception.DuplicateEmailException;
import be.pxl.demo.exception.RefreshTokenExpiredException;
import be.pxl.demo.exception.ResourceNotFoundException;
import be.pxl.demo.repository.RefreshTokenRepository;
import be.pxl.demo.repository.UserRepository;
import be.pxl.demo.service.AuthenticationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.UUID;

@Service
public class DefaultAuthenticationService implements AuthenticationService {

    private static final Logger LOGGER = LogManager.getLogger(DefaultAuthenticationService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtilities jwtUtilities;
    private final UserDetailsService userDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.jwtRefreshTokenExpirationTime}")
    private Long refreshTokenExpirationTime;

    public DefaultAuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtilities jwtUtilities, UserDetailsService userDetailsService, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtilities = jwtUtilities;
        this.userDetailsService = userDetailsService;
        this.refreshTokenRepository = refreshTokenRepository;
    }


    public void register(RegisterDto registerDto) {
        if (userRepository.existsByEmail(registerDto.email())) {
            throw new DuplicateEmailException();
        } else {
            User user = new User();
            user.setEmail(registerDto.email());
            user.setFirstName(registerDto.firstName());
            user.setLastName(registerDto.lastName());
            user.setPassword(passwordEncoder.encode(registerDto.password()));
            user.setRole(Role.USER);
            userRepository.save(user);
        }
    }


    @Transactional
    public TokenDto refreshToken(String refreshToken) {
        jwtUtilities.validateToken(refreshToken);
        String email = jwtUtilities.extractUsername(refreshToken);
        UUID jti = jwtUtilities.extractTokenId(refreshToken);

        User user = (User) userDetailsService.loadUserByUsername(email);

        RefreshToken storedRefreshToken = refreshTokenRepository.findByUuidAndUser(jti, user).orElseThrow(ResourceNotFoundException::new);
        if (storedRefreshToken.getExpirationTime().isBefore(Instant.now())) {
            refreshTokenRepository.delete(storedRefreshToken);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("The refresh token is expired.");
            }
            throw new RefreshTokenExpiredException();
        }

        refreshTokenRepository.delete(storedRefreshToken);

        String newAccessToken = jwtUtilities.generateToken(user.getUsername(),
                user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList());

        String newRefreshToken = createAndStoreRefreshToken(user);
        return new TokenDto(newAccessToken, newRefreshToken);
    }


    @Transactional
    public TokenDto authenticate(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.email(),
                        loginDto.password()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        String token = jwtUtilities.generateToken(user.getUsername(), Collections.singletonList(user.getRole().name()));
        String refreshToken = createAndStoreRefreshToken(user);

        return new TokenDto(token, refreshToken);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public String createAndStoreRefreshToken(User user) {
        refreshTokenRepository.deleteByUser(user);
        UUID uuid = UUID.randomUUID();
        Instant expirationDate = Instant.now().plus(refreshTokenExpirationTime, ChronoUnit.MILLIS);
        String token = jwtUtilities.generateRefreshToken(user.getUsername(), uuid.toString(), expirationDate);
        RefreshToken refreshToken = new RefreshToken(uuid, token, expirationDate, user);
        refreshTokenRepository.save(refreshToken);
        return token;
    }
}
