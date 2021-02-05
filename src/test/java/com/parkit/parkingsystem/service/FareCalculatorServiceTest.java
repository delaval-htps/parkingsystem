package com.parkit.parkingsystem.service;

import static org.assertj.core.api.Assertions.assertThat;
// import static junit
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import java.util.Date;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * class Test with all unit tests for the class {@link FareCalculatorService}.
 * 
 * @author delaval
 *
 */
public class FareCalculatorServiceTest {
  private static FareCalculatorService fareCalculatorService;
  private Ticket ticket;

  @BeforeAll
  private static void setUp() {
    fareCalculatorService = new FareCalculatorService();
  }

  @BeforeEach
  private void setUpPerTest() {
    ticket = new Ticket();
  }

  /**
   * class test to check the calculation of far to any type of vehicle.
   * 
   * @author delaval
   *
   */
  @Nested
  @Tag("CalculateFar")
  @DisplayName("CalculateFar for any type of  vehicle")
  class CalculateFar {

    @Test
    void calculateFareCar() {
      // ARRANGE
      Date inTime = new Date();
      inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
      Date outTime = new Date();
      ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

      // ACT
      ticket.setInTime(inTime);
      ticket.setOutTime(outTime);
      ticket.setParkingSpot(parkingSpot);
      fareCalculatorService.calculateFare(ticket);

      // ASSERT
      assertEquals(Fare.CAR_RATE_PER_HOUR, ticket.getPrice());
    }

    @Test
    void calculateFareBike() {
      // ARRANGE
      Date inTime = new Date();
      inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
      Date outTime = new Date();
      ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

      // ACT
      ticket.setInTime(inTime);
      ticket.setOutTime(outTime);
      ticket.setParkingSpot(parkingSpot);
      fareCalculatorService.calculateFare(ticket);

      // ASSERT
      assertEquals(Fare.BIKE_RATE_PER_HOUR, ticket.getPrice());
    }


    @Test
    void calculateFareUnkownType() {
      // ARRANGE
      Date inTime = new Date();
      inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
      Date outTime = new Date();
      ParkingSpot parkingSpot = new ParkingSpot(1, null, false);

      // ACT
      ticket.setInTime(inTime);
      ticket.setOutTime(outTime);
      ticket.setParkingSpot(parkingSpot);

      // ASSERT
      assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }


    @Test
    void calculateFareBikeWithFutureInTime() {
      // ARRANGE
      Date inTime = new Date();
      inTime.setTime(System.currentTimeMillis() + (60 * 60 * 1000));
      Date outTime = new Date();
      ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

      // ACT
      ticket.setInTime(inTime);
      ticket.setOutTime(outTime);
      ticket.setParkingSpot(parkingSpot);

      // ACT
      assertThrows(IllegalArgumentException.class,
          () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    void calculateFareBikeWithLessThanOneHourParkingTime() {
      // ARRANGE
      Date inTime = new Date();
      // 45 minutes parking time should give 3/4th
      inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));
      // parking fare
      Date outTime = new Date();
      ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

      // ACT
      ticket.setInTime(inTime);
      ticket.setOutTime(outTime);
      ticket.setParkingSpot(parkingSpot);
      fareCalculatorService.calculateFare(ticket);

      // ASSERT
      assertEquals(ticket.getPrice(),
          (double) Math.round(0.75 * Fare.BIKE_RATE_PER_HOUR * 100) / 100);
    }

    @Test
    void calculateFareCarWithLessThanOneHourParkingTime() {
      // ARRANGE
      Date inTime = new Date();
      // 45 minutes parking time should give 3/4th
      inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));
      // parking fare
      Date outTime = new Date();
      ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

      // ACT
      ticket.setInTime(inTime);
      ticket.setOutTime(outTime);
      ticket.setParkingSpot(parkingSpot);
      fareCalculatorService.calculateFare(ticket);

      // ASSERT
      assertEquals(ticket.getPrice(),
          (double) Math.round(0.75 * Fare.CAR_RATE_PER_HOUR * 100) / 100);
    }

    @Test
    void calculateFareCarWithMoreThanADayParkingTime() {
      // ARRANGE
      Date inTime = new Date();
      // 24 hours parking time should give 24 *
      inTime.setTime(System.currentTimeMillis() - (24 * 60 * 60 * 1000));
      // parking fare per hour
      Date outTime = new Date();
      ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

      // ACT
      ticket.setInTime(inTime);
      ticket.setOutTime(outTime);
      ticket.setParkingSpot(parkingSpot);
      fareCalculatorService.calculateFare(ticket);

      // ASSERT
      assertEquals(ticket.getPrice(), (24 * Fare.CAR_RATE_PER_HOUR));
    }
  }

  /**
   * Test class to verify if any type of vehicle with a parking time under 30 minutes has a park for
   * free.<br>
   * <strong>Test cases:</strong>
   * <ul>
   * <li>parking time [0,29, 30] minutes</li>
   * <li>type of vehicle: [Bike,Car,Other]</li>
   * </ul>
   * <strong>Test case set =</strong> {[0,Bike], [29,Car],[30,Bike],[30,Car],[29,Other],[30,Other]}
   * 
   * @author delaval
   *
   */
  @Nested
  @Tag("CalculateFarUnder30Min")
  @DisplayName("Calculate Far for parking time under 30min")


  class CalculateFarUnder30Min {

    @Test
    void calculateFareCarWithTwentyNineMinutesParkingTime() {
      // ARRANGE
      Date inTime = new Date();
      inTime.setTime(System.currentTimeMillis() - (29 * 60 * 1000));
      Date outTime = new Date();
      ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
      
      // ACT
      ticket.setInTime(inTime);
      ticket.setOutTime(outTime);
      ticket.setParkingSpot(parkingSpot);
      fareCalculatorService.calculateFare(ticket);

      // ASSERT
      assertEquals(0, ticket.getPrice());
    }

    @Test
    void calculateFareBikeWithZeroMinuteParkingTime() {
      // ARRANGE
      Date inTime = new Date();
      inTime.setTime(System.currentTimeMillis());
      Date outTime = new Date();
      ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
      
      // ACT
      ticket.setInTime(inTime);
      ticket.setOutTime(outTime);
      ticket.setParkingSpot(parkingSpot);
      fareCalculatorService.calculateFare(ticket);

      // ASSERT
      assertEquals(0, ticket.getPrice());
    }

    @Test
    void calculateFareCarWithThirtyMinutesParkingTime() {
      // ARRANGE
      Date inTime = new Date();

      // thirtyOneMinutesDouble is equal to 31 minutes in hour in type double
      inTime.setTime(System.currentTimeMillis() - (30 * 60 * 1000));
      final double thirtyMinutesDouble = 30f / 60;

      Date outTime = new Date();
      ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

      // ACT
      ticket.setInTime(inTime);
      ticket.setOutTime(outTime);
      ticket.setParkingSpot(parkingSpot);
      fareCalculatorService.calculateFare(ticket);

      // ASSERT
      assertEquals(ticket.getPrice(), thirtyMinutesDouble * Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    void calculateFareBikeWithThirtyMinutesParkingTime() {
      // ARRANGE
      Date inTime = new Date();

      // thirtyOneMinutesDouble is equal to 31 minutes in hour in type double
      inTime.setTime(System.currentTimeMillis() - (30 * 60 * 1000));
      final double thirtyMinutesDouble = 30f / 60;

      Date outTime = new Date();
      ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

      // ACT
      ticket.setInTime(inTime);
      ticket.setOutTime(outTime);
      ticket.setParkingSpot(parkingSpot);
      fareCalculatorService.calculateFare(ticket);

      // ASSERT
      assertEquals(ticket.getPrice(), thirtyMinutesDouble * Fare.BIKE_RATE_PER_HOUR);
    }

    @Test
    void calculateFareUnkownTypeWithTwentyNineMinutesParkingTime() {
      // ARRANGE
      Date inTime = new Date();
      inTime.setTime(System.currentTimeMillis() - (29 * 60 * 1000));
      Date outTime = new Date();
      ParkingSpot parkingSpot = new ParkingSpot(1, null, false);

      // ACT
      ticket.setInTime(inTime);
      ticket.setOutTime(outTime);
      ticket.setParkingSpot(parkingSpot);

      // ASSERT
      assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    void calculateFareUnkownTypeWithThirtyMinutesParkingTime() {
      // ARRANGE
      Date inTime = new Date();
      inTime.setTime(System.currentTimeMillis() - (30 * 60 * 1000));
      Date outTime = new Date();
      ParkingSpot parkingSpot = new ParkingSpot(1, null, false);

      // ACT
      ticket.setInTime(inTime);
      ticket.setOutTime(outTime);
      ticket.setParkingSpot(parkingSpot);

      // ASSERT
      assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }
  }

  /**
   * Test class to verify that a recurring user has a discount of 5% to his parking price.<br>
   * <strong>Test cases:</strong>
   * <ul>
   * <li>parking time [FirstTime, RecurringUser]</li>
   * <li>type of vehicle: [Bike,Car,Other]</li>
   * </ul>
   * <strong>Test case set =</strong> {[RecurringUser,Bike],
   * [RecurringUser,Car],[FirsTime,Bike],[FirsTime,Car],[FirstTime,Other],[Recurring,Other]}
   * 
   * @author delaval
   *
   */
  @Nested
  @Tag("CalculateFarWithDiscount")
  @DisplayName("calculate far for recurring users")
  class CalculateFarWithFivePourcentsDiscount {
    @Test
    void calculateFarRecurringCarOneHourParkingTime() {
      //ARRANGE
      Date inTime = new Date();
      inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
      Date outTime = new Date();

      ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);


      //ACT
      ticket.setInTime(inTime);
      ticket.setOutTime(outTime);
      ticket.setParkingSpot(parkingSpot);
      ticket.setIsRecurringUser(true);
      fareCalculatorService.calculateFare(ticket);

      //ASSERT
      assertThat(ticket.getPrice()).isEqualTo(0.95 * Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    void calculateFarRecurringBikeOneHourParkingTime() {
      // ARRANGE
      Date inTime = new Date();
      inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
      Date outTime = new Date();

      ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);


      // ACT
      ticket.setInTime(inTime);
      ticket.setOutTime(outTime);
      ticket.setParkingSpot(parkingSpot);
      ticket.setIsRecurringUser(true);
      fareCalculatorService.calculateFare(ticket);

      // ASSERT
      assertThat(ticket.getPrice()).isEqualTo(0.95 * Fare.BIKE_RATE_PER_HOUR);
    }

    @Test
    void calculateFarCarFirstPArkingTime() {
      // ARRANGE
      Date inTime = new Date();
      inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
      Date outTime = new Date();

      ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);


      // ACT
      ticket.setInTime(inTime);
      ticket.setOutTime(outTime);
      ticket.setParkingSpot(parkingSpot);
      ticket.setIsRecurringUser(false);
      fareCalculatorService.calculateFare(ticket);

      // ASSERT
      assertThat(ticket.getPrice()).isEqualTo(Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    void calculateFarBikeFirstPArkingTime() {
      // ARRANGE
      Date inTime = new Date();
      inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
      Date outTime = new Date();

      ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);


      // ACT
      ticket.setInTime(inTime);
      ticket.setOutTime(outTime);
      ticket.setParkingSpot(parkingSpot);
      ticket.setIsRecurringUser(false);
      fareCalculatorService.calculateFare(ticket);


      // ASSERT
      assertThat(ticket.getPrice()).isEqualTo(Fare.BIKE_RATE_PER_HOUR);
    }

    @Test
    void calculateFarUnknowTypeUserFirstPArkingTime() {
      // ARRANGE
      Date inTime = new Date();
      inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
      Date outTime = new Date();

      ParkingSpot parkingSpot = new ParkingSpot(1, null, false);


      // ACT
      ticket.setInTime(inTime);
      ticket.setOutTime(outTime);
      ticket.setParkingSpot(parkingSpot);
      ticket.setIsRecurringUser(false);

      // ASSERT
      assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    void calculateFarUnknowTypeUserRecurring() {
      // ARRANGE
      Date inTime = new Date();
      inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
      Date outTime = new Date();

      ParkingSpot parkingSpot = new ParkingSpot(1, null, false);


      // ACT
      ticket.setInTime(inTime);
      ticket.setOutTime(outTime);
      ticket.setParkingSpot(parkingSpot);
      ticket.setIsRecurringUser(true);

      // ASSERT
      assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }
  }
}
