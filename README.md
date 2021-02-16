# Parking System
A command line app for managing the parking system. 
This app uses Java to run and stores the data in Mysql DB.

# Versions
**V0.0.1**:
 
- Correction of Unit Testing in FareCalculatorServiceTest
- Implementation of Integration Testing in ParkingDataBaseIT to check the database.
- Implementation of Javadoc in **doc/**

**V0.1.1**:

* <ins>Implementation of story#1</ins>: Free 30 min Parking in TDD
    * implementation of class UT  CalculateFarUnder30Min in FarCalculateServiceTest
    * implementation of code in FarCalculateService to check Uts: OK
    

* <ins>Implementation of Story#2:</ins> 5%-Discount for recurring users in TDD
    * implementation of class UT CalculateFarWithFivePourcentsDiscount in FareCalculateServiceTest
    * update of method getTicket() in TicketDao to check if user is recurring 
    * update of SQL Queries in DbConstants to check if suer is recurring and to get the good ticket for the vehicle 
    * implementation of code in FareCalculateService to check UTs: OK


* update Javadoc

**V0.1.2**:

* update of pom.xml to configure the output directory of Jacoco's report correctly for a Sonarcloud's use.

**V0.1.3**:

* update of classes to correct bugs in relation to report of sonarcloud

**V0.1.4**:

* update pom.xml to integrate correctly log4j2, logBack, logCaptor...
* update for FareCalculator to round the price of the ticket without code's regression
* add UTs for ParkingService,FareCalculatorService and InteractiveShell to rise the branch's coverage 

**V0.1.5**:

* fix all bugs in the project according to Sonarcloud
* the coverage pass to 85% to 53% because of update especially in com.parkit.parkingsystem.dao  (need to rise the coverage in the next release...)
* update of javadoc

**V0.1.6**:

* add ParkingSpotDaoIT and TicketDaoIT for integration test with database Test.
* add ParkingSpotTest for UT with the override method equal()
* update FareCalculatorServiceTest, ParkingServiceTest, ParkingDataBaseIT to rise coverage
* branch coverage rise to 82.8% with Jacoco.
* update javadoc and send to sonarcloud

**V0.1.7 LTS**:

* sonarcloud didn't give the correct coverage according to jacoco report.
* update of pom.xml to have the correct report in jacoco
* **version LTS:** <ins>tests are OK and sonarcloud report coverage is 84,6%</ins>
 
## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

What things you need to install the software and how to install them

- Java 1.8
- Maven 3.6.2
- Mysql 8.0.17

### Installing

A step by step series of examples that tell you how to get a development env running:

1.Install Java:

https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html

2.Install Maven:

https://maven.apache.org/install.html

3.Install MySql:

https://dev.mysql.com/downloads/mysql/

After downloading the mysql 8 installer and installing it, you will be asked to configure the password for the default `root` account.
This code uses the default root account to connect and the password can be set as `rootroot`. If you add another user/credentials make sure to change the same in the code base.

### Running App

Post installation of MySQL, Java and Maven, you will have to set up the tables and data in the data base.
For this, please run the sql commands present in the `Data.sql` file under the `resources` folder in the code base.

Finally, you will be ready to import the code into an IDE of your choice and run the App.java to launch the application.

### Testing

The app has unit tests and integration tests written. More of these need to be added and in some places that can be seen mentioned as `TODO` comments. The existing tests need to be triggered from maven-surefire plugin while we try to generate the final executable jar file.

To run the tests from maven, go to the folder that contains the pom.xml file and execute the below command.


* To run Unit Tests -> `mvn test`
* To run Unit Tests and Integration Tests -> `mvn verify` 	
* To run only Integration Tests -> `mvn verify -DskipUTs`

### Jacoco reports

#### In V0.1.1:
After run the tests with the CLI , the reports of Jacoco are in :

* For unit tests : **target/site/jacoco-unit-test-coverage-report/index.html**
* For integration tests: **target/site/jacoco-integration-test-coverage-report/index.html**

#### In V0.1.2:
There is only one report of jacoco and is located in **target/site/jacoco/index.html**

This modification was implemented to be sure to have report of coverage in sonarcloud, because in V0.1.1 sonarcloud didn't find the reports of Jacoco even if it was correctly configured 
