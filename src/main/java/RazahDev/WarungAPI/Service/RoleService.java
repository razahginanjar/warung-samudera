package RazahDev.WarungAPI.Service;

import RazahDev.WarungAPI.Constant.Role;
import RazahDev.WarungAPI.Entity.UserRole;

public interface RoleService {
    UserRole getOrSave(Role role);
}
