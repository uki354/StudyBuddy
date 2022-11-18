package com.localhost.studybuddy.user;

import com.localhost.studybuddy.role.Role;
import com.localhost.studybuddy.role.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.HashSet;
import java.util.Set;

import static com.localhost.studybuddy.role.RoleServiceImpl.DEFAULT_ROLE;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService{

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleService roleService;

    @Override
    public void createUser(UserDto userDto) {
        if(checkIfUserAlreadyExist(userDto.getEmail()))
            throw new RuntimeException("User with email  " + userDto.getEmail() + " already exists");

        UserModel user = UserModel.builder()
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .name(userDto.getName())
                .lastname(userDto.getLastname())
                .build();

        addDefaultRole(user);
        userRepository.save(user);
    }



    private void addDefaultRole(UserModel user){
        Set<Role> roles = new HashSet<>();
        roles.add(roleService.findRoleByName(DEFAULT_ROLE));
        user.setRoles(roles);
    }

    private boolean checkIfUserAlreadyExist(String email){
        return userRepository.existsByEmail(email);
    }
}
