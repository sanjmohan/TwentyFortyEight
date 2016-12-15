// GUI for Twenty Forty Eight game

package sam.game;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * GUI for game
 */
public class TwentyFortyEight
{
	/**
	 * Initializes GUI
	 * @param args Command line arguments (not used here)
	 */
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				init();				
			}	
		});
	}
	
	/**
	 * Initialize/construct GUI components
	 */
	private static void init()
	{
		/* board needs focus for arrow key inputs to work, so other components must not be focusable */
		
		final Gameboard board = new Gameboard();	// final for inner classes wtf
		board.setVisible(true);
		board.setBackground(Color.WHITE);
		
		JFrame frame = new JFrame("2048!");
		frame.setBounds(400, 200, 470, 720);		// (x, y, width, height)
		
		JPanel top = new JPanel();
		top.setVisible(true);
		top.setLayout(new BorderLayout());
		
		JPanel heading = new JPanel();
		heading.setLayout(new BorderLayout());
		heading.setBackground(Color.WHITE);
		heading.setFocusable(false);
		
		JTextArea txt = new JTextArea("\n     2048!");
		txt.setEditable(false);
		txt.setFocusable(false);
		txt.setFont(new Font("TimesRoman", Font.PLAIN, 50));
		
		JLabel v2 = new JLabel("V. 2.0");
		v2.setBorder(new EmptyBorder(0, 200, 0, 0)); 	// padding
		v2.setFocusable(false);
		
		final JLabel score = new JLabel() {
			@Override
			public void paintComponent(Graphics g) 		// every repaint(), calls board.getScore()
			{
				super.paintComponent(g);
				g.setColor(Color.BLACK);
				g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
				g.drawString("Score: " + board.getScore(), 50, 50);
			}
		};
		score.setFocusable(false);
		Timer update = new Timer(50, new ActionListener() {		// to constantly refresh score
			public void actionPerformed(ActionEvent e)
			{
				score.repaint();
			}
		});
		update.start();
		
		JButton button = new JButton("New Game");
		button.setFont(new Font("TimesRoman", Font.ITALIC, 20));
		button.setBackground(Color.CYAN);
		button.setFocusable(false);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				board.makeNewGame();
			}
		});
		
		heading.add(txt, BorderLayout.NORTH);
		heading.add(v2, BorderLayout.SOUTH);
		heading.add(score, BorderLayout.CENTER);
		heading.add(button, BorderLayout.EAST);
		heading.setPreferredSize(new Dimension(400, 200));
		
		top.add(heading, BorderLayout.NORTH);
		top.add(board, BorderLayout.CENTER);
		
		frame.add(top);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		board.requestFocus();
	}
}
