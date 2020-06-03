package net.starvec;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.TimeZone;

public class AirUpdateThread extends Thread
{
	private static final int MIN_TIME_BETWEEN_REFRESH = 1000;
	private static final int DESIRED_LOOP_PERIOD = 20000;
	
	private ArrayList<PurpleAir> sensors;
	Connection dbConnection;
	
	public AirUpdateThread(ArrayList<PurpleAir> sensors, Connection dbConnection)
	{
		this.sensors = sensors;
		this.dbConnection = dbConnection;
	}
	
	public void run()
	{
		while (true)
		{
			// for every sensor
			for (int i = 0; i < sensors.size(); i++)
			{
				// refresh the data, handle that refreshed data, and sleep
				System.out.println("Attempting to get new data for air sensor " + sensors.get(i).getPrimaryName() + "(" + sensors.get(i).getPrimaryId() + ")");
				sensors.get(i).refreshSensorData();
				handleSensorRefresh(i);
				sleep(MIN_TIME_BETWEEN_REFRESH);
			}
			
			sleep(Math.max((DESIRED_LOOP_PERIOD - sensors.size()*MIN_TIME_BETWEEN_REFRESH), MIN_TIME_BETWEEN_REFRESH));
		}
	}
	
	private void handleSensorRefresh(int i)
	{
		PurpleAir sensor = sensors.get(i);
		LocalDateTime readingTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(sensor.getLastPrimaryReadingTime()*1000), 
                TimeZone.getDefault().toZoneId());
		
		try 
		{
			// insert sensor A data
			DBAction.executeUpdateUncaught(dbConnection,
					"INSERT INTO air_reading " +
						"VALUES (" +
							sensor.getPrimaryId() + ", " +
							sensor.getPrimaryP0_3Count() + ", " +
							sensor.getPrimaryP0_5Count() + ", " +
							sensor.getPrimaryP1Count() + ", " +
							sensor.getPrimaryP2_5Count() + ", " +
							sensor.getPrimaryP5Count() + ", " +
							sensor.getPrimaryP10Count() + ", " +
							sensor.getPrimaryPm1Value() + ", " +
							sensor.getPrimaryPm2_5Value() + ", " +
							sensor.getPrimaryPm10Value() + ", " +
							sensor.getTemperatureF() + ", " + 
							sensor.getHumidity() + ", " + 
							sensor.getPressure() + ", " +
							"\"" + readingTime + "\"" +
						");"
					);
		
		
			// insert sensor B data
			DBAction.executeUpdateUncaught(dbConnection,
					"INSERT INTO air_reading (" +
							"ar_sensor_id, " +
							"ar_p0_3_count, " +
							"ar_p0_5_count, " +
							"ar_p1_count, " +
							"ar_p2_5_count, " +
							"ar_p5_count, " +
							"ar_p10_count, " +
							"ar_pm1_value, " +
							"ar_pm2_5_value, " +
							"ar_pm10_value, " +
							"ar_datetime" +
						") " +
						"VALUES (" +
							sensor.getSecondaryId() + ", " +
							sensor.getSecondaryP0_3Count() + ", " +
							sensor.getSecondaryP0_5Count() + ", " +
							sensor.getSecondaryP1Count() + ", " +
							sensor.getSecondaryP2_5Count() + ", " +
							sensor.getSecondaryP5Count() + ", " +
							sensor.getSecondaryP10Count() + ", " +
							sensor.getSecondaryPm1Value() + ", " +
							sensor.getSecondaryPm2_5Value() + ", " +
							sensor.getSecondaryPm10Value() + ", " +
							"\"" + readingTime + "\"" +
						");"
					);
			
			System.out.println("New air quality data recorded");
		} 
		catch (SQLException e) 
		{
			if (e.toString().contains("[SQLITE_CONSTRAINT_PRIMARYKEY]"))
				System.out.println("No new air quality data available");
			else
				e.printStackTrace();
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
