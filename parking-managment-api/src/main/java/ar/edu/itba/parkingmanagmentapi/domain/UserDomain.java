package ar.edu.itba.parkingmanagmentapi.domain;

import ar.edu.itba.parkingmanagmentapi.domain.enums.UserType;
import ar.edu.itba.parkingmanagmentapi.model.User;
import lombok.Builder;
import lombok.Value;

public record UserDomain(
        String firstName,
        String lastName,
        String email,
        UserType type
) {

    public User toEntity(String passwordHash) {
        return new User(firstName, lastName, email, passwordHash);
    }
}

