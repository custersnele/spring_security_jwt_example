package be.pxl.demo.service;

import be.pxl.demo.api.dto.LoginDto;
import be.pxl.demo.api.dto.RegisterDto;
import be.pxl.demo.api.dto.TokenDto;

public interface AuthenticationService {
        TokenDto authenticate(LoginDto loginDto);
        void register (RegisterDto registerDto);
        TokenDto refreshToken(String refreshToken);
}
