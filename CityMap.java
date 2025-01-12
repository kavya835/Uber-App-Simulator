// The city consists of a grid of 9 X 9 City Blocks

// Streets are east-west (1st street to 9th street)
// Avenues are north-south (1st avenue to 9th avenue)

// Example 1 of Interpreting an address:  "34 4th Street"
// A valid address *always* has 3 parts.
// Part 1: Street/Avenue residence numbers are always 2 digits (e.g. 34).
// Part 2: Must be 'n'th or 1st or 2nd or 3rd (e.g. where n => 1...9)
// Part 3: Must be "Street" or "Avenue" (case insensitive)

// Use the first digit of the residence number (e.g. 3 of the number 34) to determine the avenue.
// For distance calculation you need to identify the the specific city block - in this example 
// it is city block (3, 4) (3rd avenue and 4th street)

// Example 2 of Interpreting an address:  "51 7th Avenue"
// Use the first digit of the residence number (i.e. 5 of the number 51) to determine street.
// For distance calculation you need to identify the the specific city block - 
// in this example it is city block (7, 5) (7th avenue and 5th street)
//
// Distance in city blocks between (3, 4) and (7, 5) is then == 5 city blocks
// i.e. (7 - 3) + (5 - 4) 

import java.util.Arrays;
import java.util.Scanner;

public class CityMap
{
  // Checks for string consisting of all digits
  // An easier solution would use String method matches()
  private static boolean allDigits(String s)
  {
    for (int i = 0; i < s.length(); i++)
      if (!Character.isDigit(s.charAt(i)))
        return false;
    return  true;
  }

  // Get all parts of address string
  private static String[] getParts(String address)
  {
    String parts[] = new String[3];
    
    if (address == null || address.length() == 0)
    {
      parts = new String[0];
      return parts;
    }
    int numParts = 0;
    Scanner sc = new Scanner(address);  //get the address from user
    while (sc.hasNext())
    {
      if (numParts >= 3)
        parts = Arrays.copyOf(parts, parts.length+1);

      parts[numParts] = sc.next();  //array holding each part of the address
      numParts++;
    }
    if (numParts == 1)
      parts = Arrays.copyOf(parts, 1);
    else if (numParts == 2)
      parts = Arrays.copyOf(parts, 2);
    return parts;
  }

  // Checks for a valid address
  public static boolean validAddress(String address)
  {    
    String splitAddress[] = getParts(address);  //split the address and store the array in a variable
    
    if(splitAddress.length != 3){   //if address does not have 3 parts, it is invalid
      return false;
    }
    if(!(allDigits(splitAddress[0]))){  //the first part of the address must be all digits
      return false;
    }
    if(!((splitAddress[0]).length() == 2)){ //the first part should include 2 digits
      return false;
    }
    if(!(((splitAddress[0].charAt(0)) >= '1') && ((splitAddress[0].charAt(0)) <= '9'))){ //the first digit of the part should be between 1 and 9 inclusive
      return false;
    }
    String aveOrStr = splitAddress[2].toLowerCase();  //the ending of the address is case insensitive
    if(!((aveOrStr.equals("avenue"))||(aveOrStr.equals("street")))){  //the last part of the address should say "street" or "avenue"
      return false;
    }
    String nth = splitAddress[1];
    boolean firstToThird = (nth.equals("1st"))||(nth.equals("2nd"))||(nth.equals("3rd"));   //check if the second part of the address says "1st", "2nd" or "3rd"
    boolean fourthToNinth = (nth.substring(1).equals("th")) && ((nth.charAt(0) >= '1')&&(nth.charAt(0) <= '9'));  //check if the address says an n-th number
    if(!(firstToThird || fourthToNinth)){   //if the second part of the address does not have a 1-st to 9-th number, it is invalid
      return false;
    }
    return true;
  }

  // Computes the city block coordinates from an address string
  public static int[] getCityBlock(String address)
  {
    int[] block = {-1, -1};
  
    String splitAddress[] = getParts(address);  //split the address and store the array in a variable

    //Determine which part of the address refers to the street and which on refers to the avenue
    if((splitAddress[2].toLowerCase()).equals("avenue")){
      block[0] = Character.getNumericValue(splitAddress[1].charAt(0));
      block[1] = Character.getNumericValue(splitAddress[0].charAt(0));
    }
    else{
      block[1] = Character.getNumericValue(splitAddress[1].charAt(0));
      block[0] = Character.getNumericValue(splitAddress[0].charAt(0));
    }

    return block;
  }
  
  // Calculates the distance in city blocks between the 'from' address and 'to' address
  public static int getDistance(String from, String to)
  {
    if(!(validAddress(from)))
    {
      return 0;
    }
    else if(!(validAddress(to)))
    {
      return 0;
    }
    
    //Get the address and store it in two arrays
    int[] fromBlock = getCityBlock(from);
    int[] toBlock = getCityBlock(to);

    //Find the distance between the two addresses
    int x = Math.abs(fromBlock[0] - toBlock[0]);
    int y = Math.abs(fromBlock[1] - toBlock[1]);
    int dist = x + y;
    return (dist);
  }

  // Determines the zone of the address
  public static int getCityZone(String address)
  {
    // Check to see if it is a valid address
    if(!(validAddress(address)))
    {
      return -1;
    }

    int[] block = getCityBlock(address);

    // Determine the zone based on the city block
    if((block[0] >= 1) && (block[0] <= 5))
    {
      if((block[1] >= 1) && (block[1] <= 5))
      {
        return 3;
      }
      else if((block[1] >= 6) && (block[1] <= 9))
      {
        return 0;
      }
    }
    else if((block[0] >= 6) && (block[0] <= 9))
    {
      if((block[1] >= 1) && (block[1] <= 5))
      {
        return 2;
      }
      else if((block[1] >= 6) && (block[1] <= 9))
      {
        return 1;
      }
    }
    return -1; //if zone cannot be found, return -1
  }
}
