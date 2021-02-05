package com.parkit.parkingsystem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
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
  @Mock
  private static FareCalculatorService fareCalculatorService;

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
      when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to set up test mock InputReaderUtil");
    }
  }

  @Test
  public void processExitingVehicleTest() {
    // ARRANGE
    when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
    parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

    final ArgumentCaptor<Ticket> ticketCaptor = ArgumentCaptor.forClass(Ticket.class);
    final ArgumentCaptor<ParkingSpot> parkingSpotCaptor =
        ArgumentCaptor.forClass(ParkingSpot.class);

    // ACT
    parkingService.processExitingVehicle();

    // ASSERT
    verify(fareCalculatorService, times(1)).calculateFare(any(Ticket.class));
    verify(parkingSpotDAO, times(1)).updateParking(parkingSpotCaptor.capture());
    verify(ticketDAO, times(1)).updateTicket(ticketCaptor.capture());

    assertThat(parkingSpotCaptor.getValue().isAvailable()).isTrue();
    assertThat(ticketCaptor.getValue().getOutTime()).isNotNull();

  }

  // erreur de base de donnée : impossible a updater le ticket
  @Test
  void processExittingVehicle_WhenUnableToUpdateTicket() {
    // ARRANGE
    when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);
    parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

    // ACT
    parkingService.processExitingVehicle();

    // ASSERT
    verify(parkingSpotDAO, times(0)).updateParking(any(ParkingSpot.class));
  }
}
