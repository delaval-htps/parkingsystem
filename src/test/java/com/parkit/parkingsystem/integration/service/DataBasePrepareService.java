package com.parkit.parkingsystem.integration.service;

import com.parkit.parkingsystem.constants.DbConstants;
import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Date;

/**
 * class for the preparation of the database.
 * 
 * @author delaval
 *
 */
public class DataBasePrepareService {

  DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();

  /**
   * method to clear elements of the database.
   * 
   */
  public void clearDataBaseEntries() {
    Connection connection = null;
    try {
      connection = dataBaseTestConfig.getConnection();

      // set parking entries to available
      connection.prepareStatement("update parking set available = true").execute();

      // clear ticket entries;
      connection.prepareStatement("truncate table ticket").execute();

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      dataBaseTestConfig.closeConnection(connection);
    }
  }

  /**
   * method to put value of a ticket in the Database for getTicketWhenCorrectValuesInDatabase() in
   * TicketDaoIT.
   */
  public boolean setTicketTest(Date inTime, Date outTime) {
    Connection con = null;
    PreparedStatement ps = null;
    try {
      con = dataBaseTestConfig.getConnection();

      ps = con.prepareStatement(DbConstants.SAVE_TICKET);

      ps.setInt(1, 1);
      ps.setString(2, "ABCDEF");
      ps.setDouble(3, Fare.CAR_RATE_PER_HOUR);
      ps.setTimestamp(4, new Timestamp(inTime.getTime()));
      if (outTime == null) {
        ps.setTimestamp(5, null);
      } else {
        ps.setTimestamp(5, new Timestamp(outTime.getTime()));
      }
      return ps.execute();

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      dataBaseTestConfig.closeConnection(con);
      dataBaseTestConfig.closePreparedStatement(ps);
    }
    return true;
  }

  /**
   * method to set the test parking with all place not available. Used with
   * ParkingSpotDaoIT.getNextAvailableSlot_WhenSlotUnavailable()
   */
  public void setAllParkingSpotNoAvailable() {
    Connection connection = null;
    try {
      connection = dataBaseTestConfig.getConnection();

      // set parking entries to no available
      connection.prepareStatement("update parking set available = false").execute();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      dataBaseTestConfig.closeConnection(connection);
    }
  }

  /**
   * method to set the test parking's row with the parkingNumber with available to false. Used with
   * ParkingSpotDaoIT.updateParking_WhenParkingNumberExist()
   * 
   * @param parkingNumber the parking number's slot to update
   */
  public void setParkingSpotNoAvailable(int parkingNumber) {
    Connection connection = null;
    PreparedStatement ps = null;
    try {
      connection = dataBaseTestConfig.getConnection();

      // set parking number's slot no available
      ps = connection
          .prepareStatement("update parking set available = false where parking_number = ?");
      ps.setInt(1, parkingNumber);
      ps.executeUpdate();

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      dataBaseTestConfig.closeConnection(connection);
    }
  }
}
