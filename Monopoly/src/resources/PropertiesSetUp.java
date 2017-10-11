/**
 * CSCI 201 Final Project
 * Group 14:
 * 				Monopoly
 * Team Members:
 * 				Matthew van Niekerk
 * 				Jesse Werner
 * 				Brandon Ho
 * 				Nicholas Terrile
 * 				Kuiren "James" Su
 * 				Chin-Yuan "Jeffrey" Hsu
 */

package resources;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

//made by brandon
public class PropertiesSetUp 
{
	private Property[] allProperties;
	public PropertiesSetUp()
	{
	//System.out.println("here");
			BufferedReader br = null;
			Property[] properties = new Property[40];
			try
			{
				//System.out.println("what");
				//System.out.println(new File(".").getAbsoluteFile());
				br = new BufferedReader(new FileReader("src/resources/Properties.txt"));
				int count = 0;
				//System.out.println("interesting");
			
				String line = br.readLine();
				while (line != null) 
				{
					line = line.trim();
					int[] rentPrices = new int[5];
				
					StringTokenizer st = new StringTokenizer(line, "|");
					String name = st.nextToken();
					String group = st.nextToken();
					int price = Integer.parseInt(st.nextToken());
					String rents = st.nextToken();
					StringTokenizer rt = new StringTokenizer(rents, ",");
					for(int i = 0; i < 5; i++)
					{
						rentPrices[i] = Integer.parseInt(rt.nextToken());
					}
					int mValue = Integer.parseInt(st.nextToken());
					int houseCost = Integer.parseInt(st.nextToken());
					int bp = Integer.parseInt(st.nextToken());
					properties[count] = new Property(name, price, group, rentPrices, houseCost, mValue, bp);
					
					// Printing out was only for debugging.
					//System.out.println("Property name: " + name + ", Property Price: " + price + ", Property Color: " 
					//		+ group);
					count++;
					line = br.readLine();
				}
			}
			catch (FileNotFoundException fnfe) 
			{
				System.out.println("file not found");
			} 
			catch (IOException ioe) 
			{
				System.out.println("error");
			} 
			finally 
			{
				if (br != null) 
				{
					try 
					{
						br.close();
					} 
					catch (IOException ioe1) 
					{
					}
				}
			}
			
			allProperties = properties;
		}
	
	public Property[] getProperties()
	{
		return allProperties;
	}
	

}
