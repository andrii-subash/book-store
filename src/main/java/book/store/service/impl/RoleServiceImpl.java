package book.store.service.impl;

import book.store.model.Role;
import book.store.repository.RoleRepository;
import book.store.service.RoleService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    public Role save(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public Role findRoleByName(Role.RoleName name) {
        return roleRepository.findRoleByName(name);
    }

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }
}
