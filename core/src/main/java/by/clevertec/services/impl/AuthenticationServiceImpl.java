package by.clevertec.services.impl;

import by.clevertec.dto.request.RequestLoginUserDto;
import by.clevertec.dto.request.RequestRegisterUserDto;
import by.clevertec.dto.response.ResponseRegisterUserDto;
import by.clevertec.enums.Roles;
import by.clevertec.models.User;
import by.clevertec.repositories.UserRepository;
import by.clevertec.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    @Transactional
    public ResponseRegisterUserDto signup(RequestRegisterUserDto input) {
        User user = new User();
        user.setUsername(input.getUsername());
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setRole(Roles.SUBSCRIBER.getRoleName());
        userRepository.save(user);

        ResponseRegisterUserDto registerUserDto = new ResponseRegisterUserDto();
        registerUserDto.setUsername(input.getUsername());

        return registerUserDto;
    }

    public User authenticate(RequestLoginUserDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getUsername(),
                        input.getPassword()
                )
        );
        return userRepository.findByUsername(input.getUsername())
                .orElseThrow();
    }
}
