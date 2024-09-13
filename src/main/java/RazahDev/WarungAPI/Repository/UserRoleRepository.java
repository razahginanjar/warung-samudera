package RazahDev.WarungAPI.Repository;

import RazahDev.WarungAPI.Constant.Role;
import RazahDev.WarungAPI.Entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, String> {
    Optional<UserRole> findUserRoleByRole(Role role);
}
