package be.pxl.demo.api;

import be.pxl.demo.api.dto.*;
import be.pxl.demo.service.AuthenticationService;
import be.pxl.demo.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    public AuthenticationController(UserService userService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public void register(@RequestBody RegisterDto registerDto) {
        authenticationService.register(registerDto);
    }

    @PostMapping("/refresh-token")
    public TokenDto refreshToken(@RequestBody RefreshTokenRequest request) {
        return authenticationService.refreshToken(request.refreshToken());
    }

    @PostMapping("/authenticate")
    public TokenDto authenticate(@RequestBody LoginDto loginDto) {
        return authenticationService.authenticate(loginDto);
    }

    @GetMapping
    public List<UserDto> findAllUsers() {
        return userService.findAllUsers();
    }
}
