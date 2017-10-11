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
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;

import resources.Player;

//Made by Jesse
public class PlayerInformationGrid extends JPanel {

	private static final long serialVersionUID = 5873550816023916659L;

	ArrayList<Player> players;
	ArrayList<JButton> playerButtons;
	ArrayList<PlayerInfoLayout> playerInfoLayouts;
	int currentPlayer = -1;
	
	public PlayerInformationGrid(ArrayList<Player> players) {
		this.players = players;
		initializeComponents();
		createGUI();
		addListeners();
	}
	
	private void initializeComponents() {
		// Create a button for each player.
		playerButtons = new ArrayList<JButton>();
		playerInfoLayouts = new ArrayList<PlayerInfoLayout>();
		for (Player p : players) {
			JButton b = new JButton("");
			playerButtons.add(b);
			PlayerInfoLayout pil = new PlayerInfoLayout(p,b);
			playerInfoLayouts.add(pil);
		}
	}

	private void createGUI() {
		// Create a 2 by X grid to store the needed number of players.
		int numColumns = (players.size()%2 == 0) ? players.size()/2 : players.size()/2+1;
		this.setLayout(new GridLayout(2,numColumns));
		
		// Add each button to the grid.
		for (PlayerInfoLayout b : playerInfoLayouts) {
			this.add(b);
		}
	}

	private void addListeners() {
		// No action inside progress area.
		for (int i = 0; i < players.size(); i++) {
			JButton b = playerButtons.get(i);
			Player p = players.get(i);
			b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new PlayerInformationWindow(p).setVisible(true);;
				}
			});
		}
	}
	
	public void setCurrentPlayer(int i) {
		currentPlayer = i;
	}
	
	private class PlayerInfoLayout extends JPanel {
		private static final long serialVersionUID = -6341876191272116746L;
		
		Player player;
		Image playerIcon;
		
		public PlayerInfoLayout(Player p, JButton b) {
			this.setLayout(new BorderLayout());
			b.setOpaque(false);
			b.setContentAreaFilled(false);
			b.setBorderPainted(false);
			b.setBackground(Color.WHITE);
			this.add(b, BorderLayout.CENTER);
			
			this.player = p;
			
			// Initialize our Image
			try {
					playerIcon = ImageIO.read(new File("images/tokens/token" + p.getGameToken() + ".png"));
			} catch (IOException ioe) {
				System.out.println("Error Loading Player Image: " + ioe.getMessage());
			}
			
			
		}
		
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			int width = this.getWidth();
			int height = this.getHeight();
			int fontHeight = g.getFont().getSize();
			
			if (currentPlayer == playerInfoLayouts.indexOf(this)) {
				g.setColor(Color.LIGHT_GRAY);
			} else {
				g.setColor(Color.WHITE);
			}
			g.fillRect(2, 2, getWidth()-4, getHeight()-4);
			g.setColor(Color.BLACK);
			g.drawRect(2, 2, getWidth()-4, getHeight()-4);
			g.drawImage(playerIcon, (width/4-(height-10)/2), 5, height-10, height-10, null);
			
			g.drawString(player.getName(), width/2, height/4+fontHeight/2);
			g.drawString("$"+player.getMoney(), width/2, height*3/4+fontHeight/2);
		}
	}
}
