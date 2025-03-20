package be.pxl.demo.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
public class PublicController {

    @GetMapping("/welcome")
    public ResponseEntity<String> welcome() {
        return ResponseEntity.ok("Welcome! This is a public content!");
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')") // ✅ Additional layer of security
    public String getAdminDashboard(Authentication authentication) {
        return "Admin Dashboard - Welcome " + authentication.getName();
    }
}
