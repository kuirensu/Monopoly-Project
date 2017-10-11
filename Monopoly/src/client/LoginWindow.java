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

// Author: Matthew van Niekerk
// edited by James Su 
package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import resources.LoginInfo;
import utilities.AppearanceSettings;

public class LoginWindow extends JFrame {
	
	private static final long serialVersionUID = 6328818746654805079L;
	
	private JLabel alertLabel;
	private JButton loginButton;
	private JButton createAccountButton;
	private JButton guestButton;
	private JTextField username;
	private JPasswordField password;
	private Client client;
	
	public LoginWindow(Client client) {
		this.client = client;
		client.setLoginWindow(this);
		initializeVariables();
		createGUI();
		addListeners();
	}
	
	private void initializeVariables() {
		alertLabel =new JLabel("");
		loginButton = new JButton("Log In");
		createAccountButton = new JButton("Create Account");
		guestButton = new JButton("Play as Guest");
		username = new JTextField("Username");
		password = new JPasswordField("Password");
		password.setEchoChar((char) 0);
	}
	
	private void createGUI() {
		//	create local variables
		JPanel mainPanel = new JPanel();
		JPanel northPanel = new JPanel();
		JPanel centerPanel = new JPanel();
		JPanel southPanel = new JPanel();
		JPanel usernamePanel = new JPanel();
		JPanel passwordPanel = new JPanel();
		JLabel monopolyLabel = new JLabel("Monopoly");
		JPanel alertPanel = new JPanel();
		JPanel buttonsPanel = new JPanel();
		
		mainPanel.setLayout(new BorderLayout());
		northPanel.setLayout(new BorderLayout());
		centerPanel.setLayout(new BorderLayout());
		southPanel.setLayout(new BorderLayout());
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.LINE_AXIS));
		
		//	make it pretty
		monopolyLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		monopolyLabel.setFont(new Font("Lucida", Font.PLAIN, 20));
		AppearanceSettings.setTextAlignment(monopolyLabel, alertLabel);
		AppearanceSettings.setSize(300, 60, password, username);
		//AppearanceSettings.unSetBorderOnButtons(loginButton, createAccountButton, guestButton);
		AppearanceSettings.setSize(120, 60, loginButton, createAccountButton, guestButton);
		AppearanceSettings.setOpaque(loginButton, createAccountButton, guestButton);
		//AppearanceSettings.setBackground(Color.DARK_GRAY, loginButton, createAccountButton, guestButton);
		AppearanceSettings.setForeground(Color.LIGHT_GRAY, username, password);
		
		//	only guest button starts as enabled (and is always enabled)
		loginButton.setEnabled(false);
		createAccountButton.setEnabled(false);
		
		alertPanel.add(alertLabel);
		usernamePanel.add(username);
		passwordPanel.add(password);
		
		AppearanceSettings.addGlue(buttonsPanel, BoxLayout.LINE_AXIS, true, loginButton, createAccountButton, guestButton);
		//AppearanceSettings.addGlue(mainPanel, BoxLayout.PAGE_AXIS, false, monopolyLabel);
		
		northPanel.add(monopolyLabel, BorderLayout.NORTH);
		northPanel.add(alertPanel, BorderLayout.SOUTH);
		centerPanel.add(usernamePanel, BorderLayout.NORTH);
		centerPanel.add(passwordPanel, BorderLayout.CENTER);
		//AppearanceSettings.addGlue(mainPanel, BoxLayout.PAGE_AXIS, false, passwordPanel);
		southPanel.add(buttonsPanel, BorderLayout.CENTER);
		
		mainPanel.add(northPanel, BorderLayout.NORTH);
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		mainPanel.add(southPanel, BorderLayout.SOUTH);
		
		add(mainPanel, BorderLayout.CENTER);
		
		setSize(400, 300);
		setLocationRelativeTo(null);
		setResizable(false);
	}
	
	private void addListeners() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//	listeners to enable/disable login and create account buttons
		username.getDocument().addDocumentListener(new MyDocumentListener());
		password.getDocument().addDocumentListener(new MyDocumentListener());
		
		//	add button action listeners
		loginButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				//login(username.getText(), new String(password.getPassword()));
				login();
			}
		});
		
		createAccountButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				//createAccount(username.getText(), new String(password.getPassword()));
				createAccount();
			}
		});
		
		guestButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				playAsGuest();
			}
		});
		
		//	change username text field to allow grey prompt text to appear/disappear
		username.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				if (username.getText().equals("")) {
					username.setText("Username");
					username.setForeground(Color.LIGHT_GRAY);
					loginButton.setEnabled(false);
					createAccountButton.setEnabled(false);
				}
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				if (username.getForeground().equals(Color.LIGHT_GRAY)) {
					username.setText("");
					username.setForeground(Color.BLACK);
				}
			}
		});
		
		//	change password field to allow grey prompt text to appear/disappear
		password.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				if (password.getPassword().length == 0) {
					password.setEchoChar((char) 0);
					password.setText("Password");
					password.setForeground(Color.LIGHT_GRAY);
					loginButton.setEnabled(false);
					createAccountButton.setEnabled(false);
				}
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				if (password.getForeground().equals(Color.LIGHT_GRAY)) {
					password.setEchoChar('*');
					password.setText("");
					password.setForeground(Color.BLACK);
				}
			}
		});
	}
	
	public void setAlertLabel(String alert) {
		alertLabel.setText(alert);
	}
	
	private boolean haveText() {
		//	returns true if the username and password fields both contain non-prompt text
		//	false if empty or grey prompt text
		boolean haveText = true;
		
		if (username.getText().trim().equals("") || password.getPassword().length == 0){
			haveText = false;
		}
		if (username.getForeground().equals(Color.LIGHT_GRAY) 
				|| password.getForeground().equals(Color.LIGHT_GRAY)) {
			haveText = false;
		}
		
		return haveText;
	}
	
	private void login() {
		client.sendLoginInfo(new LoginInfo(username.getText(), new String(password.getPassword()), true  ) );
	}
	
	private void createAccount() {
		client.sendLoginInfo(new LoginInfo(username.getText(), new String(password.getPassword()), false  ) );
	
	}
	
	private void playAsGuest() {
		client.sendMessage("Guest Login: ");
	}
	
	private class MyDocumentListener implements DocumentListener {
		//	custom DocumentListener from solution code
		@Override
		public void insertUpdate(DocumentEvent e) {
			loginButton.setEnabled(haveText());
			createAccountButton.setEnabled(haveText());
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			loginButton.setEnabled(haveText());
			createAccountButton.setEnabled(haveText());
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			loginButton.setEnabled(haveText());
			createAccountButton.setEnabled(haveText());
		}
		
	}
}
