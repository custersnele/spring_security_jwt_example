package be.pxl.demo.api;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @GetMapping
    public String getAdminDashboard(Authentication authentication) {
        return "Dashboard - Welcome " + authentication.getName();
    }
}
