package RazahDev.WarungAPI.Controller;

import RazahDev.WarungAPI.Constant.APIUrl;
import RazahDev.WarungAPI.DTO.Auth.AuthRequest;
import RazahDev.WarungAPI.DTO.Auth.LoginResponse;
import RazahDev.WarungAPI.DTO.Auth.RegisterResponse;
import RazahDev.WarungAPI.DTO.GenericResponse;
import RazahDev.WarungAPI.Service.Impl.AuthServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = APIUrl.AUTH_API)
public class AuthController {
    private final AuthServiceImpl service;

    @PostMapping(path = "/register")
    public ResponseEntity<GenericResponse<?>> register(@RequestBody AuthRequest request)
    {
        RegisterResponse register = service.register(request);
        return ResponseEntity.status(HttpStatus.CREATED.value())
                .body(
                        GenericResponse.builder()
                                .status(HttpStatus.CREATED.value())
                                .data(register)
                                .message("Successfully registered")
                                .build()
                );
    }

    @PostMapping(path = "/login")
    public ResponseEntity<GenericResponse<?>> login(@RequestBody AuthRequest request)
    {
        LoginResponse login = service.login(request);
        return ResponseEntity.status(HttpStatus.OK.value())
                .body(
                        GenericResponse.builder()
                                .status(HttpStatus.OK.value())
                                .data(login)
                                .message("Successfully login")
                                .build()
                );
    }
}
