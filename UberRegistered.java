// Manage registered users and drivers

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class UberRegistered
{
    // These variables are used to generate user account and driver ids
    private static int firstUserAccountID = 900;
    private static int firstDriverId = 700;

    // Generate a new user account id
    public static String generateUserAccountId(int size)
    {
        return "" + firstUserAccountID + (size);
    }

    // Generate a new driver id
    public static String generateDriverId(ArrayList<Driver> current)
    {
        return "" + firstDriverId + current.size();
    }

    // Load the users from file 
    public static ArrayList<User> loadPreregisteredUsers(String f) throws FileNotFoundException, NoSuchElementException, NumberFormatException
    {        
        File inputFile = new File(f);
        Scanner userFile = new Scanner(inputFile);

        ArrayList<User> users = new ArrayList<>(); //arraylist of users

        while(userFile.hasNextLine())
        {   
            // Create a new User based on the infomation from the file and add the User to the arraylist of users
            String name = userFile.nextLine();
            String address = userFile.nextLine();
            double wallet = Double.parseDouble(userFile.nextLine());

            int size = users.size();
            users.add(new User(generateUserAccountId(size), name, address, wallet));
        }
        userFile.close();
        return users;
        

    }

    // Load drivers from file
    public static ArrayList<Driver> loadPreregisteredDrivers(String f) throws FileNotFoundException, NoSuchElementException
    {        
        File inputFile = new File(f);
        Scanner driverFile = new Scanner(inputFile);

        ArrayList<Driver> drivers = new ArrayList<>(); //arraylist of drivers

        while(driverFile.hasNextLine())
        {
            // Create a new Driver based on the infomation from the file and add the Driver to the arraylist of drivers
            String name = driverFile.nextLine();
            String model = driverFile.nextLine();
            String license = driverFile.nextLine();
            String address = driverFile.nextLine();

            Driver myDriver = new Driver(generateDriverId(drivers), name, model, license, address);
            myDriver.setZone(CityMap.getCityZone(myDriver.getAddress())); //determine the city zone of the driver
            drivers.add(myDriver); //set the driver's city zone
        }
        driverFile.close();
        return drivers;
        

    }
}

