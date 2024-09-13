package RazahDev.WarungAPI.Service.Impl;


import RazahDev.WarungAPI.Constant.Role;
import RazahDev.WarungAPI.Entity.Customer;
import RazahDev.WarungAPI.Entity.UserAccount;
import RazahDev.WarungAPI.Entity.UserRole;
import RazahDev.WarungAPI.DTO.Auth.AuthRequest;
import RazahDev.WarungAPI.DTO.Auth.LoginResponse;
import RazahDev.WarungAPI.DTO.Auth.RegisterResponse;
import RazahDev.WarungAPI.Repository.UserAccountRepository;
import RazahDev.WarungAPI.Service.AuthService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserAccountRepository userAccountRepository;
    private final RoleServiceImpl roleService;
    private final PasswordEncoder passwordEncoder;
    private final CustomerServiceImpl customerServiceImpl;
    private final JwtServiceImpl jwtService;
    private final AuthenticationManager authenticationManager;


    @Value(value = "${toko_tiktak.SUPER_ADMIN.username}")
    private String superAdminUsername;

    @Value(value = "${toko_tiktak.SUPER_ADMIN.password}")
    private String superAdminPassword;

    //create a super admin
    @Transactional(rollbackFor = Exception.class)
    @PostConstruct
    public void initSuperAdmin()
    {
        userAccountRepository.findByUsername(superAdminUsername).orElseGet(
                () -> userAccountRepository.saveAndFlush(UserAccount.builder()
                                .username(superAdminUsername)
                                .password(passwordEncoder.encode(superAdminPassword))
                                .userRole(List.of(
                                        roleService.getOrSave(Role.ROLE_SUPER_ADMIN),
                                        roleService.getOrSave(Role.ROLE_ADMIN),
                                        roleService.getOrSave(Role.ROLE_CUSTOMER)
                                ))
                                .isEnabled(true)
                        .build())
        );
    }


    @Transactional(rollbackFor = Exception.class)
    public RegisterResponse register(AuthRequest request)
    throws DataIntegrityViolationException
    {
        UserRole role = roleService.getOrSave(Role.ROLE_CUSTOMER);
        String hashPassword = passwordEncoder.encode(request.getPassword());
        UserAccount account = UserAccount.builder()
                .username(request.getUsername())
                .password(hashPassword)
                .userRole(List.of(role))
                .isEnabled(true)
                .build();
        userAccountRepository.saveAndFlush(account);

        Customer customer = Customer.builder()
                .status(true)
                .userAccount(account)
                .build();
        customerServiceImpl.createFromCustomer(customer);

        return RegisterResponse.builder()
                .username(account.getUsername())
                .roles(account.getAuthorities().stream().map(
                        GrantedAuthority::getAuthority
                ).toList())
                .build();
    }



    @Transactional(rollbackFor = Exception.class)
    public LoginResponse login(AuthRequest request) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        );
        Authentication authenticate = authenticationManager.authenticate(authentication);// get data from token
        UserAccount user = (UserAccount) authenticate.getPrincipal(); //get the whole user account that it search for
        String s = jwtService.generateToken(user);
        return LoginResponse.builder()
                .token(s).username(user.getUsername())
                .roles(user.getAuthorities().stream().map(
                        GrantedAuthority::getAuthority
                ).toList())
                .build();
    }
}
