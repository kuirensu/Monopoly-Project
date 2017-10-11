package resources;
/**
 * CSCI 201 Final Project
 * Group 14:
 *                 Monopoly
 * Team Members:
 *                 Matthew van Niekerk
 *                 Jesse Werner
 *                 Brandon Ho
 *                 Nicholas Terrile
 *                 Kuiren "James" Su
 *                 Chin-Yuan "Jeffrey" Hsu
 */


import java.util.ArrayList;

import client.ProgressArea;
import utilities.Constants;
//made by brandon
public class CommunityChestStuff 
{
	private int deckPosition;
	private ArrayList<Player> players;
	private ProgressArea progress;
	
	public CommunityChestStuff(ArrayList<Player> woo, ProgressArea lesgo)
	{
		deckPosition = 9;
		players = woo;
		progress = lesgo;
	}

	public void handleCommunity(Player p)
	{
		progress.addProgress(p.getName() + " has drawn a community chest card that reads: ");
		if(deckPosition == 17)
			deckPosition = 0;
		
		if(deckPosition == 0)
		{
			progress.addProgress("Advance to Go \n");
			p.setCurrentLocation(0);
			p.addMoney(Constants.goMoney);
		}
		else if(deckPosition == 1)
		{
			progress.addProgress("Bank error in your favor - Collect 200\n");
			p.addMoney(200);
		}
		else if(deckPosition == 2)
		{
			progress.addProgress("Doctor's Fee - Pay $50");
			p.addMoney(-50);
		}
		else if(deckPosition == 3)
		{
			progress.addProgress("Get out of Jail Free Card!\n");
			p.setJailCards(p.getJailCards() + 1);
		}
		else if(deckPosition == 4)
		{
			progress.addProgress("Go To Jail\n");
			p.setCurrentLocation(10);
			p.setInJail(true);
		}
		else if(deckPosition == 5)
		{
			progress.addProgress("It's your birthday - Collect $10 from each player\n");
			for(Player aa : players)
			{
				if(!aa.getName().equals(p.getName()))
				{
					aa.addMoney(-10);
					p.addMoney(10);
				}
			}
		}
		else if(deckPosition == 6)
		{
			progress.addProgress("Grand Opera Opening - Collect $50 from each player for opening night seats\n");
			for(Player aa : players)
			{
				if(!aa.getName().equals(p.getName()))
				{
					aa.addMoney(-50);
					p.addMoney(50);
				}
			}
		}
		else if(deckPosition == 7)
		{
			progress.addProgress("Income Tax refund - Collect $20");
			p.addMoney(20);
		}
		else if(deckPosition == 8)
		{
			progress.addProgress("Life insurance matures - Collect $100");
			p.addMoney(100);
		}
		else if(deckPosition == 9)
		{
			progress.addProgress("Pay Hospital $100\n");
			p.addMoney(-100);
		}
		else if(deckPosition == 10)
		{
			progress.addProgress("Pay school tax of $150\n");
			p.addMoney(-50);
		}
		else if(deckPosition == 11)
		{
			progress.addProgress("Receive $25 consultancy fee\n");
			p.addMoney(25);
		}
		else if(deckPosition == 12)
		{
			progress.addProgress("You are assessed for street repairs - $40 per house, $115 per hotel\n");
			int owed = 0;
			for(Property a : p.getProperties())
			{
				if(a.getHotel())
					owed += 115;
				else
				{
					owed += a.getNumHouses() * 40;
				}
			}
			p.addMoney(-owed);
		}
		else if(deckPosition == 13)
		{
			progress.addProgress("You have won second prize in a beauty contest \n");
			p.addMoney(10);
		}
		else if(deckPosition == 14)
		{
			progress.addProgress("You inherit $100\n");
			p.addMoney(100);
		}
		else if(deckPosition == 15)
		{
			progress.addProgress("From sale of stock you get $45\n");
			p.addMoney(45);
		}
		else if(deckPosition == 16)
		{
			progress.addProgress("Holiday fund matures - Receive $100\n");
			p.addMoney(100);
		}
		deckPosition++;
	}
	
}
