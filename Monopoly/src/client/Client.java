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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import resources.LoginInfo;
import resources.Player;
import resources.Property;
/*-----------------------------------------
 * Author: James Su
 * 
 * 
 * 
 * 
 */
public class Client extends Thread{
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private Socket s = null;
	private int thisPlayerID = -1;
	private String thisPlayerName; //set to login user name or guest name when assigned 
	//private ArrayList<String> teamNames; 
	private ArrayList<Player> playerList;
	private LoginWindow loginWindow;
	private StartWindow startWindow;
	private MainWindow mainWindow;
	public Client() {
		new IpAndPortPane();
		
	}
	public void sendMessage(String message) {
		try {
			oos.writeObject(message);
			oos.flush();
		} catch (IOException ioe) {
			System.out.println("ioe sending message: " + ioe.getMessage());
		}
	}
	
	//send login information to server, whether an existing user or new registered user
	public void sendLoginInfo(LoginInfo loginInfo){
		try {
			oos.writeObject(loginInfo);
			oos.flush();
		} catch (IOException ioe) {
			System.out.println("ioe sending message: " + ioe.getMessage());
		}
	}
	
	//stop listening to server
	public void endClient(){
		try {
			if (s != null) {
				s.close();
			}
		} catch (IOException ioe) {
			System.out.println("ioe in end: " + ioe.getMessage());
		}
	
	}
	@SuppressWarnings("unchecked")
	public void run() {
		try {
			while(true) {
				Object obj = (Object ) ois.readObject();
				if(obj instanceof String){
					String message = (String)obj;
					System.out.println("Got message: " + message);
					interpretMessage(message);
					//oos.writeObject("OK");
					//oos.flush();
				}else if (obj instanceof ArrayList<?>){//checking what arraylist contains, if contain strings then its otherplayerInfo, else is playerlist
					if(((ArrayList<?>)obj).size()!=0){
						if(((ArrayList<?>)obj).get(0) instanceof String){ //after login success, server send otherplayerInfo to consctuct a startwindow
							ArrayList<String> otherPlayerInfo = (ArrayList<String>)obj; //pass to startwindow to initialize it 
							loginWindow.setVisible(false);
							startWindow = new StartWindow(thisPlayerName, otherPlayerInfo, this);
							startWindow.setVisible(true);
						}else if(((ArrayList<?>)obj).get(0) instanceof Player){
							playerList = (ArrayList<Player>)obj; //send array of players maybe??
							synchronized(playerList){
								for (Player p : playerList) {
									System.out.println("Players' names are: " + p.getName());
								}
							}
							mainWindow = new MainWindow(playerList, this);
							mainWindow.setVisible(true);
							startWindow.dispose();
							//start main game gui
						}
					}else{//this player is the first player who logged in, button in startwindow should be set to start instead of ready
						loginWindow.setVisible(false);
						startWindow = new StartWindow(thisPlayerName, null, this);
						startWindow.setVisible(true);
						
					}
				}
			}
		} catch (ClassNotFoundException cnfe) {
			System.out.println("cnfe: " + cnfe.getMessage());
		} catch (IOException ioe) {
			System.out.println("ioe in run: " + ioe.getMessage());
		}
	}
	private void interpretMessage(String message){
		if(message.contains("Login success: ")){
			System.out.println(message);
			loginWindow.setAlertLabel(message);
			message = message.replace("Login success: ", "");
			message = message.trim();
			thisPlayerName = message; //set this player's name 
		}else if(message.contains("Login deny")){
			System.out.println(message);
			loginWindow.setAlertLabel(message);
		}else if(message.contains("Creating account success")){
			System.out.println(message);
			loginWindow.setAlertLabel(message);
		}else if(message.contains("Creating account deny")){
			System.out.println(message);
			loginWindow.setAlertLabel(message);
		}else if(message.contains("Guest name: ")){ //guest doesn't have a name so we give them one
			message = message.replace("Guest name: ", "");
			message = message.trim();
			thisPlayerName = message;
		}else if (message.contains("Update ID: ")){
			message = message.replace("Update ID:", "");
			message = message.trim();
			int id = Integer.parseInt(message);
			thisPlayerID = id;
		}
		else if(message.contains("Guest Login: ") && startWindow != null){
			message = message.replace("Guest Login: ", "");
			message = message.trim();
			String guestName = message;
			startWindow.userJoined(guestName);
			//do something after a Guest logs in 
		}else if(message.contains("User Login: ") && startWindow != null){
			message = message.replace("User Login: ", "");
			message = message.trim();
			String username = message;
			startWindow.userJoined(username);
			//do something after a User logs in 
		}else if(message.contains("::Picked Token::") && startWindow != null){ //ClientName::Picked Token::TokenID
			String[] command = message.split("::");
			String clientName = command[0];
			int tokenID = Integer.parseInt(command[2]);
			startWindow.refreshPlayer(clientName, tokenID);
			startWindow.setTokenButtons();
			//do something after the client picked a token
			//let other players know
			//ready to start unless quit
		}else if(message.contains("Ready::") && startWindow != null){
			String[] command = message.split("::");
			String clientName = command[1];
			if(thisPlayerID == 0){
				//if this client is host do sth
				startWindow.addReadyPlayer(clientName);
				startWindow.checkReady();
			}else{
				System.out.println(message);
			}
		}else if(message.contains("EndTurn") && mainWindow != null ){
			// End turn message only states end turn.
			// No other information sent in message
			mainWindow.endTurnButtonPushed();
		}else if(message.startsWith("ExitGame") && mainWindow != null ) {
			// A player has left. Close game
			mainWindow.quitGame();
		}else if(message.contains("Rolled::") && mainWindow != null){
			// Each client receives which dice the active player rolled
			String[] command = message.split("::");
			int roll1 = Integer.parseInt(command[1]);
			int roll2 = Integer.parseInt(command[2]);
			mainWindow.rollDice(roll1, roll2);
		}else if(message.contains("PurchasedProperty::") && mainWindow != null){
			// Active player buys property at location specified
			String[] command = message.split("::");
			int propertyLocation = Integer.parseInt(command[1]);
			mainWindow.buyProperty(propertyLocation);
		}else if(message.contains("PayBail") && mainWindow != null){
			// Active player pays bail to leave jail
			mainWindow.payBail();
		}else if(message.contains("useGetOutOfJailFreeCard") && mainWindow != null){
			// Active player uses a get out of jail free card to leave jail
			mainWindow.useGetOutOfJailFreeCard();
		}else if(message.contains("::PurchasedHouse::") && mainWindow != null){
			// A player purchases a house
			String[] command = message.split("::");
			int clientID = Integer.parseInt(command[0]);
			int propertyID = Integer.parseInt(command[2]); //house on the property
			Player currentPlayer = mainWindow.getPlayerList().get(clientID);
			Property property = mainWindow.getPropertiesArray()[propertyID];
			property.addBuilding();
			currentPlayer.subtractMoney(property.getHouseCost());
			mainWindow.updateProgressArea(currentPlayer.getName() + " built a house on " + property.getName()); 
			mainWindow.updateProgressArea("Total number of houses on " + property.getName() + ": " + property.getNumHouses());
			mainWindow.repaint();
		}else if(message.contains("::MortgagedProperty::") && mainWindow != null){
			// A player mortgages a property
			String[] command = message.split("::");
			int clientID = Integer.parseInt(command[0]);
			int propertyID = Integer.parseInt(command[2]);
			mainWindow.getPropertiesArray()[propertyID].setMortgaged(true);
			mainWindow.getPlayerList().get(clientID).addMoney(mainWindow.getPropertiesArray()[propertyID].getMortgageValue());
			mainWindow.updateProgressArea(mainWindow.getPlayerList().get(clientID).getName() +
					" mortgaged " + mainWindow.getPropertiesArray()[propertyID].getName());
		}else if(message.contains("::ReclaimedProperty::") && mainWindow != null){
			// A player reclaims their property
			String[] command = message.split("::");
			int clientID = Integer.parseInt(command[0]);
			int propertyID = Integer.parseInt(command[2]);
			mainWindow.getPropertiesArray()[propertyID].setMortgaged(false);
			mainWindow.getPlayerList().get(clientID).subtractMoney(mainWindow.getPropertiesArray()[propertyID].getMortgageValue());
			mainWindow.updateProgressArea(mainWindow.getPlayerList().get(clientID-1).getName() +
										" reclaimed " + mainWindow.getPropertiesArray()[propertyID].getName());
		}else if(message.contains("::MortgagedHouse::") && mainWindow != null){
			// A player sells a building on their property
			String[] command = message.split("::");
			int clientID = Integer.parseInt(command[0]);
			int propertyID = Integer.parseInt(command[2]); //house on the property
			Player currentPlayer = mainWindow.getPlayerList().get(clientID-1);
			Property property = mainWindow.getPropertiesArray()[propertyID];
			property.removeBuilding();
			currentPlayer.addMoney(property.getSellHouseCost());
			mainWindow.updateProgressArea(currentPlayer.getName() + " sold a house on " + property.getName()); 
			mainWindow.updateProgressArea("Total number of houses on " + property.getName() + ": " + property.getNumHouses());
			mainWindow.repaint();
		}else if(message.contains("You are connected")){
			System.out.println("You are connected");
		}else if(message.contains("Client Logout: ")){
			message = message.replace("Client Logout: ", "");
			message = message.trim();
			String username = message;
			if(startWindow != null){// a client in startwindow logs out 
				startWindow.userLeft(username);
				
			}
		}else if(message.contains("Host Logout: ")){
			if(startWindow != null){// a client in startwindow logs out 
				Object[] options = {"OK"};
			    int n = JOptionPane.showOptionDialog(startWindow,
			                   "Host exited the game ","Message",
			                   JOptionPane.PLAIN_MESSAGE,
			                   JOptionPane.QUESTION_MESSAGE,
			                   null,
			                   options,
			                   options[0]);
			    if(n==0){
			    	loginWindow.setVisible(true);
			    	startWindow.dispose();
			    	startWindow = null;
			    	thisPlayerID = -1;
			    }
	
			}
		}else if (message.contains("ExitGame")){
			if(mainWindow != null){
				Object[] options = {"OK"};
			    int n = JOptionPane.showOptionDialog(mainWindow,
			                   "Sorry, someone exited the game. The game will be terminate","Message",
			                   JOptionPane.PLAIN_MESSAGE,
			                   JOptionPane.QUESTION_MESSAGE,
			                   null,
			                   options,
			                   options[0]);
			    if(n==0){
			    	System.exit(0);
			    }
			}
			
		}
	}
	
	public int getID() {
		return thisPlayerID;
	}
	
	
	public void setLoginWindow(LoginWindow loginWindow ){
		this.loginWindow = loginWindow;
	}
	public boolean ableToStart(){
		return (s!=null);
	}
	
	private class IpAndPortPane {
	   public IpAndPortPane() {
	      JTextField xField = new JTextField(5);
	      JTextField yField = new JTextField(5);

	      JPanel myPanel = new JPanel();
	      myPanel.add(new JLabel("IP:"));
	      myPanel.add(xField);
	      myPanel.add(Box.createHorizontalStrut(15)); // a spacer
	      myPanel.add(new JLabel("Port:"));
	      myPanel.add(yField);

	      int result = JOptionPane.showConfirmDialog(null, myPanel, 
	               "Please Enter IP and Port Values", JOptionPane.OK_CANCEL_OPTION);
	      if (result == JOptionPane.OK_OPTION) {
	         System.out.println("Ip: " + xField.getText());
	         System.out.println("Port: " + yField.getText());
	         
	         try {
	 			//teamNames = new ArrayList<String>();
	 			
	 			s = new Socket(xField.getText(), Integer.parseInt(yField.getText()));
	 			oos = new ObjectOutputStream(s.getOutputStream());
	 			ois = new ObjectInputStream(s.getInputStream());
	 			System.out.println("client constructed");

	 			
	 		} catch (IOException ioe) {
	 			System.out.println("ioe construct: " + ioe.getMessage());
	 			s = null;
	 		}
	      }
	   }
	   
	}
	
	public static void main(String args[]){
		
		
		Client client = new Client();
		if(client.ableToStart()){
			client.start();
			new LoginWindow(client).setVisible(true);
		}else{
			System.out.println("Unable to connect, self terminate");
			return;
		}
		
	}
	
}
