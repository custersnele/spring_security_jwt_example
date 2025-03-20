package be.pxl.demo.api;

import be.pxl.demo.api.dto.LoginDto;
import be.pxl.demo.api.dto.RegisterDto;
import be.pxl.demo.api.dto.TokenDto;
import be.pxl.demo.api.dto.UserDto;
import be.pxl.demo.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public void register(@RequestBody RegisterDto registerDto) {
        userService.register(registerDto);
    }

    @PostMapping("/authenticate")
    public TokenDto authenticate(@RequestBody LoginDto loginDto) {
        return userService.authenticate(loginDto);
    }

    @GetMapping
    public List<UserDto> findAllUsers() {
        return userService.findAllUsers();
    }
}
