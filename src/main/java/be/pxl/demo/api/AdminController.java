package be.pxl.demo.api;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/hello")
    public String sayHello(Authentication authentication) {
        return "Welcome " + authentication.getPrincipal() +" you are authenticated as Admin!";
    }
}
