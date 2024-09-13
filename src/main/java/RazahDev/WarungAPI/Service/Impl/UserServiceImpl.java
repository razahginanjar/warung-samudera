package RazahDev.WarungAPI.Service.Impl;


import RazahDev.WarungAPI.Entity.UserAccount;
import RazahDev.WarungAPI.Repository.UserAccountRepository;
import RazahDev.WarungAPI.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserAccountRepository userAccountRepository;

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userAccountRepository.findByUsername(username).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account do not exist")
        );
    }

    @Override
    @Transactional(readOnly = true)
    public UserAccount getUserByID(String idUser) {
        return userAccountRepository.findById(idUser).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account Not Found")
        );
    }

    @Override
    @Transactional(readOnly = true)
    public UserAccount getByContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<UserAccount> byUsername = userAccountRepository.findByUsername(authentication.getPrincipal().toString());
        return byUsername.orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Users not found")
        );
    }
}
