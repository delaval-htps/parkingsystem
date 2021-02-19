package com.parkit.parkingsystem;

import com.parkit.parkingsystem.service.InteractiveShell;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * class Main to run the application.
 *
 * @author delaval
 */
public class App {
  private static final Logger logger = LogManager.getLogger("App");

  public static void main(final String[] args) {
    logger.warn("Initializing Parking System");
    InteractiveShell.loadInterface();
  }
}
