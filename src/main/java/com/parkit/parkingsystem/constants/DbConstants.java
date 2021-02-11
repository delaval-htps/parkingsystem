package com.parkit.parkingsystem.constants;

/**
 * class representing all SQL Query.
 * 
 * @author delaval
 *
 */
public class DbConstants {

  private DbConstants() {
    throw new IllegalStateException("Utility class");
  }

  public static final String GET_NEXT_PARKING_SPOT =
      "select min(PARKING_NUMBER) from parking where AVAILABLE = true and TYPE = ?";
  public static final String UPDATE_PARKING_SPOT =
      "update parking set available = ? where PARKING_NUMBER = ?";

  public static final String SAVE_TICKET =
      "insert into ticket(PARKING_NUMBER, "
          + "VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME) values(?,?,?,?,?)";

  public static final String UPDATE_TICKET = "update ticket set PRICE=?, OUT_TIME=? where ID=?";


  // detected problem: when a vehicle exited ,getTicket() return the ticket of the first park use.
  // So when user is a recurring , the far is calculated and store in this ticket of first park use
  // with an update of its out_time( that is not null)
  // Problem: the user has a bad fare with 5% of discount (but bad, calculated with his first
  // in_time of park use) and when the vehicle is exited , we have got the good ticket in the DB
  // with no price and no out_time.
  // Resolution: select with the out_time = null
  public static final String GET_TICKET =
      "select t.PARKING_NUMBER, t.ID, t.PRICE, t.IN_TIME, t.OUT_TIME, p.TYPE "
          + "from ticket t,parking p where p.parking_number ="
          + " t.parking_number and t.VEHICLE_REG_NUMBER=? and t.OUT_TIME is null"
          + " order by t.IN_TIME  limit 1";

  public static final String VERIFY_RECURRING_USER =
      "select count(*) from ticket where VEHICLE_REG_NUMBER =? and OUT_TIME is not null";
}
