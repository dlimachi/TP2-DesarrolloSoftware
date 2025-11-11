package ar.edu.itba.parkingmanagmentapi.service;

import ar.edu.itba.parkingmanagmentapi.domain.ParkingLotDomain;
import ar.edu.itba.parkingmanagmentapi.domain.repositories.DomainParkingLotRepository;
import ar.edu.itba.parkingmanagmentapi.exceptions.NotFoundException;
import ar.edu.itba.parkingmanagmentapi.model.Manager;
import ar.edu.itba.parkingmanagmentapi.model.ParkingLot;
import ar.edu.itba.parkingmanagmentapi.model.User;
import ar.edu.itba.parkingmanagmentapi.repository.ParkingLotRepository;
import ar.edu.itba.parkingmanagmentapi.security.service.SecurityService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ParkingLotServiceImpl implements ParkingLotService {

    // TODO: remove
    private final ParkingLotRepository parkingLotRepositoryEntity;

    private final DomainParkingLotRepository parkingLotRepository;
    private final SecurityService securityService;

    @Override
    public ParkingLotDomain createParkingLot(ParkingLotDomain parkingLot) {
        Manager currentManager = securityService.getCurrentManager().get();
        parkingLot.setManager(currentManager);

        ParkingLotDomain entity = parkingLotRepository.save(parkingLot);
        return entity;
    }

    @Override
    public ParkingLotDomain updateParkingLot(ParkingLotDomain request) {
        return parkingLotRepository.update(request);
    }

    @Override
    @Transactional
    public ParkingLotDomain findById(Long id) {
        return parkingLotRepository.findById(id);
    }

    @Override
    public List<ParkingLotDomain> findAll() {
        return parkingLotRepository.findAll();
    }

    @Override
    public void deleteParkingLot(Long id) {
      parkingLotRepository.deleteById(id);
    }

    // TODO: this method is used internally by other services but it uses the entity directly.
    // It should be removed when the other services are adapted to domain.
    @Override
    public ParkingLot findEntityById(Long id) {
        return parkingLotRepositoryEntity.findById(id)
                .orElseThrow(() -> new NotFoundException("ParkingLot not found"));
    }

    @Override
    public Optional<User> getManagerOfParkingLot(Long parkingLotId) {
        try {
          var parkingLot = parkingLotRepository.findById(parkingLotId);
          return Optional.of(parkingLot.getManager().getUser());
        } catch (NotFoundException e) {
          return Optional.empty();
        }
    }

    @Override
    public List<ParkingLotDomain> findByUserId(Long userId) {
        return parkingLotRepository.findByManagerUserId(userId);
    }

}
