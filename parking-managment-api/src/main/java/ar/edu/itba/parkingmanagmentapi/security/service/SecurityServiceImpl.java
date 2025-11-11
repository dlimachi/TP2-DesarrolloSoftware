package ar.edu.itba.parkingmanagmentapi.security.service;

import ar.edu.itba.parkingmanagmentapi.domain.UserDomain;
import ar.edu.itba.parkingmanagmentapi.domain.repositories.UserDomainRepositoryImpl;
import ar.edu.itba.parkingmanagmentapi.exceptions.AuthenticationFailedException;
import ar.edu.itba.parkingmanagmentapi.model.Manager;
import ar.edu.itba.parkingmanagmentapi.model.User;
import ar.edu.itba.parkingmanagmentapi.repository.ManagerRepository;
import ar.edu.itba.parkingmanagmentapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    private final UserDomainRepositoryImpl userRepository;

    @Override
    public Optional<String> getCurrentUserEmail() {
        final SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext != null && securityContext.getAuthentication() != null) {
            return Optional.of(securityContext.getAuthentication().getName());
        }
        return Optional.empty();
    }


    @Cacheable
    @Override
    public Optional<UserDomain> getCurrentUser() {
        final Optional<String> mayBeEmail = getCurrentUserEmail();

        if (mayBeEmail.isEmpty()) {
            throw new AuthenticationFailedException("User is not authorized to access this resource.");
        }

        return userRepository.findUserByEmail(mayBeEmail.get());
    }

    @Override
    @Cacheable
    public Optional<UserDomain> getCurrentManager() {
        Optional<UserDomain> currentUserOpt = getCurrentUser();
        UserDomain currentUser = currentUserOpt
                .orElseThrow(() -> new AuthenticationFailedException("No authenticated user found"));

        return userRepository.findManagerByUser(currentUser);
    }

}
