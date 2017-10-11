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

package server;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import resources.JDBCDriver;
import resources.Player;
/*-----------------------------------------
 * Author: James Su
 * 
 * 
 * 
 * 
 */
public class Server extends Thread{
	private ServerSocket ss = null;
	private boolean stop;  //when set to true stop running thread, stop accepting more players   
	private ArrayList<ServerThread> serverThreads; 
	private ArrayList<ServerThread> actualPlayerTheads; //keep track of player who actually logs in order
	private JDBCDriver jDBCDriver;
	private ArrayList<Player> players;
	 //who logged in, what token they picked
	private int numberOfGuests = 1; //used to assign guest name 
	public Server(){
		new PortPane();
		
	}
	//send message to all other clients except the one who is doing the action
	public void sendMessageToAllOtherClients(String message, ServerThread serverThread) {
		synchronized(actualPlayerTheads){
			for (ServerThread st : actualPlayerTheads) {
				if(st !=serverThread ){
					st.sendMessage(message);				
				}
			}
		}
		
	}
	public void sendPlayersToClients() {
		synchronized(actualPlayerTheads){//construct the actual list of player once the game starts
			for (int i = 0; i < actualPlayerTheads.size(); i++) {
				ServerThread st = actualPlayerTheads.get(i);
				String playername = st.getClientName();
				Player newPlayer = new Player(playername, getWins(playername), getNumberOfGames(playername), st.getServerThreadID());
				newPlayer.setGameToken(st.getTokenPicked());
				players.add(newPlayer);
			}
		}
		synchronized(actualPlayerTheads){
			for (ServerThread st : actualPlayerTheads) {
				st.sendPlayers(players);
			}
		}
		
	}
	public void sendOtherPlayerInfo(ServerThread serverThread){
		ArrayList<String> otherPlayerInfo = new ArrayList<String>();
		synchronized(actualPlayerTheads){//construct the actual list of player once a new player login, the list will construct his startwindow
			for (ServerThread st : actualPlayerTheads) {
				
				if(st !=serverThread ){
					String name = st.getClientName();
					String id = Integer.toString(st.getTokenPicked());
					otherPlayerInfo.add(name+"::Picked Token::"+id);
				}
			}
			
		}
		serverThread.sendOtherPlayerNameAndTokenChose(otherPlayerInfo);
	}
	public void run(){
		try{
			while (!stop && actualPlayerTheads.size() != 8 ) {
				System.out.println("waiting for connection...");
				Socket s = ss.accept();
				System.out.println("connection from " + s.getInetAddress());
				ServerThread st = new ServerThread(s, this);
				st.sendMessage("You are connected");
				serverThreads.add(st);
				
				//serverThreadID ++;
			}
		}catch(BindException be) {
			System.out.println("be: " + be.getMessage());
		}catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		} 
	}
	public void removeEveryPlayer(){
		actualPlayerTheads.clear();
	}
	
	
	//check login info match database
	public boolean verifyUser(String username, String password){
		jDBCDriver.connect();
		
		String mUsername = username;
		String mPassword = password;
		
		if(!jDBCDriver.doesExist(mUsername)){
			System.out.println("This username does not exist.");
			jDBCDriver.stop();
			return false;
		}else{
			
			//if the user gave the wrong password
			if (!mPassword.equals(jDBCDriver.getPassword(mUsername))){
				System.out.println("Wrong password.");
				jDBCDriver.stop();
				return false;
			}
			//login successful
			else{
				jDBCDriver.stop();
				return true;
			}
		}	
		
	}
	//create account if username valid 
	public boolean createUser(String username, String password){
		jDBCDriver.connect();
		String mUsername = username;
		String mPassword = password;
		//username cannot name themself guest
		if(mUsername.contains("Guest")){
			System.out.println("Cannot name a user Guest.");
			jDBCDriver.stop();
			return false;
		}
		
		//check if username has been created
		if(jDBCDriver.doesExist(mUsername)){
			System.out.println("This username already exist.");
			jDBCDriver.stop();
			return false;
		}
		//username has not been created, create account and login
		else{
			jDBCDriver.add(mUsername, mPassword);
			jDBCDriver.stop();
			return true;
		}
	}
	//get win ratio from database 
	/* no need for this method
	public int getWinRatio(String username){
		jDBCDriver.connect();
		String mUsername = username;
		if(jDBCDriver.doesExist(mUsername)){
			int wins = jDBCDriver.getNumberOfWins(mUsername);
			int numberOfGame =  jDBCDriver.getNumberOfGameplays(mUsername);
			jDBCDriver.stop();
			if(numberOfGame == 0){ //no game played yet 
				return 0;
			}else {
				return wins/numberOfGame;
			}
		}else {
			System.out.println("This username does not exist.");
			jDBCDriver.stop();
			return -1;
		}
		
	}
	*/
	public int getWins(String username){
		if(!username.contains("Guest")){
			jDBCDriver.connect();
			int wins = jDBCDriver.getNumberOfWins(username);
			jDBCDriver.stop();
			return wins;
		}else {
			return 0;
		}
		
	}
	public int getNumberOfGames(String username){
		if(!username.contains("Guest")){
			jDBCDriver.connect();
			int wins = jDBCDriver.getNumberOfGameplays(username);
			jDBCDriver.stop();
			return wins;
		}else {
			return 0;
		}
		
	}
	public void incrementWins(String username){
		jDBCDriver.connect();
		jDBCDriver.incrementWins(username);
		jDBCDriver.stop();
	}
	public void incrementNumberOfGameplays(){
		jDBCDriver.connect();
		synchronized(players){
			for (Player p: players){
				if(!p.getName().contains("Guest")){
					jDBCDriver.incrementGameplays(p.getName());
					System.out.println("Incremented number of game for " +p.getName()+" by 1.");
				}
			}
		}
		jDBCDriver.stop();
	}
	
	//start a new game 
	public void refreshServer(){
		serverThreads.clear();
		stop = false;
		run();
	}
	public void stopServer(){
		stop=true;
	}
	//end server thread 
	public void closeServer(){
		if (ss != null) {
			try {
				ss.close();
			} catch (IOException ioe) {
				System.out.println("ioe closing ss: " + ioe.getMessage());
			}
		}
	}
	
	//give an id to the login user or guest, ID is based on the capacity of arraylist
	public void setID(ServerThread serverThread){
		if(actualPlayerTheads.size() <= 8){
			int serverThreadID = actualPlayerTheads.size() -1;
			serverThread.setServerThreadID(serverThreadID);
		}else{
			
			System.out.println("exceed server capacity");
			return;
		}
		
	}
	
	//if a player logs out during the game, update all players' serverThreadID 
	
	public void removeFromPlayerThread(ServerThread st){
		if(st != null){
			
			synchronized(actualPlayerTheads){
				actualPlayerTheads.remove(st);
			}
		}else {
			return;
		}
		int newThreadID = 1;
		//reasign id after a player logs out 
		synchronized(actualPlayerTheads){
			for (ServerThread playerThread:actualPlayerTheads ){
				playerThread.setServerThreadID(newThreadID);
				newThreadID++;	
			}
		}
		
		
	}
	
	//add playerthread when player logs in or play as guest
	public void addToPalyerThread(ServerThread st){
		if(st != null){
			synchronized(actualPlayerTheads){
				if(actualPlayerTheads.size() <8){
					actualPlayerTheads.add(st);
				}else{
					System.out.println("exceed 8 player capacity");
					return;
				}
			}
		
		}
		
	}
	
	public void addGuestName(ServerThread st){
		String newGuestName = "Guest" + Integer.toString(numberOfGuests);
		System.out.println("set new guestname: " +newGuestName );
		numberOfGuests++;
		st.setGuestName(newGuestName);
	}
	
	public boolean cannotAddPlayer(){
		return actualPlayerTheads.size() == 8;
	}
	public boolean ableToStart(){
		return (ss!=null);
	}
	private class PortPane {
	   public PortPane() {
	      JTextField portField = new JTextField(5);

	      JPanel myPanel = new JPanel();
	      myPanel.add(new JLabel("Port:"));
	      myPanel.add(portField);

	      int result = JOptionPane.showConfirmDialog(null, myPanel, 
	               "Please Enter Port Values", JOptionPane.OK_CANCEL_OPTION);
	      if (result == JOptionPane.OK_OPTION) {
	         System.out.println("Port: " + portField.getText());
	         int port = Integer.parseInt(portField.getText());
	         try {
	 			//teamNames = new ArrayList<String>();
	        	System.out.println(InetAddress.getLocalHost());
	 			ss = new ServerSocket(port);
	 			serverThreads = new ArrayList<ServerThread>();
	 			actualPlayerTheads = new ArrayList<ServerThread>();
	 			players = new ArrayList<Player>();
	 			stop = false;
	 			jDBCDriver = new JDBCDriver();
	 			System.out.println("Server constructed");
	 		} catch (IOException ioe) {
	 			System.out.println("ioe construct: " + ioe.getMessage());
	 			ss = null;
	 		}
	      }else if (result == JOptionPane.OK_OPTION) {
	    	  ss = null;
	      }
	   }
		   
	}
	
	public static void main(String args[]){
		Server server = new Server();
		if(server.ableToStart()){
			server.start();
		}else {
			System.out.println("Terminate");
			return;
		}
		
	}

}
