package ar.edu.itba.parkingmanagmentapi.domain;

import ar.edu.itba.parkingmanagmentapi.domain.enums.UserType;
import ar.edu.itba.parkingmanagmentapi.model.User;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserDomain {
    private String firstName;
    private String lastName;
    private String email;

    private UserType type;

    public User fromUserDomain(UserDomain userDomain, String passwordHash) {
        return new User(userDomain.email, userDomain.firstName, userDomain.lastName, passwordHash);
    }
}
