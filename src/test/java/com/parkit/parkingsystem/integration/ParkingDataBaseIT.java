package com.parkit.parkingsystem.integration;

import static org.assertj.db.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.parkit.parkingsystem.dao.ParkingSpotDao;
import com.parkit.parkingsystem.dao.TicketDao;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.assertj.db.type.Changes;
import org.assertj.db.type.Source;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * class of integration tests to check the use of Database.
 * 
 * @author delaval
 *
 */
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ParkingDataBaseIT {

  private static DataBaseTestConfig dataBaseTestConfig;
  private static DataBasePrepareService dataBasePrepareService;
  private static ParkingService parkingService;
  private static ParkingSpotDao parkingSpotDAO;
  private static TicketDao ticketDAO;

  private static Source source;

  @Mock
  private static InputReaderUtil inputReaderUtil;


  @BeforeAll
  private static void setUp() throws Exception {
    dataBaseTestConfig = new DataBaseTestConfig();
    dataBasePrepareService = new DataBasePrepareService();

    parkingSpotDAO = new ParkingSpotDao();
    parkingSpotDAO.setDataBaseConfig(dataBaseTestConfig);
    ticketDAO = new TicketDao();
    ticketDAO.setDataBaseConfig(dataBaseTestConfig);

    source = new Source("jdbc:mysql://localhost:3306/test", "root", "Jsadmin4all");
  }

  @BeforeEach
  private void setUpPerTest() throws Exception {
    dataBasePrepareService.clearDataBaseEntries();
    when(inputReaderUtil.readSelection()).thenReturn(1);
    when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
  }

  @AfterAll
  private static void tearDown() {
   
  }

  /**
   * Test to check if a ticket is correctly save in database when a car is parked.
   * 
   * @throws Exception when {@link InputReaderUtil} not be able to read the vehicle numbers
   */
  @Order(1)
  @Test
  void testParkingACar() throws Exception {
    // GIVEN

    Changes changesWhenParkingCar = new Changes(source);
    changesWhenParkingCar.setStartPointNow();

    //WHEN
    parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    parkingService.processIncomingVehicle();
    changesWhenParkingCar.setEndPointNow();

    // THEN:
    // check that a ticket is actually saved in DB comparing its values before and after

    assertThat(changesWhenParkingCar).changeOfCreationOnTable("ticket")

        .rowAtStartPoint().doesNotExist()

        .rowAtEndPoint()
        .value("ID").isNotNull()
        .value("PARKING_NUMBER").isNumber()
        .value("PRICE").isEqualTo(0)
        .value("IN_TIME").isNotNull()
        .value("OUT_TIME").isNull();

    // check that Parking table is updated with availability
    assertThat(changesWhenParkingCar).changeOfModificationOnTable("parking")

        .rowAtStartPoint()
        .value("AVAILABLE").isTrue()

        .rowAtEndPoint()
        .value("AVAILABLE").isFalse();

  }

  /**
   * test to check if the far and out time are correctly generated and save in database.
   * 
   * @throws Exception when {@link InputReaderUtil} not be able to read the vehicle number
   */
  @Order(2)
  @Test
  void testParkingLotExit() throws Exception {
    // GIVEN:change testParkingACar() by parkingService.processIncomingVehicle to not depends of the
    // first IT and respect "FIRST"

    Changes changesWhenExitedCar = new Changes(source);

    parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    parkingService.processIncomingVehicle();
    changesWhenExitedCar.setStartPointNow();

    // WHEN
    // Be careful : we have to wait a minimum of 1 second before run processExitingVehicule() else
    // the outTime can be earlier then the inTime of exiting vehicle

    try {
      Thread.sleep(1000);
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
    }

    parkingService.processExitingVehicle();

    changesWhenExitedCar.setEndPointNow();

    // THEN
    // check that the fare generated correctly comparing the values before and after knowing that
    // FareCalculator.calculateFar() was tested in FareCalculatorTest

    assertThat(changesWhenExitedCar).changeOfModificationOnTable("ticket")

        .rowAtStartPoint().exists()
        .value("PRICE").isEqualTo(0)
        .value("OUT_TIME").isNull()

        .rowAtEndPoint()
        .value("PRICE").isGreaterThanOrEqualTo(0)
        .value("OUT_TIME").isDateTime().isNotNull();

    // check out time are populated correctly in the database comparing values after and before

    assertThat(changesWhenExitedCar).changeOfModificationOnTable("parking")

        .rowAtStartPoint().value("AVAILABLE").isFalse()

        .rowAtEndPoint().value("AVAILABLE").isTrue();
  }
}
