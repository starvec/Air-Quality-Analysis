package net.starvec;

import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.AbstractListModel;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Component;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JList;
import javax.swing.JSeparator;
import javax.swing.ScrollPaneConstants;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class InterfaceAddSensor 
{
	private JFrame frame;
	private JTextField textFieldSearch;
	private JTextField textFieldSensorID;
	private JTextField textFieldSensorName;
	private JButton btnSearch;
	private JList<String> listSensor;

	private static ArrayList<Sensor> sensorIdAndNames = new ArrayList<>();
	private static ArrayList<PurpleAir> sensors;
	private static ArrayList<String> sensorDisplayNames;

	private static Connection dbConnection;
	private static Component parentComponent;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		Connection dbConnection = DBAction.openDatabaseConnection("data.db");

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try 
				{
					sensors = new ArrayList<>();
					sensorDisplayNames = new ArrayList<>();
					InterfaceAddSensor window = new InterfaceAddSensor(sensors, sensorDisplayNames, dbConnection, null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public InterfaceAddSensor(ArrayList<PurpleAir> sensors, ArrayList<String> sensorDisplayNames, Connection dbConnection, Component parentComponent) 
	{
		this.dbConnection = dbConnection;
		this.parentComponent = parentComponent;
		this.sensors = sensors;
		this.sensorDisplayNames = sensorDisplayNames;
		
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
				| UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
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

		// frame
		frame = new JFrame();
		frame.setTitle("Add Sensor");
		frame.setIconImages(icons);
		frame.setResizable(false);
		frame.setBounds(100, 100, 256, 360);
		frame.setLocationRelativeTo(parentComponent);
		SpringLayout springLayout = new SpringLayout();
		frame.getContentPane().setLayout(springLayout);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// sensor id label
		JLabel lblSensorID = new JLabel("Sensor ID");
		springLayout.putConstraint(SpringLayout.SOUTH, lblSensorID, 20, SpringLayout.NORTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.NORTH, lblSensorID, 4, SpringLayout.NORTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, lblSensorID, 4, SpringLayout.WEST, frame.getContentPane());
		frame.getContentPane().add(lblSensorID);

		// sensor id text field
		textFieldSensorID = new JTextField();
		textFieldSensorID.setToolTipText("ID of sensor to add");
		springLayout.putConstraint(SpringLayout.NORTH, textFieldSensorID, 4, SpringLayout.SOUTH, lblSensorID);
		springLayout.putConstraint(SpringLayout.SOUTH, textFieldSensorID, 24, SpringLayout.SOUTH, lblSensorID);
		textFieldSensorID.setColumns(10);
		frame.getContentPane().add(textFieldSensorID);

		// add sensor button
		JButton btnAddSensor = new JButton("Add");
		springLayout.putConstraint(SpringLayout.NORTH, btnAddSensor, 0, SpringLayout.NORTH, textFieldSensorID);
		springLayout.putConstraint(SpringLayout.SOUTH, btnAddSensor, 0, SpringLayout.SOUTH, textFieldSensorID);
		springLayout.putConstraint(SpringLayout.EAST, btnAddSensor, -4, SpringLayout.EAST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, btnAddSensor, -86, SpringLayout.EAST, frame.getContentPane());
		frame.getContentPane().add(btnAddSensor);

		springLayout.putConstraint(SpringLayout.EAST, textFieldSensorID, -4, SpringLayout.WEST, btnAddSensor);

		btnAddSensor.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) {
				handleButtonAddSensor();
			}
		});

		// sensor name label
		JLabel lblSensorName = new JLabel("Sensor Display Name");
		springLayout.putConstraint(SpringLayout.NORTH, lblSensorName, 4, SpringLayout.SOUTH, textFieldSensorID);
		springLayout.putConstraint(SpringLayout.SOUTH, lblSensorName, 20, SpringLayout.SOUTH, textFieldSensorID);
		springLayout.putConstraint(SpringLayout.WEST, lblSensorName, 0, SpringLayout.WEST, lblSensorID);
		frame.getContentPane().add(lblSensorName);

		// sensor name text field
		textFieldSensorName = new JTextField();
		textFieldSensorName.setToolTipText("Friendly name to show for sensor");
		springLayout.putConstraint(SpringLayout.NORTH, textFieldSensorName, 4, SpringLayout.SOUTH, lblSensorName);
		springLayout.putConstraint(SpringLayout.SOUTH, textFieldSensorName, 24, SpringLayout.SOUTH, lblSensorName);
		springLayout.putConstraint(SpringLayout.EAST, textFieldSensorName, -4, SpringLayout.EAST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, textFieldSensorName, 0, SpringLayout.WEST, lblSensorName);
		frame.getContentPane().add(textFieldSensorName);
		textFieldSensorName.setColumns(10);

		// separator
		JSeparator separator = new JSeparator();
		springLayout.putConstraint(SpringLayout.NORTH, separator, 92, SpringLayout.NORTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, separator, 94, SpringLayout.NORTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, separator, -4, SpringLayout.EAST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, separator, 4, SpringLayout.WEST, frame.getContentPane());
		frame.getContentPane().add(separator);

		// sensor search label
		JLabel lblSearch = new JLabel("Sensor Search");
		springLayout.putConstraint(SpringLayout.NORTH, lblSearch, 0, SpringLayout.SOUTH, separator);
		springLayout.putConstraint(SpringLayout.SOUTH, lblSearch, 16, SpringLayout.SOUTH, separator);
		frame.getContentPane().add(lblSearch);

		// sensor search text field
		textFieldSearch = new JTextField();
		textFieldSearch.setToolTipText("Term to search by");
		springLayout.putConstraint(SpringLayout.NORTH, textFieldSearch, 4, SpringLayout.SOUTH, lblSearch);
		springLayout.putConstraint(SpringLayout.SOUTH, textFieldSearch, 24, SpringLayout.SOUTH, lblSearch);
		springLayout.putConstraint(SpringLayout.WEST, textFieldSearch, 4, SpringLayout.WEST, frame.getContentPane());
		textFieldSearch.setColumns(10);
		frame.getContentPane().add(textFieldSearch);

		springLayout.putConstraint(SpringLayout.WEST, textFieldSensorID, 0, SpringLayout.WEST, textFieldSearch);

		// search sensor button
		btnSearch = new JButton("Search");
		springLayout.putConstraint(SpringLayout.NORTH, btnSearch, 0, SpringLayout.NORTH, textFieldSearch);
		springLayout.putConstraint(SpringLayout.SOUTH, btnSearch, 0, SpringLayout.SOUTH, textFieldSearch);
		springLayout.putConstraint(SpringLayout.EAST, btnSearch, -4, SpringLayout.EAST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, btnSearch, -86, SpringLayout.EAST, frame.getContentPane());
		frame.getContentPane().add(btnSearch);

		springLayout.putConstraint(SpringLayout.EAST, textFieldSearch, -4, SpringLayout.WEST, btnSearch);

		btnSearch.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) {
				handleButtonSearch();
			}
		});

		// search results scroll pane
		JScrollPane scrollPane = new JScrollPane();
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 4, SpringLayout.SOUTH, textFieldSearch);
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane, -4, SpringLayout.SOUTH, frame.getContentPane());
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		springLayout.putConstraint(SpringLayout.WEST, scrollPane, 4, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, scrollPane, -4, SpringLayout.EAST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, lblSearch, 0, SpringLayout.WEST, scrollPane);
		frame.getContentPane().add(scrollPane);

		// search results list
		listSensor = new JList<>();
		scrollPane.setViewportView(listSensor);

		listSensor.addMouseListener(new MouseAdapter() 
		{
			public void mouseClicked(MouseEvent e) {
				handleClickSensorList();
			}
		});
	}

	// handles when the add sensor button is pressed
	private void handleButtonAddSensor()
	{
		int sensorID = Integer.parseInt(textFieldSensorID.getText());
		int sensorIndex = sensors.size();

		sensors.add(new PurpleAir(sensorID));

		if (textFieldSensorName.getText().equals(""))
		{
			sensorDisplayNames.add(sensors.get(sensorIndex).getPrimaryName());
			handleNewSensor(sensorIndex, null);
		}
		else
		{
			sensorDisplayNames.add(textFieldSensorName.getText());
			handleNewSensor(sensorIndex, textFieldSensorName.getText());
		}
		
		frame.dispose();
	}

	// handles when the search button is pressed
	void handleButtonSearch()
	{
		getSensorList(textFieldSearch.getText());
		
		listSensor.setModel(new AbstractListModel<String>() 
		{
			public int getSize() {
				return sensorIdAndNames.size();
			}

			public String getElementAt(int index) {
				return sensorIdAndNames.get(index).getName();
			}
		});
	}

	// handles when a user clicks on an element in the list of sensors
	private void handleClickSensorList()
	{
		// get the id and name of the selected sensor and use them to fill the appropriate text fields
		if (listSensor.getSelectedIndex() >= 0)
		{
			textFieldSensorID.setText(Integer.toString(sensorIdAndNames.get(listSensor.getSelectedIndex()).getId()));
			textFieldSensorName.setText(sensorIdAndNames.get(listSensor.getSelectedIndex()).getName());
			
			getNearestAirport(sensorIdAndNames.get(listSensor.getSelectedIndex()).getId());
		}
	}

	public static void handleNewSensor(int i, String friendlyName)
	{
		PurpleAir sensor = sensors.get(i);

		// insert new sensor into database
		DBAction.executeUpdate(dbConnection, 
				"INSERT INTO sensor (" +
						"s_primary_sensor_id, " +
						"s_secondary_sensor_id, " +
						"s_sensor_name, " + 
						"s_sensor_type, " +
						"s_latitude, " + 
						"s_longitude, " +
						"s_location_type " +
						") " +
						"VALUES (" +
						sensor.getPrimaryId() + ", " + 
						sensor.getSecondaryId() + ", " +
						"\"" + sensor.getPrimaryName() + "\", " +
						"\"" + sensor.getPrimarySensorType() + "+" + sensor.getSecondarySensorType() + "+" + sensor.getTertiarySensorType() + "\"," +
						sensor.getLat() + ", " + 
						sensor.getLon() + ", " + 
						"\"" + sensor.getLocationType() + "\");"
				);

		// if a friendly name was provided, add it
		if (friendlyName != null)
		{		
			DBAction.executeUpdate(dbConnection, 
					"UPDATE sensor " +
							"SET s_sensor_name_friendly =  \"" + friendlyName + "\" " +
							"WHERE s_primary_sensor_id == " + sensor.getPrimaryId() + ";"
					);
		}
	}

	// a case insensitive version of String.contains
	private static boolean containsIgnoreCase(String str, String subString) {
		return str.toLowerCase().contains(subString.toLowerCase());
	}
	
	// gets all sensors that match a search term
	private static void getSensorList(String term)
	{	
		sensorIdAndNames.clear();
		
		ResultSet result = DBAction.executeQuery(dbConnection, 
				"SELECT sm.sm_sensor_id AS id, sm.sm_sensor_name AS name " +
					"FROM sensor_master sm " +
					"WHERE sm.sm_is_child = 0 AND sm.sm_sensor_name LIKE '%" + term + "%' " +
					"ORDER BY sm.sm_sensor_name;"
				);
		try
		{
			while(result.next())
			{				
				sensorIdAndNames.add(new Sensor(result.getInt("id"), result.getString("name")));
			}
		}
		catch(SQLException sqle)
		{
			System.err.println(sqle.getMessage());
		}
	}
	
	private static void getNearestAirport(int sensorId)
	{
		ResultSet sensorResult = DBAction.executeQuery(dbConnection, 
				"SELECT sm.sm_latitude AS lat, sm.sm_longitude AS lon " +
					"FROM sensor_master sm " +
					"WHERE sm.sm_sensor_id = " +  Integer.toString(sensorId) + ";"
				);
		
		ResultSet airportResult = DBAction.executeQuery(dbConnection, 
				"SELECT am.am_airport_id AS id, am.am_latitude AS lat, am.am_longitude AS lon " +
					"FROM airport_master am;"
				);
				
		
		try
		{
			if(sensorResult.next())
			{				
				float sensorLat = sensorResult.getFloat("lat");
				float sensorLon = sensorResult.getFloat("lon");
				float minDistance = Float.MAX_VALUE;
				String minDistanceId = "";
				
				while (airportResult.next())
				{
					float thisDistance = LocationTools.getDistFromCoords(sensorLat, sensorLon, airportResult.getFloat("lat"), airportResult.getFloat("lon"));
					if (thisDistance < minDistance)
					{
						minDistance = thisDistance;
						minDistanceId = airportResult.getString("id");
					}
						
				}
				
				System.out.println(minDistance);
				System.out.println(minDistanceId);
			}
		}
		catch(SQLException sqle)
		{
			System.err.println(sqle.getMessage());
		}
	}

	private static class Sensor
	{
		private int id;
		private String name;

		public Sensor(int id, String name)
		{
			this.id = id;
			this.name = name;
		}

		public int getId() {
			return id;
		}

		public String getName() {
			return name;
		}
	}
}