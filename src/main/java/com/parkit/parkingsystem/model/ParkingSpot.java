package com.parkit.parkingsystem.model;

import com.parkit.parkingsystem.constants.ParkingType;

/**
 * represent the parking space in the park.
 * 
 * @author delaval
 */
public class ParkingSpot {
  private int number;
  private ParkingType parkingType;
  private boolean isAvailable;

  /**
   * Constructor with parameters.
   * 
   * @param number the number of the place
   * @param parkingType the type of place ie: BIKE or CAR {@link ParkingType}
   * @param isAvailable boolean to represent the availability of parking space
   */
  public ParkingSpot(int number, ParkingType parkingType, boolean isAvailable) {
    this.number = number;
    this.parkingType = parkingType;
    this.isAvailable = isAvailable;
  }

  public int getId() {
    return number;
  }

  public void setId(int number) {
    this.number = number;
  }

  public ParkingType getParkingType() {
    return parkingType;
  }

  public void setParkingType(ParkingType parkingType) {
    this.parkingType = parkingType;
  }

  public boolean isAvailable() {
    return isAvailable;
  }

  public void setAvailable(boolean available) {
    isAvailable = available;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ParkingSpot that = (ParkingSpot) o;
    return number == that.number;
  }

  @Override
  public int hashCode() {
    return number;
  }
}
