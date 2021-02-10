package com.parkit.parkingsystem.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.parkit.parkingsystem.util.InputReaderUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import nl.altindag.log.LogCaptor;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runners.MethodSorters;
import org.mockito.junit.jupiter.MockitoExtension;



/**
 * class test to improve {@link InteractiveShell}.
 * 
 * @author delaval
 *
 */
@ExtendWith(MockitoExtension.class)
@FixMethodOrder(MethodSorters.DEFAULT)
public class InteractiveShellTest {

  private static final LogCaptor logCaptor = LogCaptor.forRoot();

  @AfterAll
  static void restoreApp() {
    InputReaderUtil.restoreScan();
  }

  @AfterEach
  public void clearLogs() {
    logCaptor.clearLogs();
  }

  /**
   * test to verify the behavior of menu with reading of unsupported answer.
   */
  @Test

  void loadMenuUnsupportedOptionTest() {
    // ARRANGE
    File fileTest = new File("InputUnsupportedOption");
    Scanner mockScan = null;
    try {
      mockScan = new Scanner(fileTest);
      InputReaderUtil.setScan(mockScan);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    logCaptor.setLogLevelToInfo();

    // ACT
    InteractiveShell.loadInterface();

    // ASSERT
    assertThat(logCaptor.getInfoLogs()).containsExactly("Welcome to Parking System!",
        "*********************************************************************",
        "Please select an option. Simply enter the number to choose an action",
        "1 New Vehicle Entering - Allocate Parking Space",
        "2 Vehicle Exiting - Generate Ticket Price", "3 Shutdown System",
        "*********************************************************************",
        "Error reading input. Please enter valid number for proceeding further",
        "Unsupported option. Please enter a number corresponding to the provided menu",
        "*********************************************************************",
        "Please select an option. Simply enter the number to choose an action",
        "1 New Vehicle Entering - Allocate Parking Space",
        "2 Vehicle Exiting - Generate Ticket Price", "3 Shutdown System",
        "*********************************************************************",
        "Exiting from the system!");
    assertThat(logCaptor.getWarnLogs()).containsExactly("App initialized!!!");
  }

  /**
   * test to verify the behavior of menu when exiting system.
   */
  @Test

  void loadMenuExitSystemTest() {
    // ARRANGE
    File fileTest = new File("InputExit");
    Scanner mockScan = null;
    try {
      mockScan = new Scanner(fileTest);
      InputReaderUtil.setScan(mockScan);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    logCaptor.setLogLevelToInfo();

    //ACT
    InteractiveShell.loadInterface();

    //ASSERT
    assertThat(logCaptor.getInfoLogs()).containsExactly("Welcome to Parking System!",
        "*********************************************************************",
        "Please select an option. Simply enter the number to choose an action",
        "1 New Vehicle Entering - Allocate Parking Space",
        "2 Vehicle Exiting - Generate Ticket Price", "3 Shutdown System",
        "*********************************************************************",
        "Exiting from the system!");
    assertThat(logCaptor.getWarnLogs()).containsExactly("App initialized!!!");

  }

  /**
   * test to verify the behavior of menu when a vehicle is incoming.
   */
  @Test
  void loadMenuProcessIncomingVehicleTest() {
    // ARRANGE
    File fileTest = new File("InputIncoming");
    Scanner mockScan = null;
    try {
      mockScan = new Scanner(fileTest);
      InputReaderUtil.setScan(mockScan);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    logCaptor.setLogLevelToInfo();

    // ACT
    InteractiveShell.loadInterface();

    // ASSERT
    assertThat(logCaptor.getInfoLogs()).containsAnyOf("Welcome to Parking System!",
        "*********************************************************************",
        "Please select an option. Simply enter the number to choose an action",
        "1 New Vehicle Entering - Allocate Parking Space",
        "2 Vehicle Exiting - Generate Ticket Price", "3 Shutdown System",
        "*********************************************************************",
        "------------------------------------", "Please select vehicle type from menu", "1 CAR",
        "2 BIKE", "---------------------------------------------------------------",
        "Please type the vehicle registration number and press enter key",
        "------------------------------------------------------------------------",
        "Please park your vehicle in spot number:",
        "Recorded in-time for vehicle number:456-az-13 is:",
        "Exiting from the system!");


  }

  /**
   * test to verify menu when a vehicle is exiting.
   */
  @Test
  void loadMenuProcessExitingVehicleTest() {
    // ARRANGE
    File fileTest = new File("InputExiting");
    Scanner mockScan = null;
    try {
      mockScan = new Scanner(fileTest);
      InputReaderUtil.setScan(mockScan);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    logCaptor.setLogLevelToInfo();

    // ACT
    InteractiveShell.loadInterface();

    // ASSERT
    assertThat(logCaptor.getInfoLogs()).containsAnyOf("Welcome to Parking System!",
        "*********************************************************************",
        "Please select an option. Simply enter the number to choose an action",
        "1 New Vehicle Entering - Allocate Parking Space",
        "2 Vehicle Exiting - Generate Ticket Price", "3 Shutdown System",
        "*********************************************************************",
        "------------------------------------------------------------------------",
        "Please pay the parking fare:", "Recorded out-time for vehicle number:",
        "Exiting from the system!");
  }

}
