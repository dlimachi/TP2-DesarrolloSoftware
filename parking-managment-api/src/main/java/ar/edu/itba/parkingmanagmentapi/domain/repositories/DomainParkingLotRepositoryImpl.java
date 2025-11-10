package ar.edu.itba.parkingmanagmentapi.domain.repositories;

import java.time.LocalDateTime;
import java.util.List;

import ar.edu.itba.parkingmanagmentapi.domain.ParkingLotDomain;
import ar.edu.itba.parkingmanagmentapi.exceptions.BadRequestException;
import ar.edu.itba.parkingmanagmentapi.exceptions.NotFoundException;
import ar.edu.itba.parkingmanagmentapi.model.ParkingLot;
import ar.edu.itba.parkingmanagmentapi.repository.ParkingLotRepository;
import ar.edu.itba.parkingmanagmentapi.repository.ScheduledReservationRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DomainParkingLotRepositoryImpl implements DomainParkingLotRepository {

  private final ParkingLotRepository parkingLotRepository;
  private final ScheduledReservationRepository scheduledReservationRepository;

  private ParkingLot findEntityById(Long id) {
    return parkingLotRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("ParkingLot with id " + id + " not found"));
  }

  @Override
  public void deleteById(Long id) {
    ParkingLot parkingLot = findEntityById(id);

    boolean hasFutureScheduledReservations = scheduledReservationRepository
        .existsBySpotParkingLotIdAndReservedStartTimeAfter(id, LocalDateTime.now());

    boolean hasUnavailableSpots = parkingLot.getSpots().stream().anyMatch(spot -> !spot.getIsAvailable());

    if (hasUnavailableSpots || hasFutureScheduledReservations) {
      throw new BadRequestException("Cannot delete parking lot: it has active or future reservations");
    }
    parkingLotRepository.deleteById(id);
  }

  @Override
  public List<ParkingLotDomain> findAll() {
    var asdf = parkingLotRepository.findAll()
        .stream()
        .map(ParkingLotDomain::fromEntity)
        .toList();
    return asdf;
  }

  @Override
  public ParkingLotDomain findById(Long id) {
    return ParkingLotDomain.fromEntity(findEntityById(id));
  }

  @Override
  public List<ParkingLotDomain> findByManagerUserId(Long id) {
    return parkingLotRepository.findByManagerUserId(id)
        .stream()
        .map(ParkingLotDomain::fromEntity)
        .toList();
  }

  @Override
  public ParkingLotDomain save(ParkingLotDomain parkingLot) {
    return ParkingLotDomain.fromEntity(parkingLotRepository.save(parkingLot.toEntity()));
  }

  @Override
  public ParkingLotDomain update(ParkingLotDomain parkingLot) {
    ParkingLot entity = findEntityById(parkingLot.getId());
    entity.setName(parkingLot.getName());
    entity.setAddress(parkingLot.getAddress());
    entity.setImageUrl(parkingLot.getImageUrl());
    entity.setLatitude(parkingLot.getLatitude());
    entity.setLongitude(parkingLot.getLongitude());

    return ParkingLotDomain.fromEntity(parkingLotRepository.save(entity));
  }

}
