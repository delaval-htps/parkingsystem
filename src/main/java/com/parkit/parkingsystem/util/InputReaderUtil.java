package com.parkit.parkingsystem.util;

import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * instrument to read data from console.
 * 
 * @author delaval
 *
 */
public class InputReaderUtil {

  private static Scanner scan = new Scanner(System.in);
  private static final Logger logger = LogManager.getLogger("InputReaderUtil");

  /**
   * method use to read the number corresponding to the choice for the menu.
   * 
   * @return the number of the menu's process
   */
  public int readSelection() {
    try {
      int input = Integer.parseInt(scan.nextLine());
      return input;
    } catch (Exception e) {
      logger.error("Error while reading user input from Shell", e);
      System.out.println("Error reading input. Please enter valid number for proceeding further");
      return -1;
    }
  }

  /**
   * method to read the number plate from the console.
   * 
   * @return a String = the number plate of the vehicle
   * @throws Exception a error when reading the number plate and display a error's message on
   *         console
   */
  public String readVehicleRegistrationNumber() throws Exception {
    try {
      String vehicleRegNumber = scan.nextLine();
      if (vehicleRegNumber == null || vehicleRegNumber.trim().length() == 0) {
        throw new IllegalArgumentException("Invalid input provided");
      }
      return vehicleRegNumber;
    } catch (Exception e) {
      logger.error("Error while reading user input from Shell", e);
      System.out.println(
          "Error reading input. Please enter a valid string for vehicle registration number");
      throw e;
    }
  }
}
