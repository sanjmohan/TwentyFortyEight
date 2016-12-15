// File representing game piece tiles

package sam.game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 * Class representing square Twenty Forty Eight game tiles
 */
public class SquarePiece
{
	private int value;
	private String valueStr;	// for number in middle
	private boolean isNewTile;	// tile [about to be] combined (not for new tile spawns)
	private int x;				// current x
	private int y;				// current y
	public static final int DX = 7;		// should be factor of 56
	public static final int DY = 7;

	/**
	 * Construct a SquarePiece with default coords (0, 0)
	 * @param val value of this tile
	 */
	protected SquarePiece(int val)
	{
		value = val;
		valueStr = Integer.toString(val);
		isNewTile = false;
		
		x = 0;
		y = 0;
	}
	
	/**
	 * Construct a SquarePiece
	 * @param val value of this tile
	 * @param x2 x coord of tile
	 * @param y2 y coord of tile
	 */
	protected SquarePiece(int val, int x2, int y2)
	{
		value = val;
		valueStr = Integer.toString(val);
		isNewTile = false;
		
		x = x2;
		y = y2;
	}
	
	/**
	 * Set coordinates to given values
	 * @param x2 x coord to set
	 * @param y2 y coord to set
	 */
	protected void setCoords(int x2, int y2)
	{
		x = x2;
		y = y2;
	}
	
	/**
	 * Get value of tile
	 * @return value of tile as integer
	 */
	protected int getValue()
	{
		return value;
	}
	
	/**
	 * Return "new" state of tile
	 * @return value of isNewTile
	 */
	protected boolean isNew()
	{
		return isNewTile;
	}
	
	/**
	 * Set "new" attribute
	 * @param isNew value to set isNewTile to
	 */
	protected void setNew(boolean isNew)
	{
		isNewTile = isNew;
	}

	/**
	 * Paint this tile
	 * @param g Graphics2D object with which to draw this tile
	 * @param x2 x coord in window to draw this tile at
	 * @param y2 y coord in window to draw this tile at
	 */
	protected void drawSquarePiece(Graphics2D g, int x2, int y2)
	{
		Color c = g.getColor();
		g.setColor(getColor());
		if (x < x2)
			x += DX;
		else if (x > x2)
			x -= DX;
		if (y < y2)
			y += DY;
		else if (y > y2)
			y -= DY;
		
		g.setColor(getColor());
		g.fillRoundRect(x, y, 100, 100, 30, 30);
		g.setColor(Color.BLACK);
		g.setFont(new Font("TimesRoman", Font.PLAIN, 25));
		g.drawString(valueStr, x+50 - valueStr.length()*6, y + 62);		// x coord: center of square, offset by multiple of string length
		g.setColor(c);
	}
	
	/**
	 * Return color of this tile based on current value of tile
	 */
	private Color getColor()
	{
		if (value == 2)
			return Color.RED;
		else if (value == 4)
			return Color.ORANGE;
		else if (value == 8)
			return Color.YELLOW;
		else if (value == 16)
			return Color.BLUE;
		else if (value == 32)
			return Color.GREEN;
		else if (value == 64)
			return Color.LIGHT_GRAY;
		else if (value == 128)
			return Color.MAGENTA;
		else if (value == 256)
			return Color.CYAN;
		else
			return Color.PINK;
	}
}
