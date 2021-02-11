package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDao;
import com.parkit.parkingsystem.dao.TicketDao;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Services to control the access of vehicle in the park.
 * 
 * @author delaval
 *
 */
public class ParkingService {

  private static final Logger logger = LogManager.getLogger(ParkingService.class);


  private static FareCalculatorService fareCalculatorService = new FareCalculatorService();

  private InputReaderUtil inputReaderUtil;
  private ParkingSpotDao parkingSpotDao;
  private TicketDao ticketDao;

  /**
   * Constructor with parameters.
   * 
   * @param inputReaderUtil the object allowing to read in console the number of validation menu and
   *        the number plate of vehicle
   * @param parkingSpotDao service to manage the parking place into the SGBD
   * @param ticketDao service to manage the parking place into the SGBD
   */
  public ParkingService(InputReaderUtil inputReaderUtil, ParkingSpotDao parkingSpotDao,
      TicketDao ticketDao) {
    this.inputReaderUtil = inputReaderUtil;
    this.parkingSpotDao = parkingSpotDao;
    this.ticketDao = ticketDao;
  }

  /**
   * define the process when a vehicle incomes into the park. it generates a ticket and gives a
   * number place available for a vehicle.
   */
  public void processIncomingVehicle() {
    try {
      ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();
      if (parkingSpot != null && parkingSpot.getId() > 0) {

        parkingSpot.setAvailable(false);
        parkingSpotDao.updateParking(parkingSpot); // allot this parking space and mark it's
        // availability as
        // false
        String vehicleRegNumber = getVehichleRegNumber();
        Date inTime = new Date();
        Ticket ticket = new Ticket();
        // ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME

        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber(vehicleRegNumber);
        ticket.setPrice(0);
        ticket.setInTime(inTime);
        ticket.setOutTime(null);
        ticketDao.saveTicket(ticket);

        logger.warn("Generated Ticket and saved in DB");
        logger.info("------------------------------------------------------------------------");
        logger.info("Please park your vehicle in spot number:{}", parkingSpot.getId());

        logger.info(
            "Recorded in-time for vehicle number: {} is: {}", vehicleRegNumber, inTime);
      }
    } catch (Exception e) {
      logger.error("Unable to process incoming vehicle", e);
    }
  }

  private String getVehichleRegNumber() throws Exception {
    logger.info("---------------------------------------------------------------");
    logger.info("Please type the vehicle registration number and press enter key");
    return inputReaderUtil.readVehicleRegistrationNumber();
  }

  /**
   * method to give the next number place available in the park.
   * 
   * @return a ParkingSpot including: the type of the vehicle, the number of the parking place used
   *         and its availability
   */
  public ParkingSpot getNextParkingNumberIfAvailable() {
    int parkingNumber = 0;
    ParkingSpot parkingSpot = null;
    try {
      ParkingType parkingType = getVehichleType();
      parkingNumber = parkingSpotDao.getNextAvailableSlot(parkingType);
      if (parkingNumber > 0) {
        parkingSpot = new ParkingSpot(parkingNumber, parkingType, true);
      } else {
        throw new Exception("Error fetching parking number from DB. Parking slots might be full");
      }
    } catch (IllegalArgumentException ie) {
      logger.error("Error parsing user input for type of vehicle", ie);
    } catch (Exception e) {
      logger.error("Error fetching next available parking slot", e);
    }
    return parkingSpot;
  }

  private ParkingType getVehichleType() {
    logger.info("------------------------------------");
    logger.info("Please select vehicle type from menu");
    logger.info("1 CAR");
    logger.info("2 BIKE");
    int input = inputReaderUtil.readSelection();
    switch (input) {
      case 1: {
        return ParkingType.CAR;
      }
      case 2: {
        return ParkingType.BIKE;
      }
      default: {
        logger.error("Incorrect input provided");
        throw new IllegalArgumentException("Entered input is invalid");
      }
    }
  }

  /**
   * define the process when a vehicle exits from the park. it updates the ticket with the out time
   * and the price and released the parking place.
   */
  public void processExitingVehicle() {
    
    try {
      String vehicleRegNumber = getVehichleRegNumber();
      Ticket ticket = ticketDao.getTicket(vehicleRegNumber);
      Date outTime = new Date();
      ticket.setOutTime(outTime);
      fareCalculatorService.calculateFare(ticket);
      if (ticketDao.updateTicket(ticket)) {
        ParkingSpot parkingSpot = ticket.getParkingSpot();
        parkingSpot.setAvailable(true);
        parkingSpotDao.updateParking(parkingSpot);
        logger.info("------------------------------------------------------------------------");
        if (ticket.getIsRecurringUser()) {
          logger.info("Welcome back! As a recurring user of our parking lot,"
              + " you'll benefit from a 5% discount!");
        }
        logger.info("Please pay the parking fare:{}", ticket.getPrice());
        logger.info("Recorded out-time for vehicle number: {} is: {}", ticket.getVehicleRegNumber(),
            outTime);
      } else {
        logger.error("Unable to update ticket information. Error occurred");
      }

    } catch (Exception e) {
      logger.error("Unable to process exiting vehicle", e);
    }
  }
}
