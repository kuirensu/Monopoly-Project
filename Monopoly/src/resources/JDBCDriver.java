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


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.mysql.jdbc.Driver;


public class JDBCDriver {
	private Connection con;
	private final static String selectName = "SELECT * FROM USERDATA WHERE MUSERNAME=?"; 
	private final static String addProduct = "INSERT INTO USERDATA(MUSERNAME, MPASSWORD, MWINS, MGAMEPLAYES) VALUES(?,?,?,?)"; 
	private final static String updateWins = "UPDATE USERDATA SET MWINS = ? WHERE MUSERNAME=?";
	private final static String updateGameplays = "UPDATE USERDATA SET MGAMEPLAYES = ? WHERE MUSERNAME=?";
	public JDBCDriver()
	{
		try{
			new Driver();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	public void connect()
	{
		try{
			//need to be changed TODO
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Monopoly?user=root&password=kuirensu&useSSL=false");//pass word need to be changed
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	public void stop()
	{
		try{
			con.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	public boolean doesExist(String username)
	{
		try{
			PreparedStatement ps = con.prepareStatement(selectName);
			ps.setString(1, username);
			ResultSet result = ps.executeQuery();
			while(result.next())
			{
				System.out.println(result.getString(1) + " exists with password " + result.getString(2));
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Unable to find user with name: " + username);
		return false;
	} 
	//get password 
	public String getPassword(String username){
		try{
			PreparedStatement ps = con.prepareStatement(selectName);
			ps.setString(1, username);
			ResultSet result = ps.executeQuery();
			while(result.next())
			{
				System.out.println("password is: " + result.getString(2));
				return result.getString(2);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Unable to find user with name: " + username);
		return null;
	}
	//get number of wins 
	public int getNumberOfWins(String username){
		try{
			PreparedStatement ps = con.prepareStatement(selectName);
			ps.setString(1, username);
			ResultSet result = ps.executeQuery();
			while(result.next())
			{
				System.out.println("number of wins is: " + result.getInt(3));
				return result.getInt(3);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Unable to find user with name: " + username);
		return -1;
	}
	
	public void incrementWins(String username){
		try{
			int wins = getNumberOfWins(username);
			PreparedStatement ps = con.prepareStatement(updateWins);
			ps.setInt(1, wins+1);
			ps.setString(2, username);
			ps.executeUpdate();
			System.out.println("Incremented " + username + "'s number of wins by one ");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	//get number of gameplays
	public int getNumberOfGameplays(String username){
		try{
			PreparedStatement ps = con.prepareStatement(selectName);
			ps.setString(1, username);
			ResultSet result = ps.executeQuery();
			while(result.next())
			{
				System.out.println("number of gameplay is: " + result.getInt(4));
				return result.getInt(4);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Unable to find user with name: " + username);
		return -1;
	}
	
	public void incrementGameplays(String username){
		try{
			int numberOfGameplays = getNumberOfGameplays(username);
			PreparedStatement ps = con.prepareStatement(updateGameplays);
			ps.setInt(1, numberOfGameplays+1);
			ps.setString(2, username);
			ps.executeUpdate();
			System.out.println("Incremented " + username + "'s number of gameplays by one ");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//create new account 
	public void add(String username, String password)
	{
		try {
			PreparedStatement ps = con.prepareStatement(addProduct);
			ps.setString(1, username);
			ps.setString(2, password);
			ps.setInt(3, 0);
			ps.setInt(4, 0);
			ps.executeUpdate();
			System.out.println("Adding username: " + username + " with password "+ password + " with zero gameplays ");
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	/*
	public static void main(String args[]){
		JDBCDriver jDBCDriver = new JDBCDriver();
		jDBCDriver.connect();
		System.out.println(jDBCDriver.doesExist("james"));
		jDBCDriver.getNumberOfWins("jj");
		jDBCDriver.stop();
	}
	*/
}
