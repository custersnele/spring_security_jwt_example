package be.pxl.demo.service;

import be.pxl.demo.api.dto.LoginDto;
import be.pxl.demo.api.dto.RegisterDto;
import be.pxl.demo.api.dto.TokenDto;
import be.pxl.demo.api.dto.UserDto;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface UserService {
        TokenDto authenticate(LoginDto loginDto);
        void register (RegisterDto registerDto);

        @PreAuthorize("hasRole('ADMIN')")
        List<UserDto> findAllUsers();
}
