package net.starvec;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.TimeZone;

import org.apache.commons.io.IOUtils;

public class WindUpdateThread extends Thread
{
	private static final int DESIRED_LOOP_PERIOD = 300000;
	
	private ArrayList<WindSensor> sensors;
	Connection dbConnection;
	
	public WindUpdateThread(ArrayList<WindSensor> sensors, Connection dbConnection)
	{
		this.sensors = sensors;
		this.dbConnection = dbConnection;
	}
	
	public void run()
	{
		while (true)
		{
			String idString = "";
			
			for (int i = 0; i < sensors.size(); i++)
			{
				if (i < sensors.size() - 1)
					idString += sensors.get(i).getAirportId() + ",";
				else
					idString += sensors.get(i).getAirportId();
			}
			
			String url = "https://www.aviationweather.gov/metar/data?ids=" + idString + "&format=raw&hours=0&taf=off&layout=off";
			String webData = "";
			try {
				webData = IOUtils.toString(new URL(url), Charset.forName("UTF-8"));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// for every sensor
			for (int i = 0; i < sensors.size(); i++) 
			{
				System.out.println("Attempting to get new data for airport wind sensor " + sensors.get(i).getAirportId());
				handleSensorRefresh(i, webData);
			}
			
			sleep(DESIRED_LOOP_PERIOD);
		}
	}
	
	private void handleSensorRefresh(int i, String webData)
	{
		WindSensor sensor = sensors.get(i);
		
		int metarStart = webData.indexOf("<code>" + sensor.getAirportId());
		String webDataSub = webData.substring(metarStart);
		int metarEnd = webDataSub.indexOf("</code>");
		String metar = webDataSub.substring(0, metarEnd);
		
		sensor.setMetar(metar);
		sensor.parseMetar();
		
		//System.out.println("Wind variable: " + sensor.bearingIsVariable());
		//System.out.println("Wind gusty: " + sensor.windHasGusts());
		
		// wind is variable and has gusts
		if (sensor.bearingIsVariable() && sensor.windHasGusts())
		{
			try
			{
				DBAction.executeUpdateUncaught(dbConnection,
						"INSERT INTO wind_reading (" +
								"wr_airport_id, " +
								"wr_variable, " +
								"wr_gusty, " +
								"wr_velocity, " +
								"wr_velocity_gust, " +
								"wr_datetime" +
							") " +
							"VALUES (" +
								"\"" + sensor.getAirportId() + "\", " +
								"\"" + sensor.bearingIsVariable() + "\", " +
								"\"" + sensor.windHasGusts() + "\", " +
								sensor.getWindVelocity() + ", " +
								sensor.getWindGustVelocity() + ", " +
								"\"" + sensor.getReadingTime() + "\"" + 
							");"
						);
				
				System.out.println("New wind data recorded");
			} 
			catch (SQLException e) 
			{
				if (e.toString().contains("[SQLITE_CONSTRAINT_PRIMARYKEY]"))
					System.out.println("No new wind data available");
				else
					e.printStackTrace();
			}
		}
		// wind is not variable but has gusts
		else if (!sensor.bearingIsVariable() && sensor.windHasGusts())
		{
			try 
			{
				DBAction.executeUpdateUncaught(dbConnection,
						"INSERT INTO wind_reading (" +
								"wr_airport_id, " +
								"wr_variable, " +
								"wr_gusty, " +
								"wr_bearing, " + 
								"wr_velocity, " +
								"wr_velocity_gust, " +
								"wr_datetime" +
							") " +
							"VALUES (" +
								"\"" + sensor.getAirportId() + "\", " +
								"\"" + sensor.bearingIsVariable() + "\", " +
								"\"" + sensor.windHasGusts() + "\", " +
								sensor.getWindBearing() + ", " +
								sensor.getWindVelocity() + ", " +
								sensor.getWindGustVelocity() + ", " +
								"\"" + sensor.getReadingTime() + "\"" +
							");"
						);
				
				System.out.println("New wind data recorded");
			} 
			catch (SQLException e) 
			{
				if (e.toString().contains("[SQLITE_CONSTRAINT_PRIMARYKEY]"))
					System.out.println("No new wind data available");
				else
					e.printStackTrace();
			}
		}
		// wind is variable but does not have gusts
		else if (sensor.bearingIsVariable() && !sensor.windHasGusts())
		{
			try 
			{
				DBAction.executeUpdateUncaught(dbConnection,
						"INSERT INTO wind_reading (" +
								"wr_airport_id, " +
								"wr_variable, " +
								"wr_gusty, " +
								"wr_velocity, " +
								"wr_datetime" +
							") " +
							"VALUES (" +
								"\"" + sensor.getAirportId() + "\", " +
								"\"" + sensor.bearingIsVariable() + "\", " +
								"\"" + sensor.windHasGusts() + "\", " +
								sensor.getWindVelocity() + ", " +
								"\"" + sensor.getReadingTime() + "\"" +
							");"
						);
				
				System.out.println("New wind data recorded");
			} 
			catch (SQLException e) 
			{
				if (e.toString().contains("[SQLITE_CONSTRAINT_PRIMARYKEY]"))
					System.out.println("No new wind data available");
				else
					e.printStackTrace();
			}
		}
		// wind is not variable and does not have gusts
		else if (!sensor.bearingIsVariable() && !sensor.windHasGusts())
		{
			try 
			{
				DBAction.executeUpdateUncaught(dbConnection,
						"INSERT INTO wind_reading (" +
								"wr_airport_id, " +
								"wr_variable, " +
								"wr_gusty, " +
								"wr_bearing, " + 
								"wr_velocity, " +
								"wr_datetime" +
							") " +
							"VALUES (" +
								"\"" + sensor.getAirportId() + "\", " +
								"\"" + sensor.bearingIsVariable() + "\", " +
								"\"" + sensor.windHasGusts() + "\", " +
								sensor.getWindBearing() + ", " +
								sensor.getWindVelocity() + ", " +
								"\"" + sensor.getReadingTime() + "\"" +
							");"
						);
				
				System.out.println("New wind data recorded");
			} 
			catch (SQLException e) 
			{
				if (e.toString().contains("[SQLITE_CONSTRAINT_PRIMARYKEY]"))
					System.out.println("No new wind data available");
				else
					e.printStackTrace();
			}
		}
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
