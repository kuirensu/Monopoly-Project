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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import resources.Player;
import resources.Property;
import utilities.Constants;

//Made by Jesse
public class GameBoard extends JPanel {

	private static final long serialVersionUID = 6731326368140967067L;
	
	ArrayList<Player> players;
	ArrayList<Image> playerImages;
	
	// Images for the game board. Only want to load once.
	Image goImage;
	Image chestImage;
	Image chanceImage;
	Image trainImage;
	Image jailImage;
	Image incomeTaxImage;
	Image goToJailImage;
	Image luxuryTaxImage;
	Image waterworksImage;
	Image electricCompanyImage;
	Image freeParkingImage;
	Image boardImage;
	Property[] properties;
	
	public GameBoard(ArrayList<Player> players, Property[] properties) {
		this.players = players;
		this.playerImages = new ArrayList<Image>();
		this.properties = properties;
		
		// Initialize our Images
		try {
			// Player Images
			for (Player p : players) {
				Image loadedImage = null;
					loadedImage = ImageIO.read(new File("images/tokens/token" + p.getGameToken() + ".png"));
					playerImages.add(loadedImage);
			}
			
			goImage = ImageIO.read(new File("images/board/go.gif"));
			chanceImage = ImageIO.read(new File("images/board/chance.gif"));
			chestImage = ImageIO.read(new File("images/board/communityChest.gif"));
			trainImage = ImageIO.read(new File("images/board/train.gif"));
			jailImage = ImageIO.read(new File("images/board/jail.png"));
			incomeTaxImage = ImageIO.read(new File("images/board/incomeTax.png"));
			goToJailImage = ImageIO.read(new File("images/board/goToJail.png"));
			luxuryTaxImage = ImageIO.read(new File("images/board/luxuryTax.png"));
			waterworksImage = ImageIO.read(new File("images/board/waterworks.png"));
			electricCompanyImage = ImageIO.read(new File("images/board/electricCompany.jpg"));
			freeParkingImage = ImageIO.read(new File("images/board/freeParking.png"));
			boardImage = ImageIO.read(new File("images/board/monopolyLogo.png"));
		} catch (IOException ioe) {
			System.out.println("Error Loading Player Image: " + ioe.getMessage());
		}
		
		// Default to a square
		this.setPreferredSize(new Dimension(715,715));
		
		// Have the board repaint if the mouse moves
		// This way we can make sure our mouseover text is up to date
		this.addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent arg0) {repaint();}
			public void mouseMoved(MouseEvent arg0) {repaint();}
		});
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		// Paint the Game Board
		paintBoard(g);
		
		// Paint the houses and hotels
		paintBuildings(g);
		
		// Paint the Players onto the Board
		paintPlayers(g);
	}
	
	private void paintBoard(Graphics g) {
		this.setPreferredSize(new Dimension(this.getHeight(),this.getHeight()));
		
		// Draw the board Background
		g.setColor(new Color(205, 230, 208));
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.BLACK);
		g.drawImage(boardImage, (getWidth()-boardImage.getWidth(null))/2, (getHeight()-boardImage.getHeight(null))/2, null);
		
		// Draw the board images
		drawImageAtLocation(g,goImage,0);
		drawImageAtLocation(g,trainImage,5);
		drawImageAtLocation(g,trainImage,15);
		drawImageAtLocation(g,trainImage,25);
		drawImageAtLocation(g,trainImage,35);
		drawImageAtLocation(g,chanceImage,7);
		drawImageAtLocation(g,chanceImage,22);
		drawImageAtLocation(g,chanceImage,36);
		drawImageAtLocation(g,chestImage,2);
		drawImageAtLocation(g,chestImage,17);
		drawImageAtLocation(g,chestImage,33);
		drawImageAtLocation(g,jailImage,10);
		drawImageAtLocation(g,incomeTaxImage,4);
		drawImageAtLocation(g,goToJailImage,30);
		drawImageAtLocation(g,luxuryTaxImage,38);
		drawImageAtLocation(g,electricCompanyImage,12);
		drawImageAtLocation(g,waterworksImage,28);
		drawImageAtLocation(g,freeParkingImage,20);
		
		g.setColor(Color.BLACK);
		//g.setStroke(new BasicStroke(3));
		// Get width and height of cells based on current dimensions
		int gridWidth = this.getWidth()/11;
		int width = this.getWidth();
		int gridHeight = this.getHeight()/11;
		int height = this.getHeight();
		
		// Draw our main horizontal lines.
		g.drawLine(0, 0, width, 0);
		g.drawLine(0, height-1, width, height-1);
		g.drawLine(0, gridHeight, width, gridHeight);
		g.drawLine(0, gridHeight*10, width, gridHeight*10);
		
		// Draw our main vertical lines
		g.drawLine(0, 0, 0, height);
		g.drawLine(width-1, 0, width-1, height);
		g.drawLine(gridWidth, 0, gridWidth, height);
		g.drawLine(gridWidth*10, 0, gridWidth*10, height);
		
		// Draw our horizontal lines
		for (int i = 1; i < 9; i++) {
			int currentHeight = gridHeight*(1+i);
			g.drawLine(0, currentHeight, gridWidth, currentHeight);
			g.drawLine(gridWidth*10, currentHeight, width, currentHeight);
		}
		
		// Draw our vertical lines
		for (int i = 1; i < 9; i++) {
			int currentX = gridWidth*(1+i);
			g.drawLine(currentX, 0, currentX, gridHeight);
			g.drawLine(currentX, gridHeight*10, currentX, height);
		}
		
		// Draw the Rectangle on Each property
		for (int i = 0; i < 40; i++) {
			// Get the upper left of our square
			int x = getXFromLocation(i);
			int y = getYFromLocation(i);
			
			if (!Constants.propertyLocations.contains(i)) continue;
			
			// get the color for the property
			Color currentColor = Color.BLACK;
			if (Constants.group1Locations.contains(i)) currentColor = Constants.group1Color;
			if (Constants.group2Locations.contains(i)) currentColor = Constants.group2Color;
			if (Constants.group3Locations.contains(i)) currentColor = Constants.group3Color;
			if (Constants.group4Locations.contains(i)) currentColor = Constants.group4Color;
			if (Constants.group5Locations.contains(i)) currentColor = Constants.group5Color;
			if (Constants.group6Locations.contains(i)) currentColor = Constants.group6Color;
			if (Constants.group7Locations.contains(i)) currentColor = Constants.group7Color;
			if (Constants.group8Locations.contains(i)) currentColor = Constants.group8Color;
			
			if (i < 11) {
				// In the top row.
				g.setColor(currentColor);
				g.fillRect(x, gridHeight*4/5, gridWidth, gridHeight/5);
				g.setColor(Color.BLACK);
				g.drawRect(x, gridHeight*4/5, gridWidth, gridHeight/5);
				
			} else if (i < 21) {
				// In the right Column
				g.setColor(currentColor);
				g.fillRect(x, y, gridWidth/5, gridHeight);
				g.setColor(Color.BLACK);
				g.drawRect(x, y, gridWidth/5, gridHeight);
				
			} else if (i < 31) {
				// In the bottom Row
				g.setColor(currentColor);
				g.fillRect(x, y, gridWidth, gridHeight/5);
				g.setColor(Color.BLACK);
				g.drawRect(x, y, gridWidth, gridHeight/5);
				
			} else {
				// In the left Column
				g.setColor(currentColor);
				g.fillRect(gridWidth*4/5, y, gridWidth/5, gridHeight);
				g.setColor(Color.BLACK);
				g.drawRect(gridWidth*4/5, y, gridWidth/5, gridHeight);
				
			}
		}
		
		// Draw a mouseover text for each property
		for (int i = 0; i < 40; i++) {
			if (isMouseWithinLocation(i)) {
				int currenty = gridHeight+20;
				g.drawString("Property: " + properties[i].getName(), gridWidth + 20, currenty);
				currenty += 20;
				if (properties[i].getOwner() != null) {
					g.drawString("Owner: " + properties[i].getOwner().getName(), gridWidth + 20, currenty);
					currenty += 20;
				}
				if (Constants.propertyLocations.contains(i)) {
					g.drawString("Current Rent: " + properties[i].getRent(), gridWidth+20, currenty);
				}
			}
		}
	}
	
	private void paintBuildings(Graphics g) {
		// calclate gride width and height for reference
		int gridHeight = getHeight()/11;
		int gridWidth = getWidth()/11;
		
		// Go through each property and check if it has houses / hotels to paint
		for (int i = 0; i < 40; i++) {
			// Check if this is a property that can be built on
			if (!Constants.propertyLocations.contains(i)) continue;
			
			// Get the upper left of our square
			int x = getXFromLocation(i);
			int y = getYFromLocation(i);
			Property p = properties[i];
			
			if (i < 11) {
				// Paint the Houses
				for (int houseNum = 0; houseNum < p.getNumHouses(); houseNum++) {
					g.setColor(Color.GREEN);
					g.fillRect(x+gridWidth/10+gridWidth*houseNum/5, gridHeight*4/5, gridWidth/5, gridHeight/5);
					g.setColor(Color.BLACK);
					g.drawRect(x+gridWidth/10+gridWidth*houseNum/5, gridHeight*4/5, gridWidth/5, gridHeight/5);
				}
				
				// Paint the hotel
				if (p.getHotel()) {
					g.setColor(Color.RED);
					g.fillRect(x+gridWidth/6, gridHeight*4/5, gridWidth*2/3, gridHeight/5);
					g.setColor(Color.BLACK);
					g.drawRect(x+gridWidth/6, gridHeight*4/5, gridWidth*2/3, gridHeight/5);
				}
				
			} else if (i < 21) {
				// Paint the Houses
				for (int houseNum = 0; houseNum < p.getNumHouses(); houseNum++) {
					g.setColor(Color.GREEN);
					g.fillRect(x, y+gridHeight/10+gridHeight*houseNum/5, gridWidth/5, gridHeight/5);
					g.setColor(Color.BLACK);
					g.drawRect(x, y+gridHeight/10+gridHeight*houseNum/5, gridWidth/5, gridHeight/5);
					
				}
				
				// Paint the hotel
				if (p.getHotel()) {
					g.setColor(Color.RED);
					g.fillRect(x, y+gridHeight/6, gridWidth/5, gridHeight*2/3);
					g.setColor(Color.BLACK);
					g.drawRect(x, y+gridHeight/6, gridWidth/5, gridHeight*2/3);
				}
			} else if (i < 31) {
				// Paint the Houses
				for (int houseNum = 0; houseNum < p.getNumHouses(); houseNum++) {
					g.setColor(Color.GREEN);
					g.fillRect(x+gridWidth/10+gridWidth*houseNum/5, y, gridWidth/5, gridHeight/5);
					g.setColor(Color.BLACK);
					g.drawRect(x+gridWidth/10+gridWidth*houseNum/5, y, gridWidth/5, gridHeight/5);
				}
				
				// Paint the hotel
				if (p.getHotel()) {
					g.setColor(Color.RED);
					g.fillRect(x+gridWidth/6, y, gridWidth*2/3, gridHeight/5);
					g.setColor(Color.BLACK);
					g.drawRect(x+gridWidth/6, y, gridWidth*2/3, gridHeight/5);
				}
			} else {
				// Paint the House
				for (int houseNum = 0; houseNum < p.getNumHouses(); houseNum++) {
					g.setColor(Color.GREEN);
					g.fillRect(gridWidth*4/5, y+gridHeight/10+gridHeight*houseNum/5, gridWidth/5, gridHeight/5);
					g.setColor(Color.BLACK);
					g.drawRect(gridWidth*4/5, y+gridHeight/10+gridHeight*houseNum/5, gridWidth/5, gridHeight/5);
				}
				
				if (p.getHotel()) {
					g.setColor(Color.RED);
					g.fillRect(gridWidth*4/5, y+gridHeight/6, gridWidth/5, gridHeight*2/3);
					g.setColor(Color.BLACK);
					g.drawRect(gridWidth*4/5, y+gridHeight/6, gridWidth/5, gridHeight*2/3);
				}
			}
		}
	}
	
	// Method to paint all the players to the game board.
	private void paintPlayers(Graphics g) {
		// keep track of squares with multiple players on them.
		int[] numPlayers = new int[40];
		for (Player p : players) {
			numPlayers[p.getCurrentLocation()]++;
		}
		
		// Number of players we've already placed on that square
		int[] currPlayers = new int[40];
		
		for (int i = 0; i < players.size(); i++) {
			Player p = players.get(i);
			int loc = p.getCurrentLocation();
			
			
			int xOffset = 0;
			int yOffset = 0;
			if (numPlayers[loc] == 1) {
				xOffset = getWidth()/55;
				yOffset = getHeight()/55;
			} else if (numPlayers[loc] < 5) {
				xOffset = (1+currPlayers[loc]%2)*getWidth()/33;
				yOffset = (1+currPlayers[loc]/2)*getWidth()/33;
			} else {
				xOffset = (currPlayers[loc]%3)*getWidth()/33;
				yOffset = (currPlayers[loc]/3)*getWidth()/33;
			}
			
			int x = getXFromLocation(p.getCurrentLocation()) + xOffset;
			int y = getYFromLocation(p.getCurrentLocation()) + yOffset;
			Image img = playerImages.get(i);
			if (numPlayers[loc] == 1) {
				g.drawImage(img, x, y, getWidth()*3/55, getHeight()*3/55, null);
			} else {
				g.drawImage(img, x, y, getWidth()/33, getHeight()/33, null);
			}
			
			// increment number of players on the square so they can be positioned correctly.
			currPlayers[loc]++;
		}
	}
	
	// Helper function to get the X coordinate of upper left of a location
	private int getXFromLocation(int location) {
		int result = 0;
		if (location < 11) {
			// In the top row.
			result = location * (this.getWidth()/11);
		} else if (location < 21) {
			// In the right Column
			result = (this.getWidth()/11)*10;
		} else if (location < 31) {
			// In the bottom Row
			result = (location - 30) * (0-1) * (this.getWidth()/11);
		} else {
			// In the left Column
			result = 0;
		}
		return result;
	}
	
	// Helper function to get the Y coordinate of upper left of a location.
	private int getYFromLocation(int location) {
		int result = 0;
		if (location < 11) {
			// In the top row.
			result = 0;
		} else if (location < 20) {
			// In the right Column
			result = (location - 10) * (this.getHeight()/11);
		} else if (location < 31) {
			// In the bottom Row
			result = (this.getHeight()/11)*10;
		} else {
			// In the left Column
			result = (location - 40) * (0-1) * (this.getHeight()/11);
		}
		return result;
	}
	
	// Helper function to draw an image to a location
	private void drawImageAtLocation(Graphics g, Image i, int location) {
		int x = getXFromLocation(location);
		int y = getYFromLocation(location);
		g.drawImage(i, x, y, getWidth()/11, getHeight()/11, null);
	}
	
	// Returns whether or not the mouse is over a property location
	private boolean isMouseWithinLocation(int l)
	{
	    Point mousePos = MouseInfo.getPointerInfo().getLocation();
	    Rectangle bounds = new Rectangle((int)this.getLocationOnScreen().getX()+getXFromLocation(l),
	    			(int)this.getLocationOnScreen().getY()+getYFromLocation(l),
	    			getWidth()/11,
	    			getHeight()/11);
	    return bounds.contains(mousePos);
	}
}
