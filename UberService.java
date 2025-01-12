// General class that simulates a ride or a delivery in a simple Uber app

abstract public class UberService implements Comparable<UberService>
{
  private Driver driver;   
  private String from;
  private String to;
  private User user;
  private String type;  // Currently Ride or Delivery but other services could be added      
  private int distance; // Units are City Blocks
  private double cost;  // Cost of the service
  
  public UberService(String from, String to, User user, int distance, double cost, String type)
  {
    this.from = from;
    this.to = to;
    this.user = user;
    this.distance = distance;
    this.cost = cost;
    this.type = type;
  }

  // Subclasses define their type (e.g. "RIDE" OR "DELIVERY") 
  abstract public String getServiceType();

  // Getters and Setters
  public Driver getDriver()
  {
    return driver;
  }
  public void setDriver(Driver driver)
  {
    this.driver = driver;
  }
  public String getFrom()
  {
    return from;
  }
  public void setFrom(String from)
  {
    this.from = from;
  }
  public String getTo()
  {
    return to;
  }
  public void setTo(String to)
  {
    this.to = to;
  }
  public User getUser()
  {
    return user;
  }
  public void setUser(User user)
  {
    this.user = user;
  }
  public int getDistance()
  {
    return distance;
  }
  public void setDistance(int distance)
  {
    this.distance = distance;
  }
  public double getCost()
  {
    return cost;
  }
  public void setCost(double cost)
  {
    this.cost = cost;
  }

  // Compare 2 service requests based on distance
  // Add the appropriate method
  public boolean isLarger(Object other)
  {
    UberService otherRequest = (UberService) other; //Cast the "Object other" parameter to UberService object
    
    if (this.getDistance() >= otherRequest.getDistance())
    {
      return true;  //if this request is greater in distance, return true
    }
    return false;
  }

  // Check if 2 service requests are equal (this and other)
  // They are equal if its the same type and the same user
  // Make sure to check the type first
  public boolean equals(Object other)
  {
    // Fill in the code
    UberService otherService = (UberService) other; //Cast the "Object other" parameter to UberService object

    if ((this.getServiceType()).equals(otherService.getServiceType()))
    {
      if ((this.getUser()).equals(otherService.getUser()))
      {
        return true; //if both requests are of the same type and for the same user, return true
      }
    }
    return false;
  }
  
  // Print Information 
  public void printInfo()
  {
    System.out.printf("\nType: %-9s From: %-15s To: %-15s", type, from, to);
    System.out.print("\nUser: ");
    user.printInfo();
  }

  //Comparable for sorting requests by distance (used in UberSystemManager)
  public int compareTo(UberService s)
  {
    if(this.equals(s))
    {
      return 0;
    }
    else if(this.isLarger(s))
    {
      return 1;
    }
    else
    {
      return -1;
    }
  }
}
