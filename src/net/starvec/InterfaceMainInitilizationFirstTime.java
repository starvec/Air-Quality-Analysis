package net.starvec;

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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.util.Arrays;

public class InterfaceMainInitilizationFirstTime 
{
	private JFrame frame;
	private JProgressBar progressBar;
	
	private Connection dbConnection;
	private String text;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					InterfaceMainInitilizationFirstTime window = new InterfaceMainInitilizationFirstTime(DBAction.openDatabaseConnection("PurpleAir.db"), 
																											"Performing first-time initilization");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public InterfaceMainInitilizationFirstTime(Connection dbConnection, String text) 
	{
		this.dbConnection = dbConnection;
		this.text = text;
		
		initialize();
		frame.setVisible(true);
		
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
                    progressBar.setValue(progress);
                } 
                else if (name.equals("state")) 
                {
                    SwingWorker.StateValue state = (SwingWorker.StateValue) evt.getNewValue();
                    switch (state) {
                        case DONE:

                            break;
                    }
                }
            }

        });
		
        worker.execute();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() 
	{
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		frame = new JFrame();
		frame.setUndecorated(true);
		frame.setBounds(100, 100, 300, 50);
		frame.setLocationRelativeTo(null);
		//frame.setBounds(100, 100, 300, 100);
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
	
	    	return null;
	    }
	    
	    private void handleSensor()
	    {			
			try 
			{
				String sensorListRaw = IOUtils.toString(new URL("https://www.purpleair.com/json?show="), Charset.forName("UTF-8"));
				
				progressBar.setValue(sensorListDownloadCompletedProgressValue);
				progressBar.setString("Parsing Air Sensor List");
				
				//System.out.println(sensorListRaw.length());
				//7965862
				//7965736
				
				int packedStatements = 0;
				String insertionStatement = "BEGIN;\n";
				
				for (int i = 0; i < sensorListRaw.length() - 5; i++)
				{	
					int iStart = i;
					
					// scan until we find "ID":
					if (sensorListRaw.charAt(i) == '\"' && sensorListRaw.charAt(i+1) == 'I' && sensorListRaw.charAt(i+2) == 'D' 
						&& sensorListRaw.charAt(i+3) == '\"' && sensorListRaw.charAt(i+4) == ':')
					{	
						int id;
						int parentId = -1;
						boolean isChild;
						String name;
						String sensorType;
						float latitude;
						float longitude;
						String locationType;
						
						i += 5;
						int iIdStart = i;								// mark start of id
						while (sensorListRaw.charAt(i) != ',') {		// scan until ,
							i++;
						}
						int iIdEnd = i;									// mark end of id
						
						// parse id
						id = Integer.parseInt(sensorListRaw.substring(iIdStart, iIdEnd));
						
						// if field after id is "ParentID"
						if (sensorListRaw.charAt(i+2) == 'P')
						{
							i += 12;
							int iParentIdStart = i;						// mark start of parent id
							while (sensorListRaw.charAt(i) != ',') {	// scan until ,
								i++;
							}
							int iParentIdEnd = i;						// mark end of parent id
							
							// parse parent id
							parentId = Integer.parseInt(sensorListRaw.substring(iParentIdStart, iParentIdEnd));
							isChild = true;
						}
						else {
							isChild = false;
						}
						
						i += 10;					
						int iNameStart = i;								// mark start of name
						while (sensorListRaw.charAt(i) != '\"') {		// scan until "
							i++;
						}
						int iNameEnd = i;								// mark end of name

						// parse name
						name = sensorListRaw.substring(iNameStart, iNameEnd);
					
						// if sensor has a location type
						if (sensorListRaw.substring(i+3, i+22).equals("DEVICE_LOCATIONTYPE"))
						{
							i += 25;
							int iLocationTypeStart = i;					// mark start of location type
							while (sensorListRaw.charAt(i) != '\"') {	// scan until "
								i++;
							}
							int iLocationTypeEnd = i;					// mark end of location type
							
							// parse location type
							locationType = sensorListRaw.substring(iLocationTypeStart, iLocationTypeEnd);
						}
						// if sensor does not have a location type
						else
						{
							// set location type
							locationType = "undefined";
						}

						// scan until "Lat":
						while (!(sensorListRaw.charAt(i) == '\"' && sensorListRaw.charAt(i+1) == 'L' && sensorListRaw.charAt(i+2) == 'a' 
								&& sensorListRaw.charAt(i+3) == 't' && sensorListRaw.charAt(i+4) == '\"' && sensorListRaw.charAt(i+5) == ':')) 
						{
							i++;
						}

						i += 6;
						int iLatStart = i;								// mark start of latitude
						while (sensorListRaw.charAt(i) != ',') {		// scan until ,
							i++;
						}
						int iLatEnd = i;								// mark end of latitude
						
						// parse latitude
						latitude = Float.parseFloat(sensorListRaw.substring(iLatStart, iLatEnd));

						i += 7;
						int iLonStart = i;								// mark start of longitude
						while (sensorListRaw.charAt(i) != ',') {		// scan until ,
							i++;
						}
						int iLonEnd = i;								// mark end of longitude
						
						// parse longitude
						longitude = Float.parseFloat(sensorListRaw.substring(iLonStart, iLonEnd));
						
						i += 30;
						int startingIndex = i;
						// scan for "Type":
						while (!(sensorListRaw.charAt(i) == '\"' && sensorListRaw.charAt(i+1) == 'T' && sensorListRaw.charAt(i+2) == 'y' 
								&& sensorListRaw.charAt(i+3) == 'p' && sensorListRaw.charAt(i+4) == 'e' && sensorListRaw.charAt(i+5) == '\"'
								&& sensorListRaw.charAt(i+6) == ':') && (i - startingIndex < 50))
						{
							i++;
						}
						i += 8;
						int iSensorTypeStart = i;						// mark start of sensor type
						while (sensorListRaw.charAt(i) != '\"') {		// scan until "
							i++;
						}
						int iSensorTypeEnd = i;							// mark end of sensor type
						
						// parse sensor type
						sensorType = sensorListRaw.substring(iSensorTypeStart, iSensorTypeEnd);
						
						if (isChild)
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
						else
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
						
						//System.out.println(insertionStatement);					
					}

					// update the progress bar as we go
					if (i%100 == 0)
						progressBar.setValue((int) (sensorListDownloadCompletedProgressValue + (i/(float)sensorListRaw.length())*(sensorListParseCompletedProgressValue - sensorListDownloadCompletedProgressValue)));
					
					if (packedStatements >= 1000)
					{
						DBAction.executeUpdate(dbConnection, insertionStatement + "END;");
						insertionStatement = "BEGIN;\n";
						packedStatements = 0;
					}
				
				//System.out.println(i);
				} // end for loop
				
				if (packedStatements > 0)
				{
					DBAction.executeUpdate(dbConnection, insertionStatement + "END;");
				}
				
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
			
			progressBar.setValue(sensorListParseCompletedProgressValue);
	    }
	    
	    private void handleAirport()
	    {
			try 
			{	
				URL urlCSV = new URL("https://ourairports.com/data/airports.csv");
				
				URLConnection urlConn = urlCSV.openConnection();

				InputStreamReader inputCSV = new InputStreamReader(((URLConnection) urlConn).getInputStream());
		        
				BufferedReader br = new BufferedReader(inputCSV);
				
				progressBar.setValue(airportListDownloadCompletedProgressValue);
				progressBar.setString("Parsing Airport List");
				
				String airport;
				int count = 0;

				while ((airport = br.readLine()) != null)
				{
					//airport = airport.replaceAll("^\"|\"$", "");
					String[] fields = airport.replaceAll("\"", "").split(",");
					
					if (fields[2].equalsIgnoreCase("medium_airport") || fields[2].equalsIgnoreCase("large_airport"))
					{
						System.out.println(Arrays.toString(fields));
						count++;
					}
					
					if (count%100 == 0)
						progressBar.setValue((int) (airportListDownloadCompletedProgressValue + (count/6000f)*(airportListParseCompletedProgressValue - airportListDownloadCompletedProgressValue)));
				}
				
				progressBar.setValue(100);
				
				//String airportListRaw = IOUtils.toString(new URL("https://ourairports.com/data/airports.csv"), Charset.forName("UTF-8"));
				
				
				
				/*
				
				String[] airports = airportListRaw.split("\n");
				System.out.println(Arrays.toString(airports));
				
				for (int i = 1; i < airports.length; i++)
				{
					String[] fields = airports[i].split(",");
					
					if (fields[2].equalsIgnoreCase("medium_airport") || fields[2].equalsIgnoreCase("large_airport"))
					{
						System.out.println(Arrays.toString(fields));
					}
				}
				*/
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
			
	    }
	}
}


