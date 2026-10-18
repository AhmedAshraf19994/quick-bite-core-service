package com.quick_bite.core_service.user;

import com.quick_bite.core_service.common.exception.ObjectNotFoundException;
import com.quick_bite.core_service.user.dto.CreateUserDto;
import com.quick_bite.core_service.user.dto.UpdateUserDto;
import com.quick_bite.core_service.user.dto.UserResponseDto;
import com.quick_bite.core_service.user.exceptions.UserExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


    public UserResponseDto createUser(CreateUserDto dto) {
        // check if user already exists
        if (this.userRepository.existsByEmail(dto.email())) {
            throw new UserExistsException();
        }

        User user = this.userMapper.toUser(dto);

        //password need to be hashed before saving too database
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));

        User savedUser = this.userRepository.save(user);

        return this.userMapper.toUserResponseDto(savedUser);
    }

    public List<UserResponseDto> getAllUsers() {
        List<User> users = this.userRepository.findAll();
        return users.stream()
                .map(this.userMapper::toUserResponseDto)
                .toList();
    }

    public UserResponseDto getUserById(Long id) {
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("User"));

        return this.userMapper.toUserResponseDto(user);
    }

    public UserResponseDto updateUserById(Long id, UpdateUserDto dto) {

            User user = this.userRepository.findById(id)
                    .orElseThrow(() -> new ObjectNotFoundException("User"));

            this.userMapper.updateUser(dto, user);

            User savedUser = this.userRepository.save(user);

            return this.userMapper.toUserResponseDto(savedUser);

        }

    public void deleteUserById (Long userId){
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User"));

        this.userRepository.delete(user);
        }

    }

