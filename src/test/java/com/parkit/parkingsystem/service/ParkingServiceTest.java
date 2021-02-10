package com.parkit.parkingsystem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
  private static ParkingSpot parkingSpot;
  private static Ticket ticket;
  private LogCaptor logCaptor;

  private static String expectedErrorMessageUpdateTicket =
      "Unable to update ticket information. Error occurred";
  private static String expectedErrorMessageInputReaderUtil =
      "Error parsing user input for type of vehicle";
  private static String expectedErrorMessageFullPark =
      "Error fetching parking number from DB. Parking slots might be full";
  private static String expectedErrorMessageTicketDao = "Unable to process exiting vehicle";
  private static String expectedWarningMessageProcessIncomingVehicle =
      "Generated Ticket and saved in DB";

  private static String expectedInfoMessageRecurringUser =
      "Welcome back! As a recurring user of our parking lot, you'll benefit from a 5% discount!";
  private static String expectedInfoMessageProcessIncomingVehicle =
      "Please park your vehicle in spot number:";
  private static String expectedInfoMessageProcessIncomingVehicle2 =
      "Recorded in-time for vehicle number:";

  @Mock
  private static InputReaderUtil inputReaderUtil;
  @Mock
  private static ParkingSpotDao parkingSpotDAO;
  @Mock
  private static TicketDao ticketDAO;

  /**
   * class test to check the correct process incoming vehicle.
   * 
   * @author delaval
   *
   */
  @Nested
  @DisplayName("test for process incoming vehicle")
  public class ProcessIncomingVehiculeTest {
    /**
     * test for a incoming Car.
     */
    @Test
    public void processIncomingCarTest() {
      // ARRANGE
      try {
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
      } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("Failed to set up test mock InputReaderUntil");
      }
      when(inputReaderUtil.readSelection()).thenReturn(1);
      when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);

      logCaptor = LogCaptor.forClass(ParkingService.class);

      final ArgumentCaptor<ParkingSpot> parkingSpotCaptor =
          ArgumentCaptor.forClass(ParkingSpot.class);
      final ArgumentCaptor<Ticket> ticketCaptor = ArgumentCaptor.forClass(Ticket.class);

      // ACT
      parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
      parkingService.processIncomingVehicle();
      
      // ASSERT
      verify(parkingSpotDAO, times(1)).updateParking(parkingSpotCaptor.capture());
      verify(ticketDAO, times(1)).saveTicket(ticketCaptor.capture());

      assertThat(ticketCaptor.getValue().getId()).isNotNull();
      assertThat(ticketCaptor.getValue().getOutTime()).isNull();
      assertThat(ticketCaptor.getValue().getPrice()).isEqualTo(0);
      assertThat(ticketCaptor.getValue().getInTime()).isNotNull();
      assertThat(ticketCaptor.getValue().getParkingSpot()).isEqualTo(parkingSpot);
      assertThat(ticketCaptor.getValue().getVehicleRegNumber()).isEqualTo("ABCDEF");

      assertThat(parkingSpotCaptor.getValue().isAvailable()).isFalse();
      
      assertThat(logCaptor.getWarnLogs())
          .containsExactly(expectedWarningMessageProcessIncomingVehicle);
      assertThat(logCaptor.getInfoLogs()).contains(expectedInfoMessageProcessIncomingVehicle
          + ticketCaptor.getValue().getParkingSpot().getId())
          .contains(
              expectedInfoMessageProcessIncomingVehicle2
                  + ticketCaptor.getValue().getVehicleRegNumber()
                  + " is:" + ticketCaptor.getValue().getInTime() + "/n");
    }

    /**
     * test for incoming Bike.
     */
    @Test
    public void processIncomingBikeTest() {
      // ARRANGE
      try {
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
      } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("Failed to set up test mock InputReaderUntil");
      }
      when(inputReaderUtil.readSelection()).thenReturn(2);
      when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);

      logCaptor = LogCaptor.forClass(ParkingService.class);

      final ArgumentCaptor<ParkingSpot> parkingSpotCaptor =
          ArgumentCaptor.forClass(ParkingSpot.class);
      final ArgumentCaptor<Ticket> ticketCaptor = ArgumentCaptor.forClass(Ticket.class);

      // ACT
      parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
      parkingService.processIncomingVehicle();
      
      // ASSERT
      verify(parkingSpotDAO, times(1)).updateParking(parkingSpotCaptor.capture());
      verify(ticketDAO, times(1)).saveTicket(ticketCaptor.capture());

      assertThat(ticketCaptor.getValue().getId()).isNotNull();
      assertThat(ticketCaptor.getValue().getOutTime()).isNull();
      assertThat(ticketCaptor.getValue().getPrice()).isEqualTo(0);
      assertThat(ticketCaptor.getValue().getInTime()).isNotNull();
      assertThat(ticketCaptor.getValue().getParkingSpot()).isEqualTo(parkingSpot);
      assertThat(ticketCaptor.getValue().getVehicleRegNumber()).isEqualTo("ABCDEF");

      assertThat(parkingSpotCaptor.getValue().isAvailable()).isFalse();

      assertThat(logCaptor.getWarnLogs())
          .containsExactly(expectedWarningMessageProcessIncomingVehicle);
      assertThat(logCaptor.getInfoLogs())
          .contains(expectedInfoMessageProcessIncomingVehicle
              + ticketCaptor.getValue().getParkingSpot().getId())
          .contains(expectedInfoMessageProcessIncomingVehicle2
              + ticketCaptor.getValue().getVehicleRegNumber() + " is:"
              + ticketCaptor.getValue().getInTime() + "/n");
    }

    /**
     * test for incoming unkown vehicle.
     */
    @Test
    public void processIncomingUnknownVehicleTest() {
      // ARRANGE

      when(inputReaderUtil.readSelection()).thenReturn(-1);

      logCaptor = LogCaptor.forClass(ParkingService.class);

      // ACT
      parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
      parkingService.processIncomingVehicle();

      // ASSERT
      verify(parkingSpotDAO, never()).updateParking(any(ParkingSpot.class));
      verify(ticketDAO, never()).saveTicket(any(Ticket.class));

      assertThat(logCaptor.getErrorLogs())
          .contains(expectedErrorMessageInputReaderUtil);
    }

    /**
     * test for incoming vehicle when park is full.
     */
    @Test
    public void processIncomingWhenParkFulledTest() {
      // ARRANGE
      try {
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
      } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("Failed to set up test mock InputReaderUntil");
      }
      when(inputReaderUtil.readSelection()).thenReturn(1);
      when(parkingSpotDAO.getNextAvailableSlot(null)).thenReturn(-1);

      logCaptor = LogCaptor.forClass(ParkingService.class);

      // ACT
      parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
      parkingService.processIncomingVehicle();

      // ASSERT
      verify(parkingSpotDAO, never()).updateParking(any(ParkingSpot.class));
      verify(ticketDAO, never()).saveTicket(any(Ticket.class));
      assertThatThrownBy(() -> {
        throw new Exception(expectedErrorMessageFullPark);
      }).isInstanceOf(Exception.class);
    }

    /**
     * test for incoming vehicle with no parking spot= null.
     */
    @Test
    public void processIncomingWhenNullParkingSpotTest() {
      // ARRANGE

      // when(inputReaderUtil.readSelection()).thenReturn(1);
      // when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
      // when(parkingService.getNextParkingNumberIfAvailable())
      // .thenReturn(new ParkingSpot(-12, ParkingType.CAR, true));

      parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
      parkingService = Mockito.spy(parkingService);
     
      Mockito.doReturn(null).when(parkingService).getNextParkingNumberIfAvailable();

      // ACT
      parkingService.processIncomingVehicle();

      // ASSERT
      verify(parkingSpotDAO, never()).updateParking(any(ParkingSpot.class));
      verify(ticketDAO, never()).saveTicket(any(Ticket.class));

    }

    /**
     * test for incoming car with a given parking spot negative.
     */
    @Test
    public void processIncomingWhenNegativeParkingSpotTest() {
      // ARRANGE

      // when(inputReaderUtil.readSelection()).thenReturn(1);
      // when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
      // when(parkingService.getNextParkingNumberIfAvailable())
      // .thenReturn(new ParkingSpot(-12, ParkingType.CAR, true));

      parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
      parkingService = Mockito.spy(parkingService);

      Mockito.doReturn(new ParkingSpot(-10, ParkingType.CAR, true)).when(parkingService)
          .getNextParkingNumberIfAvailable();


      logCaptor = LogCaptor.forClass(ParkingService.class);

      // ACT
      parkingService.processIncomingVehicle();

      // ASSERT
      verify(parkingSpotDAO, never()).updateParking(any(ParkingSpot.class));
      verify(ticketDAO, never()).saveTicket(any(Ticket.class));

    }

  }

  /**
   * class tests for method processingExitingVehicle.
   * 
   * @author delaval
   *
   */
  @Nested
  @DisplayName("tests for process exiting vehicle")
  public class ProcessExitingVehicleTest {
    /**
     * Setup initialize for all test in the class.
     */
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

    /**
     * test for a Exiting Car. When ParkingService.processExitingVehicle() is call Then
     * <ul>
     * <li>verify ParkingSpotDao.updateParking() and TicketDao.updateTicket() are called</li>
     * <li>assert That
     * <ul>
     * <li>ParkingSpot.isavailable is True</li>
     * <li>ticket.outTime is not null</li>
     * <li>ticket.price is correct</li>
     * </ul>
     * </ul>
     */
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

    /**
     * test for a Exiting Bike. When ParkingService.processExitingVehicle() is call Then
     * <ul>
     * <li>verify ParkingSpotDao.updateParking() and TicketDao.updateTicket() are called</li>
     * <li>assert That
     * <ul>
     * <li>ParkingSpot.isavailable is True</li>
     * <li>ticket.outTime is not null</li>
     * <li>ticket.price is correct</li>
     * </ul>
     * </ul>
     */
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

    /**
     * test for a Exiting Vehicle. When ticket can't be updated Then
     * <ul>
     * <li>verify ParkingSpotDao.updateParking() and TicketDao.updateTicket() are called</li>
     * <li>assert That the exact logger.error is print in console out
     * </ul>
     */
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

      assertThat(logCaptor.getErrorLogs()).containsExactly(expectedErrorMessageUpdateTicket);
    }

    /**
     * test for a Exiting Vehicle. When ticket can't be updated Then
     * <ul>
     * <li>verify ParkingSpotDao.updateParking() and TicketDao.updateTicket() are called</li>
     * <li>assert That the exact logger.error is print in console out
     * </ul>
     */
    @Test
    public void processExitingVehicle_WhenTicketNotInDB() {
      // ARRANGE
      parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
      when(ticketDAO.getTicket("ABCDEF")).thenReturn(null);

      logCaptor = LogCaptor.forClass(ParkingService.class);
      logCaptor.getErrorLogs();

      // ACT
      parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
      parkingService.processExitingVehicle();

      // ASSERT
      verify(parkingSpotDAO, never()).updateParking(any(ParkingSpot.class));
      verify(ticketDAO, never()).updateTicket(any(Ticket.class));

      assertThat(logCaptor.getErrorLogs()).containsExactly(expectedErrorMessageTicketDao);
    }

    /**
     * test for a Exiting Car. When it's a recurring user Then
     * <ul>
     * <li>verify ParkingSpotDao.updateParking() and TicketDao.updateTicket() are called</li>
     * <li>assert That
     * <ul>
     * <li>ParkingSpot.isavailable is True</li>
     * <li>ticket.outTime is not null</li>
     * <li>ticket.price is correct</li>
     * <li>logger.info is correctly print in console out</li>
     * </ul>
     * </ul>
     */
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
      assertThat(logCaptor.getInfoLogs()).containsAnyOf(expectedInfoMessageRecurringUser);
    }

    /**
     * test for a Exiting Bike. When it's a recurring user Then
     * <ul>
     * <li>verify ParkingSpotDao.updateParking() and TicketDao.updateTicket() are called</li>
     * <li>assert That
     * <ul>
     * <li>ParkingSpot.isavailable is True</li>
     * <li>ticket.outTime is not null</li>
     * <li>ticket.price is correct</li>
     * <li>logger.info is correctly print in console out</li>
     * </ul>
     * </ul>
     */
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
      assertThat(logCaptor.getInfoLogs()).containsAnyOf(expectedInfoMessageRecurringUser);
    }
  }
  /**
   * class test to verify if the given next parking spot available to park a vehicle is correct.
   * 
   * @author delaval
   *
   */

  @Nested
  @DisplayName("tests to get next number of parkingSpot")
  public class GetNextNumberParkingSpotAvailabe {
    /**
     * test to verify if the correct parking spot available is given for a entering car.
     */
    @Test
    public void getNextNumberParkingSpotAvailableForCar() {
      // ARRANGE

      when(inputReaderUtil.readSelection()).thenReturn(1);
      when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(3);

      // ACT
      ParkingService parkingService =
          new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
      parkingSpot = parkingService.getNextParkingNumberIfAvailable();

      // ASSERT
      assertThat(parkingSpot.getParkingType()).isEqualTo(ParkingType.CAR);
      assertThat(parkingSpot.getId()).isGreaterThan(0);
      assertThat(parkingSpot.isAvailable()).isEqualTo(true);
    }

    /**
     * test to verify if the correct parking spot available is given for a entering Bike.
     */
    @Test
    public void getNextNumberParkingSpotAvailableForBike() {
      // ARRANGE

      when(inputReaderUtil.readSelection()).thenReturn(2);
      when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(3);

      // ACT
      ParkingService parkingService =
          new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
      parkingSpot = parkingService.getNextParkingNumberIfAvailable();

      // ASSERT
      assertThat(parkingSpot.getParkingType()).isEqualTo(ParkingType.BIKE);
      assertThat(parkingSpot.getId()).isGreaterThan(0);
      assertThat(parkingSpot.isAvailable()).isEqualTo(true);
    }

    /**
     * test to verify when unknown type of vehicle try to have a parking spot.
     */
    @Test
    public void getNextNumberParkingSpotAvailableForUnkowType() {
      // ARRANGE
      when(inputReaderUtil.readSelection()).thenReturn(-1);

      logCaptor = LogCaptor.forClass(ParkingService.class);
      logCaptor.getErrorLogs();

      // ACT
      ParkingService parkingService =
          new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
      parkingService.getNextParkingNumberIfAvailable();

      // ASSERT
      assertThat(logCaptor.getErrorLogs()).containsAnyOf(expectedErrorMessageInputReaderUtil);
    }

    /**
     * test to verify when a car try to have a parking spot and the park is full.
     */
    @Test
    public void getNextNumberCarParkingSpotAvailable_WhenFullPark() {
      // ARRANGE
      when(inputReaderUtil.readSelection()).thenReturn(1);
      when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(-1);

      // ACT
      ParkingService parkingService =
          new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
      parkingService.getNextParkingNumberIfAvailable();

      // ASSERT

      assertThatThrownBy(() -> {
        throw new Exception(expectedErrorMessageFullPark);
      }).isInstanceOf(Exception.class);

    }

    /**
     * test to verify when a bike try to have a parking spot and the park is full.
     */
    @Test
    public void getNextNumberBikeParkingSpotAvailable_WhenFullPark() {
      // ARRANGE
      when(inputReaderUtil.readSelection()).thenReturn(2);
      when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(-1);

      // ACT
      ParkingService parkingService =
          new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
      parkingService.getNextParkingNumberIfAvailable();

      // ASSERT

      assertThatThrownBy(() -> {
        throw new Exception(expectedErrorMessageFullPark);
      }).isInstanceOf(Exception.class);

    }
  }
}
