package com.parkit.parkingsystem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.parkit.parkingsystem.util.InputReaderUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import nl.altindag.log.LogCaptor;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mock;
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

  @Mock
  private static ParkingService parkingService;

  @BeforeAll
  static void setUp() {
    logCaptor.setLogLevelToInfo();
  }

  @AfterAll
  static void restoreApp() {
    InputReaderUtil.restoreScan();
    InteractiveShell.restoreInteractiveShell();
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

    doNothing().when(parkingService).processIncomingVehicle();
    InteractiveShell.setParkingService(parkingService);

    // ACT
    InteractiveShell.loadInterface();

    // ASSERT
    verify(parkingService, times(1)).processIncomingVehicle();

    assertThat(logCaptor.getInfoLogs()).containsOnly("Welcome to Parking System!",
        "*********************************************************************",
        "Please select an option. Simply enter the number to choose an action",
        "1 New Vehicle Entering - Allocate Parking Space",
        "2 Vehicle Exiting - Generate Ticket Price", "3 Shutdown System",
        "*********************************************************************",
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

    doNothing().when(parkingService).processExitingVehicle();
    InteractiveShell.setParkingService(parkingService);

    // ACT
    InteractiveShell.loadInterface();

    // ASSERT
    verify(parkingService, times(1)).processExitingVehicle();
    assertThat(logCaptor.getInfoLogs()).containsOnly("Welcome to Parking System!",
        "*********************************************************************",
        "Please select an option. Simply enter the number to choose an action",
        "1 New Vehicle Entering - Allocate Parking Space",
        "2 Vehicle Exiting - Generate Ticket Price", "3 Shutdown System",
        "*********************************************************************",
        "Exiting from the system!");
  }

}
