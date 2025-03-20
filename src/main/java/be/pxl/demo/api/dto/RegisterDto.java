package be.pxl.demo.api.dto;

import be.pxl.demo.domain.Role;

public record RegisterDto(String firstName,
                          String lastName,
                          String email,
                          String password,
                          Role userRole) {
}
