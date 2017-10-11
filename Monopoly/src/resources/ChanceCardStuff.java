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

import client.MainWindow;
import client.ProgressArea;
import utilities.Constants;
//made by brandon
public class ChanceCardStuff 
{
	
	private int deckPosition;
	private ArrayList<Player> players;
	private ProgressArea progress;
	private MainWindow mw;
	
	public ChanceCardStuff(ArrayList<Player> woo, ProgressArea lesgo, MainWindow m)
	{
		deckPosition = 0;
		players = woo;
		progress = lesgo;
		mw = m;
	}

	public void handleChance(Player p)
	{
		progress.addProgress(p.getName() + " has drawn a chance card that reads: ");
		if(deckPosition == 17)
			deckPosition = 0;
		
		if(deckPosition == 0)
		{
			progress.addProgress("Advance to Go\n");
			p.addMoney(Constants.goMoney);
			p.setCurrentLocation(0);
		}
		else if(deckPosition == 1)
		{
			progress.addProgress("Advance to Illinois Ave.\n");
			if(p.getCurrentLocation() > 24)
				p.addMoney(Constants.goMoney);
			p.setCurrentLocation(24);
			mw.rollDice(0,0);
		}
		else if(deckPosition == 2)
		{
			progress.addProgress("Advance to nearest Utility. If unowned, you may buy it from the Bank. If owned, throw dice and ay owner ten times the amount thrown.\n");
			if(p.getCurrentLocation() >= 12 && p.getCurrentLocation() < 28)
			{
				p.setCurrentLocation(28);
			}
			else if(p.getCurrentLocation() <= 39)
			{
				p.setCurrentLocation(12);
				p.addMoney(Constants.goMoney);
			}
			else
				p.setCurrentLocation(12);
			mw.rollDice(0,0);
		}
		else if(deckPosition == 3 || deckPosition == 12)
		{
			//5, 15, 25, 35
			progress.addProgress("Advance to the nearest Railroad and pay owner twice the rental to which he/she is otherwise entitled. If Railroad is unowned, you may buy it from the Bank.");
			if(p.getCurrentLocation() < 5 )
			{
				p.setCurrentLocation(5);
			}
			else if(p.getCurrentLocation() > 35)
			{
				p.setCurrentLocation(5);
				p.addMoney(Constants.goMoney);
			}
			else if(p.getCurrentLocation() < 15)
			{
				p.setCurrentLocation(15);
			}
			else if(p.getCurrentLocation() < 25)
			{
				p.setCurrentLocation(25);
			}
			else 
			{
				p.setCurrentLocation(35);
			}
			mw.rollDice(0,0);
		}
		else if(deckPosition == 4)
		{
			progress.addProgress("Advance to St. Charles Place\n");
			if(p.getCurrentLocation() > 11)
			{
				p.addMoney(Constants.goMoney);
			}
		
			p.setCurrentLocation(11);
			
			mw.rollDice(0,0);
		}
		else if(deckPosition == 5)
		{
			progress.addProgress("Bank pays you dividend of $50\n");
			p.addMoney(50);
		}
		else if(deckPosition == 6)
		{
			progress.addProgress("Get out of Jail free Card!\n");
			p.setJailCards(p.getJailCards() + 1);
		}
		else if(deckPosition == 7)
		{
			progress.addProgress("Go back 3 spaces\n");
			if(p.getCurrentLocation() >= 3)
			{
				p.setCurrentLocation(p.getCurrentLocation() - 3);
				mw.rollDice(0, 0);
			}
			else
			{
				if(p.getCurrentLocation() == 2)
					p.setCurrentLocation(39);
				else if(p.getCurrentLocation() == 1)
					p.setCurrentLocation(38);
				else if(p.getCurrentLocation() == 0)
					p.setCurrentLocation(37);
				mw.rollDice(0,0);
			}
			
		}
		else if(deckPosition == 8)
		{
			progress.addProgress("Go Directly to Jail\n");
			p.setCurrentLocation(10);
			p.setInJail(true);
			mw.rollDice(0,0);
		}
		else if(deckPosition == 9)
		{
			progress.addProgress("Make general repairs on all your property - for each house pay $25, for each hotel pay $100\n");
			int owed = 0;
			for(Property a : p.getProperties())
			{
				if(a.getHotel())
					owed += 100;
				else
				{
					owed += a.getNumHouses() * 25;
				}
			}
			p.addMoney(-owed);
		}
		else if(deckPosition == 10)
		{
			progress.addProgress("Pay poor tax of $15\n");
			p.addMoney(-15);
		}
		else if(deckPosition == 11)
		{
			progress.addProgress("Take a trip to Reading Railroad - If you pass Go collect $200\n");
			if(p.getCurrentLocation() >= 5)
			{
				p.addMoney(Constants.goMoney);
			}
			p.setCurrentLocation(5);
			mw.rollDice(0,0);
		}
		else if(deckPosition == 16)
		{
			progress.addProgress("Take a walk on the boardwalk\n");
			p.setCurrentLocation(39);
			mw.rollDice(0,0);
		}
		else if(deckPosition == 13) //pay each player 50
		{
			progress.addProgress("You have been elected chairman of the  board - pay each player $50");
			for(Player aa : players)
			{
				if(!aa.getName().equals(p.getName()))
				{
					aa.addMoney(50);
					p.addMoney(-50);
				}
			}
		}
		else if(deckPosition == 14)
		{
			progress.addProgress("Your building loan matures\n");
			p.addMoney(150);
		}
		else if(deckPosition == 15)
		{
			progress.addProgress("You have won a crossword competition\n");
			p.addMoney(100);
		}
		
		deckPosition++;
	}
}
