package be.pxl.demo.service.mapper;

import be.pxl.demo.api.dto.UserDto;
import be.pxl.demo.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "role", target = "userRole")
    UserDto toDto(User user);

    List<UserDto> toDtoList(List<User> users);
}
