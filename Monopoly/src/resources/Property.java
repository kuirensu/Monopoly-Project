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

import java.util.ArrayList;
import utilities.Constants;

//made by brandon

public class Property {
	private String name;
	private int numHouses;
	private int houseCost;
	private boolean hasHotel;
	private int mortgageValue;
	private Player owner;
	private int price;
	private String group;
	private int[] rentCosts;
	boolean mortgaged;
	int boardPosition; 
	private boolean canBuild;

	public Property(String n, int p, String g, int[] costs, int hCost, int mValue, int bp)
	{
		owner = null;
		
		name = n;
		price = p;
		group = g;
		rentCosts = costs;
		houseCost = hCost;
		mortgageValue = mValue;
		mortgaged = false;
		hasHotel = false;
		numHouses = 0;
		boardPosition = bp;
		new ArrayList<Property>();
		canBuild = false;
	}
	
	public void setCanBuild(){
		canBuild = true;
	}
	
	public boolean getCanBuild(){
		return canBuild;
	}
	public Integer getBoardPosition(){
		return boardPosition;
	}
	public void addBuilding()
	{
		if(!group.equals("Utilities")&&!group.equals("Stations"))
		{
			if(numHouses == 4)
			{
				hasHotel = true;
				numHouses = 0;
			}
			else
				numHouses++;
		}
		
	}
	
	public void removeBuilding()
	{
		if(!group.equals("Utilities")&&!group.equals("Stations"))
		{
			if(hasHotel == true) {
				hasHotel = false;
				numHouses = 4;
			}
			else{
				numHouses--;
			}
		}
	}

	
	public int getRent()
	{
		if(numHouses== 0&&owner != null&&!group.equals("Utilities")&&!group.equals("Stations"))
		{
			ArrayList<Integer> locations = null;
			if(group.equals("Brown"))
				locations = Constants.group1Locations;
			else if(group.equalsIgnoreCase("light blue"))
				locations = Constants.group2Locations;
			else if(group.equalsIgnoreCase("purple"))
				locations = Constants.group3Locations;
			else if(group.equalsIgnoreCase("orange"))
				locations = Constants.group4Locations;
			else if(group.equalsIgnoreCase("red"))
				locations = Constants.group5Locations;
			else if(group.equalsIgnoreCase("yellow"))
				locations = Constants.group6Locations;
			else if(group.equalsIgnoreCase("green"))
				locations = Constants.group7Locations;	
			else if(group.equalsIgnoreCase("dark blue"))
				locations = Constants.group8Locations;
			if(owner.getProperties().containsAll(locations))
				return rentCosts[0] * 2;
		}
		if(!group.equals("Utilities")&&!group.equals("Stations"))
		{
			if(hasHotel)
				return rentCosts[4];
			return rentCosts[numHouses];
		}
		return 0;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNumHouses() {
		return numHouses;
	}

	public void setHouses(int houses) {
		this.numHouses = houses;
	}

	public int getHouseCost() {
		return houseCost;
	}
	public int getSellHouseCost(){
		return houseCost/2;
	}
	public void setHouseCost(int houseCost) {
		this.houseCost = houseCost;
	}

	public boolean getHotel() {
		return hasHotel;
	}
	
	public boolean isMortgaged()
	{
		return mortgaged;
	}
	public void setMortgaged(boolean isMortgaged){
		mortgaged = isMortgaged;
	}
	
	public int getMortgageValue() {
		return mortgageValue;
	}

	public void setMortgageValue(int mortgageValue) {
		this.mortgageValue = mortgageValue;
	}

	public Player getOwner() {
		return owner;
	}

	public void setOwner(Player owner) {
		this.owner = owner;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}
	
	public void resetProperty() {
		mortgaged = false;
		numHouses = 0;
		hasHotel = false;
		owner = null;
		canBuild = false;
	}
}
