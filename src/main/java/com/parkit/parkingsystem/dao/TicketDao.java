package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DbConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Service to manage(CRUD) entity of a ticket into the SGBD.
 *
 * @author delaval
 */
public class TicketDao {

  private static final Logger logger = LogManager.getLogger("TicketDAO");

  private DataBaseConfig dataBaseConfig = new DataBaseConfig();

  public DataBaseConfig getDataBaseConfig() {
    return dataBaseConfig;
  }

  public void setDataBaseConfig(DataBaseConfig dataBaseConfig) {
    this.dataBaseConfig = dataBaseConfig;
  }

  /**
   * save the ticket in the database ticket.
   *
   * @param ticket represent the ticket of a vehicle
   * @return boolean true or false if the ticket was saved or not
   */
  public boolean saveTicket(Ticket ticket) {
    Connection con = null;
    PreparedStatement ps = null;
    try {
      con = dataBaseConfig.getConnection();
      // save the ticket with its values
      ps = con.prepareStatement(DbConstants.SAVE_TICKET);
      // ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)

      ps.setInt(1, ticket.getParkingSpot().getId());
      ps.setString(2, ticket.getVehicleRegNumber());
      ps.setDouble(3, ticket.getPrice());
      ps.setTimestamp(4, new Timestamp(ticket.getInTime().getTime()));
      ps.setTimestamp(
          5, (ticket.getOutTime() == null) ? null : (new Timestamp(ticket.getOutTime().getTime())));
      return ps.execute();
    } catch (Exception ex) {
      logger.error("Error fetching next available slot", ex);
    } finally {
      dataBaseConfig.closeConnection(con);
      dataBaseConfig.closePreparedStatement(ps);
    }
    return false;
  }

  /**
   * return the ticket for vehicle with the number plate given in parameter.
   *
   * @param vehicleRegNumber the number plate of the vehicle
   * @return the ticket associated with the vehicle
   */
  public Ticket getTicket(String vehicleRegNumber) {
    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    Ticket ticket = null;

    try {
      con = dataBaseConfig.getConnection();

      // get values of the ticket
      ps = con.prepareStatement(DbConstants.GET_TICKET);
      // ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
      ps.setString(1, vehicleRegNumber);
      rs = ps.executeQuery();
      if (rs.next()) {
        ticket = new Ticket();
        ParkingSpot parkingSpot =
            new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(6)), false);
        ticket.setParkingSpot(parkingSpot);
        ticket.setId(rs.getInt(2));
        ticket.setVehicleRegNumber(vehicleRegNumber);
        ticket.setPrice(rs.getDouble(3));
        ticket.setInTime(rs.getTimestamp(4));
        ticket.setOutTime(rs.getTimestamp(5));
        ticket.setIsRecurringUser(verifyRecurringUserTicket(vehicleRegNumber));
      }

    } catch (Exception ex) {
      logger.error("Error fetching next available slot", ex);
    } finally {
      dataBaseConfig.closeConnection(con);
      dataBaseConfig.closeResultSet(rs);
      dataBaseConfig.closePreparedStatement(ps);
    }
    return ticket;
  }

  /**
   * update a ticket given in the parameter with the correct informations.
   *
   * @param ticket the ticket of vehicle
   * @return boolean true or false if the ticket was correctly updated
   */
  public boolean updateTicket(Ticket ticket) {
    Connection con = null;
    PreparedStatement ps = null;
    try {
      con = dataBaseConfig.getConnection();
      ps = con.prepareStatement(DbConstants.UPDATE_TICKET);
      ps.setDouble(1, ticket.getPrice());
      ps.setTimestamp(2, new Timestamp(ticket.getOutTime().getTime()));
      ps.setInt(3, ticket.getId());
      ps.execute();
      return true;
    } catch (Exception ex) {
      logger.error("Error saving ticket info", ex);
    } finally {
      dataBaseConfig.closeConnection(con);
      dataBaseConfig.closePreparedStatement(ps);
    }
    return false;
  }

  /**
   * method to verify in DB table ticket if the user is recurring or not.
   *
   * @param vehicleRegNumber the number plate of the vehicle
   * @return boolean true or false
   */
  public boolean verifyRecurringUserTicket(String vehicleRegNumber) {
    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      con = dataBaseConfig.getConnection();

      // First: verification of type of user : recurring or not ?
      ps = con.prepareStatement(DbConstants.VERIFY_RECURRING_USER);
      ps.setString(1, vehicleRegNumber);
      rs = ps.executeQuery();
      rs.next();
      return (rs.getInt(1) > 0);

    } catch (Exception e) {
      logger.error("Error to verify if it's a recurring user", e);
      return false;
    } finally {
      dataBaseConfig.closeConnection(con);
      dataBaseConfig.closePreparedStatement(ps);
      dataBaseConfig.closeResultSet(rs);
    }
  }
}
