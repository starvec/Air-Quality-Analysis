package net.starvec;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class InterfaceMainInitialization {

	private JFrame frame;
	
	private JProgressBar progressBar;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					InterfaceMainInitialization window = new InterfaceMainInitialization();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public InterfaceMainInitialization() 
	{
		initialize();
		frame.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() 
	{
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		
		UIManager.put("ProgressBar.selectionForeground", Color.white);
		UIManager.put("ProgressBar.selectionBackground", Color.black);

	    List<Image> icons = new ArrayList<Image>();
		icons.add(Toolkit.getDefaultToolkit().getImage(Interface.class.getResource("/net/starvec/mclogo16.png")));
		icons.add(Toolkit.getDefaultToolkit().getImage(Interface.class.getResource("/net/starvec/mclogo20.png")));
		icons.add(Toolkit.getDefaultToolkit().getImage(Interface.class.getResource("/net/starvec/mclogo24.png")));
		icons.add(Toolkit.getDefaultToolkit().getImage(Interface.class.getResource("/net/starvec/mclogo32.png")));
		icons.add(Toolkit.getDefaultToolkit().getImage(Interface.class.getResource("/net/starvec/mclogo40.png")));
		icons.add(Toolkit.getDefaultToolkit().getImage(Interface.class.getResource("/net/starvec/mclogo48.png")));
		icons.add(Toolkit.getDefaultToolkit().getImage(Interface.class.getResource("/net/starvec/mclogo60.png")));
		icons.add(Toolkit.getDefaultToolkit().getImage(Interface.class.getResource("/net/starvec/mclogo64.png")));
		icons.add(Toolkit.getDefaultToolkit().getImage(Interface.class.getResource("/net/starvec/mclogo72.png")));
		icons.add(Toolkit.getDefaultToolkit().getImage(Interface.class.getResource("/net/starvec/mclogo128.png")));
		
		frame = new JFrame();
		frame.setUndecorated(true);
		frame.setIconImages(icons);
		frame.setBounds(100, 100, 300, 50);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SpringLayout springLayout = new SpringLayout();
		frame.getContentPane().setLayout(springLayout);	
		
		JLabel lbl = new JLabel("Initializing");
		lbl.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lbl.setHorizontalAlignment(SwingConstants.CENTER);
		springLayout.putConstraint(SpringLayout.NORTH, lbl, 4, SpringLayout.NORTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, lbl, 4, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, lbl, -4, SpringLayout.EAST, frame.getContentPane());
		frame.getContentPane().add(lbl);
		
		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		progressBar.setForeground(new Color(51, 187, 76));
		springLayout.putConstraint(SpringLayout.NORTH, progressBar, 4, SpringLayout.SOUTH, lbl);
		springLayout.putConstraint(SpringLayout.WEST, progressBar, 4, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, progressBar, -4, SpringLayout.SOUTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, progressBar, -4, SpringLayout.EAST, frame.getContentPane());
		frame.getContentPane().add(progressBar);
	}

}
