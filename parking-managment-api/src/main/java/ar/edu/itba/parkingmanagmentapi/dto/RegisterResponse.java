package ar.edu.itba.parkingmanagmentapi.dto;

import ar.edu.itba.parkingmanagmentapi.domain.UserDomain;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterResponse {
    private String email;

    public RegisterResponse fromUserDomain(UserDomain userDomain) {
        return new RegisterResponse(userDomain.email());
    }
} 