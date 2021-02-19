package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.dao.ParkingSpotDao;
import com.parkit.parkingsystem.dao.TicketDao;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Interface of display menu on console out.
 *
 * @author delaval
 */
public class InteractiveShell {

  private InteractiveShell() {
    throw new IllegalStateException("utility class");
  }

  private static final Logger logger = LogManager.getLogger("InteractiveShell");

  private static ParkingService parkingService;
  private static boolean instantiation = true;

  /**
   * method to mock easily ParkingService in {@link InteractiveShellTest}.
   *
   * @param parkingService this object will be use in InteractiveShellTest to be mock and verify its
   *        calls
   */
  public static void setParkingService(ParkingService parkingService) {
    InteractiveShell.parkingService = parkingService;
    instantiation = false;
  }

  /**
   * method to restore the behavior of the class to run correctly. this method is only call in
   * InteractiveShellTest.java
   */
  public static void restoreInteractiveShell() {
    instantiation = true;
  }

  /**
   * method to set a menu on console according to actions in the park.
   */
  public static void loadInterface() {
    logger.warn("App initialized!!!");
    logger.info("Welcome to Parking System!");

    boolean continueApp = true;
    InputReaderUtil inputReaderUtil = new InputReaderUtil();
    ParkingSpotDao parkingSpotDao = new ParkingSpotDao();
    TicketDao ticketDao = new TicketDao();
    if (instantiation) {
      parkingService = new ParkingService(inputReaderUtil, parkingSpotDao, ticketDao);
    }

    while (continueApp) {
      loadMenu();
      int option = inputReaderUtil.readSelection();
      switch (option) {
        case 1: {
          parkingService.processIncomingVehicle();
          break;
        }
        case 2: {
          parkingService.processExitingVehicle();
          break;
        }
        case 3: {
          logger.info("Exiting from the system!");
          continueApp = false;
          break;
        }
        default:
          logger.info(
              "Unsupported option. Please enter a number corresponding to the provided menu");
      }
    }
  }

  private static void loadMenu() {
    logger.info("*********************************************************************");
    logger.info("Please select an option. Simply enter the number to choose an action");
    logger.info("1 New Vehicle Entering - Allocate Parking Space");
    logger.info("2 Vehicle Exiting - Generate Ticket Price");
    logger.info("3 Shutdown System");
    logger.info("*********************************************************************");

  }
}
