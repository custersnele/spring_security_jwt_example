package be.pxl.demo.api.dto;

public record RegisterDto(String firstName,
                          String lastName,
                          String email,
                          String password) {
}
