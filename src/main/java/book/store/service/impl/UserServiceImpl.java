package book.store.service.impl;

import book.store.dto.user.UserRegistrationRequestDto;
import book.store.dto.user.UserRegistrationResponseDto;
import book.store.exception.RegistrationException;
import book.store.mapper.UserRegistrationMapper;
import book.store.model.Role;
import book.store.model.User;
import book.store.repository.UserRepository;
import book.store.service.RoleService;
import book.store.service.UserService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRegistrationMapper userRegistrationMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    @Override
    public UserRegistrationResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new RegistrationException("Unable to complete registration!");
        }
        User user = userRegistrationMapper.toModel(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setRoles(Set.of(roleService.findRoleByName(Role.RoleName.USER)));
        User savedUser = userRepository.save(user);
        return userRegistrationMapper.toDto(user);
    }
}
