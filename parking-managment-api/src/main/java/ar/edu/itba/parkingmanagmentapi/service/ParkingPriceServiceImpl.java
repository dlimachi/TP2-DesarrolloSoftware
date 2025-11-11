package ar.edu.itba.parkingmanagmentapi.service;

import ar.edu.itba.parkingmanagmentapi.config.AppConstants;
import ar.edu.itba.parkingmanagmentapi.domain.DateTimeRange;
import ar.edu.itba.parkingmanagmentapi.dto.ParkingPriceRequest;
import ar.edu.itba.parkingmanagmentapi.dto.ParkingPriceResponse;
import ar.edu.itba.parkingmanagmentapi.dto.enums.VehicleType;
import ar.edu.itba.parkingmanagmentapi.exceptions.BadRequestException;
import ar.edu.itba.parkingmanagmentapi.exceptions.NotFoundException;
import ar.edu.itba.parkingmanagmentapi.model.ParkingLot;
import ar.edu.itba.parkingmanagmentapi.model.ParkingPrice;
import ar.edu.itba.parkingmanagmentapi.repository.ParkingPriceRepository;
import ar.edu.itba.parkingmanagmentapi.repository.ParkingPriceSpecifications;
import ar.edu.itba.parkingmanagmentapi.validators.ParkingPriceRequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParkingPriceServiceImpl implements ParkingPriceService {

    private final ParkingPriceRepository parkingPriceRepository;
    private final ParkingLotService parkingLotService;

    private final ParkingPriceRequestValidator parkingPriceRequestValidator;

    @Override
    public ParkingPriceResponse create(Long parkingLotId, ParkingPriceRequest request) {
        parkingPriceRequestValidator.validate(request);
        ParkingLot parkingLot = parkingLotService.findEntityById(parkingLotId);

        validateNoOverlap(parkingLot, parkingLotId, request);

        ParkingPrice parkingPrice = new ParkingPrice();
        parkingPrice.setVehicleType(VehicleType.fromName(request.getVehicleType()));
        parkingPrice.setPrice(request.getPrice());
        parkingPrice.setValidFrom(request.getValidFrom());
        parkingPrice.setValidTo(request.getValidTo());
        parkingPrice.setParkingLot(parkingLot);

        ParkingPrice saved = parkingPriceRepository.save(parkingPrice);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public ParkingPriceResponse update(Long parkingLotId, Long id, ParkingPriceRequest request) {
        parkingPriceRequestValidator.validate(request);
        ParkingLot parkingLot = parkingLotService.findEntityById(parkingLotId);

        ParkingPrice parkingPrice = parkingPriceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ParkingPrice not found"));

        validateNoOverlap(parkingLot, parkingLotId, request);

        parkingPrice.setVehicleType(VehicleType.fromName(request.getVehicleType()));
        parkingPrice.setPrice(request.getPrice());
        parkingPrice.setValidFrom(request.getValidFrom());
        parkingPrice.setValidTo(request.getValidTo());

        ParkingPrice updated = parkingPriceRepository.save(parkingPrice);
        return toResponse(updated);
    }

    @Override
    public BigDecimal calculateEstimatedPrice(Long parkingLotId, VehicleType vehicleType, DateTimeRange range) {
        ParkingPrice price = findActivePriceBySpotIdAndVehicleType(parkingLotId, vehicleType);

        long hours = Duration.between(range.getStart(), range.getEnd()).toHours();
        if (hours == 0) hours = AppConstants.MINIMUM_BILLING_HOURS;

        return price.getPrice().multiply(BigDecimal.valueOf(hours));
    }

    public ParkingPrice findActivePriceBySpotIdAndVehicleType(Long parkingLotId, VehicleType vehicleType) {
        List<ParkingPrice> prices = parkingPriceRepository.findByParkingLotIdAndVehicleType(
                parkingLotId,
                vehicleType
        );

        if (prices.isEmpty()) {
            throw new NotFoundException("No active price found for parking lot " + parkingLotId +
                    " and vehicle type " + vehicleType);
        }

        return prices.get(0);
    }

    private void validateNoOverlap(ParkingLot parkingLot, Long excludeId, ParkingPriceRequest request) {
        VehicleType vehicleType = VehicleType.fromName(request.getVehicleType());
        List<ParkingPrice> existingPrices =
                parkingPriceRepository.findByParkingLotAndVehicleType(parkingLot, vehicleType);

        boolean overlaps = existingPrices.stream()
                .filter(p -> !p.getId().equals(excludeId))
                .anyMatch(existing -> {
                    LocalDateTime newFrom = request.getValidFrom();
                    LocalDateTime newTo = request.getValidTo();

                    LocalDateTime existingFrom = existing.getValidFrom();
                    LocalDateTime existingTo = existing.getValidTo();

                    return (newTo == null || existingFrom.isBefore(newTo)) &&
                            (existingTo == null || newFrom.isBefore(existingTo));
                });

        if (overlaps) {
            throw new BadRequestException("There is already an overlapping price for this type of vehicle.");
        }
    }

    @Override
    public ParkingPriceResponse getById(Long parkingLotId, Long id) {
        ParkingPrice parkingPrice = parkingPriceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ParkingPrice not found"));
        return toResponse(parkingPrice);
    }

    @Override
    public List<ParkingPriceResponse> getByParkingLot(Long parkingLotId) {
        return parkingPriceRepository.findByParkingLotId(parkingLotId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public void delete(Long parkingLotId, Long id) {
        // WARNING: unused variable?
        ParkingLot parkingLot = parkingLotService.findEntityById(parkingLotId);

        ParkingPrice price = parkingPriceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ParkingPrice not found with id " + id));

        parkingPriceRepository.delete(price);
    }

    @Override
    public List<ParkingPriceResponse> getByFilters(Long parkingLotId, BigDecimal min, BigDecimal max,
                                                   VehicleType vehicleType, LocalDateTime from, LocalDateTime to, String sort) {
        Specification<ParkingPrice> spec = ParkingPriceSpecifications.withFilters(parkingLotId, min, max, vehicleType, from, to);

        Sort sortSpec = "desc".equalsIgnoreCase(sort)
                ? Sort.by(Sort.Direction.DESC, "price")
                : Sort.by(Sort.Direction.ASC, "price");

        return parkingPriceRepository.findAll(spec, sortSpec)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public BigDecimal calculateEstimatedPrice(Long parkingLotId, DateTimeRange range) {
        return BigDecimal.ZERO;
    }

    @Override
    public boolean existsActiveByParkingLotIdAndVehicleType(Long parkingLotId, VehicleType vehicleType) {
        return !parkingPriceRepository.findByParkingLotIdAndVehicleType(parkingLotId, vehicleType).isEmpty();
    }

    private ParkingPriceResponse toResponse(ParkingPrice entity) {
        return new ParkingPriceResponse(entity.getId(), entity.getVehicleType().getName(), entity.getPrice(), entity.getValidFrom(), entity.getValidTo());
    }
}
