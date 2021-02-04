package com.parkit.parkingsystem.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDao;
import com.parkit.parkingsystem.dao.TicketDao;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * class of tests to check the use of {@link ParkingService}.
 * 
 * @author delaval
 *
 */
@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {
  private static ParkingService parkingService;

  @Mock
  private static InputReaderUtil inputReaderUtil;
  @Mock
  private static ParkingSpotDao parkingSpotDAO;
  @Mock
  private static TicketDao ticketDAO;

  @BeforeEach
  private void setUpPerTest() {
    try {
      when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

      ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
      Ticket ticket = new Ticket();
      ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
      ticket.setParkingSpot(parkingSpot);
      ticket.setVehicleRegNumber("ABCDEF");
      when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
      when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

      when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

      parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to set up test mock objects");
    }
  }

  @Test
  public void processExitingVehicleTest() {
    parkingService.processExitingVehicle();
    verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
  }
}
