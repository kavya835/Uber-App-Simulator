import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

/*
 * 
 * This class contains the main logic of the system.
 * 
 *  It keeps track of all users, drivers and service requests (RIDE or DELIVERY)
 * 
 */
public class UberSystemManager
{
  private Map<String, User> users;
  private ArrayList<Driver> drivers;

  private Queue<UberService>[] serviceRequests; // An array of queues

  public double totalRevenue; // Total revenues accumulated via rides and deliveries
  
  // Rates per city block
  private static final double DELIVERYRATE = 1.2;
  private static final double RIDERATE = 1.5;
  // Portion of a ride/delivery cost paid to the driver
  private static final double PAYRATE = 0.1;

  //These variables are used to generate user account and driver ids
  int userAccountId = 900;
  int driverId = 700;

  @SuppressWarnings("unchecked")
  public UberSystemManager()
  {
    users   = new TreeMap<String, User>();
    drivers = new ArrayList<Driver>();
    
    serviceRequests = new Queue[4]; // Array of requests has 4 queues
    // Initialize the priority queues (so that they are not null)
    for (int i = 0; i < serviceRequests.length; i++){
      serviceRequests[i] = new LinkedList<>();
    }
    
    totalRevenue = 0;
  }
  
  // Given user account id, find user in list of users
  // Return null if not found
  public User getUser(String accountId)
  {
    if((users.keySet()).contains(accountId)){
      return users.get(accountId);
    }
    return null;
  }
  
  // Check for duplicate user
  private void userExists(User user) throws UserExistsException
  {
    for (String key : users.keySet()){
      if((users.get(key)).equals(user)){
        throw new UserExistsException("User Already Exists in System");
      }
    }
  }
  
 // Check for duplicate driver
 private void driverExists(Driver driver) throws DriverExistsException
 {
    for (int i = 0; i < drivers.size(); i++){
      if((drivers.get(i)).equals(driver)){
        throw new DriverExistsException("Driver Already Exists in System");
      }
    }
 }
  
  // Given a user, check if user ride/delivery request already exists in service requests
  private void existingRequest(UberService req) throws RideRequestExistsException, DeliveryRequestExistsException
  {
    for (int j = 0; j < 4; j++){
      for (UberService otherRequest : serviceRequests[j]){
        if (((req.getServiceType()).equals("RIDE")) && otherRequest.equals(req))
        {
          throw new RideRequestExistsException("User Already Has Ride Request");
        }
        if (((req.getServiceType()).equals("DELIVERY")) && otherRequest.equals(req))
        {
          throw new DeliveryRequestExistsException ("User Already Has Delivery Request at Restaurant with this Food Order");
        }
      }
    }
  }

  // Calculate the cost of a ride or of a delivery based on distance 
  private double getDeliveryCost(int distance)
  {
    return distance * DELIVERYRATE;
  }

  private double getRideCost(int distance)
  {
    return distance * RIDERATE;
  }

  // Go through all drivers and see if one is available
  // Choose the first available driver
  // Return null if no available driver
  private Driver getAvailableDriver()
  {
    for (int i = 0; i < drivers.size(); i++)
    {
      if ((drivers.get(i)).getStatus() == Driver.Status.AVAILABLE)
      {
        return (drivers.get(i));
      }
    }
    return null;
  }

  // Print Information (printInfo()) about all registered users in the system
  public void listAllUsers()
  {
    System.out.println();
    int index = 0;
    for (String userId: users.keySet())
    {
      index++;
      System.out.printf("%-2s. ", index);
      users.get(userId).printInfo(); //print the details of user
      System.out.println();
    }
  }

  // Print Information (printInfo()) about all registered drivers in the system
  public void listAllDrivers()
  {
    System.out.println();

    for (int i = 0; i < drivers.size(); i++)
    {
      int index = i + 1;
      System.out.printf("%-2s. ", index);
      drivers.get(i).printInfo(); //print the details of driver
      System.out.println(); 
    }
  }

  // Print Information (printInfo()) about all current service requests
  public void listAllServiceRequests()
  {
    for (int j = 0; j < 4; j++){
      System.out.println();
      System.out.println("ZONE " + j);
      System.out.println("======");
      System.out.println();

      for (int i = 0; i < serviceRequests[j].size(); i++)
      {
        int index = i + 1;
        System.out.printf("%-1s. ", index);
        System.out.print("------------------------------------------------------------");
        UberService topRequest = serviceRequests[j].poll(); //remove request from queue and store it in a variable for easier access
        topRequest.printInfo();; //print the details of request
        serviceRequests[j].offer(topRequest); //add the request back to the queue
        System.out.println();
        System.out.println();
      }
    }
  }

  // Add a new user to the system
  public void registerNewUser(String name, String address, double wallet) throws InvalidNameException, InvalidAddressException, InvalidWalletException, UserExistsException
  {
    //Check for all valid parameters
    if (name.isBlank())
    {
      throw new InvalidNameException("Invalid User Name");
    }
    
    if (!(CityMap.validAddress(address)))
    {
      throw new InvalidAddressException("Invalid User Address");
    }

    if (wallet < 0)
    {
      throw new InvalidWalletException("Invalid Money in Wallet");
    }

    //Create User object using the information entered and add it to the list of existing users
    int size = users.size();
    String id = UberRegistered.generateUserAccountId(size);
    User newUser = new User(id, name, address, wallet);
    userExists(newUser);

    users.put(id, newUser);
  }

  // Add a new driver to the system
  public void registerNewDriver(String name, String carModel, String carLicencePlate, String address) throws InvalidNameException, InvalidCarModelException, InvalidLicencePlateException, InvalidAddressException, DriverExistsException
  {
    //Check for all valid parameters
    if (name.isBlank())
    {
      throw new InvalidNameException("Invalid Driver Name");
    }

    if (carModel.isBlank())
    {
      throw new InvalidCarModelException("Invalid Car Model");
    }

    if (carLicencePlate.isBlank())
    {
      throw new InvalidLicencePlateException("Invalid Car Licence Plate");
    }

    if (!(CityMap.validAddress(address)))
    {
      throw new InvalidAddressException("Invalid Driver Address");
    }

    //Create Driver object using the information entered and add it to the list of existing drivers
    Driver newDriver = new Driver(UberRegistered.generateDriverId(drivers), name, carModel, carLicencePlate, address);
    driverExists(newDriver);

    drivers.add(newDriver);
  }

  // Request a ride. User wallet will be reduced when drop off happens
  public void requestRide(String accountId, String from, String to) throws InvalidAddressException, UserNotFoundException, InsufficientTravelDistanceException, NoDriverAvailableException, InsufficientFundsException, RideRequestExistsException
  {
    //Check for all valid parameters
    if ((!(CityMap.validAddress(from))) || (!(CityMap.validAddress(to))))
    {
      throw new InvalidAddressException("Invalid Address");
    }
    
    int rideDist = CityMap.getDistance(from, to);
    if (rideDist <= 1)
    {
      throw new InsufficientTravelDistanceException("Insufficient Travel Distance");
    }

    User myUser = getUser(accountId);
    if (myUser == null)
    {
      throw new UserNotFoundException("User Account Not Found");
    }

    //Check if the user has enough funds to cover the cost of the ride
    double price = getRideCost(rideDist);
    if (myUser.getWallet() < price)
    {
      throw new InsufficientFundsException("Insufficient Funds");
    }
    
    //Create a UberRide object using the collected information and make sure that the request does not already exist
    UberRide newRide = new UberRide(from, to, myUser, rideDist, price);
    existingRequest(newRide);

    int zone = CityMap.getCityZone(from);
    serviceRequests[zone].add(newRide);
    myUser.addRide();
  }


  // Request a food delivery. User wallet will be reduced when drop off happens
  public void requestDelivery(String accountId, String from, String to, String restaurant, String foodOrderId) throws UserNotFoundException, InvalidAddressException, NoDriverAvailableException, InsufficientFundsException, DeliveryRequestExistsException
  {
    if ((!(CityMap.validAddress(from))) || (!(CityMap.validAddress(to))))
    {
      throw new InvalidAddressException("Invalid Address");
    }

    int deliDist = CityMap.getDistance(from, to);

    User myUser = getUser(accountId);

    if (myUser == null)
    {
      throw new UserNotFoundException("User Account Not Found");
    }

    //Check if the user has enough funds to cover the cost of the delivery
    double price = getDeliveryCost(deliDist);
    if (myUser.getWallet() < price)
    {
      throw new InsufficientFundsException("Insufficient Funds");
    }

    //Create a UberDelivery object using the collected information and make sure that the request does not already exist
    UberDelivery newDelivery = new UberDelivery(from, to, myUser, deliDist, price, restaurant, foodOrderId);
    existingRequest(newDelivery);

    int zone = CityMap.getCityZone(from);
    serviceRequests[zone].add(newDelivery);
    myUser.addDelivery();
  }


  // Cancel an existing service request.
  public void cancelServiceRequest(int zone, int request) throws InvalidRequestNumberException, InvalidZoneNumberException
  {
    //Check if zone number is valid
    if (!((zone <= 3) && (zone >= 0)))
    {
      throw new InvalidZoneNumberException("Invalid Zone # " + zone);
    }

    //Check if request number is valid
    if (!((request <= serviceRequests[zone].size()) && (request > 0)))
    {
      throw new InvalidRequestNumberException("Invalid Request # " + request);
    }

    User myUser = null;
    String service = "";
    
    for (int i = 0; i < request; i++){
      UberService req = serviceRequests[zone].poll(); //Remove request from list of requests

      if((i+1) == request)
      {
        //Determine the user and the service type (ride or delivery)
        myUser = req.getUser();
        service = req.getServiceType();
      }
      else{
        serviceRequests[zone].offer(req);
      }
    }

    //Remove the ride/delivery from user's total ride/delivery count
    if (service.equals("RIDE"))
    {
      myUser.removeRide();
    }
    else{
      myUser.removeDelivery();
    }
  }
  
  // Driver picks up a request
  public void pickup(String driverAccountId) throws DriverNotFoundException, NoServiceRequestException
  {
    // Determine the driver
    Driver myDriver = null;
    for (int i = 0; i < drivers.size(); i++){
      if (((drivers.get(i)).getId()).equals(driverAccountId)){
        myDriver = drivers.get(i);
      }
    }

    //Check if driver ID number is valid
    if (myDriver == null)
    {
      throw new DriverNotFoundException("Driver Not Found");
    }

    // Determine the zone of the driver
    String address = myDriver.getAddress();
    int zone = CityMap.getCityZone(address);
    myDriver.setZone(zone);

    // Ensure that there is a request in the driver's zone and have the driver pickup the first request
    UberService req = serviceRequests[zone].poll();
    if (req == null){
      throw new NoServiceRequestException("No Service Request in Zone " + zone);
    }
    myDriver.setService(req);
    myDriver.setStatus(Driver.Status.DRIVING);

    myDriver.setAddress(req.getFrom());

    System.out.println("Driver " + driverAccountId + " Picking Up in Zone " + zone);
  }


  // Drop off a ride or a delivery. This completes a service.
  public void dropOff(String driverAccountId) throws DriverNotFoundException, InvalidDriverStatusException
  {
    // Determine the driver
    Driver myDriver = null;
    for (int i = 0; i < drivers.size(); i++){
      if (((drivers.get(i)).getId()).equals(driverAccountId)){
        myDriver = drivers.get(i);
      }
    }

    //Check if driver ID number is valid
    if (myDriver == null)
    {
      throw new DriverNotFoundException("Driver Not Found");
    }

    // Make sure that the driver has picked up a request
    if (!((myDriver.getStatus()).equals(Driver.Status.DRIVING))){
      throw new InvalidDriverStatusException("Driver is not driving");
    }

    //Determine the service type (ride or delivery) and total cost
    UberService req = myDriver.getService();
    
    String service = req.getServiceType();
    double costService = 0;
    
    if (service.equals("RIDE"))
    {
      costService = RIDERATE*(req.getDistance());
    }
    else{
      costService = DELIVERYRATE*(req.getDistance());
    }

    totalRevenue += costService; //add cost to the total revenues
    
    //Determine the driver's pay and pay the driver and set the driver's status to available
    double driverFee = PAYRATE*costService;
    myDriver.pay(driverFee);
    myDriver.setStatus(Driver.Status.AVAILABLE);
    myDriver.setAddress(req.getTo());
    myDriver.setService(null);
    int zone = CityMap.getCityZone(req.getTo());
    myDriver.setZone(zone);

    totalRevenue -= driverFee;  //decrement the revenues by the portion given to the driver

    //Determine the user and decrement their wallet by the cost of the request
    User myUser = req.getUser();
    myUser.payForService(costService);

    //Remove the request from list of requests
    for (int i = 0; i < serviceRequests[zone].size(); i++){
      UberService otherRequest = serviceRequests[zone].poll();
      if(req.equals(otherRequest)){
        break;
      }
      serviceRequests[zone].offer(otherRequest);
    }
  }

  // Have the driver driver to a specific address
  public void driveTo(String driverAccountId, String address) throws InvalidAddressException, DriverNotFoundException, InvalidDriverStatusException
  {
    // Check if address is valid
    if (!(CityMap.validAddress(address)))
    {
      throw new InvalidAddressException("Invalid Driver Address");
    }

    // Determine the driver
    Driver myDriver = null;
    for (int i = 0; i < drivers.size(); i++){
      if (((drivers.get(i)).getId()).equals(driverAccountId)){
        myDriver = drivers.get(i);
      }
    }
    
    //Check if driver ID number is valid
    if (myDriver == null)
    {
      throw new DriverNotFoundException("Driver Not Found");
    }

    // Make sure that the driver is not in the process of completing a request
    if (!((myDriver.getStatus()).equals(Driver.Status.AVAILABLE))){
      throw new InvalidDriverStatusException("Driver is not available");
    }

    // Change the driver's address and zone
    myDriver.setAddress(address);
    int zone = CityMap.getCityZone(address);
    myDriver.setZone(zone);
  }

  // Sort users by name
  // Then list all users
  public void sortByUserName()
  {
    ArrayList<User> userList = new ArrayList<User>(users.values());
    Collections.sort(userList, new NameComparator()); //sort users using NameComparator helper class
    System.out.println();
    int index = 0;
    for (User u : userList)
    {
      index++;
      System.out.printf("%-2s. ", index);
      u.printInfo(); //print the details of user
      System.out.println();
    }
  }

  // Helper class for method sortByUserName
  private class NameComparator implements Comparator<User>
  {
    public int compare(User u1, User u2)
    {
      return (u1.getName().compareTo(u2.getName())); //compare the names to see which one comes first alphabetically
    }    
  }

  // Sort users by number amount in wallet
  // Then list all users
  public void sortByWallet()
  {
    ArrayList<User> userList = new ArrayList<User>(users.values());
    Collections.sort(userList, new UserWalletComparator()); //sort users according to WalletComparator
    System.out.println();
    int index = 0;
    for (User u : userList)
    {
      index++;
      System.out.printf("%-2s. ", index);
      u.printInfo(); //print the details of user
      System.out.println();
    }
    
  }
  // Helper class for use by sortByWallet
  private class UserWalletComparator implements Comparator<User>
  {
    public int compare(User u1, User u2)
    {
      //Compare the amount of money in wallet to see which user has less (comes first)
      if(u1.getWallet() > u2.getWallet())
      {
        return 1;
      }
      else if(u1.getWallet() < u2.getWallet())
      {
        return -1;
      }
      else
      {
        return 0;
      }
    }
  }

  // Set the users into a Map
  public void setUsers(ArrayList<User> userList)
  {
    for(User u: userList)
    {
      users.put(u.getAccountId(), u);
    }
  }

  // Set the drivers
  public void setDrivers(ArrayList<Driver> drivers)
  {
    this.drivers = drivers;
  }

}


// Exceptions classes
class UserExistsException extends RuntimeException
{
  public UserExistsException(){}
  public UserExistsException(String message){
    super(message);
  }
}
class DriverExistsException extends RuntimeException
{
  public DriverExistsException(){}
  public DriverExistsException(String message){
    super(message);
  }
}
class RideRequestExistsException extends RuntimeException
{
  public RideRequestExistsException(){}
  public RideRequestExistsException(String message){
    super(message);
  }
}
class DeliveryRequestExistsException extends RuntimeException
{
  public DeliveryRequestExistsException(){}
  public DeliveryRequestExistsException(String message){
    super(message);
  }
}

class InvalidNameException extends RuntimeException
{
  public InvalidNameException(){}
  public InvalidNameException(String message){
    super(message);
  }
}
class InvalidAddressException extends RuntimeException
{
  public InvalidAddressException(){}
  public InvalidAddressException(String message){
    super(message);
  }
}
class InvalidWalletException extends RuntimeException
{
  public InvalidWalletException(){}
  public InvalidWalletException(String message){
    super(message);
  }
}
class InvalidCarModelException extends RuntimeException
{
  public InvalidCarModelException(){}
  public InvalidCarModelException(String message){
    super(message);
  }
}
class InvalidLicencePlateException extends RuntimeException
{
  public InvalidLicencePlateException(){}
  public InvalidLicencePlateException(String message){
    super(message);
  }
}

class InsufficientTravelDistanceException extends RuntimeException
{
  public InsufficientTravelDistanceException(){}
  public InsufficientTravelDistanceException(String message){
    super(message);
  }
}
class InsufficientFundsException extends RuntimeException
{
  public InsufficientFundsException(){}
  public InsufficientFundsException(String message){
    super(message);
  }
}

class NoDriverAvailableException extends RuntimeException
{
  public NoDriverAvailableException(){}
  public NoDriverAvailableException(String message){
    super(message);
  }
}

class InvalidRequestNumberException extends RuntimeException
{
  public InvalidRequestNumberException(){}
  public InvalidRequestNumberException(String message){
    super(message);
  }
}
class InvalidZoneNumberException extends RuntimeException
{
  public InvalidZoneNumberException(){}
  public InvalidZoneNumberException(String message){
    super(message);
  }
}

class UserNotFoundException extends RuntimeException
{
  public UserNotFoundException(){}
  public UserNotFoundException(String message){
    super(message);
  }
}
class DriverNotFoundException extends RuntimeException
{
  public DriverNotFoundException(){}
  public DriverNotFoundException(String message){
    super(message);
  }
}

class NoServiceRequestException extends RuntimeException
{
  public NoServiceRequestException(){}
  public NoServiceRequestException(String message){
    super(message);
  }
}

class InvalidDriverStatusException extends RuntimeException
{
  public InvalidDriverStatusException(){};
  public InvalidDriverStatusException(String message){
    super(message);
  }
}
