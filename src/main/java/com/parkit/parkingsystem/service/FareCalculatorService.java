package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

/**
 * Service of calculation of fare for a use of park for any vehicule with it's ticket.
 * 
 * @author delaval
 *
 */
public class FareCalculatorService {

  /**
   * calculate the fare for one ticket.
   * 
   * @param ticket the ticket {@link Ticket} for a vehicle including:
   *        <ul>
   *        <li>the type of vehicle (CAR or BIKE)</li>
   *        <li>the inTime time when vehicle go into the park</li>
   *        <li>the outTime : time when vehicle go out of the park</li>
   *        <li>the parkingSpot: the spot where the vehicle was parked</li>
   *        <li>the wehicleRegNumber : the number plate of vehicle
   *        <li>the price: price of parking duration</li>
   *        </ul>
   * 
   */
  public void calculateFare(Ticket ticket) {
    if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
      throw new IllegalArgumentException(
          "Out time provided is incorrect:" + ticket.getOutTime().toString());
    }

    Long inHour = ticket.getInTime().getTime();
    Long outHour = ticket.getOutTime().getTime();

    Long duration = outHour - inHour;



    // calculation of the duration in a number of hours in type double (need to cast in double
    // else we loose the decimal numbers)

    double durationNumberHours =
        (duration >= (0.5 * 60 * 60 * 1000)) ? (double) (duration / (1000 * 60)) / 60 : 0;

    switch (ticket.getParkingSpot().getParkingType()) {
      case CAR: {
        ticket.setPrice(durationNumberHours * Fare.CAR_RATE_PER_HOUR);
        break;
      }
      case BIKE: {
        ticket.setPrice(durationNumberHours * Fare.BIKE_RATE_PER_HOUR);
        break;
      }
      default:
        throw new IllegalArgumentException("Unkown Parking Type");
    }
    if (ticket.getIsRecurringUser()) {
      ticket.setPrice(ticket.getPrice() * 0.95);
    }

  }
}
