package net.starvec;

import java.util.ArrayList;
import java.util.TimeZone;
import java.util.Vector;

import javax.swing.UIManager;

import java.awt.EventQueue;
import java.lang.Math;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;

public class Main
{
	private static final int MIN_TIME_BETWEEN_REFRESH = 1000;
	
	static ArrayList<PurpleAir> sensors = new ArrayList<>();
	static ArrayList<String> sensorDisplayNames = new ArrayList<>();
	
	static Connection dbConnection;
	
	public static void main(String[] args)
	{	
		// create the database connection
		dbConnection = DBAction.openDatabaseConnection("PurpleAir.db");
		
		// query the database to get all previously monitored sensors
		ResultSet result = DBAction.executeQuery(dbConnection, 
				"SELECT s.s_primary_sensor_id AS id, s.s_sensor_name AS name, s.s_sensor_name_friendly AS name_friendly " +
				"FROM sensor s;"
				);
		try
		{
			while(result.next())
			{				
				sensors.add(new PurpleAir(result.getInt("id")));
				
				if (result.getString("name_friendly") == null)
					sensorDisplayNames.add(result.getString("name"));
				else
					sensorDisplayNames.add(result.getString("name_friendly"));
				
				sleep(MIN_TIME_BETWEEN_REFRESH);
			}
		}
		catch(SQLException sqle)
		{
			System.err.println(sqle.getMessage());
		}
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try 
				{
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					Interface window = new Interface(dbConnection, sensors, sensorDisplayNames);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		// sleep for a bit to make sure we don't request data from the Purple Air servers too often
		sleep(MIN_TIME_BETWEEN_REFRESH);
		
		// start data update threads
		AirUpdateThread airUpdateThread = new AirUpdateThread(sensors, dbConnection);
		airUpdateThread.run();
	}
	
	private static void sleep(int millis)
	{
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
