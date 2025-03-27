package be.pxl.demo.service.impl;

import be.pxl.demo.api.dto.UserDto;
import be.pxl.demo.domain.User;
import be.pxl.demo.exception.ResourceNotFoundException;
import be.pxl.demo.repository.UserRepository;
import be.pxl.demo.service.UserService;
import be.pxl.demo.service.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DefaultUserService implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public DefaultUserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toDto).toList();
    }

    @Override
    @Transactional
    public void promoteToAdmin(Long id) {
        User user = userRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        user.promoteToAdmin();
    }
}
