package be.pxl.demo.api;

import be.pxl.demo.config.JwtAuthenticationFilter;
import be.pxl.demo.config.JwtUtilities;
import be.pxl.demo.config.SpringSecurityConfig;
import be.pxl.demo.security.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PublicController.class) // Loads only the AdminController with MockMvc
@Import(SpringSecurityConfig.class)
public class PublicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtUtilities jwtUtilities;
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void testWelcome_ShouldReturnWelcomeMessage() throws Exception {
        mockMvc.perform(get("/public/welcome"))
                .andExpect(status().isOk()) // Expect 200 OK
                .andExpect(content().string("Welcome! This is a public content!"));
    }
}
