package ar.edu.itba.parkingmanagmentapi.service.orchestrator;

import ar.edu.itba.parkingmanagmentapi.service.ScheduledReservationService;
import ar.edu.itba.parkingmanagmentapi.service.SpotService;
import ar.edu.itba.parkingmanagmentapi.service.WalkInStayService;

public class ReservationOrchestratorService {

    private final ScheduledReservationService scheduledReservationService;
    private final WalkInStayService walkInStayService;
    private final SpotService spotService;

    public ReservationOrchestratorService(ScheduledReservationService scheduledReservationService, WalkInStayService walkInStayService, SpotService spotService) {
        this.scheduledReservationService = scheduledReservationService;
        this.walkInStayService = walkInStayService;
        this.spotService = spotService;
    }




}
