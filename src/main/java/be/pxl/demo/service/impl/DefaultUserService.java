package be.pxl.demo.service.impl;

import be.pxl.demo.api.dto.LoginDto;
import be.pxl.demo.api.dto.RegisterDto;
import be.pxl.demo.api.dto.TokenDto;
import be.pxl.demo.api.dto.UserDto;
import be.pxl.demo.config.JwtUtilities;
import be.pxl.demo.domain.User;
import be.pxl.demo.repository.UserRepository;
import be.pxl.demo.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtilities jwtUtilities;

    public DefaultUserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtilities jwtUtilities) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtilities = jwtUtilities;
    }


    public void register(RegisterDto registerDto) {

        if (userRepository.existsByEmail(registerDto.email())) {
            throw new RuntimeException("email is already taken !");
        } else {
            User user = new User();
            user.setEmail(registerDto.email());
            user.setFirstName(registerDto.firstName());
            user.setLastName(registerDto.lastName());
            user.setPassword(passwordEncoder.encode(registerDto.password()));
            user.setRole(registerDto.userRole());
            userRepository.save(user);
        }
    }

    @Override
    public List<UserDto> findAllUsers() {
        return userRepository.findAll().stream().map(u -> new UserDto(u.getFirstName(), u.getLastName(), u.getEmail(), u.getRole())).toList();
    }


    @Override
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
        return new TokenDto(token);
    }
}
