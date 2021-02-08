package com.parkit.parkingsystem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDao;
import com.parkit.parkingsystem.dao.TicketDao;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;
import java.util.Date;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
  private static ParkingSpot parkingSpot;
  private static Ticket ticket;
  private LogCaptor logCaptor;

  private static String expectedErrorMessage =
      "Unable to update ticket information. Error occurred";
  private static String expectedInfoMessage =
      "Welcome back! As a recurring user of our parking lot, you'll benefit from a 5% discount!";

  @Mock
  private static InputReaderUtil inputReaderUtil;
  @Mock
  private static ParkingSpotDao parkingSpotDAO;
  @Mock
  private static TicketDao ticketDAO;


  @Nested
  @DisplayName("tests for process exiting vehicle")
  public class ProcessExitingVehicleTest {

    @BeforeEach
    public void setUpTest() {
      try {
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
      } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("Failed to set up test mock InputReaderUntil");
      }

      ticket = new Ticket();
      ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
      ticket.setVehicleRegNumber("ABCDEF");

      when(ticketDAO.getTicket(anyString())).thenReturn(ticket);

    }

    @Test
    public void processExitingCarTest() {
      // ARRANGE
      parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
      ticket.setParkingSpot(parkingSpot);

      when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
      when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

      final ArgumentCaptor<Ticket> ticketCaptor = ArgumentCaptor.forClass(Ticket.class);
      final ArgumentCaptor<ParkingSpot> parkingSpotCaptor =
          ArgumentCaptor.forClass(ParkingSpot.class);

      // ACT
      parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
      parkingService.processExitingVehicle();

      // ASSERT
      verify(parkingSpotDAO, times(1)).updateParking(parkingSpotCaptor.capture());
      verify(ticketDAO, times(1)).updateTicket(ticketCaptor.capture());

      assertThat(parkingSpotCaptor.getValue().isAvailable()).isTrue();
      assertThat(ticketCaptor.getValue().getOutTime()).isNotNull();
      assertThat(ticketCaptor.getValue().getPrice()).isEqualTo(Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    public void processExitingBikeTest() {
      // ARRANGE
      parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
      ticket.setParkingSpot(parkingSpot);
      when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
      when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

      final ArgumentCaptor<Ticket> ticketCaptor = ArgumentCaptor.forClass(Ticket.class);
      final ArgumentCaptor<ParkingSpot> parkingSpotCaptor =
          ArgumentCaptor.forClass(ParkingSpot.class);

      // ACT
      parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
      parkingService.processExitingVehicle();

      // ASSERT

      verify(parkingSpotDAO, times(1)).updateParking(parkingSpotCaptor.capture());
      verify(ticketDAO, times(1)).updateTicket(ticketCaptor.capture());

      assertThat(parkingSpotCaptor.getValue().isAvailable()).isTrue();
      assertThat(ticketCaptor.getValue().getOutTime()).isNotNull();
      assertThat(ticketCaptor.getValue().getPrice()).isEqualTo(Fare.BIKE_RATE_PER_HOUR);

    }

    @Test
    public void processExitingVehicle_WhenUnupdatedTicket() {
      // ARRANGE
      parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
      ticket.setParkingSpot(parkingSpot);

      when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);

      logCaptor = LogCaptor.forClass(ParkingService.class);
      logCaptor.getErrorLogs();

      // ACT
      parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
      parkingService.processExitingVehicle();

      // ASSERT
      verify(parkingSpotDAO, never()).updateParking(any(ParkingSpot.class));
      verify(ticketDAO, times(1)).updateTicket(any(Ticket.class));

      assertThat(logCaptor.getErrorLogs()).containsExactly(expectedErrorMessage);
    }

    @Test
    public void processExitingCar_WhenRecurringUserTest() {
      // ARRANGE
      parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
      ticket.setParkingSpot(parkingSpot);
      ticket.setIsRecurringUser(true);

      when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
      when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

      logCaptor = LogCaptor.forClass(ParkingService.class);
      logCaptor.getErrorLogs();

      final ArgumentCaptor<Ticket> ticketCaptor = ArgumentCaptor.forClass(Ticket.class);
      final ArgumentCaptor<ParkingSpot> parkingSpotCaptor =
          ArgumentCaptor.forClass(ParkingSpot.class);

      // ACT
      parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
      parkingService.processExitingVehicle();

      // ASSERT
      verify(ticketDAO, times(1)).updateTicket(ticketCaptor.capture());
      verify(parkingSpotDAO, times(1)).updateParking(parkingSpotCaptor.capture());

      assertThat(parkingSpotCaptor.getValue().isAvailable()).isTrue();
      assertThat(ticketCaptor.getValue().getOutTime()).isNotNull();
      assertThat(ticketCaptor.getValue().getPrice())
          .isEqualTo(Fare.roundedFare(0.95 * Fare.CAR_RATE_PER_HOUR));
      assertThat(logCaptor.getInfoLogs()).containsAnyOf(expectedInfoMessage);
    }

    @Test
    public void processExitingBike_WhenRecurringUserTest() {
      // ARRANGE
      parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
      ticket.setParkingSpot(parkingSpot);
      ticket.setIsRecurringUser(true);

      when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
      when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

      logCaptor = LogCaptor.forClass(ParkingService.class);
      logCaptor.getErrorLogs();

      final ArgumentCaptor<Ticket> ticketCaptor = ArgumentCaptor.forClass(Ticket.class);
      final ArgumentCaptor<ParkingSpot> parkingSpotCaptor =
          ArgumentCaptor.forClass(ParkingSpot.class);

      // ACT
      parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
      parkingService.processExitingVehicle();

      // ASSERT
      verify(ticketDAO, times(1)).updateTicket(ticketCaptor.capture());
      verify(parkingSpotDAO, times(1)).updateParking(parkingSpotCaptor.capture());

      assertThat(parkingSpotCaptor.getValue().isAvailable()).isTrue();
      assertThat(ticketCaptor.getValue().getOutTime()).isNotNull();
      assertThat(ticketCaptor.getValue().getPrice())
          .isEqualTo(Fare.roundedFare(0.95 * Fare.BIKE_RATE_PER_HOUR));
      assertThat(logCaptor.getInfoLogs()).containsAnyOf(expectedInfoMessage);
    }
  }

}
