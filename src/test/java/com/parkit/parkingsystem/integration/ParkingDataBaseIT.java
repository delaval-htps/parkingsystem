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
import org.junit.jupiter.api.Test;
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
public class ParkingDataBaseIT {
  private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
  private static ParkingSpotDao parkingSpotDAO;
  private static TicketDao ticketDAO;
  private static DataBasePrepareService dataBasePrepareService;

  @Mock
  private static InputReaderUtil inputReaderUtil;


  @BeforeAll
  private static void setUp() throws Exception {
    parkingSpotDAO = new ParkingSpotDao();
    parkingSpotDAO.setDataBaseConfig(dataBaseTestConfig);
    ticketDAO = new TicketDao();
    ticketDAO.setDataBaseConfig(dataBaseTestConfig);
    dataBasePrepareService = new DataBasePrepareService();
  }

  @BeforeEach
  private void setUpPerTest() throws Exception {
    when(inputReaderUtil.readSelection()).thenReturn(1);
    when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

    dataBasePrepareService.clearDataBaseEntries();

  }

  @AfterAll
  private static void tearDown() {
   
  }

  /**
   * Test to check if a ticket is correctly save in database when a car is parked.
   */
  @Test
  public void testParkingACar() {
    // GIVEN
    
    Source source = new Source("jdbc:mysql://localhost:3306/test", "root", "Jsadmin4all");
    Changes changesWhenParkingCar = new Changes(source);
    changesWhenParkingCar.setStartPointNow();
    
    //WHEN
    ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
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
   */
  @Test
  public void testParkingLotExit() {
    // GIVEN:change testParkingACar() by parkingService.processIncomingVehicle to not depends of the
    // first IT and respect "FIRST"

    ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

    parkingService.processIncomingVehicle();

    Source source = new Source("jdbc:mysql://localhost:3306/test", "root", "Jsadmin4all");
    Changes changesWhenExitedCar = new Changes(source);

    changesWhenExitedCar.setStartPointNow();


    // WHEN

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
        .value("OUT_TIME").isDateTime();

    // check out time are populated correctly in the database comparing values after and before

    assertThat(changesWhenExitedCar).changeOfModificationOnTable("parking")

        .rowAtStartPoint().value("AVAILABLE").isFalse()

        .rowAtEndPoint().value("AVAILABLE").isTrue();


  }
}
