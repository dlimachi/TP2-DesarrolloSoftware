package ar.edu.itba.parkingmanagmentapi.service;

import ar.edu.itba.parkingmanagmentapi.domain.UserDomain;
import ar.edu.itba.parkingmanagmentapi.domain.repositories.UserDomainRepository;
import ar.edu.itba.parkingmanagmentapi.dto.*;
import ar.edu.itba.parkingmanagmentapi.exceptions.AlreadyExistsException;
import ar.edu.itba.parkingmanagmentapi.exceptions.NotFoundException;
import ar.edu.itba.parkingmanagmentapi.repository.ManagerRepository;
import ar.edu.itba.parkingmanagmentapi.security.provider.EmailBasedAuthenticationProvider;
import ar.edu.itba.parkingmanagmentapi.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final JwtUtil jwtUtil;
    private final EmailBasedAuthenticationProvider emailAuthProvider;
    private final UserDomainRepository userRepository;
    private final ManagerRepository managerRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    /**
     * Authenticates a user and generates a JWT token with all user roles
     */
    public LoginResponse login(String email, String password) {
        logger.info("Intento de login para usuario: {}", email);

        Authentication authentication = emailAuthProvider.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        UserDomain user = userRepository.findUserByEmail(userDetails.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found"));
        List<String> roles = userDetails.getAuthorities().stream().map(
                authority -> authority.getAuthority().replace("ROLE_", "")
        ).collect(Collectors.toList());

        // Generate access token and refresh token
        String token = jwtUtil.generateTokenWithRoles(email, roles);
        var refreshToken = refreshTokenService.createRefreshToken(user.toEntityNoPass(), true);

        logger.info("Login exitoso para usuario: {} con roles: {}", email, roles);

        return LoginResponse.builder()
                .token(token)
                .email(email)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    /**
     * Registers a new user
     */
    public RegisterResponse register(UserDomain userDomain, String password) {
        logger.info("Intento de registro para usuario: {} como {}", userDomain.getEmail(), userDomain.getType());

        if (userRepository.existsUserByEmail(userDomain.getEmail())) {
            logger.warn("Intento de registro con email ya existente: {}", userDomain.getEmail());
            throw new AlreadyExistsException("Email already registered");
        }

        // User user = userDomain.toEntity(passwordEncoder.encode(password));
        userDomain.setPasswordHash(passwordEncoder.encode(password));
        userRepository.saveUser(userDomain);

        logger.info("Usuario registrado exitosamente: {} como manager: {}", userDomain.getEmail(), userDomain.getType());
        return new RegisterResponse(userDomain.getEmail());
    }

    @Override
    public RefreshTokenResponse refresh(String refreshToken) {
        var rotated = refreshTokenService.validateAndRotate(refreshToken);
        var user = rotated.getUser();
        List<String> roles = new ArrayList<>();
        roles.add("USER");
        if (managerRepository.existsByUserId(user.getId())) {
            roles.add("MANAGER");
        }

        String newAccessToken = jwtUtil.generateTokenWithRoles(user.getEmail(), roles);
        return RefreshTokenResponse.builder()
                .token(newAccessToken)
                .email(user.getEmail())
                .refreshToken(rotated.getToken())
                .build();
    }

    @Override
    public void logout(String refreshToken) {
        refreshTokenService.revokeByToken(refreshToken);
    }
}
