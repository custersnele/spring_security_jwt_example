package be.pxl.demo.api.dto;

import be.pxl.demo.domain.Role;

public record UserDto(String firstName,
                      String lastName,
                      String email,
                      Role userRole) {
}
