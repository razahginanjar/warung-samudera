package RazahDev.WarungAPI.Service.Impl;


import RazahDev.WarungAPI.Constant.Role;
import RazahDev.WarungAPI.Entity.UserRole;
import RazahDev.WarungAPI.Repository.UserRoleRepository;
import RazahDev.WarungAPI.Service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final UserRoleRepository roleRepository;


    public UserRole getOrSave(Role role) {
        return roleRepository.findUserRoleByRole(role).orElseGet(
                () -> roleRepository.saveAndFlush(UserRole.builder()
                        .role(role)
                        .build())
        );
    }
}
