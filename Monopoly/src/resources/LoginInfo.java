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

import java.io.Serializable;
/*-----------------------------------------
 * Author: James Su
 * 
 * 
 * 
 * 
 */
public class LoginInfo implements Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2538820092699761031L;
	private String username;
	private String password;
	private boolean login; // check if the client is login or creating account
	
	public LoginInfo(String username, String password , boolean login){
		this.username = username;
		this.password = password;
		if(login){
			this.login = true;
		}else {
			this.login = false;
		}
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	//true if the user click login, false if the user clicked create account
	public boolean isLogin() {
		return login;
	}
}

