package com.localhost.studybuddy.role;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RoleServiceImpl implements  RoleService{

    private final RoleRepository roleRepository;

    public static final String DEFAULT_ROLE = "ROLE_USER";

    @Override
    public Role findRoleByName(String name) {
        return roleRepository.getRoleByName(name)
                .orElseThrow(()-> new RuntimeException("Role with name " + name + " not found"));
    }



}
