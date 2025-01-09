package by.clevertec.controller;

import by.clevertec.dto.request.RequestLoginUserDto;
import by.clevertec.dto.request.RequestRegisterUserDto;
import by.clevertec.dto.response.ResponseRegisterUserDto;
import by.clevertec.models.User;
import by.clevertec.services.AuthenticationService;
import by.clevertec.services.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
@Slf4j
public class AuthenticationController {
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    @Operation(summary = "Register new user")
    @PostMapping("/signup")
    public ResponseEntity<?> register(@Validated @RequestBody RequestRegisterUserDto registerUserDto,
                                                        BindingResult bindingResult) {

        if(bindingResult.hasErrors()){
            for (ObjectError error : bindingResult.getAllErrors()) {
                log.error("Validation errors occurred while processing user registration: - " + error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        ResponseRegisterUserDto registeredUser = authenticationService.signup(registerUserDto);
        return ResponseEntity.ok(registeredUser);
    }

    @Operation(summary = "Authenticate user by name and password")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody RequestLoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }
}
