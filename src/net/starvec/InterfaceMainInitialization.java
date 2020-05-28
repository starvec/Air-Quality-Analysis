package net.starvec;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class InterfaceMainInitialization 
{
	private static final int MIN_TIME_BETWEEN_REFRESH = 1000;
	
	private JFrame frame;
	private JProgressBar progressBar;
	
	private static ArrayList<PurpleAir> airSensors;
	private static ArrayList<String> airSensorDisplayNames;
	private static ArrayList<WindSensor> windSensors;
	private Connection dbConnection;
	private boolean finished;

	//
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try 
				{
					airSensors = new ArrayList<>();
					airSensorDisplayNames = new ArrayList<>();
					windSensors = new ArrayList<>();
					InterfaceMainInitialization window = new InterfaceMainInitialization(airSensors, airSensorDisplayNames, windSensors, DBAction.openDatabaseConnection("data.db"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	//
	public InterfaceMainInitialization(ArrayList<PurpleAir> sensors, ArrayList<String> sensorDisplayNames, ArrayList<WindSensor> windSensors, Connection dbConnection) 
	{
		this.airSensors = sensors;
		this.airSensorDisplayNames = sensorDisplayNames;
		this.windSensors = windSensors;
		this.dbConnection = dbConnection;
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

	//
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
		
		JLabel lbl = new JLabel("Performing initialization");
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

	//
	class ProgressWorker extends SwingWorker<Object, Object>
	{	
	    @Override
	    protected Object doInBackground() throws Exception 
	    {
	    	int sensorCount = 0;
	    	int currentSensor = 0;
	    	
	    	progressBar.setString("Querying Database");
	    	
	    	// query the database to count the number of previously monitored air sensors
			ResultSet result = DBAction.executeQuery(dbConnection, 
					"SELECT COUNT(s.s_primary_sensor_id) AS count " +
					"FROM sensor s;"
					);
			try
			{
				if (result.next())
				{
					sensorCount += result.getInt("count");
				}
			}
			catch(SQLException sqle)
			{
				System.err.println(sqle.getMessage());
			}
			
			// query the database to count the number of previously monitored airport wind sensors
			result = DBAction.executeQuery(dbConnection, 
					"SELECT COUNT(a.a_airport_id) AS count " +
					"FROM airport a;"
					);
			try
			{
				if (result.next())
				{
					sensorCount += result.getInt("count");
				}
			}
			catch(SQLException sqle)
			{
				System.err.println(sqle.getMessage());
			}
	    	
	    	// query the database to get all previously monitored air sensors
			result = DBAction.executeQuery(dbConnection, 
					"SELECT s.s_primary_sensor_id AS id, s.s_sensor_name AS name, s.s_sensor_name_friendly AS name_friendly " +
					"FROM sensor s " +
					"ORDER BY s.s_sensor_name_friendly;"
					);
			try
			{
				while(result.next())
				{
					airSensors.add(new PurpleAir(result.getInt("id")));
					
					if (result.getString("name_friendly") == null)
					{
						progressBar.setString("Initializing " + result.getString("name"));
						airSensorDisplayNames.add(result.getString("name"));
					}
					else
					{
						progressBar.setString("Initializing " + result.getString("name_friendly"));
						airSensorDisplayNames.add(result.getString("name_friendly"));
					}	
					
					progressBar.setValue((int)((float)100/(sensorCount) * currentSensor));
					currentSensor++;
					
					sleep(MIN_TIME_BETWEEN_REFRESH);
				}
			}
			catch(SQLException sqle)
			{
				System.err.println(sqle.getMessage());
			}
			
			// query the database to get all previously monitored airport wind sensors
			result = DBAction.executeQuery(dbConnection, 
					"SELECT a.a_airport_id AS id, a.a_airport_name AS name " +
					"FROM airport a " +
					"ORDER BY a.a_airport_name;"
					);
			try
			{
				while(result.next())
				{
					windSensors.add(new WindSensor(result.getString("id"), dbConnection));

					progressBar.setString("Initializing " + result.getString("name"));
					progressBar.setValue((int)((float)100/(sensorCount) * currentSensor));
					currentSensor++;
					
					sleep(MIN_TIME_BETWEEN_REFRESH);
				}
			}
			catch(SQLException sqle)
			{
				System.err.println(sqle.getMessage());
			}
			
			progressBar.setString("Done");
			progressBar.setValue(100);
			
	    	return null;
	    }
	    
	    private void sleep(int millis)
		{
			try {
				Thread.sleep(millis);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
