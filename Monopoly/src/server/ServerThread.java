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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import resources.LoginInfo;
import resources.Player;
/*-----------------------------------------
 * Author: James Su
 * 
 * 
 * 
 * 
 */
public class ServerThread extends Thread  {
	
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private int ID = -1; //after player login as user or guest, he is given an id from 1 to 8
	private int tokenPicked =-1; //set to actual token id after client-connected picked a token 
	private Server server;
	@SuppressWarnings("unused")
	private Socket s;
	private String clientName;
	public ServerThread(Socket s, Server server) {
		try {
			this.server = server;
			this.s = s;
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
			sendMessage(s.getInetAddress() + ", you are connected");
			this.start();  //start this thread after client is connected 
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}
	}
	
	//send message to the client that is connected to this server thread
	public void sendMessage(String message) {
		try {
			System.out.println("Sent: " + message);
			oos.writeObject(message);
			oos.flush();
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}
	}
	//send actual teamnames to client once the game starts
	public void sendOtherPlayerNameAndTokenChose( ArrayList<String> otherPlayerInfo ){
		try {
			oos.writeObject(otherPlayerInfo);
			oos.flush();
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}
	}
	public void sendPlayers( ArrayList<Player> players ){
		try {
			oos.writeObject(players);
			oos.flush();
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}
	}
	public void run(){
		try {
			while(true) {
				Object obj = (Object ) ois.readObject();
				if(obj instanceof String){
					String message = (String)obj;
					interpretMessage(message);
				}else if(obj instanceof LoginInfo){//user login or new account login
					LoginInfo loginInfo = (LoginInfo)obj;
					verifyOrCreateUser(loginInfo);
				}
		
			}
		} catch (ClassNotFoundException cnfe) {
			System.out.println("cnfe in run: " + cnfe.getMessage());
		} catch (IOException ioe) {
			System.out.println("ioe in run: " + ioe.getMessage());
		}
		
	}
	
	private void interpretMessage(String message){
		if(message.contains("Guest Login: ")){	//guest login
			if(!server.cannotAddPlayer()){	
				server.addGuestName(this); //set guest name from server
				server.addToPalyerThread(this);
				server.setID(this);
				sendMessage("Login success: "+clientName);
				server.sendOtherPlayerInfo(this);
				server.sendMessageToAllOtherClients(message+clientName, this); //send this guest client's name to all other clients
			}
			//send player information they need to get to the start window 
		}else if(message.contains("Client Logout: ")){// a player log out during starting game gui or main gui
			server.sendMessageToAllOtherClients(message+ clientName, this); // tell all other client that this client logs out with its ID number
			server.removeFromPlayerThread(this);
		
		}else if (message.contains("Host Logout: ")){
			server.sendMessageToAllOtherClients("Host Logout: ", this);
			server.removeEveryPlayer();
		}else if(message.contains("Startgame")){
			server.incrementNumberOfGameplays();
			server.stopServer(); // server doesnt take more people after host clicked start 
			server.sendPlayersToClients();
			
		}else if(message.contains("::Picked Token::")){
			server.sendMessageToAllOtherClients(message, this);
			String[] command = message.split("::");
			int tokenID = Integer.parseInt(command[2]);
			tokenPicked = tokenID;
		}else if(message.contains("Winnner: ")){
			message = message.replace("Winnner: ", "");
			message = message.trim();
			String winnerName = message;
			server.incrementWins(winnerName);
		}else if(message.contains("ExitGame")){
			server.sendMessageToAllOtherClients(message, this);
			server.removeEveryPlayer();
		}else{
			server.sendMessageToAllOtherClients(message, this); //if it is other message,  send to other players
		}
	}
	
	
	//verify the user or create an account for the user 
	private void verifyOrCreateUser(LoginInfo loginInfo){
		if(loginInfo.isLogin()){ // if client clicked login, verify account
			boolean match = server.verifyUser(loginInfo.getUsername(), loginInfo.getPassword());
			if(match){
				if(!server.cannotAddPlayer()){ //if there are not yet 8 people in game room
					clientName = loginInfo.getUsername();
					server.sendMessageToAllOtherClients("User Login: "+ clientName, this );	
					sendMessage("Login success: "+clientName);
					server.sendOtherPlayerInfo(this);
					server.addToPalyerThread(this);
					server.setID(this); //server generate an id for this user
				}else{
					sendMessage("Login deny, room is full");
				}
			}else{
				sendMessage("Login deny, retype your username and password");
			}
		}else{ //if the client clicked create account, create an account
			boolean succeed = server.createUser(loginInfo.getUsername(), loginInfo.getPassword());
			if(succeed){
				clientName = loginInfo.getUsername();
				sendMessage("Creating account success: "+clientName);
				if(!server.cannotAddPlayer()){//if there are not yet 8 people in game room
					server.sendMessageToAllOtherClients("User Login: "+ clientName, this );	
					sendMessage("Login success: "+clientName);
					server.sendOtherPlayerInfo(this);
					server.addToPalyerThread(this);
					server.setID(this);
				}else{
					sendMessage("Login deny, room is full");
				}
			}else{
				sendMessage("Creating account deny, try a different name");
			}
		}
	}


	public String getClientName(){
		return clientName;
	}
	public void setGuestName(String name){
		clientName = name;
		if(name.contains("Guest")){ //guest doesn't have a name so we give them one
			sendMessage("Guest name: "+ name);
		}
	}
	//update id on both client and serverThread
	public void setServerThreadID(int id){
		sendMessage("Update ID: " + id);
		ID = id;
	}
	public int getServerThreadID(){
		return ID;
	}
	public int getTokenPicked(){
		return tokenPicked;
	}
}
