package ar.edu.itba.parkingmanagmentapi.service;

import ar.edu.itba.parkingmanagmentapi.domain.UserDomain;
import ar.edu.itba.parkingmanagmentapi.domain.enums.UserType;
import ar.edu.itba.parkingmanagmentapi.dto.*;
import ar.edu.itba.parkingmanagmentapi.exceptions.AlreadyExistsException;
import ar.edu.itba.parkingmanagmentapi.exceptions.NotFoundException;
import ar.edu.itba.parkingmanagmentapi.model.Manager;
import ar.edu.itba.parkingmanagmentapi.model.User;
import ar.edu.itba.parkingmanagmentapi.repository.ManagerRepository;
import ar.edu.itba.parkingmanagmentapi.repository.UserRepository;
import ar.edu.itba.parkingmanagmentapi.security.provider.EmailBasedAuthenticationProvider;
import ar.edu.itba.parkingmanagmentapi.util.JwtUtil;
import ar.edu.itba.parkingmanagmentapi.validators.LoginRequestValidator;
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
    private final UserRepository userRepository;
    private final ManagerRepository managerRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginRequestValidator loginRequestValidator;
    private final RefreshTokenService refreshTokenService;

    /**
     * Authenticates a user and generates a JWT token with all user roles
     */
    public LoginResponse login(LoginRequest loginRequest) {
        logger.info("Intento de login para usuario: {}", loginRequest.getEmail());

        loginRequestValidator.validate(loginRequest);
        Authentication authentication = emailAuthProvider.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found"));
        List<String> roles = userDetails.getAuthorities().stream().map(
                authority -> authority.getAuthority().replace("ROLE_", "")
        ).collect(Collectors.toList());

        // Generate access token and refresh token
        String token = jwtUtil.generateTokenWithRoles(loginRequest.getEmail(), roles);
        var refreshToken = refreshTokenService.createRefreshToken(user, true);

        logger.info("Login exitoso para usuario: {} con roles: {}", loginRequest.getEmail(), roles);

        return LoginResponse.builder()
                .token(token)
                .email(loginRequest.getEmail())
                .refreshToken(refreshToken.getToken())
                .build();
    }

    /**
     * Registers a new user
     */
    public RegisterResponse register(UserDomain userDomain, String password) {
        logger.info("Intento de registro para usuario: {} como {}", userDomain.email(), userDomain.type());

        if (userRepository.existsByEmail(userDomain.email())) {
            logger.warn("Intento de registro con email ya existente: {}", userDomain.email());
            throw new AlreadyExistsException("Email already registered");
        }

        User user = userDomain.toEntity(passwordEncoder.encode(password));
        User savedUser = userRepository.save(user);

        if (userDomain.type() == UserType.MANAGER) {
            saveManager(savedUser);
        }

        logger.info("Usuario registrado exitosamente: {} como manager: {}", userDomain.email(), userDomain.type());
        return new RegisterResponse(savedUser.getEmail());
    }

    public void saveManager(User user) {
        Manager manager = new Manager(user);
        managerRepository.save(manager);
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