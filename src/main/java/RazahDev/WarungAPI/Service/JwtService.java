package RazahDev.WarungAPI.Service;

import RazahDev.WarungAPI.Entity.UserAccount;
import RazahDev.WarungAPI.DTO.JWTClaims;

public interface JwtService {
    String generateToken(UserAccount userAccount);
    Boolean verifyToken(String token);
    JWTClaims claimToken(String token);
}
