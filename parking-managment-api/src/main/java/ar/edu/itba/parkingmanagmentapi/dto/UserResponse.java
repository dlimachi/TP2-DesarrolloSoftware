package ar.edu.itba.parkingmanagmentapi.dto;

import ar.edu.itba.parkingmanagmentapi.domain.UserDomain;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private Long id;

    private String email;

    private String firstName;

    private String lastName;

    private String imageUrl;

    private UserDetailDTO userDetail;
}
