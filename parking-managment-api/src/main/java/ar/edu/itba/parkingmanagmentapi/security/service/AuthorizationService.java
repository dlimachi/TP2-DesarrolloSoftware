package ar.edu.itba.parkingmanagmentapi.security.service;

import ar.edu.itba.parkingmanagmentapi.repository.AdminRepository;
import ar.edu.itba.parkingmanagmentapi.repository.ScheduledReservationRepository;
import ar.edu.itba.parkingmanagmentapi.repository.UserRepository;
import ar.edu.itba.parkingmanagmentapi.repository.WalkInStayRepository;
import ar.edu.itba.parkingmanagmentapi.service.ManagerService;
import ar.edu.itba.parkingmanagmentapi.service.ParkingLotService;
import ar.edu.itba.parkingmanagmentapi.service.SpotService;
import ar.edu.itba.parkingmanagmentapi.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final SecurityService securityService;
    private final ParkingLotService parkingLotService;
    private final SpotService spotService;

    private final AdminRepository adminRepository;

    private final ManagerService managerService;

    private final UserRepository userRepository;

    private final VehicleService vehicleService;

    private final ScheduledReservationRepository scheduledReservationRepository;

    private final WalkInStayRepository walkInStayRepository;

    public boolean isCurrentUserAdmin() {
        return securityService.getCurrentUser()
                .flatMap(user -> adminRepository.findByUserId(user.id()))
                .isPresent();
    }

    public boolean isCurrentUserManager() {
        return securityService.getCurrentUser()
                .map(user -> managerService.isUserManager(user.id()))
                .orElse(false);
    }

    private boolean isCurrentUserRole() {
        return securityService.getCurrentUser()
                .map(user -> userRepository.existsById(user.id()))
                .orElse(false);
    }

    public boolean isCurrentUser(Long userId) {
        if (Objects.isNull(userId)) {
            return false;
        }
        return securityService.getCurrentUser()
                .map(user -> userId.equals(user.id()))
                .orElse(false);
    }


    public boolean isCurrentUserManagerOfParkingLot(Long parkingLotId) {
        return securityService.getCurrentUser()
                .flatMap(user -> parkingLotService.getManagerOfParkingLot(parkingLotId)
                        .map(manager -> Objects.equals(manager.id(), user.id())))
                .orElse(false);
    }

    public boolean isCurrentUserManagerOfSpot(Long spotId) {
        return securityService.getCurrentUser()
                .flatMap(user -> spotService.getManagerOfSpot(spotId)
                        .map(managerUser -> managerUser.getId().equals(user.id())))
                .orElse(false);
    }

    public boolean isCurrentUserOwnerOfVehicle(String licensePlate) {
        return securityService.getCurrentUser()
                .map(user -> vehicleService.isUserOwnerOfVehicle(user.id(), licensePlate))
                .orElse(false);
    }

    public boolean canCurrentUserUpdateScheduledReservation(Long reservationId) {
        return securityService.getCurrentUser()
                .map(currentUser -> {
                    boolean isOwner = scheduledReservationRepository.findOwnerByReservationId(reservationId)
                            .map(owner -> owner.getId().equals(currentUser.id()))
                            .orElse(false);
                    boolean isManager = scheduledReservationRepository.findManagerByReservationId(reservationId)
                            .map(manager -> manager.getId().equals(currentUser.id()))
                            .orElse(false);
                    return isOwner || isManager;
                })
                .orElse(false);
    }

    public boolean canCurrentUserUpdateWalkInStayReservation(Long reservationId) {
        return securityService.getCurrentUser()
                .map(currentUser -> {
                    boolean isOwner = walkInStayRepository.findOwnerByReservationId(reservationId)
                            .map(owner -> owner.getId().equals(currentUser.id()))
                            .orElse(false);
                    boolean isManager = walkInStayRepository.findManagerByReservationId(reservationId)
                            .map(manager -> manager.getId().equals(currentUser.id()))
                            .orElse(false);
                    return isOwner || isManager;
                })
                .orElse(false);
    }

    public boolean isCurrentUserManagerOfReservation(Long reservationId) {
        return securityService.getCurrentUser()
                .map(currentUser -> walkInStayRepository.findManagerByReservationId(reservationId)
                        .map(manager -> manager.getId().equals(currentUser.id()))
                        .orElse(false))
                .orElse(false);
    }
}