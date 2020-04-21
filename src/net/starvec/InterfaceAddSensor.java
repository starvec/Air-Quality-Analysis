package net.starvec;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.SpringLayout;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

import org.apache.commons.io.IOUtils;

import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.AbstractListModel;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Component;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.util.ArrayList;

import javax.swing.JList;
import javax.swing.JSeparator;
import javax.swing.ScrollPaneConstants;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JProgressBar;

public class InterfaceAddSensor 
{
	private JFrame frame;
	private JTextField textFieldSearch;
	private JTextField textFieldSensorID;
	private JProgressBar progressBarSearching;
	private JTextField textFieldSensorName;
	private JButton btnSearch;
	private JList<String> listSensor;
	
	private ArrayList<Sensor> sensorIdAndNames = new ArrayList<>();
	private ArrayList<Sensor> allSensorIdAndNames = new ArrayList<>();
	private static ArrayList<PurpleAir> sensors;
	private static ArrayList<String> sensorDisplayNames;
	
	private static Connection dbConnection;
	private static Component parentComponent;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) 
	{
		Connection dbConnection = null;
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
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
	public InterfaceAddSensor(ArrayList<PurpleAir> sensors, ArrayList<String> sensorDisplayNames, Connection dbConnection, Component parentComponent) {
		this.dbConnection = dbConnection;
		this.parentComponent = parentComponent;
		this.sensors = sensors;
		this.sensorDisplayNames = sensorDisplayNames;
		initialize();
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() 
	{
		UIManager.put("ProgressBar.selectionForeground", Color.white);
		UIManager.put("ProgressBar.selectionBackground", Color.black);
		
		// frame
		frame = new JFrame();
		frame.setTitle("Add Sensor");
		frame.setResizable(false);
		frame.setBounds(100, 100, 256, 360);
		frame.setLocationRelativeTo(parentComponent);
		SpringLayout springLayout = new SpringLayout();
		frame.getContentPane().setLayout(springLayout);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
		
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
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 4, SpringLayout.SOUTH, textFieldSearch);
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
		
		// search progress bar
		progressBarSearching = new JProgressBar();
		progressBarSearching.setString("");
		progressBarSearching.setStringPainted(true);
		progressBarSearching.setForeground(new Color(51, 187, 76));
		springLayout.putConstraint(SpringLayout.NORTH, progressBarSearching, -24, SpringLayout.SOUTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, progressBarSearching, -4, SpringLayout.SOUTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, progressBarSearching, -4, SpringLayout.EAST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, progressBarSearching, 4, SpringLayout.WEST, frame.getContentPane());
		frame.getContentPane().add(progressBarSearching);
		
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane, -4, SpringLayout.NORTH, progressBarSearching);
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
	}
	
	// handles when the search button is pressed
	void handleButtonSearch()
	{
		btnSearch.setEnabled(false);
		ProgressWorker worker = new ProgressWorker();
		
		worker.addPropertyChangeListener(new PropertyChangeListener()
		{
			@SuppressWarnings("serial")
			@Override
            public void propertyChange(PropertyChangeEvent evt) 
            {
                String name = evt.getPropertyName();
                if (name.equals("progress")) 
                {
                    int progress = (int) evt.getNewValue();
                    progressBarSearching.setValue(progress);
                } 
                else if (name.equals("state")) 
                {
                    SwingWorker.StateValue state = (SwingWorker.StateValue) evt.getNewValue();
                    switch (state) {
                        case DONE:
                        	listSensor.setModel(new AbstractListModel<String>() 
            				{
            					public int getSize() {
            						return sensorIdAndNames.size();
            					}
            					
            					public String getElementAt(int index) {
            						return sensorIdAndNames.get(index).getName();
            					}
            				});
                            btnSearch.setEnabled(true);
                            break;
                    }
                }
            }

        });
		
        worker.execute();
	}
	
	// handles when a user clicks on an element in the list of sensors
	private void handleClickSensorList()
	{
		// get the id and name of the selected sensor and use them to fill the appropriate text fields
		if (listSensor.getSelectedIndex() >= 0)
		{
			textFieldSensorID.setText(Integer.toString(sensorIdAndNames.get(listSensor.getSelectedIndex()).getId()));
			textFieldSensorName.setText(sensorIdAndNames.get(listSensor.getSelectedIndex()).getName());
		}
	}
	
	// searches through all the sensor names for a term and stores the names and IDs of matches in sensorIdsAndNames
	private void searchForTerm(String term)
	{
		progressBarSearching.setEnabled(true);
		
		if (allSensorIdAndNames.size() == 0)
			getSensorList();
		
		sensorIdAndNames.clear();
		
		progressBarSearching.setString("Searching Sensors");
		
		for (int i = 0; i < allSensorIdAndNames.size(); i++)
		{
			if (containsIgnoreCase(allSensorIdAndNames.get(i).getName(), term)) {
				sensorIdAndNames.add(allSensorIdAndNames.get(i));
			}
			
			if (i%1000 == 10)
				progressBarSearching.setValue(75 + (i/allSensorIdAndNames.size())*25);
		}
		
		progressBarSearching.setValue(100);
		progressBarSearching.setString("Done");
	}
	
	// gets all the sensor names and IDs and stores them in allSensorIdAndNames
	private void getSensorList()
	{
		int jsonCompletedProgressValue = 25;
		int parsingCompletedProgressValue = 75;
		
		progressBarSearching.setIndeterminate(true);
		progressBarSearching.setString("Downloading Sensor Data");
		
		try 
		{	
			String json = IOUtils.toString(new URL("https://www.purpleair.com/json?show="), Charset.forName("UTF-8"));
			String idAndName;
			
			progressBarSearching.setIndeterminate(false);
			progressBarSearching.setValue(jsonCompletedProgressValue);
			progressBarSearching.setString("Parsing Sensor Data");
			
			for (int i = 0; i < json.length() - 5; i++)
			{		
				if (json.charAt(i) == '\"' && json.charAt(i+1) == 'I' && json.charAt(i+2) == 'D' && json.charAt(i+3) == '\"' && json.charAt(i+4) == ':')
				{
					int j = i+5;
					while (json.charAt(j) != ',') {
						j++;
					}
					
					if (json.charAt(j+2) == 'L')
					{
						int k = j+10;
						while (json.charAt(k) != '\"') {
							k++;
						}

						allSensorIdAndNames.add(new Sensor(Integer.parseInt(json.substring(i+5, j)), json.substring(j+10, k)));
					}
				}
				
				if (i%1000 == 0)
					progressBarSearching.setValue(jsonCompletedProgressValue + (i/json.length())*50);
			}
			
			progressBarSearching.setValue(parsingCompletedProgressValue);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// a case insensitive version of String.contains
		public static boolean containsIgnoreCase(String str, String subString) {
	        return str.toLowerCase().contains(subString.toLowerCase());
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
	
	class ProgressWorker extends SwingWorker<Object, Object>
	{
        @Override
        protected Object doInBackground() throws Exception 
        {
        	searchForTerm(textFieldSearch.getText());
        	return null;
        }
    }
	
	private class Sensor
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