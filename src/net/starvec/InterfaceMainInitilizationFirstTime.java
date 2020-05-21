package net.starvec;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.io.IOUtils;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.JProgressBar;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class InterfaceMainInitilizationFirstTime 
{
	private JFrame frame;
	private JProgressBar progressBar;
	
	private Connection dbConnection;
	private String text;
	private boolean finished;

	//
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					InterfaceMainInitilizationFirstTime window = new InterfaceMainInitilizationFirstTime(DBAction.openDatabaseConnection("data.db"), "Performing first-time initilization");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	//
	public InterfaceMainInitilizationFirstTime(Connection dbConnection, String text) 
	{
		this.dbConnection = dbConnection;
		this.text = text;
		finished = false;
		
		initialize();
		frame.setVisible(true);
		
		ProgressWorker worker = new ProgressWorker();
		
		worker.addPropertyChangeListener(new PropertyChangeListener()
		{
			@Override
            public void propertyChange(PropertyChangeEvent evt) 
            {
                String name = evt.getPropertyName();
                if (name.equals("progress")) 
                {
                    int progress = (int) evt.getNewValue();
                    progressBar.setValue(progress);
                } 
                else if (name.equals("state")) 
                {
                    SwingWorker.StateValue state = (SwingWorker.StateValue) evt.getNewValue();
                    switch (state) 
                    {
                        case DONE:
                        {
                        	finished = true;
                        	frame.setVisible(false);
                        	frame.dispose();
                        	break;
                        }              
                    }
                }
            }

        });
		
        worker.execute();
	}
	
	// returns true if the ProgressWorker has finished
	public boolean finished() {
		return finished;
	}

	//
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
		
		JLabel lbl = new JLabel(text);
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
	
	class ProgressWorker extends SwingWorker<Object, Object>
	{
		private int sensorListDownloadCompletedProgressValue = 25;
		private int airportListDownloadCompletedProgressValue = 75;
		private int	sensorListParseCompletedProgressValue = 50;
		private int	airportListParseCompletedProgressValue = 100;
		
	    @Override
	    protected Object doInBackground() throws Exception 
	    {
	    	progressBar.setString("Downloading Air Sensor List");
			handleSensor();
			
			progressBar.setString("Downloading Airport List");
			handleAirport();
			
			DBAction.executeUpdate(dbConnection, "UPDATE config SET c_value = 1 WHERE c_property = 'run_before';");
	    	return null;
	    }
	    
	    // downloads a list of all PurpleAir sensors, parses through it, and adds them to the database
	    private void handleSensor()
	    {	    	
			try 
			{
				DBAction.executeUpdate(dbConnection, "DELETE FROM sensor_master WHERE 1;");
				
				String sensorListRaw = IOUtils.toString(new URL("https://www.purpleair.com/json?show="), Charset.forName("UTF-8"));	
				
				progressBar.setValue(sensorListDownloadCompletedProgressValue);
				progressBar.setString("Parsing Air Sensor List");
				
				int packedStatements = 0;
				String insertionStatement = "";
				
				// split each sensor into its own string
				String sensorStrings[] = sensorListRaw.split("\"ID\":");
				
				for (int s = 1; s < sensorStrings.length; s++)
				{
					String sensorString = sensorStrings[s];
					
					int id;
					int parentId;
					boolean isChild;
					String name;
					String sensorType;
					float latitude;
					float longitude;
					boolean hasLocation;
					String locationType;
					
					int start = 0;
					int end = 0;
					while (sensorString.charAt(end) != ',') {
						end++;
					}
					
					// parse id
					id = Integer.parseInt(sensorString.substring(start, end));
					
					// if sensor has parent id
					if (sensorString.indexOf("\"ParentID\":") != -1)
					{
						isChild = true;
						start = sensorString.indexOf("\"ParentID\":") + 11;
						end = start;
						while (sensorString.charAt(end) != ',') {
							end++;
						}
					
						// parse parent id
						parentId = Integer.parseInt(sensorString.substring(start, end));
					}
					else
					{
						isChild = false;
						parentId = -1;
					}
					
					start = sensorString.indexOf("\"Label\":") + 9;
					end = start;
					while (sensorString.charAt(end) != ',') {
						end++;
					}
					
					// parse name
					name = sensorString.substring(start, end-1);
					
					// if sensor has type
					if (sensorString.indexOf("\"Type\":") != -1)
					{
						start = sensorString.indexOf("\"Type\":") + 8;
						end = start;
						while (sensorString.charAt(end) != ',') {
							end++;
						}
						
						// parse sensor type
						sensorType = sensorString.substring(start, end-1);
					}
					else
					{
						sensorType = "undefined";
					}					
					
					// if sensor has location
					if (sensorString.indexOf("\"Lat\":") != -1)
					{
						hasLocation = true;
						start = sensorString.indexOf("\"Lat\":") + 6;
						end = start;
						while (sensorString.charAt(end) != ',') {
							end++;
						}	
						
						// parse latitude
						latitude = Float.parseFloat(sensorString.substring(start, end));
						
						start = sensorString.indexOf("\"Lon\":") + 6;
						end = start;
						while (sensorString.charAt(end) != ',') {
							end++;
						}
						
						// parse longitude
						longitude = Float.parseFloat(sensorString.substring(start, end));
					}
					else
					{
						hasLocation = false;
						latitude = -1;
						longitude = -1;
					}					
					
					// if sensor has location type
					if (sensorString.indexOf("\"DEVICE_LOCATIONTYPE\":") != -1)
					{
						start = sensorString.indexOf("\"DEVICE_LOCATIONTYPE\":") + 23;
						end = start;
						while (sensorString.charAt(end) != ',') {
							end++;
						}
						
						// parse location type
						locationType = sensorString.substring(start, end-1);
					}
					else
					{
						locationType = "undefined";
					}
					
					// add correct insertion statement to statements list
					if (isChild && hasLocation)
					{	
						packedStatements++;
						insertionStatement += "INSERT INTO sensor_master (" +
												"sm_sensor_id, " + 
												"sm_parent_sensor_id, " +
												"sm_is_child, " + 
												"sm_sensor_name, " +
												"sm_latitude, " + 
												"sm_longitude, " +
												"sm_location_type" +
												") " +
													"VALUES (" +
														id + ", " +
														parentId + ", " + 
														isChild + ", " +
														"\"" + name + "\", " + 
														latitude + ", " + 
														longitude + ", " + 
														"\"" + locationType + "\");\n";
					}
					else if (!isChild && hasLocation)
					{
						packedStatements++;
						insertionStatement += "INSERT INTO sensor_master (" +
												"sm_sensor_id, " +
												"sm_is_child, " +
												"sm_sensor_name, " +
												"sm_sensor_type, " +
												"sm_latitude, " +
												"sm_longitude, " +
												"sm_location_type" +
												") " +
													"VALUES (" +
														id + ", " +
														isChild + ", " +
														"\"" + name + "\", " + 
														"\"" + sensorType + "\", " +
														latitude + ", " + 
														longitude + ", " + 
														"\"" + locationType + "\");\n";
					}
					else if (isChild && !hasLocation)
					{
						packedStatements++;
						insertionStatement += "INSERT INTO sensor_master (" +
												"sm_sensor_id, " + 
												"sm_parent_sensor_id, " +
												"sm_is_child, " + 
												"sm_sensor_name, " +
												"sm_location_type" +
												") " +
													"VALUES (" +
														id + ", " +
														parentId + ", " + 
														isChild + ", " +
														"\"" + name + "\", " + 
														"\"" + locationType + "\");\n";
					}
					else if (!isChild && !hasLocation)
					{
						packedStatements++;
						insertionStatement += "INSERT INTO sensor_master (" +
												"sm_sensor_id, " +
												"sm_is_child, " +
												"sm_sensor_name, " +
												"sm_sensor_type, " +
												"sm_location_type" +
												") " +
													"VALUES (" +
														id + ", " +
														isChild + ", " +
														"\"" + name + "\", " + 
														"\"" + sensorType + "\", " +
														"\"" + locationType + "\");\n";
					}
					// should not be able to get here
					else
					{
						System.err.println("Should not be able to get here");
					}
				
				
					// send the multi-line insertion statement to the database, then reset the string to accept more statements
					if (packedStatements >= 1000)
					{
						DBAction.executeUpdateAsTransaction(dbConnection, insertionStatement);
						insertionStatement = "";
						packedStatements = 0;
					}
					
					// update the progress bar as we go
					if (s%100 == 0)
						progressBar.setValue((int) (sensorListDownloadCompletedProgressValue + (s/(float)sensorStrings.length)*(sensorListParseCompletedProgressValue - sensorListDownloadCompletedProgressValue)));
				}
				
				// if there are any insertion statements left, send them to the database
				if (packedStatements > 0)
				{
					DBAction.executeUpdateAsTransaction(dbConnection, insertionStatement);
				}
				
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
			
			progressBar.setValue(sensorListParseCompletedProgressValue);
	    }
	    
	    // downloads a list of all airports, finds ones that should have weather stations, and adds them to the database
	    private void handleAirport()
	    {
			try 
			{	
				DBAction.executeUpdate(dbConnection, "DELETE FROM airport_master WHERE 1;");
				
				URL urlCSV = new URL("https://ourairports.com/data/airports.csv");
				
				URLConnection urlConn = urlCSV.openConnection();

				InputStreamReader inputCSV = new InputStreamReader(((URLConnection) urlConn).getInputStream());
		        
				BufferedReader br = new BufferedReader(inputCSV);
				
				progressBar.setValue(airportListDownloadCompletedProgressValue);
				progressBar.setString("Parsing Airport List");
				
				String airport;
				String insertionStatement = "";
				int count = 0;
				int packedStatements = 0;
				
				while ((airport = br.readLine()) != null)
				{
					String[] fields = new String[100];
					int currentField = 0;
					boolean betweenQuotes = false;
					int start = 0;
					
					for (int i = 0; i < airport.length(); i++)
					{
						if (airport.charAt(i) == ',' && !betweenQuotes)
						{
							fields[currentField] = airport.substring(start, i).replaceAll("\"", "");
							currentField++;
							start = i + 1;
						}
						else if (airport.charAt(i) == '\"')
						{
							betweenQuotes = !betweenQuotes;
						}
					}
					
					// medium and large airports are the ones expected to have a weather station
					if (fields[2].equalsIgnoreCase("medium_airport") || fields[2].equalsIgnoreCase("large_airport"))
					{
						insertionStatement += "INSERT INTO airport_master " +
												"VALUES (" +
													"\"" + fields[1] + "\", " +
													"\"" + fields[3] + "\", " +
													fields[4] + ", " + 
													fields[5] + ", " +
													"\"" + fields[10] + "\");\n";
						count++;
						packedStatements++;
					}
					
					// send the multi-line insertion statement to the database, then resets the string to accept more statements
					if (packedStatements >= 1000)
					{
						DBAction.executeUpdateAsTransaction(dbConnection, insertionStatement);
						insertionStatement = "";
						packedStatements = 0;
					}
					
					// update the progress bar as we go
					if (count%100 == 0)
						progressBar.setValue((int) (airportListDownloadCompletedProgressValue + (count/6000f)*(airportListParseCompletedProgressValue - airportListDownloadCompletedProgressValue)));
				}
				
				// if there are any insertion statements left, send them to the database
				if (packedStatements > 0)
					DBAction.executeUpdateAsTransaction(dbConnection, insertionStatement);
				
				progressBar.setValue(100);
				progressBar.setString("Done");
			} 
			catch (IOException e) {
				e.printStackTrace();
			}	
	    }
	}
}


