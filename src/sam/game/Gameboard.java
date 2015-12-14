// Sanjay
package sam.game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;


/*	Grid:	(66, 66)	(122, 66)	(178, 66)	(234, 66)
 * 
 * 			(66, 122)	________	________	________
 * 
 * 			(66, 178)	________	________	________
 * 
 * 			(66, 234)	________	________	________
 * 
 * 	matrix to grid pixels:	i*56 + 10, j*56 + 10
 * 
 *  directions: 0 => up; 1 => right; 2 => down; 3 => left
 * 
 *  NOTE: matrix[x][y] refers to a spot down x, across y
 *  	  screenCoords[x][y] refers to a point across x, down y
 *  	In this program, pieces coords are inverses of each SquarePiece's screen/visual coordinates
 *  
 *  For Revision:
 *  	JAVA ENUM
 *  	comments
 *  	clean
 *  	fix imports
 *  	APPLET!
 *  	change all pieces coords to inverses and change g.drawSquarePiece() accordingly
 */

public class Gameboard extends JPanel implements ActionListener
{
	private SquarePiece[][] pieces;		// "real" spots at even indices (0, 2, 4...)
	private int score;
	private boolean gameOver;
	private int updates;
	private javax.swing.Timer t;
	private boolean currentlyMoving;
	protected ArrayList<Integer> moveList;	// queue of moves; first moves at index 0
	
	protected Gameboard()
	{
		super();
		moveList = new ArrayList<Integer>();
		addKey("UP");
		addKey("LEFT");
		addKey("DOWN");
		addKey("RIGHT");
		
		score = 0;
		updates = 0;
		gameOver = false;
		t = new javax.swing.Timer(2, this);
		currentlyMoving = false;
		this.setVisible(true);
		this.setFocusable(true);
		pieces = new SquarePiece[8][8];
		makeNewPiece();
		makeNewPiece();
	}
	
	private void addKey(String key)
	{
		final String tag = new String(key);		// final in order to use anonymous class
		this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key), tag);
		
		this.getActionMap().put(tag, new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (tag.equals("UP"))
					addMove(0);
				else if (tag.equals("RIGHT"))
					addMove(1);
				else if (tag.equals("LEFT"))
					addMove(3);
				else if (tag.equals("DOWN"))
					addMove(2);
			}
		});		
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D)g;
		super.paintComponent(g);
		g2.setColor(Color.BLACK);
		
		/* draw lines */
		for (int i = 0; i < 3; i++)
			g2.drawLine(i * 112 + 115, 20, i * 112 + 115, 440);
		for (int j = 0; j < 3; j++)
			g2.drawLine(15, j * 112 + 115, 435, j * 112 + 115);

		/* draw all of the pieces */
		for (int i = 0; i <  pieces.length; i++)
		{
			for (int j = 0; j < pieces[i].length; j++)
			{
				SquarePiece sq = pieces[i][j];
				if (sq != null)
					sq.drawSquarePiece(g2, i*56 + 10, j*56 + 10);
			}
		}

		if (gameOver)
		{
			g2.setColor(Color.WHITE);
			g2.fillRect(0, 200, 500, 60);
			g2.setColor(Color.BLACK);
			g2.setFont(new Font("TimesRoman", Font.BOLD, 65));
			g2.drawString("GAME OVER", 20, 250);
		}
	}
	
	private void makeNewPiece()
	{
		int val = Math.random() < 0.9 ? 2 : 4;				// (boolean statement) ? (true result) : (false result);
		SquarePiece sq = new SquarePiece(val);
		ArrayList<Point> empties = new ArrayList<Point>();
		for (int i = 0; i < pieces.length; i+=2)			// += 2 to only get "real" spots
			for(int j = 0; j < pieces[i].length; j+=2)
				if (pieces[i][j] == null)
					empties.add(new Point(i, j));			// make a list of all empty spaces in pieces
		if (empties.size() > 0);
		{
			Point newPt = empties.get((int)(Math.random()*empties.size()));		// choose random empty point from the list
			int x = (int)newPt.getX();
			int y = (int)newPt.getY();
			sq.setCoords(x *56 + 10, y *56 + 10);	// set new piece coords to new point
			pieces[x][y] = sq;		// add the new SquarePiece to the empty point in pieces
		}
	}
	
	protected int getScore()
	{
		return score;
	}
	
	protected boolean checkGameOver()
	{
		for (int i = 0; i < pieces.length; i += 2)
		{
			for (int j = 0; j < pieces[0].length; j += 2)
			{
				if (pieces[i][j] == null)			// if this spot is empty
					return false;
				else if (i + 2 < pieces.length && pieces[i+2][j] == null)	 // if there's a next row and nothing in that row
					return false;
				else if (i + 2 < pieces.length && pieces[i][j].getValue() == pieces[i+2][j].getValue())	// if there's a next row and something equal in that row
					return false;
				else if (j + 2 < pieces[0].length && pieces[i][j+2] == null) // if there's a next column and nothing in that column
					return false;
				else if (j + 2 < pieces[0].length && pieces[i][j].getValue() == pieces[i][j+2].getValue())	// if there's a next column and something equal in that column
					return false;
			}
		}
		return true;
	}
	
	// used by timer in move()
	@Override
	public void actionPerformed(ActionEvent e)
	{
		repaint();
		updates++;
		if (updates >= (336 / SquarePiece.DX))		// if repainted enough times to move a piece across the board, then reset
		{
			updates = 0;
			t.stop();
			combine(moveList.remove(0));				// combine pieces that should be combined and remove current move from queue
			for (int i = 0; i <  pieces.length; i++)	// make all of the pieces not New
			{
				for (int j = 0; j < pieces[i].length; j++)
				{
					SquarePiece sq = pieces[i][j];
					if (sq != null)
						sq.setNew(false);
				}
			}
			currentlyMoving = false;
			makeNewPiece();
			repaint();
			gameOver = checkGameOver();		// check gameOver
			move();							// go to next move in moveList, if any
		}
	}
	
	// any piece in an odd row or odd column is ready to be combined
	private void combine(int direction)
	{
		if (direction == 0)
			combineUp();
		else if (direction == 1)
			combineRight();
		else if (direction == 2)
			combineDown();
		else if (direction == 3)
			combineLeft();
	}
	
	private void combineUp()
	{
		for (int i = 0; i < pieces.length - 1; i+=2)	// even row, odd column	(remember, pieces coords are inverses of screen coords)
		{
			for (int j = 1; j < pieces[i].length - 2; j+=2)
			{
				if (pieces[i][j] != null)
				{
					pieces[i][j-1] = new SquarePiece(pieces[i][j].getValue() * 2, i*56 + 10, (j-1)*56 + 10);	// yay a new piece!
					pieces[i][j-1].setNew(true);				// to prevent unwanted other combinations in the same turn
					score += pieces[i][j].getValue() * 2;		// update score by adding twice value of old piece
					pieces[i][j] = null;
				}
			}
		}
	}
	
	private void combineRight()
	{
		for (int i = 1; i < pieces.length - 2; i+=2)	// odd row, even column
		{
			for (int j = 0; j < pieces[i].length - 1; j+=2)
			{
				if (pieces[i][j] != null)
				{
					pieces[i+1][j] = new SquarePiece(pieces[i][j].getValue() * 2, (i+1)*56 + 10, j*56 + 10);	// yay a new piece!
					pieces[i+1][j].setNew(true);				// to prevent unwanted other combinations in the same turn
					score += pieces[i][j].getValue() * 2;		// update score by adding twice value of old piece
					pieces[i][j] = null;
				}
			}
		}
	}
	
	private void combineDown()
	{
		for (int i = 0; i < pieces.length - 1; i+=2)	// odd row, even column
		{
			for (int j = 1; j < pieces[i].length - 2; j+=2)
			{
				if (pieces[i][j] != null)
				{
					pieces[i][j+1] = new SquarePiece(pieces[i][j].getValue() * 2, i*56 + 10, (j+1)*56 + 10);	// yay a new piece!
					pieces[i][j+1].setNew(true);				// to prevent unwanted other combinations in the same turn
					score += pieces[i][j].getValue() * 2;		// update score by adding twice value of old piece
					pieces[i][j] = null;
				}
			}
		}
	}
	
	private void combineLeft()
	{
		for (int i = 1; i < pieces.length - 2; i+=2)	// even row, odd column
		{
			for (int j = 0; j < pieces[i].length - 1; j+=2)
			{
				if (pieces[i][j] != null)
				{
					pieces[i-1][j] = new SquarePiece(pieces[i][j].getValue() * 2, (i-1)*56 + 10, j*56 + 10);	// yay a new piece!
					pieces[i-1][j].setNew(true);				// to prevent unwanted other combinations in the same turn
					score += pieces[i][j].getValue() * 2;		// update score by adding twice value of old piece
					pieces[i][j] = null;
				}
			}
		}
	}
	
	
	protected void addMove(int direction)
	{
		moveList.add(new Integer(direction));
		move();
	}
	
	// makes move, starts animation
	private void move()
	{
		boolean movedThisTurn = false;
		if (currentlyMoving || moveList.size() == 0)
			return;
		if (moveList.get(0) == 0)			// process move (this move is removed in actionPerformed at end of piece translation, or at end of this method)
			movedThisTurn = moveUp();
		else if (moveList.get(0) == 1)
			movedThisTurn = moveRight();
		else if (moveList.get(0) == 2)
			movedThisTurn = moveDown();
		else if (moveList.get(0) == 3)
			movedThisTurn = moveLeft();
		
		if (movedThisTurn)
		{
			currentlyMoving = true;
			t.setDelay(2);			// just so i dont have to look up at constructorso change delay
			t.start();
		}
		else						// if no squares shifted, remove the current move from queue and checks for more moves
		{
			moveList.remove(0);
			move();					// prevents it from getting stuck after button mashing
		}
	}
	
	// returns true if any pieces moved, else false
	private boolean moveUp()
	{
		System.out.println("up!");
		boolean moved = false;		// if anything moves, becomes true; used to only spawn new pieces after valid moves
		for (int i = 0; i < pieces.length; i++)
		{
			for (int j = 2; j < pieces.length; j++)		// process top down
			{
				int i2 = i, j2 = j;
				if (pieces[i2][j2] != null)
				{
					// if two lower is empty, and
					// if three lower is in bounds, then if that spot is empty, make it one lower
					while (j2-2 >= 0 && pieces[i2][j2-2] == null && pieces[i2][j2-1] == null)
					{
						moved = true;
						pieces[i2][j2-2] = pieces[i2][j2];
						pieces[i2][j2] = null;
						j2-=2;
					}
					if (i2%2 == 0 && j2%2 == 0 && j2-2 >= 0 && 
							pieces[i2][j2-2].getValue() == pieces[i2][j2].getValue() && !pieces[i2][j2-2].isNew())
					{
						pieces[i2][j2-1] = pieces[i2][j2];
						pieces[i2][j2-2].setNew(true);
						pieces[i2][j2] = null;
						moved = true;
					}
				}
			}
		}
		return moved;
	}

	// returns true if any pieces moved, else false
	private boolean moveDown()
	{
		System.out.println("down!");
		boolean moved = false;		// if anything moves, becomes true
		for (int i = 0; i < pieces.length; i++)
		{
			for (int j = pieces[i].length-3; j >= 0; j--)		// process bottom up
			{
				int i2 = i, j2 = j;
				if (pieces[i2][j2] != null)
				{
					while (j2+2 < pieces[i2].length && pieces[i2][j2+2] == null && pieces[i2][j2+1] == null)
					{
						moved = true;
						pieces[i2][j2+2] = pieces[i2][j2];
						pieces[i2][j2] = null;
						j2+=2;
					}
					if (i2%2 == 0 && j2%2 == 0 && j2+2 < pieces[i2].length && 
							pieces[i2][j2+2].getValue() == pieces[i2][j2].getValue() && !pieces[i2][j2+2].isNew())
					{
						pieces[i2][j2+1] = pieces[i2][j2];
						pieces[i2][j2+2].setNew(true);
						pieces[i2][j2] = null;
						moved = true;
					}
				}
			}
		}
		return moved;
	}
	
	// returns true if any pieces moved, else false
	private boolean moveLeft()
	{
		System.out.println("left!");
		boolean moved = false;		// if anything moves, becomes true
		for (int i = 2; i < pieces.length; i++)		// process left to right
		{
			for (int j = 0; j < pieces.length; j++)
			{
				int i2 = i, j2 = j;
				if (pieces[i2][j2] != null)
				{
					while (i2-2 >= 0 && pieces[i2-2][j2] == null && pieces[i2-1][j2] == null)
					{
						moved = true;
						pieces[i2-2][j2] = pieces[i2][j2];
						pieces[i2][j2] = null;
						i2-=2;
					}
					if (i2%2 == 0 && j2%2 == 0 && i2-2 >= 0 && 
							pieces[i2-2][j2].getValue() == pieces[i2][j2].getValue() && !pieces[i2-2][j2].isNew())
					{
						pieces[i2-1][j2] = pieces[i2][j2];
						pieces[i2-2][j2].setNew(true);
						pieces[i2][j2] = null;
						moved = true;
					}
				}
			}
		}
		return moved;
	}
	
	// returns true if any pieces moved, else false
	private boolean moveRight()
	{
		System.out.println("right!");
		boolean moved = false;		// if anything moves, becomes true
		for (int i = pieces.length - 3; i >= 0; i--)		// process right to left
		{
			for (int j = 0; j < pieces.length; j++)
			{
				if (pieces[i][j] != null)
				{
					int i2 = i, j2 = j;
					while (i2+2 < pieces.length && pieces[i2+2][j2] == null && pieces[i2+1][j2] == null)
					{
						moved = true;
						pieces[i2+2][j2] = pieces[i2][j2];
						pieces[i2][j2] = null;
						i2+=2;
					}
					if (i2%2 == 0 && j2%2 == 0 && i2+2 < pieces.length && 
							pieces[i2+2][j2].getValue() == pieces[i2][j2].getValue() && !pieces[i2+2][j2].isNew())
					{
						pieces[i2+1][j2] = pieces[i2][j2];
						pieces[i2+2][j2].setNew(true);
						pieces[i2][j2] = null;
						moved = true;
					}
				}
			}
		}
		return moved;
	}
	
	// ends timer, makes new pieces matrix, resets score and moveList, creates starting piece
	protected void makeNewGame()
	{
		t.stop();
		pieces = new SquarePiece[8][8];
		makeNewPiece();
		makeNewPiece();
		score = 0;
		gameOver = false;
		updates = 0;
		currentlyMoving = false;
		moveList = new ArrayList<Integer>();
		repaint();
	}
}