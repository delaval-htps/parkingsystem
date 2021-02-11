package com.parkit.parkingsystem.constants;

/**
 * class representing the rate per hour for a type if vehicle.
 * 
 * @author delaval
 *
 */
public class Fare {

  private Fare() {
    throw new IllegalStateException("utility class");
  }

  public static final double BIKE_RATE_PER_HOUR = 1.00;
  public static final double CAR_RATE_PER_HOUR = 1.50;

  /**
   * method to round Fare with 2 numbers after comma.
   * 
   * @param price a price of type double
   * @return the price rounded with 2 numbers after the comma
   */
  public static double roundedFare(double price) {
    return (double) Math.round(price * 100) / 100;
  }
}
