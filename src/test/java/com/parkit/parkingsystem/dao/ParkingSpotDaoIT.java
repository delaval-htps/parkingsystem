package com.parkit.parkingsystem.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;


class ParkingSpotDaoIT {
  private static ParkingSpotDao parkingSpotDao;
  private static DataBaseConfig dataBaseConfig;
  private static DataBasePrepareService dataBasePrepareService;
  private static ParkingSpot parkingSpot;
  private int result;

  
  @BeforeAll
  static void setUpBeforeClass() throws Exception {
    parkingSpotDao = new ParkingSpotDao();
    dataBaseConfig = new DataBaseTestConfig();
    dataBasePrepareService = new DataBasePrepareService();

  }

  @BeforeEach
  void setUp() throws Exception {
    dataBasePrepareService.clearDataBaseEntries();
  }

  @AfterAll
  static void restoreDbTest() {
    dataBasePrepareService.clearDataBaseEntries();
  }

  @Nested
  class GetNextAvailableSlotTest {
    @Test
    void getNextAvailableSlot_WhenNoDatabase() {
      // ARRANGE
      dataBaseConfig = null;
      parkingSpotDao.setDataBaseConfig(dataBaseConfig);

      // ACT...

      // ASSERT
      assertThrows(NullPointerException.class,
          () -> parkingSpotDao.getNextAvailableSlot(ParkingType.CAR),
          "Error fetching next available slot");

    }

    @Test
    void getNextAvailableSlotCar_WhenSlotAvailable() {
      // ARRANGE
      dataBaseConfig = new DataBaseTestConfig();
      parkingSpotDao.setDataBaseConfig(dataBaseConfig);

      // ACT...
      result = parkingSpotDao.getNextAvailableSlot(ParkingType.CAR);

      // ASSERT
      assertThat(result).isGreaterThan(0);
    }

    @Test
    void getNextAvailableSlotBike_WhenSlotAvailable() {
      // ARRANGE
      dataBaseConfig = new DataBaseTestConfig();
      parkingSpotDao.setDataBaseConfig(dataBaseConfig);

      // ACT...
      result = parkingSpotDao.getNextAvailableSlot(ParkingType.BIKE);

      // ASSERT
      assertThat(result).isGreaterThan(0);
    }

    @Test
    void getNextAvailableSlot_WhenSlotUnavailable() {
      // ARRANGE
      dataBaseConfig = new DataBaseTestConfig();
      dataBasePrepareService.setAllParkingSpotNoAvailable();
      parkingSpotDao.setDataBaseConfig(dataBaseConfig);


      // ACT...
      result = parkingSpotDao.getNextAvailableSlot(ParkingType.CAR);

      // ASSERT
      assertThat(result).isEqualTo(0);
    }
  }

  @Nested
  class UpdateParkingTest {
    @Test
    void updateParking_WhenNoDatabase() {
      // ARRANGE
      dataBaseConfig = null;
      parkingSpotDao.setDataBaseConfig(dataBaseConfig);
      parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

      // ACT...

      // ASSERT
      assertThrows(NullPointerException.class, () -> parkingSpotDao.updateParking(parkingSpot),
          "Error updating parking info");

    }

    @Test
    void updateParking_WhenParkingNumberExist() {

      DataBaseConfig dataBaseConfig = new DataBaseTestConfig();
      dataBasePrepareService.setParkingSpotNoAvailable(1);
      parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);


      // ACT...
      parkingSpotDao.setDataBaseConfig(dataBaseConfig);
      boolean result = parkingSpotDao.updateParking(parkingSpot);
      // ASSERT
      assertThat(result).isTrue();
    }


    @Test
    void updateParking_WhenParkingNumberNotExisted() {

      DataBaseConfig dataBaseConfig = new DataBaseTestConfig();
      dataBasePrepareService.setParkingSpotNoAvailable(1);
      parkingSpot = new ParkingSpot(6, ParkingType.CAR, false);


      // ACT...
      parkingSpotDao.setDataBaseConfig(dataBaseConfig);
      boolean result = parkingSpotDao.updateParking(parkingSpot);
      // ASSERT
      assertThat(result).isFalse();
    }

  }
}

