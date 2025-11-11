package ar.edu.itba.parkingmanagmentapi.service;

import ar.edu.itba.parkingmanagmentapi.domain.UserDomain;
import ar.edu.itba.parkingmanagmentapi.dto.LoginResponse;
import ar.edu.itba.parkingmanagmentapi.dto.RefreshTokenResponse;
import ar.edu.itba.parkingmanagmentapi.dto.RegisterResponse;

public interface AuthService {

    /**
     * Authenticates a user and generates a JWT token
     */
    LoginResponse login(String email, String password);

    RegisterResponse register(UserDomain userDomain, String password);

    /**
     * Refreshes a JWT token
     * @param refreshToken the refresh token
     * @return RefreshTokenResponse with the new token
     */
    RefreshTokenResponse refresh(String refreshToken);

    /**
     * Logs out a session by revoking the provided refresh token.
     * @param refreshToken the refresh token to revoke
     */
    void logout(String refreshToken);

}
