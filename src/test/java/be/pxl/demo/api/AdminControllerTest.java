package be.pxl.demo.api;

import be.pxl.demo.config.JwtAuthenticationFilter;
import be.pxl.demo.config.JwtUtilities;
import be.pxl.demo.config.SpringSecurityConfig;
import be.pxl.demo.domain.Role;
import be.pxl.demo.domain.User;
import be.pxl.demo.security.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class) // Loads only the AdminController with MockMvc
@Import({SpringSecurityConfig.class, JwtUtilities.class})
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtilities jwtUtilities;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void testSayHello_WithAdminUser_ShouldReturnWelcomeMessage() throws Exception {
        String jwtToken = jwtUtilities.generateToken("admin", Collections.singletonList("ADMIN"));
        User user = new User();
        user.setRole(Role.ADMIN);
        user.setEmail("admin");
        Mockito.when(customUserDetailsService.loadUserByUsername("admin")).thenReturn(user);

        mockMvc.perform(get("/admin/hello").header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk()) // Expect 200 OK
                .andExpect(content().string("Welcome admin you are authenticated as Admin!"));
    }

    @Test
    void testSayHello_WithUser_ShouldReturnAccessDenied() throws Exception {
        String jwtToken = jwtUtilities.generateToken("eve", Collections.singletonList("USER"));
        User user = new User();
        user.setRole(Role.USER);
        user.setEmail("eve");
        Mockito.when(customUserDetailsService.loadUserByUsername("eve")).thenReturn(user);

        mockMvc.perform(get("/admin/hello").header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().is(403));
    }

    @Test
    void testSayHello_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/admin/hello"))
                .andExpect(status().isUnauthorized()); // Expect 401 Unauthorized
    }
}
