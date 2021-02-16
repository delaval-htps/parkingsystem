package com.parkit.parkingsystem.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import com.parkit.parkingsystem.constants.ParkingType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ParkinSpotTest {
  
  private static ParkingSpot parkingSpot;
  private boolean result;
  @BeforeAll
  static void setUpBeforeClass() throws Exception {
    parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
  }

  @BeforeEach
  void setUp() throws Exception {}

  @Test
  void equalWhenSameParkingSpotTest() {
    // ARRANGE...
    // ACT
    result = parkingSpot.equals(parkingSpot);
    // ASSERT
    assertThat(result).isTrue();
  }
  
  @Test
  void equalWhenNotSameParkingSpotTest() {
    // ARRANGE...
    ParkingSpot anotherParkingSpot = new ParkingSpot(2, ParkingType.BIKE, false);
    // ACT
    result = parkingSpot.equals(anotherParkingSpot);
    // ASSERT
    assertThat(result).isFalse();
  }

  @Test
  void equalWhenNullParkingSpotTest() {
    // ARRANGE...
    ParkingSpot anotherParkingSpot = null;
    // ACT
    result = parkingSpot.equals(anotherParkingSpot);
    // ASSERT
    assertThat(result).isFalse();
  }

  @Test
  void equalWhenNotParkingSpotClassTest() {
    // ARRANGE...
    Ticket anotherParkingSpot = new Ticket();
    // ACT
    result = parkingSpot.equals(anotherParkingSpot);
    // ASSERT
    assertThat(result).isFalse();
  }

}
