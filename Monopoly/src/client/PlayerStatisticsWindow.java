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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import resources.Player;

public class PlayerStatisticsWindow extends JFrame {
	public static final long serialVersionUID = 1;
	private Player player;
	private JLabel nameLabel, winsLabel, lossesLabel;
	private JButton closeButton;
	
	public PlayerStatisticsWindow(Player p) {
		super("Player Statistics");
		player = p;
		initializeComponents();
		createGUI();
		addEvents();
	}
	
	private void initializeComponents() {
		nameLabel = new JLabel(player.getName());
		winsLabel = new JLabel("Total Wins: " + player.getWins());
		lossesLabel = new JLabel("Total Losses: " + (player.getGamesPlayed() - player.getWins()));
		closeButton = new JButton("Close");
	}
	
	private void createGUI() {		
		setSize(400, 250);
		setLocation(0, 0);
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.5;
		c.weighty = 0.5;
		add(nameLabel, c);
		
		c.gridy = 1;
		add(winsLabel, c);
		
		c.gridy = 2;
		add(lossesLabel, c);
		
		c.gridy = 3;
		add(closeButton, c);
	}
	
	private void addEvents() {
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				dispose();
			}
		});
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				dispose();
			}
		});
	}
}
