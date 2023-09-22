package book.store.service;

import book.store.model.Role;
import java.util.List;

public interface RoleService {
    Role save(Role role);

    Role findRoleByName(Role.RoleName roleName);

    List<Role> findAll();
}
