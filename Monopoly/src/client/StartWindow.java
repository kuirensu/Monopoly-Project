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

//	Author: Matthew van Niekerk
package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import utilities.AppearanceConstants;

public class StartWindow extends JFrame {
	
	private static final long serialVersionUID = 7971764919438259602L;
	
	private JLabel selectPrompt;
	private JLabel yourName;
	private JLabel playersLabel;
	private JLabel tokensLabel;
	private JButton readyButton;
	private ArrayList<PlayerInfoLayout> playerInfoPanels;
	//private ArrayList<Image> chosenIcons;
	private ArrayList<JButton> tokenButtons;
	private JPanel playerGrid;
	private JPanel tokenGrid;
	//private Socket s;
	private String playerName;
	private int playerToken;
	private Client client;
	private ArrayList<Image> tokenImages;
	private ArrayList<String> allPlayerInfo;
	private ArrayList<String> readyPlayers;
	private boolean playerReady;

	public StartWindow(String playerName, ArrayList<String> otherPlayerInfo, Client client) {
		this.playerName = playerName;
		this.client = client;
		playerToken = -1;
		if (otherPlayerInfo == null) {
			allPlayerInfo = new ArrayList<String>();
		} else {
			allPlayerInfo = otherPlayerInfo;
		}
		allPlayerInfo.add(playerName + "::Picked Token::" + playerToken);
		initializeVariables();
		createGUI();
		addActionListeners();
		setTokenButtons();
	}
	
	private void initializeVariables() {
		selectPrompt = new JLabel("Select your game token.", JLabel.CENTER);
		//	Change to variable
		yourName = new JLabel("Name: " + playerName, JLabel.CENTER);
		playersLabel = new JLabel("Players:", JLabel.CENTER);
		tokensLabel = new JLabel("Tokens:", JLabel.CENTER);
		readyButton = new JButton("Ready");
		readyButton.setEnabled(false);
		if (allPlayerInfo.size() == 1) {
			readyButton.setText("Start");
		}
		playerInfoPanels = new ArrayList<PlayerInfoLayout>(8);
		for (int i = 0; i < 8; i++) {
			if (i < allPlayerInfo.size()) {
				String[] temp = allPlayerInfo.get(i).split("::");
				String clientName = temp[0];
				int tokenID = Integer.parseInt(temp[2]);
				playerInfoPanels.add(i, new PlayerInfoLayout(clientName, tokenID));
			} else {
				playerInfoPanels.add(i, new PlayerInfoLayout("No Player", -1));
			}
		}
		
		//chosenIcons = new ArrayList<Image>();
		tokenImages = new ArrayList<Image>(8);
		tokenButtons = new ArrayList<JButton>(8);
		
		for (int i = 0; i < 8; i++) {
			JButton temp = new JButton();
			try {
				Image img = ImageIO.read(new File("images/tokens/token" + i + ".png"));
				tokenImages.add(img);
				temp.setIcon(new ImageIcon(tokenImages.get(i)));
			} catch (IOException e) {
				e.printStackTrace();
			}
			temp.setSize(100, 100);
			tokenButtons.add(temp);
		}
		
		playerGrid = new JPanel();
		tokenGrid = new JPanel();
		//s = new Socket();
		readyPlayers = new ArrayList<String>();
		playerReady = false;
	}
	
	private void createGUI() {
		JPanel mainPanel = new JPanel();
		JPanel northPanel = createNorthPanel();
		JPanel centerPanel = createCenterPanel();
		JPanel southPanel = createSouthPanel();
		
		mainPanel.setLayout(new BorderLayout());
		
		mainPanel.add(northPanel, BorderLayout.NORTH);
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		mainPanel.add(southPanel, BorderLayout.SOUTH);
		
		add(mainPanel);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setSize(600, 800);
		setLocationRelativeTo(null);
	}
	
	private JPanel createNorthPanel() {
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BorderLayout());
		northPanel.add(selectPrompt, BorderLayout.NORTH);
		northPanel.add(yourName, BorderLayout.CENTER);
		return northPanel;
	}
	
	private JPanel createCenterPanel() {
		JPanel centerPanel = new JPanel();
		JPanel playerPanel = new JPanel();
		JPanel tokenPanel = new JPanel();
		
		centerPanel.setLayout(new BorderLayout());
		playerPanel.setLayout(new BorderLayout());
		tokenPanel.setLayout(new FlowLayout());
		
		playerGrid.setLayout(new GridLayout(4, 2));
		tokenGrid.setLayout(new GridLayout(2, 4));
		
		for (int i = 0; i < 8; i++) {
			playerGrid.add(playerInfoPanels.get(i));
		}
		
		for (int i = 0; i < 8; i++) {
			tokenGrid.add(tokenButtons.get(i));
		}
		
		playerPanel.add(playersLabel, BorderLayout.NORTH);
		playerPanel.add(playerGrid, BorderLayout.CENTER);
		
		tokenPanel.add(tokensLabel);
		tokenPanel.add(tokenGrid);
		
		centerPanel.add(playerPanel, BorderLayout.NORTH);
		centerPanel.add(tokenPanel, BorderLayout.CENTER);
		
		return centerPanel;
	}

	private JPanel createSouthPanel() {
		JPanel southPanel = new JPanel();
		
		southPanel.add(readyButton);
		
		return southPanel;
	}
	
	protected void refreshPlayer(String playerName, int playerToken) {
		//	updates the player's token in the respective PlayerInfoLayout and
		//	sets the player's token in allPlayerInfo
		for (int i = 0; i < 8; i++) {
			if (playerInfoPanels.get(i).getName().equals(playerName)) {
				playerInfoPanels.get(i).setToken(playerToken);
				allPlayerInfo.set(i, playerName + "::Picked Token::" + playerToken);
			}
		}
	}
	
	protected void addReadyPlayer(String username) {
		readyPlayers.add(username);
	}
	
	protected void checkReady() {
		System.out.println("Ready Players: " + readyPlayers + "  Player Token: " + playerToken);
		if (readyPlayers.size() == allPlayerInfo.size()-1 && allPlayerInfo.size() > 1 && playerToken != -1) {
			readyButton.setEnabled(true);
		}
	}
	
	private void addActionListeners() {
		addWindowListener(new ExitWindowListener(this));
		
		for (int i = 0; i < 8; i++) {
			setUpTokenButtons(i);
		}
		
		readyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				for (int i = 0; i < 8; i++) {
					tokenButtons.get(i).setEnabled(false);	
				}
				if (readyButton.getText().equals("Ready")) {
					readyButton.setEnabled(false);
					client.sendMessage("Ready::" + playerName);
					playerReady = true;
				} else {
					//	start game
					client.sendMessage("Startgame");
				}
			}
		});
	}
	
	private void setUpTokenButtons(int i) {
		tokenButtons.get(i).addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				playerToken = i;
				refreshPlayer(playerName, playerToken);
				/*for (int x = 0; x < 8; x++) {
					String[] temp = allPlayerInfo.get(x).split("::");
					String clientName = temp[0];
					int tokenID = Integer.parseInt(temp[2]);
					if (playerName.equals(clientName)) {
						allPlayerInfo.set(x, playerName + "::Picked Token::" + playerToken);
						break;
					}
				}*/
				client.sendMessage(playerName + "::Picked Token::" + playerToken);
				/*for (int x = 0; x < 8; x++) {
					tokenButtons.get(x).setEnabled(true);
				}
				tokenButtons.get(i).setEnabled(false);
				*/
				setTokenButtons();
				if (readyButton.getText().equals("Ready")) {
					readyButton.setEnabled(true);
				} else {
					checkReady();
				}
			}
		});
	}
	
	public void setTokenButtons() {
		if (!playerReady) {
			for (int i = 0; i < 8; i++) {
				tokenButtons.get(i).setEnabled(true);
			}
			for (int i = 0; i < allPlayerInfo.size(); i++) {
				String[] temp = allPlayerInfo.get(i).split("::");
				int tokenID = Integer.parseInt(temp[2]);
				if (tokenID != -1) {
					tokenButtons.get(tokenID).setEnabled(false);
				}
			}
		}
	}
	
	public void userJoined(String username) {
		allPlayerInfo.add(username + "::Picked Token::" + (-1));
		for (int i = 0; i < 8; i++) {
			if (playerInfoPanels.get(i).getName().equals("No Player")) {
				playerInfoPanels.get(i).setName(username);
				playerInfoPanels.get(i).setToken(-1);
				break;
			}
		}
	}
	
	public void userLeft(String username) {
		for (int i = 0; i < allPlayerInfo.size(); i++) {
			String[] temp = allPlayerInfo.get(i).split("::");
			String name = temp[0];
			int tokenID = Integer.parseInt(temp[2]);
			if (name.equals(username)) {
				allPlayerInfo.remove(i);
				playerInfoPanels.remove(i);
				playerInfoPanels.add(new PlayerInfoLayout("No Player", -1)); 
				playerGrid.removeAll();
				for (int x = 0; x < 8; x++) {
					playerGrid.add(playerInfoPanels.get(x));
				}
				playerGrid.repaint();
				playerGrid.revalidate();
				if (!playerReady) {
					if (tokenID!=-1) {
						tokenButtons.get(tokenID).setEnabled(true);
					}
				}
				if (readyPlayers.size() > 0) {//host
					if (readyPlayers.contains(name)) {
						readyPlayers.remove(name);
					}
				}
				checkReady();
			}
		}
	}
	
	private class PlayerInfoLayout extends JPanel {
		private static final long serialVersionUID = -6341876191272116746L;
		
		String playerName;
		int playerToken;
		
		public PlayerInfoLayout(String playerName, int playerToken) {
			this.setLayout(new BorderLayout());
			this.setPreferredSize(new Dimension(200, 100));
			this.playerName = playerName;
			this.playerToken = playerToken;
			// Initialize our Image
			/*try {
					playerToken = ImageIO.read(new File("images/tokens/token" + p.getGameToken() + ".png"));
			} catch (IOException ioe) {
				System.out.println("Error Loading Player Image: " + ioe.getMessage());
			}*/
		}
		
		public String getName() {
			return playerName;
		}
		
		public void setName(String playerName) {
			this.playerName = playerName;
			this.repaint();
		}
		
		public void setToken(int t) {
			playerToken = t;
			this.repaint();
		}
		
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			int width = this.getWidth();
			int height = this.getHeight();
			int fontHeight = g.getFont().getSize();
			
			g.drawRect(2, 2, getWidth()-4, getHeight()-4);
			if (playerToken != -1) {
				g.drawImage(tokenImages.get(playerToken), 5, 5, height-10, height-10, null);
			}
			
			g.drawString(playerName, width/2, height/4+fontHeight/2);
		}
	}
	
	private class ExitWindowListener extends WindowAdapter{

		private JFrame frame;
		
		public ExitWindowListener(JFrame frame) {
			this.frame = frame;
		}
		
		 public void windowClosing(WindowEvent e) {
			 int answer = JOptionPane.showConfirmDialog(frame, "Are you sure you want to quit?", "Quit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, AppearanceConstants.exitIcon);
		     
			 if (answer == JOptionPane.YES_OPTION) {
				 System.out.println(playerName);
				 if (client.getID() == 0) {
					 client.sendMessage("Host Logout: ");
				 } else {
					 client.sendMessage("Client Logout: ");
				 }
				 frame.dispose();
			 } 
		 }
	}
}
