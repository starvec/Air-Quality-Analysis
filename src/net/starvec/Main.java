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
	
	static ArrayList<PurpleAir> airSensors = new ArrayList<>();
	static ArrayList<String> airSensorDisplayNames = new ArrayList<>();
	static ArrayList<WindSensor> windSensors = new ArrayList<>();
	static Config config;
	
	static Connection dbConnection;
	
	public static void main(String[] args)
	{		
		// create the database connection
		dbConnection = DBAction.openDatabaseConnection("data.db");
		
		// create the config object and connect it to the database
		config = new Config(dbConnection);	
		
		// if the application has not been run before, run first time initialization
		if (!config.valueIs("run_before", "1"))
		{
			InterfaceMainInitilizationFirstTime window = new InterfaceMainInitilizationFirstTime(dbConnection, "Performing first-time initilization");
			while (!window.finished()) {
				sleep(100);
			}
		}
		// else, run standard initialization
		else
		{
			InterfaceMainInitialization window = new InterfaceMainInitialization(airSensors, airSensorDisplayNames, windSensors, dbConnection);
			while (!window.finished()) {
				sleep(100);
			}
		}	
			
		// run the main interface
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try 
				{
					Interface window = new Interface(dbConnection, airSensors, airSensorDisplayNames);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		// sleep for a bit to make sure we don't request data from the Purple Air servers too often
		sleep(MIN_TIME_BETWEEN_REFRESH);
		
		// start data update threads
		AirUpdateThread airUpdateThread = new AirUpdateThread(airSensors, dbConnection);
		airUpdateThread.start();

		WindUpdateThread windUpdateThread = new WindUpdateThread(windSensors, dbConnection);
		windUpdateThread.start();
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
