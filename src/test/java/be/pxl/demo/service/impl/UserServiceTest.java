package be.pxl.demo.service.impl;

import be.pxl.demo.api.dto.UserDto;
import be.pxl.demo.builder.UserBuilder;
import be.pxl.demo.domain.User;
import be.pxl.demo.repository.UserRepository;
import be.pxl.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserServiceTest {

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        List<User> users = Arrays.asList(UserBuilder.anUser().build(), UserBuilder.anUser().build());
        when(userRepository.findAll()).thenReturn(users);
    }

    @Test
    @WithMockUser(roles = "ADMIN") // Simulate a user with ADMIN role
    void findAllUsers_ShouldReturnUsers_WhenUserIsAdmin() {
        List<UserDto> users = userService.findAllUsers();
        assertNotNull(users);
        assertEquals(2, users.size());
    }

    @Test
    @WithMockUser(roles = "USER") // Simulate a user with USER role
    void findAllUsers_ShouldThrowException_WhenUserIsNotAdmin() {
        assertThrows(AccessDeniedException.class, () -> userService.findAllUsers());
    }

    @Test
    void findAllUsers_ShouldThrowException_WhenUserIsNotAuthenticated() {
        assertThrows(AuthenticationCredentialsNotFoundException.class, () -> userService.findAllUsers());
    }
}
