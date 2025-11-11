package ar.edu.itba.parkingmanagmentapi.service;

import ar.edu.itba.parkingmanagmentapi.config.AppConstants;
import ar.edu.itba.parkingmanagmentapi.domain.DateTimeRange;
import ar.edu.itba.parkingmanagmentapi.domain.ParkingLotDomain;
import ar.edu.itba.parkingmanagmentapi.domain.ParkingPriceDomain;
import ar.edu.itba.parkingmanagmentapi.domain.repositories.DomainParkingPriceRepository;
import ar.edu.itba.parkingmanagmentapi.dto.enums.VehicleType;
import ar.edu.itba.parkingmanagmentapi.exceptions.BadRequestException;
import ar.edu.itba.parkingmanagmentapi.exceptions.NotFoundException;
import ar.edu.itba.parkingmanagmentapi.util.ParkingPriceFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParkingPriceServiceImpl implements ParkingPriceService {

    private final DomainParkingPriceRepository domainParkingPriceRepository;
    private final ParkingLotService parkingLotService;
    // private final ParkingPriceRequestValidator validator;

    @Override
    public ParkingPriceDomain create(Long parkingLotId, ParkingPriceDomain domain) {
        ParkingLotDomain parkingLot = parkingLotService.findById(parkingLotId);

        validateNoOverlap(parkingLot, null, domain);

        domain.setParkingLot(parkingLot);

        return domainParkingPriceRepository.save(domain);
    }

    @Override
    @Transactional
    public ParkingPriceDomain update(Long parkingLotId, Long id, ParkingPriceDomain domain) {
        ParkingLotDomain parkingLot = parkingLotService.findById(parkingLotId);

        validateNoOverlap(parkingLot, id, domain);

        domain.setId(id);
        domain.setParkingLot(parkingLot);

        return domainParkingPriceRepository.update(domain);
    }

    // TODO! REMOVE arg parkingLotId
    @Override
    public void delete(Long parkingLotId, Long id) { domainParkingPriceRepository.deleteById(id); }
    // TODO! REMOVE arg parkingLotId
    @Override
    public ParkingPriceDomain getById(Long parkingLotId, Long id) {
        return domainParkingPriceRepository.findById(id);
    }

    @Override
    public List<ParkingPriceDomain> getByParkingLot(Long parkingLotId) {
        return domainParkingPriceRepository.findByParkingLotIdAndVehicleType(parkingLotId, null);
    }

    @Override
    public BigDecimal calculateEstimatedPrice(Long parkingLotId, DateTimeRange range) {
        return calculateEstimatedPrice(parkingLotId, null, range); // return BigDecimal.ZERO
    }

    @Override
    public BigDecimal calculateEstimatedPrice(Long parkingLotId, VehicleType vehicleType, DateTimeRange range) {
        ParkingPriceDomain price = findActivePriceBySpotIdAndVehicleType(parkingLotId, vehicleType);

        long hours = Duration.between(range.getStart(), range.getEnd()).toHours();
        if (hours == 0)
            hours = AppConstants.MINIMUM_BILLING_HOURS;

        return price.getPrice().multiply(BigDecimal.valueOf(hours));
    }

    @Override
    public ParkingPriceDomain findActivePriceBySpotIdAndVehicleType(Long parkingLotId, VehicleType vehicleType) {
        List<ParkingPriceDomain> prices = domainParkingPriceRepository.findByParkingLotIdAndVehicleType(parkingLotId, vehicleType);
        if (prices.isEmpty()) {
            throw new NotFoundException("No active price found for parking lot " + parkingLotId + " and vehicle type " + vehicleType);
        }
        return prices.get(0);
    }

    @Override
    public boolean existsActiveByParkingLotIdAndVehicleType(Long parkingLotId, VehicleType vehicleType) {
        return domainParkingPriceRepository.existsActivePrice(parkingLotId, vehicleType);
    }

    @Override
    public List<ParkingPriceDomain> getByFilters(Long parkingLotId, ParkingPriceFilter filter) {
        return domainParkingPriceRepository.findByFilters(parkingLotId, filter);
    }

    private void validateNoOverlap(ParkingLotDomain parkingLot, Long excludeId, ParkingPriceDomain newDomain) {
        List<ParkingPriceDomain> existingPrices = domainParkingPriceRepository.findByParkingLotIdAndVehicleType(parkingLot.getId(), newDomain.getVehicleType());

        boolean overlaps = existingPrices.stream()
                .filter(p -> !p.getId().equals(excludeId))
                .anyMatch(existing -> {
                    LocalDateTime newFrom = newDomain.getValidFrom();
                    LocalDateTime newTo = newDomain.getValidTo();
                    LocalDateTime existingFrom = existing.getValidFrom();
                    LocalDateTime existingTo = existing.getValidTo();

                    return (newTo == null || existingFrom.isBefore(newTo)) && (existingTo == null || newFrom.isBefore(existingTo));
                });

        if (overlaps) {
            throw new BadRequestException("There is already an overlapping price for this type of vehicle.");
        }
    }
}
