package ar.edu.itba.parkingmanagmentapi.dto;

import ar.edu.itba.parkingmanagmentapi.domain.UserDomain;
import ar.edu.itba.parkingmanagmentapi.domain.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    public UserDomain fromManagerRegisterRequest(RegisterRequest registerRequest, boolean isManager) {
        return UserDomain.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .type(isManager ? UserType.MANAGER : UserType.USER)
                .build();
    }
} 