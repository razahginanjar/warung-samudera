package RazahDev.WarungAPI.Service;

import RazahDev.WarungAPI.Entity.UserAccount;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserAccount getUserByID(String idUser);
    UserAccount getByContext();
}
