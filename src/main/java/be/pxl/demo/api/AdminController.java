package be.pxl.demo.api;

import be.pxl.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/hello")
    @PreAuthorize("hasRole('ADMIN')") // Additional layer of security
    public String sayHello(Authentication authentication) {
        return "Welcome " + authentication.getPrincipal() +" you are authenticated as Admin!";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/{id}/promote")
    public ResponseEntity<?> promoteToAdmin(@PathVariable Long id) {
        userService.promoteToAdmin(id);
        return ResponseEntity.ok().build();
    }
}
