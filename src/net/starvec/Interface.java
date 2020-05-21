package net.starvec;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.JList;
import java.awt.Font;
import java.awt.Image;

import javax.swing.AbstractListModel;
import javax.swing.JPanel;
import javax.swing.ScrollPaneConstants;
import java.awt.Toolkit;
import javax.swing.JTabbedPane;
import javax.swing.JSplitPane;

public class Interface {

	private JFrame frmMercedCollege;
	private SpringLayout springLayout;
	
	JTabbedPane tabbedPaneGraph;
	
	Connection dbConnection;
	
	private static ArrayList<PurpleAir> sensors;
	private ArrayList<String> sensorDisplayNames;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) 
	{
		Connection dbConnection = null;
		
		ArrayList<String> sensorDisplayNames = new ArrayList<>();
		sensorDisplayNames.add("Sensor Alpha");
		sensorDisplayNames.add("Sensor Beta");
		sensorDisplayNames.add("Sensor Gamma");
		sensorDisplayNames.add("Sensor Delta");
		
		EventQueue.invokeLater(new Runnable()
		{
			public void run() 
			{
				try {
					Interface window = new Interface(dbConnection, sensors, sensorDisplayNames);
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Interface(Connection dbConnection, ArrayList<PurpleAir> sensors, ArrayList<String> sensorDisplayNames) 
	{
		this.dbConnection = dbConnection;
		this.sensors = sensors;
		this.sensorDisplayNames = sensorDisplayNames;
		initialize(sensors, sensorDisplayNames);
		frmMercedCollege.setVisible(true);
	}

	private void initialize(ArrayList<PurpleAir> sensors, ArrayList<String> sensorDisplayNames) 
	{
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		
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
		
		frmMercedCollege = new JFrame();
		frmMercedCollege.setTitle("Merced College - Air Quality Monitor");
		frmMercedCollege.setIconImages(icons);
		frmMercedCollege.setBounds(100, 100, 1280, 720);
		frmMercedCollege.setLocationRelativeTo(null);
		frmMercedCollege.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		springLayout = new SpringLayout();
		frmMercedCollege.getContentPane().setLayout(springLayout);
		frmMercedCollege.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	    frmMercedCollege.addWindowListener(new WindowAdapter() {
	        @Override
	        public void windowClosing(WindowEvent event) 
	        {
	        	DBAction.closeDatabaseConnection(dbConnection);
	        	frmMercedCollege.dispose();
	        	System.exit(0);
	        }
	    });
		
		JScrollPane scrollPaneSensors = new JScrollPane();
		scrollPaneSensors.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		springLayout.putConstraint(SpringLayout.NORTH, scrollPaneSensors, 32, SpringLayout.NORTH, frmMercedCollege.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, scrollPaneSensors, 0, SpringLayout.WEST, frmMercedCollege.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPaneSensors, 0, SpringLayout.SOUTH, frmMercedCollege.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, scrollPaneSensors, 256, SpringLayout.WEST, frmMercedCollege.getContentPane());
		frmMercedCollege.getContentPane().add(scrollPaneSensors);
			
		JButton btnAddSensor = new JButton("Add");
		springLayout.putConstraint(SpringLayout.EAST, btnAddSensor, 84, SpringLayout.WEST, frmMercedCollege.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, btnAddSensor, 0, SpringLayout.NORTH, scrollPaneSensors);
		springLayout.putConstraint(SpringLayout.NORTH, btnAddSensor, 0, SpringLayout.NORTH, frmMercedCollege.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, btnAddSensor, 0, SpringLayout.WEST, frmMercedCollege.getContentPane());
		frmMercedCollege.getContentPane().add(btnAddSensor);
		
		btnAddSensor.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				EventQueue.invokeLater(new Runnable() 
				{
					public void run() 
					{
						try 
						{
							UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
							InterfaceAddSensor window = new InterfaceAddSensor(sensors, sensorDisplayNames, dbConnection, frmMercedCollege);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		
		JButton btnEditSensor = new JButton("Edit");
		springLayout.putConstraint(SpringLayout.NORTH, btnEditSensor, 0, SpringLayout.NORTH, frmMercedCollege.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, btnEditSensor, 0, SpringLayout.EAST, btnAddSensor);
		springLayout.putConstraint(SpringLayout.SOUTH, btnEditSensor, 0, SpringLayout.NORTH, scrollPaneSensors);
		frmMercedCollege.getContentPane().add(btnEditSensor);
		
		JList<String> listSensors = new JList<String>();
		listSensors.setModel(new AbstractListModel<String>() 
		{
			public int getSize() {
				return sensorDisplayNames.size();
			}
			
			public String getElementAt(int index) {
				return sensorDisplayNames.get(index);
			}
		});
		
		scrollPaneSensors.setViewportView(listSensors);
		
		JButton btnDeleteSensor = new JButton("Delete");
		springLayout.putConstraint(SpringLayout.EAST, btnEditSensor, 0, SpringLayout.WEST, btnDeleteSensor);
		springLayout.putConstraint(SpringLayout.WEST, btnDeleteSensor, -85, SpringLayout.EAST, scrollPaneSensors);
		springLayout.putConstraint(SpringLayout.EAST, btnDeleteSensor, -1, SpringLayout.EAST, scrollPaneSensors);
		springLayout.putConstraint(SpringLayout.EAST, btnEditSensor, 0, SpringLayout.WEST, btnDeleteSensor);
		springLayout.putConstraint(SpringLayout.NORTH, btnDeleteSensor, 0, SpringLayout.NORTH, frmMercedCollege.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, btnDeleteSensor, 0, SpringLayout.NORTH, scrollPaneSensors);
		frmMercedCollege.getContentPane().add(btnDeleteSensor);
		
		tabbedPaneGraph = new JTabbedPane(JTabbedPane.TOP);
		springLayout.putConstraint(SpringLayout.SOUTH, tabbedPaneGraph, -240, SpringLayout.SOUTH, frmMercedCollege.getContentPane());
		springLayout.putConstraint(SpringLayout.NORTH, tabbedPaneGraph, 0, SpringLayout.NORTH, frmMercedCollege.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, tabbedPaneGraph, 0, SpringLayout.EAST, scrollPaneSensors);
		springLayout.putConstraint(SpringLayout.EAST, tabbedPaneGraph, 0, SpringLayout.EAST, frmMercedCollege.getContentPane());
		frmMercedCollege.getContentPane().add(tabbedPaneGraph);
		
		JPanel panelParticleCountGraph = new JPanel();
		panelParticleCountGraph.setBorder(null);
		tabbedPaneGraph.addTab("Particle Count", null, panelParticleCountGraph, null);
		tabbedPaneGraph.setEnabledAt(0, true);
		panelParticleCountGraph.setLayout(new SpringLayout());
		
		JPanel panelParticleMassGraph = new JPanel();
		tabbedPaneGraph.addTab("Particle Mass", null, panelParticleMassGraph, null);
		panelParticleMassGraph.setLayout(new SpringLayout());
		
		JPanel panelTPH = new JPanel();
		tabbedPaneGraph.addTab("Temperature/Humidity/Pressure", null, panelTPH, null);
		panelTPH.setLayout(new SpringLayout());
		
		JSplitPane splitPaneMultiViewGraph = new JSplitPane();
		tabbedPaneGraph.addTab("Side-By-Side", null, splitPaneMultiViewGraph, null);
		
		JPanel panelMultiViewGraphA = new JPanel();
		splitPaneMultiViewGraph.setLeftComponent(panelMultiViewGraphA);
		
		JPanel panelMultiViewGraphB = new JPanel();
		splitPaneMultiViewGraph.setRightComponent(panelMultiViewGraphB);
		
		frmMercedCollege.addComponentListener(new ComponentAdapter() 
		{
		    public void componentResized(ComponentEvent componentEvent) {
		        handleWindowResize();
		    }
		    
		    public void componentMoved(ComponentEvent componentEvent) {
		        handleWindowResize();
		    }
		});
	}
	
	public void handleWindowResize()
	{
		int width = frmMercedCollege.getSize().width;
        int height = frmMercedCollege.getSize().height;
        
        //springLayout.putConstraint(SpringLayout.SOUTH, panelParticleCount, (int)(height/-2.0), SpringLayout.SOUTH, frmMercedCollege.getContentPane());
		//springLayout.putConstraint(SpringLayout.EAST, panelParticleCount, (int)((width - 256)/-2.0), SpringLayout.EAST, frmMercedCollege.getContentPane());
		
		//springLayout.putConstraint(SpringLayout.WEST, panelParticleMass, (int)((width - 256)/-2.0), SpringLayout.EAST, frmMercedCollege.getContentPane());
		//springLayout.putConstraint(SpringLayout.SOUTH, panelParticleMass, (int)(height/-2.0), SpringLayout.SOUTH, frmMercedCollege.getContentPane());
	}
}
