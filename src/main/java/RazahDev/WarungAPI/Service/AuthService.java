package RazahDev.WarungAPI.Service;

import RazahDev.WarungAPI.DTO.Auth.AuthRequest;
import RazahDev.WarungAPI.DTO.Auth.LoginResponse;
import RazahDev.WarungAPI.DTO.Auth.RegisterResponse;

public interface AuthService {
    RegisterResponse register(AuthRequest request);
    LoginResponse login(AuthRequest request);
}
