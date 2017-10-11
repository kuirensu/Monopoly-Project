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

package client;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import resources.ChanceCardStuff;
import resources.CommunityChestStuff;
import resources.Player;
import resources.PropertiesSetUp;
import resources.Property;
import utilities.Constants;

public class MainWindow extends JFrame {

	private static final long serialVersionUID = 424155134277101665L;

	// Tracks the game state
	private ArrayList<Player> players;
	private int currentPlayer;
	
	// Control Buttons
	private JButton rollButton;
	private JButton manageBuildingsButton;
	private JButton managePropertiesButton;
	private JButton endTurnButton;
	private JButton exitButton;
	
	// Other Custom Objects For GUI
	private ProgressArea progressArea;
	private PlayerInformationGrid playerInformationGrid;
	private GameBoard gameBoard;
	private Property[] properties;
	
	// Menu option
	JMenuItem menuPlayerStats;
	
	// Tracks Rolling to Play First
	private boolean determineOrder;
	private int highRoll;
	private Vector<Integer> playersToRoll;
	private Vector<Integer> rollTies;
	
	// Debt Paying Stuff
	private boolean payingDebt;
	private boolean doubleRolledWhilePaying;
	private int debtOwed;
	private Player payingPlayer;
	private Player creditor;
	private ChanceCardStuff chanceCardHandler;
	private CommunityChestStuff comChestHandler;
	// Reference to client object for communication
	private Client client;
	private int ownedPlayer; // Index of client in players array
	private boolean gameOver = false; // keeps track if game ended for quitting.
	
	
	public MainWindow(ArrayList<Player> players, Client client) {
		super("Monopoly");
		
		// Initialize our variables
		PropertiesSetUp p = new PropertiesSetUp();
		properties = p.getProperties();
		
		this.players = players;
		this.client = client;
		
		// Set the the GUI
		initializeComponents();
		createGUI();
		addListeners();
		
		// Game starts with players rolling to see who goes first.
		progressArea.addProgress(players.get(currentPlayer).getName() + ", roll to see who goes first.\n");
	}
	
	private void initializeComponents() {
		// Give the players a starting amount of money
		for (Player p : players) {
			p.addMoney(1500);
		}
		
		// Initialize our player tracking to default values.
		currentPlayer = 0;
		ownedPlayer = client.getID();
		determineOrder = true;
		highRoll = 0;
		playersToRoll = new Vector<Integer>();
		for (int i = 1; i < players.size(); i++) {
			playersToRoll.add(i);
		}
		rollTies = new Vector<Integer>();
		
		// Initialize our various buttons.
		rollButton = new JButton("Roll Dice");
		manageBuildingsButton = new JButton("Manage Buildings");
		managePropertiesButton = new JButton("Manage Properties");
		endTurnButton = new JButton("End Turn");
		endTurnButton.setEnabled(false);
		exitButton = new JButton("Exit");
		
		// Initialize our custom Panels
		progressArea = new ProgressArea();
		playerInformationGrid = new PlayerInformationGrid(players);
		gameBoard = new GameBoard(players, properties);
		
		// Initialize menu items.
		menuPlayerStats = new JMenuItem("View Player Statistics");
		
		chanceCardHandler = new ChanceCardStuff(players, progressArea, this);
		comChestHandler = new CommunityChestStuff(players, progressArea);
	}

	private void createGUI() {
		this.setSize(1280,720);
		this.setResizable(true);
		this.setMinimumSize(getSize());
		
		// Set up the menu bar
		if (!players.get(ownedPlayer).getName().startsWith("Guest")) {
			JMenuBar menuBar = new JMenuBar();
			JMenu menu = new JMenu("Menu");
			menuBar.add(menu);
			menu.add(menuPlayerStats);
			setJMenuBar(menuBar);
		}
		
		// Use a border layout
		this.setLayout(new BorderLayout());
		this.add(gameBoard, BorderLayout.WEST);
		
		// Use GridBagLayout for remaining components
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		// Set up the teams.
		c.fill = GridBagConstraints.BOTH;
		c.weightx = .1;
		c.weighty = 1;
		c.gridheight = 2;
		c.gridwidth = 2;
		controlPanel.add(playerInformationGrid, c);
		
		// Add the column of buttons
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weighty = 0.5;
		c.gridy = 2;
		c.gridx = 0;
		controlPanel.add(rollButton, c);
		c.gridy++;
		controlPanel.add(manageBuildingsButton, c);
		c.gridy++;
		controlPanel.add(managePropertiesButton, c);
		c.gridy++;
		controlPanel.add(endTurnButton, c);
		c.gridy++;
		controlPanel.add(exitButton, c);
		
		// Add the progress area
		c.gridy = 2;
		c.gridx = 1;
		c.weightx = 1;
		c.gridheight = 5;
		controlPanel.add(progressArea, c);
		
		// Add control panel to the main board
		this.add(controlPanel, BorderLayout.CENTER);
	}
	
	private void addListeners() {
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				client.sendMessage("ExitGame");
				System.exit(0);
			}
		});
		
		// Show the player statistics Window
		menuPlayerStats.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new PlayerStatisticsWindow(players.get(ownedPlayer)).setVisible(true);;
			}
		});
		
		// Repaint the window when it is resized.
		// Froces the gameBoard to stay square
		this.addWindowStateListener(new WindowStateListener() {
			public void windowStateChanged(WindowEvent arg0) {
				gameBoard.repaint();
				repaint();
			}
		});
		
		// Have the roll button move the current player.
		rollButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (currentPlayer == ownedPlayer) rollDice();
			}	
		});
		
		// Have the End Turn button increment the current player.
		// The same button is overloaded for paying off game debt.
		endTurnButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (currentPlayer == ownedPlayer) {
					client.sendMessage("EndTurn");
					endTurnButtonPushed();
				}
			}
		});
		
		// Opens the Manage properties window when clicked
		managePropertiesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (currentPlayer == ownedPlayer) new ManagePropertiesWindow(players.get(ownedPlayer), MainWindow.this, client).setVisible(true);
			}
		});
		
		// Opens the Manage buildings window when clicked
		manageBuildingsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (currentPlayer == ownedPlayer) new ManageBuildingsWindow(players.get(ownedPlayer), MainWindow.this, client).setVisible(true);
			}
		});
		
		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				client.sendMessage("ExitGame");
				System.exit(0);
			}
		});
	}
	
	public void endTurnButtonPushed() {
		if (payingDebt) {
			payDebt();
		} else {
			endTurn();
		}
	}
		
	private void payDebt() {
		// Check if the player has gained enough money to settle debt
		if (payingPlayer.getMoney() >= 0) {
			payingPlayer = null;
			if (creditor != null) creditor.addMoney(debtOwed);
			creditor = null;
			debtOwed = 0;
			endTurnButton.setText("End Turn");
			payingDebt = false;
			
			// Remind the player to roll again now that debt is settled.
			if (doubleRolledWhilePaying) {
				endTurnButton.setEnabled(false);
				progressArea.addProgress(players.get(currentPlayer).getName() +" gets to roll again.\n");
			}
		} else {
			// Remind the player what they need to do to continue.
			if (currentPlayer == ownedPlayer) JOptionPane.showMessageDialog(null, "Need more money to repay debt.");
		}
	}
	
	private void endTurn() {
		//Find next player.
		progressArea.addProgress("\n");
		do {currentPlayer = (currentPlayer+1) % players.size();}
		while (players.get(currentPlayer).isBankrupt());
		
		playerInformationGrid.setCurrentPlayer(currentPlayer);
		playerInformationGrid.repaint();
		
		progressArea.addProgress(players.get(currentPlayer).getName() +"'s turn to go.\n");
		managePropertiesButton.setEnabled(true);
		manageBuildingsButton.setEnabled(true);
		
		// Check if the new player is in jail and handle it appropriately.
		if(players.get(currentPlayer).isInJail() == true){
			managePropertiesButton.setEnabled(false);
			manageBuildingsButton.setEnabled(false);
			rollButton.setEnabled(false);
			endTurnButton.setEnabled(false);
			if (currentPlayer == ownedPlayer) {
				InJailPopup popup = new InJailPopup();
				popup.setVisible(true);
			}
		} else {
			rollButton.setEnabled(true);
			endTurnButton.setEnabled(false);
		}
	}
	
	// Private Roll Dice used when player pushes button
	private void rollDice(){
		if (payingDebt && currentPlayer == ownedPlayer) {
			// Remind the player to not roll the dice before rolling
			JOptionPane.showMessageDialog(null, "Repay Debt Before Rolling Again");
			return;
		}

		// Get a random dice roll
		Random rand = new Random();
		int roll1 = rand.nextInt(6)+1;
		int roll2 = rand.nextInt(6)+1;

		client.sendMessage("Rolled::"+roll1+"::"+roll2);
		rollDice(roll1, roll2);
	}
	
	// Public rollDice can be called by client when game change occurs
	public void rollDice(int roll1, int roll2) {
		// Disable the button. Let the player only roll once
		rollButton.setEnabled(false);
		Player p = players.get(currentPlayer);
		if(roll1 != 0)
		progressArea.addProgress(p.getName() + " rolled a " + roll1 +
				" and a " + roll2 + ".\n");
		
		// Determine who goes first if needed
		if (determineOrder) {
			rollToDetermineOrder(roll1,roll2);
			return;
		}
		
		// Reset the double count if no doubles rolled
		if (roll1 != roll2 && p.getDoubles() > 0) {
			p.setDoubles(0);
		}
		
		// Check if we land in go to jail
		if(p.getCurrentLocation() + roll1 + roll2 % 40 == 30){
			p.setCurrentLocation(Constants.jailLocation);
			p.setInJail(true);
			progressArea.addProgress(p.getName() + " landed on go to jail, " + p.getName() + " has been sent to jail.\n");
			endTurnButton.setEnabled(true);
			repaint();
			gameBoard.repaint();
			return;
		}
		
		// If doubles are rolled in jail, remove the player from jail
		if(p.isInJail() && roll1 == roll2){
			p.setInJail(false);
			endTurnButton.setEnabled(true);
			progressArea.addProgress(p.getName() + " rolled a double and is now free! \n");
		} else if(p.isInJail()) {
			progressArea.addProgress(p.getName() + " is still stuck in jail. \n");
			endTurnButton.setEnabled(true);
			
			// Repaint the game board and update the progress area
			gameBoard.repaint();
			playerInformationGrid.repaint();
			
			progressArea.addProgress("\n");
			return;
		} else if (roll1 == roll2 && roll1 != 0) {
			// If three doubles are rolled in a row, send the player to jail
			p.setDoubles(p.getDoubles()+1);
			if (p.getDoubles() == 3) {
				p.setDoubles(0);
				p.setCurrentLocation(Constants.jailLocation);
				p.setInJail(true);
				progressArea.addProgress("   was sent to jail for rolling doubles too many times.\n\n");
				gameBoard.repaint();
				playerInformationGrid.repaint();
				endTurnButton.setEnabled(true);
				return;
			}
			// If we rolled doubles, let the player roll again. Otherwise Player may end turn
			progressArea.addProgress("    will get to roll again.\n");
		}
		
		// Move the player
		int newLocation = (p.getCurrentLocation()+roll1+roll2) % 40;
		if(p.getCurrentLocation()+roll1+roll2 >= 40)
		{
			p.addMoney(Constants.goMoney);
			progressArea.addProgress("    passed Go and collected $"+Constants.goMoney+".\n");
		}
		p.setCurrentLocation(newLocation);
		progressArea.addProgress("    landed on "+properties[newLocation].getName()+".\n");
		
		// Repaint the game board and update the progress area
		gameBoard.repaint();
		playerInformationGrid.repaint();

		// Process the new location. Receives debt if applicable
		int debt = processNewLocation(newLocation, roll1, roll2);
		
		//Determine if player went bankrupt
		checkBankruptcy(debt, roll1, roll2);

		// Repaint the game board and update the progress area
		gameBoard.repaint();
		playerInformationGrid.repaint();
		
		// If we rolled doubles, let the player roll again. Otherwise Player may end turn
		if (players.get(currentPlayer).getDoubles() > 0 && !payingDebt) {
			rollButton.setEnabled(true);
		} else {
			endTurnButton.setEnabled(true);
		}
	}
	
	private void rollToDetermineOrder(int roll1, int roll2) {
		if (roll1+roll2 == highRoll) {
			// Tied the high Roll
			rollTies.add(currentPlayer);
		} else if (roll1+roll2 > highRoll) {
			// New, lone leader in rolling
			rollTies.removeAllElements();
			rollTies.add(currentPlayer);
			highRoll = roll1+roll2;
		}
		
		// We are done rolling
		if (playersToRoll.size() == 0) {
			if (rollTies.size() > 1) {
				// If there was a tie restart the rolling process with the tied players
				for (int i = 0; i < rollTies.size(); i++) {
					playersToRoll.add(rollTies.get(i));
				}
				currentPlayer = playersToRoll.get(0);
				playersToRoll.remove(0);
				progressArea.addProgress("\nThere was a tie. "+players.get(currentPlayer).getName() + ", roll again.\n");
			} else {
				// There was no tie.
				currentPlayer = rollTies.get(0);
				progressArea.addProgress("\n"+players.get(currentPlayer).getName() + " goes first.\n");
				determineOrder = false;
				playerInformationGrid.setCurrentPlayer(currentPlayer);
				playerInformationGrid.repaint();
			}
		} else {
			// We are not done. Check which player needs to roll next
			currentPlayer = playersToRoll.get(0);
			playersToRoll.remove(0);
			progressArea.addProgress("\n"+players.get(currentPlayer).getName() + ", roll to see who goes first.\n");
		}
		rollButton.setEnabled(true);
	}
	
	private int processNewLocation(int newLocation, int roll1, int roll2) {
		// Handle Cost of new location if needed
		int debt = 0;
		// Reset the creditor when we roll.
		creditor = null;
		Player p = players.get(currentPlayer);
		if(properties[newLocation].getPrice() != 0) {
			// Landed on a property
			if (properties[newLocation].getOwner() == null) {
				// Offer the player to buy the new property if they can afford it
				if (p.getMoney() >= properties[newLocation].getPrice() && currentPlayer == ownedPlayer) {
					int n = JOptionPane.showConfirmDialog(
						    getContentPane(),
						    "Would you like to buy "+properties[newLocation].getName()+
						    " for $"+properties[newLocation].getPrice()+"?",
						    "Buy Property?",
						    JOptionPane.YES_NO_OPTION);
					if (n == 0) {
						client.sendMessage("PurchasedProperty::"+newLocation);
						buyProperty(newLocation);
					}
				}
			} else {
				// The property is owned, so check to pay rent
				if (properties[newLocation].isMortgaged() || properties[newLocation].getOwner() == p) {
					//Nothing
				} else {
					int rent = 0;
					if (properties[newLocation].getGroup().equals("Stations")) {
						rent = 25;
						for (Property property : properties[newLocation].getOwner().getProperties()) {
							if (property.getGroup().equals("Stations")) {
								rent *= 2;
							}
						}
					} else if (properties[newLocation].getGroup().equals("Utilities")) {
						int count = 0;
						for (Property property : properties[newLocation].getOwner().getProperties()) {
							if (property.getGroup().equals("Utilities")) {
								count++;
							}
						}
						if (count == 1) {
							rent = 4 * (roll1+roll2);
						} else {
							rent = 10 * (roll1+roll2);
						}
					} else {
						rent = properties[newLocation].getRent();
					}
					debt = rent;
					if (p.getMoney() < debt) {
						debt -= p.getMoney();
					} else {
						debt = 0;
					}
					creditor = properties[newLocation].getOwner();
					p.addMoney(-rent);
					properties[newLocation].getOwner().addMoney(rent-debt);
					progressArea.addProgress("    paid $"+(rent-debt)+" in rent to " + properties[newLocation].getOwner().getName() + "\n");
				}
			}
		} else {
			if (properties[newLocation].getName().equals("Chance")) {
				//TODO Deal with chance location
				chanceCardHandler.handleChance(p);
			} else if (properties[newLocation].getName().equals("Go To Jail")) {
				p.setCurrentLocation(Constants.jailLocation);
				p.setInJail(true);
			} else if (properties[newLocation].getName().equals("Community Chest")) {
				//TODO Deal with community chest
				comChestHandler.handleCommunity(p);
			} else if (properties[newLocation].getName().equals("Income Tax")) {
				/*Object[] options = {"Pay $"+Constants.incomeTax,
                "Pay 10% of Total Worth"};
				int n = JOptionPane.showOptionDialog(getContentPane(),
						"Would you like to estimate your tax at $"+Constants.incomeTax+" or pay 10% of your total worth (including cash, properties, and buildings)?",
						"Income Tax",
						JOptionPane.DEFAULT_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,     //do not use a custom Icon
						options,  //the titles of buttons
						options[0]);
				if (n == 0) {
					debt = Constants.incomeTax;
					if (p.getMoney() < debt) {
						debt -= p.getMoney();
					} else {
						debt = 0;
					}
					p.addMoney(-Constants.incomeTax);
					progressArea.addProgress("    paid $"+(Constants.incomeTax-debt)+" in tax.\n");
				} else {
					int totalWorth = p.getMoney();
					for (Property property : p.getProperties()) {
						totalWorth += property.getPrice();
						if (property.getHotel()) {
							totalWorth += 5 * property.getHouseCost();
						} else {
							totalWorth += property.getNumHouses() * property.getHouseCost();
						}
					}
					int tax = totalWorth/10;
					debt = tax;
					if (p.getMoney() < debt) {
						debt -= p.getMoney();
					} else {
						debt = 0;
					}
					p.addMoney(-tax);
					progressArea.addProgress("    paid $"+(tax-debt)+" in tax.\n");
				}*/
				// To simplify networking make the income tax choice for the player
				int flatTax = Constants.incomeTax;
				int percentageTax = p.getMoney();
				for (Property property : p.getProperties()) {
					percentageTax += property.getPrice();
					if (property.getHotel()) {
						percentageTax += 5 * property.getHouseCost();
					} else {
						percentageTax += property.getNumHouses() * property.getHouseCost();
					}
				}
				percentageTax = percentageTax/10;
				debt = (flatTax < percentageTax) ? flatTax : percentageTax;
				int tax = debt;
				if (p.getMoney() < debt) {
					debt -= p.getMoney();
				} else {
					debt = 0;
				}
				p.addMoney(-tax);
				progressArea.addProgress("    paid $"+(tax-debt)+" in tax.\n");
			} else if (properties[newLocation].getName().equals("Go")) {
				//Nothing. Go money handled above.
			} else if (properties[newLocation].getName().equals("Just Visiting")) {
				//Nothing
			} else if (properties[newLocation].getName().equals("Free Parking")) {
				//Nothing
			} else if (properties[newLocation].getName().equals("Luxury Tax")) {
				debt = Constants.luxuryTax;
				if (p.getMoney() < debt) {
					debt -= p.getMoney();
				} else {
					debt = 0;
				}
				p.addMoney(-Constants.luxuryTax);
				progressArea.addProgress("    paid $"+(Constants.luxuryTax-debt)+" in tax.\n");
			}
		}
		
		// Return the debt value.
		return debt;
	}
	
	// Public so clients can call this and stay in sync
	public void buyProperty(int location) {
		Player p = players.get(currentPlayer);
		p.addMoney(-properties[location].getPrice());
		p.addProperty(properties[location]);
		properties[location].setOwner(p);
		progressArea.addProgress("    bought "+properties[location].getName()
				+" for $"+properties[location].getPrice()+".\n");
	}
	
	private void checkBankruptcy(int debt, int roll1, int roll2) {
		Player p = players.get(currentPlayer);
		if (debt > 0) {
			// Player could not fully afford rent
			progressArea.addProgress("    still owes "+debt+" in debt.\n");
			
			// Check how much money the player could generate
			int worth = 0;
			for (Property property : p.getProperties()) {
				if (!property.isMortgaged()) {
					if (property.getHotel()) {
						worth += (5 * (property.getHouseCost()/2));
					} else {
						worth += (property.getNumHouses() * (property.getHouseCost()/2));
					}
					worth += property.getMortgageValue();
				}
			}
			
			// Can't afford to pay rent even selling
			if (worth < debt) {
				if (creditor == null) {
					for (Property property : p.getProperties()) {
						property.resetProperty();
					}
				} else {
					creditor.addMoney(worth);
					for (Property property : p.getProperties()) {
						property.resetProperty();
						property.setMortgaged(true);
						property.setOwner(creditor);
						creditor.addProperty(property);
					}
				}
				
				// The player is bankrupt now :(
				progressArea.addProgress("    is bankrupt.\n");
				players.get(currentPlayer).setBankrupt(true);
				
				// Check for game end
				if (gameIsOver()) {
					for (Player winner: players) {
						if (!winner.isBankrupt()) {
							// Display the winner
							winner.addWin();
							gameOver = true;
							new WinnerAnnouncementWindow(winner).setVisible(true);
							
							// Send the winner to the database. Only do this once from the host
							if (!winner.getName().startsWith("Guest")) {
								
								if (ownedPlayer == 0) client.sendMessage("Winner: " + winner.getName());
							}
						}
					}
				}
			} else {
				payingDebt = true;
				debtOwed = debt;
				endTurnButton.setText("Repay Debt");
				payingPlayer = players.get(currentPlayer);
				progressArea.addProgress("    needs to sell/mortgage to pay remaining debt.\n");
				endTurnButton.setEnabled(true);
				doubleRolledWhilePaying = (roll1 == roll2);
			}
		}
	}
	
	// Allows objects to print to the main window's progress area.
	public void updateProgressArea(String update){
		progressArea.addProgress(update + ".\n");
	}
	
	// Helper method to check for end of game
	private boolean gameIsOver() {
		int remainingPlayers = 0;
		
		for (Player p : players) {
			if (!p.isBankrupt()) remainingPlayers++;
		}
		
		return remainingPlayers == 1;
	}
	
	// Access Method
	public Property[] getPropertiesArray(){
		return properties;
	}
	
	// Access Method
	public ArrayList<Player> getPlayerList(){
		return players;
	}
	
	public void payBail() {
		players.get(currentPlayer).subtractMoney(50);
		players.get(currentPlayer).setInJail(false);
		progressArea.addProgress("    paid $50 to leave jail.");
		rollButton.setEnabled(false);
		endTurnButton.setEnabled(true);
	}
	
	public void useGetOutOfJailFreeCard() {
		players.get(currentPlayer).useJailCard();
		players.get(currentPlayer).setInJail(false);
		progressArea.addProgress("    used a get out of jail free card.");
		rollButton.setEnabled(false);
		endTurnButton.setEnabled(true);
	}
	
	public void quitGame() {
		if (!gameOver) {
			JOptionPane.showMessageDialog(null, "Another Player Left. Closing the Game.");
			System.exit(0);
		}
	}
	
	// Helper Class For Custom Jail Behavior
	private class InJailPopup extends JFrame {
		private static final long serialVersionUID = 8782904345955253349L;
		private JLabel inJailLabel, imageLabel;
		private JButton payButton, rollDiceButton, jailFreeButton;
		
		public InJailPopup(){
			initializeComponents();
			createGUI();
			addListeners();
		}
		
		private void initializeComponents(){
			inJailLabel = new JLabel("You are currently in jail");
			
			ImageIcon icon = new ImageIcon("images/board/jail.png");
			imageLabel = new JLabel(icon, JLabel.CENTER);
			payButton = new JButton("Pay $50");
			rollDiceButton = new JButton ("Roll Dice");
			jailFreeButton = new JButton ("Use get out of jail free card");
		}
		
		private void createGUI(){
			setSize(700, 250);
			setLocation(300,350);
			JPanel centerPanel = new JPanel();
			JPanel buttonPanel = new JPanel(new GridLayout(1,3));
			centerPanel.add(inJailLabel);
			buttonPanel.add(payButton);
			buttonPanel.add(rollDiceButton);
			buttonPanel.add(jailFreeButton);
			add(centerPanel, BorderLayout.NORTH);
			add(imageLabel, BorderLayout.CENTER);
			add(buttonPanel, BorderLayout.SOUTH);
		}
		
		private void addListeners(){
			this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
			
			payButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					// Check the player can afford to get our of jail
					if(players.get(currentPlayer).getMoney() >= 50){
						payBail();
						client.sendMessage("PayBail");
						dispose();
					}
					else{
						inJailLabel.setText("You do not have enough money to pay with, please choose another option");
					}
				}
			});
			
			rollDiceButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					// Attempt to roll the dice to leave jail
					rollDice();
					dispose();
				}
			});
			
			jailFreeButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					// Attempt to use a get out of jail free card
					if(players.get(currentPlayer).getJailCards() > 0){
						useGetOutOfJailFreeCard();
						client.sendMessage("useGetOutOfJailFreeCard");
						dispose();
					}
					else{
						inJailLabel.setText("You do not have any get out of jail free cards, please choose another option");
					}
				}
			});
		}
	}
}
