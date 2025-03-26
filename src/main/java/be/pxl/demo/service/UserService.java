package be.pxl.demo.service;

import be.pxl.demo.api.dto.UserDto;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface UserService {
    @PreAuthorize("hasRole('ADMIN')")
    List<UserDto> findAllUsers();
}
