package by.clevertec.services;

import by.clevertec.dto.request.RequestLoginUserDto;
import by.clevertec.dto.request.RequestRegisterUserDto;
import by.clevertec.dto.response.ResponseRegisterUserDto;
import by.clevertec.models.User;

public interface AuthenticationService {
    /**
     * Registers a new user in the system.
     *
     * @param input the registration details of the user
     * @return the registered user
     */
    ResponseRegisterUserDto signup(RequestRegisterUserDto input);

    /**
     * Authenticates a user based on their username and password.
     *
     * @param input the login details of the user
     * @return the authenticated user
     */
    User authenticate(RequestLoginUserDto input);
}
