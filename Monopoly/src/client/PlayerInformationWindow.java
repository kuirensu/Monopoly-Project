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

/**
* Merged Nick and Jeffrey's versions of PlayerInformationWindow.java
 */

package client;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import resources.Player;
import resources.Property;
import utilities.AppearanceSettings;

public class PlayerInformationWindow extends JFrame {
	public static final long serialVersionUID = 1;
	private Player player;
	private JLabel nameLabel, moneyLabel, moneyValueLabel, jailCardsLabel, jailCardsValueLabel, 
		propertiesLabel;
	private JTextArea propertiesArea;
	private JScrollPane scrollPane;
	private JButton closeButton;
	
	public PlayerInformationWindow(Player p) {
		super("Player Information");
		player = p;
		initializeComponents();
		createGUI();
		addEvents();
	}
	
	private void initializeComponents() {
		nameLabel = new JLabel(player.getName());
		moneyLabel = new JLabel("Money");
		moneyValueLabel = new JLabel("$"+player.getMoney());
		jailCardsLabel = new JLabel("Get Out of Jail Free Cards");
		jailCardsValueLabel = new JLabel(Integer.toString(player.getJailCards()));
		propertiesLabel = new JLabel("Properties");
		propertiesArea = new JTextArea();
		scrollPane = new JScrollPane(propertiesArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		closeButton = new JButton("Close");
	}
	
	private void createGUI() {
		//Visual Settings
		Font boldNormal = new Font(nameLabel.getFont().getFontName(), Font.BOLD, 
				nameLabel.getFont().getSize());
		Font boldLarge = new Font(nameLabel.getFont().getFontName(), Font.BOLD, 24);
		AppearanceSettings.setFont(boldNormal, moneyLabel, jailCardsLabel, propertiesLabel);
		AppearanceSettings.setFont(boldLarge, nameLabel);
		AppearanceSettings.setSize(400, 300, propertiesArea);
		AppearanceSettings.setTextComponents(propertiesArea);
		setPropertiesArea();
		
		setSize(500, 500);
		setLocation(0, 0);
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.5;
		c.weighty = 0.5;
		add(nameLabel, c);
		
		c.gridy = 1;
		JPanel jp1 = new JPanel(new GridLayout(2, 2));
		jp1.add(moneyLabel);
		jp1.add(jailCardsLabel);
		jp1.add(moneyValueLabel);
		jp1.add(jailCardsValueLabel);
		add(jp1, c);
		
		c.gridy = 2;
		add(propertiesLabel, c);
		
		c.gridy = 3;
		add(scrollPane, c);
		
		c.gridy = 4;
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
	
	public void update(Player player) {
		this.player = player;
		nameLabel.setText(player.getName());
		moneyLabel.setText("$"+player.getMoney());
		jailCardsLabel.setText(Integer.toString(player.getJailCards()));
		setPropertiesArea();
	}
	
	private void setPropertiesArea() {
		propertiesArea.setText("");
		if (player.getProperties().size() == 0) {
			return;
		}
		Vector<Property> properties = sortProperties(player.getProperties());
		String group = properties.get(0).getGroup();
		propertiesArea.append(group+"\n");
		for (Property property : properties) {
			if (!property.getGroup().equals(group)) {
				group = property.getGroup();
				propertiesArea.append("\n"+group+"\n");
			}
			propertiesArea.append("    "+property.getName());
			if (property.isMortgaged()) {
				propertiesArea.append(" (Mortgaged)");
			} else {
				if (property.getHotel()) {
					propertiesArea.append(" (Hotel)");
				} else {
					int numHouses = property.getNumHouses();
					if (numHouses == 1) {
						propertiesArea.append(" (1 House)");
					} else if (numHouses > 1) {
						propertiesArea.append(" ("+numHouses+" Houses)");
					}
				}
			}
			propertiesArea.append("\n");
		}
	}
	
	private Vector<Property> sortProperties(Vector<Property> properties) {
		Vector<Property> output = new Vector<Property>();
		Vector<String> names = new Vector<String>();
		names.add("Mediterranean Avenue");
		names.add("Baltic Avenue");
		names.add("Reading Railroad");
		names.add("Oriental Avenue");
		names.add("Vermont Avenue");
		names.add("Connecticut Avenue");
		names.add("St. Charles Place");
		names.add("Electric Company");
		names.add("States Avenue");
		names.add("Virginia Avenue");
		names.add("Pennsylvania Railroad");
		names.add("St. James Place");
		names.add("Tennessee Avenue");
		names.add("New York Avenue");
		names.add("Kentucky Avenue");
		names.add("Indiana Avenue");
		names.add("Illinois Avenue");
		names.add("B. & O. Railroad");
		names.add("Atlantic Avenue");
		names.add("Ventnor Avenue");
		names.add("Water Works");
		names.add("Marvin Gardens");
		names.add("Pacific Avenue");
		names.add("North Carolina Avenue");
		names.add("Pennsylvania Avenue");
		names.add("Short Line");
		names.add("Park Place");
		names.add("Boardwalk");
		for (String name : names) {
			for (Property p : properties) {
				if (p.getName().equals(name)) {
					output.add(p);
					break;
				}
			}
		}
		return output;
	}
}
