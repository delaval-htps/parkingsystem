package com.parkit.parkingsystem.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class InputReaderUtilTest {

  private static final LogCaptor logCaptor = LogCaptor.forRoot();

  private final InputReaderUtil inputReaderUtil = new InputReaderUtil();

  @AfterEach
  void restorLog() {
    logCaptor.clearLogs();
  }

  @AfterAll
  static void restoreScan() {
    InputReaderUtil.restoreScan();
  }

  @Nested
  class ReadSelectionTest {

    @Test
    void readSelection_WhenInteger() {
      // ARRANGE
      File fileTest = new File("src/test/resources/FileInputReaderUtilTest/IntegerTest");
      Scanner mockScan = null;
      try {
        mockScan = new Scanner(fileTest);
        InputReaderUtil.setScan(mockScan);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
      // ACT
      int result = inputReaderUtil.readSelection();

      // ASSERT

      assertThat(result).isPositive().isInstanceOf(Integer.class);
    }

    @Test
    void readSelection_WhenNoInteger() {
      // ARRANGE
      File fileTest = new File("src/test/resources/FileInputReaderUtilTest/LetterTest");
      Scanner mockScan = null;
      try {
        mockScan = new Scanner(fileTest);
        InputReaderUtil.setScan(mockScan);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
      // ACT...
      int result = inputReaderUtil.readSelection();
      // ASSERT
      assertThat(result).isEqualTo(-1);
      assertThat(logCaptor.getErrorLogs())
          .containsExactly("Error while reading user input from Shell");
      assertThat(logCaptor.getInfoLogs())
          .containsExactly("Error reading input. Please enter valid number for proceeding further");
    }
  }

  @Nested
  class ReadVehicleRegistrationNumberTest {

    @Test
    void readVehicleRegistrationNumber_WhenCorrectNumberPlate() {
      // ARRANGE
      File fileTest = new File("src/test/resources/FileInputReaderUtilTest/NumberPlateTest");
      Scanner mockScan = null;
      try {
        mockScan = new Scanner(fileTest);
        InputReaderUtil.setScan(mockScan);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
      // ACT...
      String result = inputReaderUtil.readVehicleRegistrationNumber();
      // ASSERT
      assertThat(result).isEqualTo("ABCDEF");
    }

    @Test
    void readVehicleRegistrationNumber_WhenSpaceNumberPlate() {
      // ARRANGE
      File fileTest = new File("src/test/resources/FileInputReaderUtilTest/NumberPlateSpaceTest");
      Scanner mockScan = null;
      try {
        mockScan = new Scanner(fileTest);
        InputReaderUtil.setScan(mockScan);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
      // ACT...
      // ASSERT
      assertThrows(IllegalArgumentException.class,
          () -> inputReaderUtil.readVehicleRegistrationNumber(), "Invalid input provided");
    }


    @Test
    void readVehicleRegistrationNumber_WhenNoData() {
      // ARRANGE
      File fileTest = new File("src/test/resources/FileInputReaderUtilTest/EmptyFileTest");
      Scanner mockScan = null;
      try {
        mockScan = new Scanner(fileTest);
        InputReaderUtil.setScan(mockScan);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
      // ACT...

      // ASSERT
      assertThrows(NoSuchElementException.class,
          () -> inputReaderUtil.readVehicleRegistrationNumber());
      assertThat(logCaptor.getErrorLogs())
          .containsExactly("Error while reading user input from Shell");
      assertThat(logCaptor.getInfoLogs()).containsExactly(
          "Error reading input. Please enter a valid string for vehicle registration number");

    }

    @Test
    void readVehicleRegistrationNumber_WhenScannerClose() {
      // ARRANGE
      File fileTest = new File("src/test/resources/FileInputReaderUtilTest/EmptyFileTest");
      Scanner mockScan = null;
      try {
        mockScan = new Scanner(fileTest);
        mockScan.close();
        InputReaderUtil.setScan(mockScan);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
      // ACT...

      // ASSERT
      assertThrows(IllegalStateException.class,
          () -> inputReaderUtil.readVehicleRegistrationNumber());
      assertThat(logCaptor.getErrorLogs())
          .containsExactly("Error while reading user input from Shell");
      assertThat(logCaptor.getInfoLogs()).containsExactly(
          "Error reading input. Please enter a valid string for vehicle registration number");

    }

  }
}

