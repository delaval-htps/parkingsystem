package com.parkit.parkingsystem.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.db.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import org.assertj.db.type.DateTimeValue;
import org.assertj.db.type.Source;
import org.assertj.db.type.Table;
import org.assertj.db.type.TimeValue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * class integration Test to test TicketDao with the database test.
 *
 * @author delaval
 */
public class TicketDaoIT {
  private static TicketDao ticketDaoUnderTest;
  private static DataBaseConfig dataBaseConfig;
  private static DataBasePrepareService dataBasePrepareService;
  private static Ticket ticket;
  private static ParkingSpot parkingSpot;
  private boolean result;


  @BeforeAll
  static void setUpBeforeClass() throws Exception {
    ticketDaoUnderTest = new TicketDao();
    dataBasePrepareService = new DataBasePrepareService();
    parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
  }


  @BeforeEach
  void setUpTest() {
    dataBasePrepareService.clearDataBaseEntries();
  }

  /**
   * class test for save a ticket in the database.
   *
   * @author delaval
   *
   */
  @Nested
  public class SaveTicketTest {

    @Test
    void saveTicketWhenNoConnectionDatabase() {
      // ARRANGE

      dataBaseConfig = null;
      ticketDaoUnderTest.setDataBaseConfig(dataBaseConfig);

      // ACT...

      // ASSERT
      assertThrows(NullPointerException.class, () -> ticketDaoUnderTest.saveTicket(ticket),
          "Error fetching next available slot");
    }


    @Test
    void saveTicketWhenNullTicket() {
      // ARRANGE
      dataBaseConfig = new DataBaseTestConfig();
      ticket = null;
      ticketDaoUnderTest.setDataBaseConfig(dataBaseConfig);
      // ACT...
      result = ticketDaoUnderTest.saveTicket(ticket);
      // ASSERT
      assertThat(result).isFalse();
    }


    @Test
    void saveTicketWhenOutTimeNull() {
      // ARRANGE
      dataBaseConfig = new DataBaseTestConfig();
      ticketDaoUnderTest.setDataBaseConfig(dataBaseConfig);
      ticket = new Ticket();
      Date inTime = new Date();
      inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
      ticket.setInTime(inTime);
      ticket.setParkingSpot(parkingSpot);
      ticket.setPrice(Fare.CAR_RATE_PER_HOUR);
      ticket.setVehicleRegNumber("ABCDEF");

      Source source = new Source("jdbc:mysql://localhost:3306/test", "root", "Jsadmin4all");
      Table tableTicket = new Table(source, "ticket");

      // ACT
      result = ticketDaoUnderTest.saveTicket(ticket);

      // ASSERT
      // because it's a update and there is no ResultObjet, ps has to be false.
      assertThat(result).isFalse();

      assertThat(tableTicket).column("ID").value().isNumber();
      assertThat(tableTicket).column("PARKING_NUMBER").value().isEqualTo(1);
      assertThat(tableTicket).column("VEHICLE_REG_NUMBER").value().isEqualTo("ABCDEF");
      assertThat(tableTicket).column("PRICE").value().isEqualTo(Fare.CAR_RATE_PER_HOUR);
      assertThat(tableTicket).column("IN_TIME").value().isDateTime()
          .isCloseTo(DateTimeValue.from(new Timestamp(inTime.getTime())), TimeValue.of(0, 0, 1));

      assertThat(tableTicket).column("OUT_TIME").value().isNull();
    }


    @Test
    void saveTicketWhenOutTimeNotNull() {
      // ARRANGE
      dataBaseConfig = new DataBaseTestConfig();
      ticketDaoUnderTest.setDataBaseConfig(dataBaseConfig);
      ticket = new Ticket();
      Date inTime = new Date();
      inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
      Date outTime = new Date();
      ticket.setInTime(inTime);
      ticket.setOutTime(outTime);
      ticket.setParkingSpot(parkingSpot);
      ticket.setPrice(Fare.CAR_RATE_PER_HOUR);
      ticket.setVehicleRegNumber("ABCDEF");

      Source source = new Source("jdbc:mysql://localhost:3306/test", "root", "Jsadmin4all");
      Table tableTicket = new Table(source, "ticket");

      // ACT
      result = ticketDaoUnderTest.saveTicket(ticket);

      // ASSERT
      // because it's a update and there is no ResultObjet, ps has to be false.
      assertThat(result).isFalse();
      assertThat(tableTicket).column("ID").value().isNumber();
      assertThat(tableTicket).column("PARKING_NUMBER").value().isEqualTo(1);
      assertThat(tableTicket).column("VEHICLE_REG_NUMBER").value().isEqualTo("ABCDEF");
      assertThat(tableTicket).column("PRICE").value().isEqualTo(Fare.CAR_RATE_PER_HOUR);
      assertThat(tableTicket).column("IN_TIME").value().isDateTime()
          .isCloseTo(DateTimeValue.from(new Timestamp(inTime.getTime())), TimeValue.of(0, 0, 1));
      assertThat(tableTicket).column("OUT_TIME").value().isDateTime()
          .isCloseTo(DateTimeValue.from(new Timestamp(outTime.getTime())), TimeValue.of(0, 0, 1));

    }
  }

  /**
   * class Test to get a ticket from a database.
   *
   * @author delaval
   *
   */
  @Nested
  class GetTicketTest {

    @Test
    void getTicketWhenNoConnectionDatabase() {
      // ARRANGE

      dataBaseConfig = null;
      ticketDaoUnderTest.setDataBaseConfig(dataBaseConfig);

      // ACT...

      // ASSERT
      assertThrows(NullPointerException.class, () -> ticketDaoUnderTest.getTicket(""),
          "Error fetching next available slot");

    }

    @Test
    void getTicketWheNullResultSet() {
      // ARRANGE
      dataBaseConfig = new DataBaseTestConfig();
      ticketDaoUnderTest.setDataBaseConfig(dataBaseConfig);
      PreparedStatement ps = Mockito.mock(PreparedStatement.class);
      try {
        when(ps.executeQuery()).thenReturn(null);
      } catch (SQLException e) {
        e.printStackTrace();
      }

      // ACT...
      ticket = ticketDaoUnderTest.getTicket("ABCDEF");

      // ASSERT
      assertThat(ticket).isNull();
    }

    @Test
    void getTicketWhenCorrectValuesInDatabase() {
      // ARRANGE
      Date inTime = new Date();
      // outTime has to be null because getTicket is used to be called only for get a ticket before
      // vehicle exits so it's outTime is necessary null( see DbContants)
      dataBasePrepareService.setTicketTest(inTime, null);
      dataBaseConfig = new DataBaseTestConfig();
      ticketDaoUnderTest.setDataBaseConfig(dataBaseConfig);

      // ACT...
      ticket = new Ticket();
      ticket = ticketDaoUnderTest.getTicket("ABCDEF");

      // ASSERT
      assertThat(ticket.getId()).isPositive();
      assertThat(ticket.getParkingSpot().getId()).isEqualTo(1);
      assertThat(ticket.getVehicleRegNumber()).isEqualTo("ABCDEF");
      assertThat(ticket.getPrice()).isEqualTo(Fare.CAR_RATE_PER_HOUR);
      assertThat(ticket.getInTime()).isCloseTo(inTime, 1000);
      assertThat(ticket.getOutTime()).isNull();
    }
  }

  /**
   * class Test to get a ticket from a database.
   *
   * @author delaval
   *
   */
  @Nested
  class UpdateTicketTest {
    @Test
    void updateTicketWhenNoConnectionDatabase() {
      // ARRANGE

      dataBaseConfig = null;
      ticketDaoUnderTest.setDataBaseConfig(dataBaseConfig);

      // ACT...

      // ASSERT
      assertThrows(NullPointerException.class, () -> ticketDaoUnderTest.updateTicket(ticket),
          "Error saving ticket info");
    }

    @Test
    void updateTicketWhenNullTicket() {
      // ARRANGE
      dataBaseConfig = new DataBaseTestConfig();
      ticket = null;
      ticketDaoUnderTest.setDataBaseConfig(dataBaseConfig);
      // ACT...
      result = ticketDaoUnderTest.updateTicket(ticket);
      // ASSERT
      assertThat(result).isFalse();
    }

    @Test
    void updateTicketWhenCorrectValuesInDatabase() {
      // ARRANGE
      Date inTime = new Date();
      inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
      Date outTime = new Date();
      ticket = new Ticket();
      ticket.setPrice(0);
      ticket.setOutTime(outTime);
      ticket.setId(1);

      dataBasePrepareService.setTicketTest(inTime, null);
      dataBaseConfig = new DataBaseTestConfig();
      ticketDaoUnderTest.setDataBaseConfig(dataBaseConfig);

      Source source = new Source("jdbc:mysql://localhost:3306/test", "root", "Jsadmin4all");
      Table tableTicket = new Table(source, "ticket");

      // ACT...
      result = ticketDaoUnderTest.updateTicket(ticket);

      // ASSERT
      assertThat(result).isTrue();
      assertThat(tableTicket).column("PRICE").value().isEqualTo(0);
      assertThat(tableTicket).column("OUT_TIME").value().isDateTime()
          .isCloseTo(DateTimeValue.from(new Timestamp(outTime.getTime())), TimeValue.of(0, 0, 1));

    }
  }

  /**
   * class Test to get a ticket from a database.
   *
   * @author delaval
   */
  @Nested
  class VerifyRecurringUserTicketTest {

    @Test
    void verifyRecurringUsertWhenNoConnectionDatabase() {
      // ARRANGE

      dataBaseConfig = null;
      ticketDaoUnderTest.setDataBaseConfig(dataBaseConfig);

      // ACT...

      // ASSERT
      assertThrows(NullPointerException.class,
          () -> ticketDaoUnderTest.verifyRecurringUserTicket("ABCDEF"),
          "Error to verify if it's a recurring user");

    }


    @Test
    void verifyRecurringUserWhenTrue() {
      // ARRANGE
      Date inTime = new Date();
      inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
      Date outTime = new Date();

      dataBasePrepareService.setTicketTest(inTime, outTime);
      dataBaseConfig = new DataBaseTestConfig();
      ticketDaoUnderTest.setDataBaseConfig(dataBaseConfig);

      // ACT...
      result = ticketDaoUnderTest.verifyRecurringUserTicket("ABCDEF");

      // ASSERT
      assertThat(result).isTrue();

    }

    @Test
    void verifyRecurringUserWhenFalse() {
      // ARRANGE
      Date inTime = new Date();
      inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));

      dataBasePrepareService.setTicketTest(inTime, null);
      dataBaseConfig = new DataBaseTestConfig();
      ticketDaoUnderTest.setDataBaseConfig(dataBaseConfig);

      // ACT...
      result = ticketDaoUnderTest.verifyRecurringUserTicket("ABCDEF");

      // ASSERT
      assertThat(result).isFalse();

    }
  }

}
