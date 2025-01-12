import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.StringTokenizer;
import javax.swing.Action;

// Simulation of a Simple Command-line based Uber App 

// This system supports "ride sharing" service and a delivery service

public class UberUI
{
  public static void main(String[] args)
  {
    // Create the System Manager - the main system code is in here 

    UberSystemManager uber = new UberSystemManager();
    
    Scanner scanner = new Scanner(System.in);
    System.out.print(">");

    // Process keyboard actions
    while (scanner.hasNextLine())
    {
      String action = scanner.nextLine();

      try {
        if (action == null || action.equals("")) 
        {
          System.out.print("\n>");
          continue;
        }
        // Quit the App
        else if (action.equalsIgnoreCase("Q") || action.equalsIgnoreCase("QUIT"))
          return;
        // Print all the registered drivers
        else if (action.equalsIgnoreCase("DRIVERS"))  // List all drivers
        {
          uber.listAllDrivers(); 
        }
        // Print all the registered users
        else if (action.equalsIgnoreCase("USERS"))  // List all users
        {
          uber.listAllUsers(); 
        }
        // Print all current ride requests or delivery requests
        else if (action.equalsIgnoreCase("REQUESTS"))  // List all requests
        {
          uber.listAllServiceRequests(); 
        }
        // Register a new driver
        else if (action.equalsIgnoreCase("REGDRIVER")) 
        {
          String name = "";
          System.out.print("Name: ");
          if (scanner.hasNextLine())
          {
            name = scanner.nextLine();
          }
          String carModel = "";
          System.out.print("Car Model: ");
          if (scanner.hasNextLine())
          {
            carModel = scanner.nextLine();
          }
          String license = "";
          System.out.print("Car License: ");
          if (scanner.hasNextLine())
          {
            license = scanner.nextLine();
          }
          String address = "";
          System.out.print("Address: ");
          if (scanner.hasNextLine())
          {
            address = scanner.nextLine();
          }
          uber.registerNewDriver(name, carModel, license, address);
          System.out.printf("Driver: %-15s Car Model: %-15s License Plate: %-10s Address: %-15s", name, carModel, license, address);
        }
        // Register a new user
        else if (action.equalsIgnoreCase("REGUSER")) 
        {
          String name = "";
          System.out.print("Name: ");
          if (scanner.hasNextLine())
          {
            name = scanner.nextLine();
          }
          String address = "";
          System.out.print("Address: ");
          if (scanner.hasNextLine())
          {
            address = scanner.nextLine();
          }
          double wallet = 0.0;
          System.out.print("Wallet: ");
          if (scanner.hasNextDouble())
          {
            wallet = scanner.nextDouble();
            scanner.nextLine(); // consume nl!! Only needed when mixing strings and int/double
          }
          uber.registerNewUser(name, address, wallet);
          System.out.printf("User: %-15s Address: %-15s Wallet: %2.2f", name, address, wallet);
        }
        // Request a ride
        else if (action.equalsIgnoreCase("REQRIDE")) 
        {
          String userId = "";
          System.out.print("User Account Id: ");
          if (scanner.hasNextLine())
          {
            userId = scanner.nextLine();
          }
          String fromAddr = "";
          System.out.print("From Address: ");
          if (scanner.hasNextLine())
          {
            fromAddr = scanner.nextLine();
          }
          String toAddr = "";
          System.out.print("To Address: ");
          if (scanner.hasNextLine())
          {
            toAddr = scanner.nextLine();
          }
          uber.requestRide(userId, fromAddr, toAddr);
          System.out.println();
          System.out.printf("RIDE for: %-15s From: %-15s To: %-15s", ((uber.getUser(userId)).getName()), fromAddr, toAddr);
        }

        // Request a food delivery
        else if (action.equalsIgnoreCase("REQDLVY")) 
        {
          String userId = "";
          System.out.print("User Account Id: ");
          if (scanner.hasNextLine())
          {
            userId = scanner.nextLine();
          }
          String fromAddr = "";
          System.out.print("From Address: ");
          if (scanner.hasNextLine())
          {
            fromAddr = scanner.nextLine();
          }
          String toAddr = "";
          System.out.print("To Address: ");
          if (scanner.hasNextLine())
          {
            toAddr = scanner.nextLine();
          }
          String restaurant = "";
          System.out.print("Restaurant: ");
          if (scanner.hasNextLine())
          {
            restaurant = scanner.nextLine();
          }
          String orderNum = "";
          System.out.print("Food Order #: ");
          if (scanner.hasNextLine())
          {
            orderNum = scanner.nextLine();
          }

          uber.requestDelivery(userId, fromAddr, toAddr, restaurant, orderNum);
          System.out.println();
          System.out.printf("DELIVERY for: %-15s From: %-15s To: %-15s", ((uber.getUser(userId)).getName()), fromAddr, toAddr);
        }

        // Sort users by name
        else if (action.equalsIgnoreCase("SORTBYNAME")) 
        {
          uber.sortByUserName();
        }
        // Sort users by number of ride they have had
        else if (action.equalsIgnoreCase("SORTBYWALLET")) 
        {
          uber.sortByWallet();
        }
      
        // Cancel a current service (ride or delivery) request
        else if (action.equalsIgnoreCase("CANCELREQ")) 
        {
          int zone = -1;
          System.out.print("Zone #: ");
          if (scanner.hasNextInt())
          {
            zone = scanner.nextInt();
            scanner.nextLine(); // consume nl character
          }
          int request = -1;
          System.out.print("Request #: ");
          if (scanner.hasNextInt())
          {
            request = scanner.nextInt();
            scanner.nextLine(); // consume nl character
          }
          uber.cancelServiceRequest(zone, request);
          System.out.println("Service request #" + request + " cancelled");
        }
        // Drop-off the user or the food delivery to the destination address
        else if (action.equalsIgnoreCase("DROPOFF")) 
        {
          String driverId = "";
          System.out.print("Driver Id: ");
          if (scanner.hasNextLine())
          {
            driverId = scanner.nextLine();
          }
          uber.dropOff(driverId);
          System.out.println("Driver " + driverId + " Dropping Off");
        }
        // Get the Current Total Revenues
        else if (action.equalsIgnoreCase("REVENUES")) 
        {
          System.out.println("Total Revenue: " + String.format("%.2f", uber.totalRevenue));
        }
        // Unit Test of Valid City Address 
        else if (action.equalsIgnoreCase("ADDR")) 
        {
          String address = "";
          System.out.print("Address: ");
          if (scanner.hasNextLine())
          {
            address = scanner.nextLine();
          }
          System.out.print(address);
          if (CityMap.validAddress(address))
            System.out.println("\nValid Address"); 
          else
            System.out.println("\nBad Address"); 
        }
        // Unit Test of CityMap Distance Method
        else if (action.equalsIgnoreCase("DIST")) 
        {
          String from = "";
          System.out.print("From: ");
          if (scanner.hasNextLine())
          {
            from = scanner.nextLine();
          }
          String to = "";
          System.out.print("To: ");
          if (scanner.hasNextLine())
          {
            to = scanner.nextLine();
          }
          System.out.print("\nFrom: " + from + " To: " + to);
          System.out.println("\nDistance: " + CityMap.getDistance(from, to) + " City Blocks");
        }
        
        // Pickup a service request
        else if (action.equalsIgnoreCase("PICKUP"))
        {
          String driverId = "";
          System.out.print("Driver Id: ");
          if (scanner.hasNextLine())
          {
            driverId = scanner.nextLine();
          }
          uber.pickup(driverId);
        }

        // Load users from a file
        else if (action.equalsIgnoreCase("LOADUSERS"))
        {
          String filename = "";
          System.out.print("User File: ");
          if (scanner.hasNextLine())
          {
            filename = scanner.nextLine();
          }
          ArrayList<User> users = new ArrayList<>();
          try{
            users = UberRegistered.loadPreregisteredUsers(filename);
            uber.setUsers(users);
            System.out.println("Users Loaded");
          }
          catch(FileNotFoundException e){
            System.out.println("Users File: " + filename + " Not Found");
          }
          catch(NoSuchElementException e){
            System.out.println("Something went wrong");
            return;
          }
          catch(NumberFormatException e)
          {
            System.out.println("Something went wrong");
            return;
          }
        }

        // Load drivers from file
        else if (action.equalsIgnoreCase("LOADDRIVERS"))
        {
          String filename = "";
          System.out.print("Drivers File: ");
          if (scanner.hasNextLine())
          {
            filename = scanner.nextLine();
          }
          ArrayList<Driver> drivers = new ArrayList<>();
          try{
            drivers = UberRegistered.loadPreregisteredDrivers(filename);
            uber.setDrivers(drivers);
            System.out.println("Drivers Loaded");
          }
          catch(FileNotFoundException e){
            System.out.println("Drivers File: " + filename + " Not Found");
          }
          catch(NoSuchElementException e){
            System.out.println("Something went wrong");
            return;
          }
        }

        // Change driver address
        else if (action.equalsIgnoreCase("DRIVETO"))
        {
          String driverId = "";
          System.out.print("Driver Id: ");
          if (scanner.hasNextLine())
          {
            driverId = scanner.nextLine();
          }
          String address = "";
          System.out.print("Address: ");
          if (scanner.hasNextLine())
          {
            address = scanner.nextLine();
          }
          uber.driveTo(driverId, address);
          System.out.println("Driver " + driverId + " Now in Zone " + CityMap.getCityZone(address));
        }
      }

      // Catching expections
      catch(UserExistsException e){
        System.out.println(e.getMessage());
      }
      catch(DriverExistsException e){
        System.out.println(e.getMessage());
      }
      catch(RideRequestExistsException e){
        System.out.println(e.getMessage());
      }
      catch(DeliveryRequestExistsException e){
        System.out.println(e.getMessage());
      }
      catch(InvalidNameException e){
        System.out.println(e.getMessage());
      }
      catch(InvalidAddressException e){
        System.out.println(e.getMessage());
      }
      catch(InvalidWalletException e){
        System.out.println(e.getMessage());
      }
      catch(InvalidCarModelException e){
        System.out.println(e.getMessage());
      }
      catch(InvalidLicencePlateException e){
        System.out.println(e.getMessage());
      }
      catch(InsufficientTravelDistanceException e){
        System.out.println(e.getMessage());
      }
      catch(InsufficientFundsException e){
        System.out.println(e.getMessage());
      }
      catch(NoDriverAvailableException e){
        System.out.println(e.getMessage());
      }
      catch(InvalidRequestNumberException e){
        System.out.println(e.getMessage());
      }
      catch(InvalidZoneNumberException e){
        System.out.println(e.getMessage());
      }
      catch(UserNotFoundException e){
        System.out.println(e.getMessage());
      }
      catch(DriverNotFoundException e){
        System.out.println(e.getMessage());
      }
      catch(NoServiceRequestException e){
        System.out.println(e.getMessage());
      }
      catch(InvalidDriverStatusException e){
        System.out.println(e.getMessage());
      }

      
      System.out.print("\n>");
    }
  }
}

