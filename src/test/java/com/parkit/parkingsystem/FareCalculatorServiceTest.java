package com.parkit.parkingsystem;

// import static junit
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import java.util.Date;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

  @Test
  public void calculateFareCar() {
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
    assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR);
  }

  @Test
  public void calculateFareBike() {
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
    assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
  }

  @Test
  public void calculateFareUnkownType() {
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
  public void calculateFareBikeWithFutureInTime() {
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
    assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
  }

  @Test
  public void calculateFareBikeWithLessThanOneHourParkingTime() {
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
    assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
  }

  @Test
  public void calculateFareCarWithLessThanOneHourParkingTime() {
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
    assertEquals((0.75 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
  }

  @Test
  public void calculateFareCarWithMoreThanADayParkingTime() {
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
    assertEquals((24 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
  }
}