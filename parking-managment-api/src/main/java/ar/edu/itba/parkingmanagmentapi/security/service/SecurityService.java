package ar.edu.itba.parkingmanagmentapi.security.service;

import ar.edu.itba.parkingmanagmentapi.domain.UserDomain;

import java.util.Optional;

public interface SecurityService {
    Optional<UserDomain> getCurrentUser();

    //boolean isCurrentUserAdmin();
    Optional<String> getCurrentUserEmail();

    Optional<UserDomain> getCurrentManager();
}
