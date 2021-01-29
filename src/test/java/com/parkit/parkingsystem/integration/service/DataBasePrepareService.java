package com.parkit.parkingsystem.integration.service;

import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import java.sql.Connection;

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
}
