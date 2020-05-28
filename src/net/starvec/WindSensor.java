package net.starvec;

import java.sql.Connection;
import java.time.Instant;

public class WindSensor
{
	private String id;
	private boolean isVariable, hasGusts;
	private int bearing, velocity, gustVelocity;
	private String readingTime;
	private Airport airport;
	
	public WindSensor(String id, Connection dbConnection)
	{
		this.id = id;
		airport = new Airport(id, dbConnection);
	}
	
	public void setMetar(String metar) {
		airport.setMetar(metar);
	}
	
	public void parseMetar()
	{	
		Instant instant = Instant.now();
		int hour, minute = 0;
		String metar = airport.getMetar();
		
		String windString = "";
		int i = metar.indexOf("KT");
		
		int j = i;
		while (j >= 0 && metar.charAt(j) != ' ') {
			j--;
		}
		
		int k = j;
		while (k>= 0 && metar.charAt(k) != 'Z') {
			k--;
		}
		
		hour = Integer.parseInt(metar.substring(k-4, k-2));
		minute = Integer.parseInt(metar.substring(k-2, k));
		
		windString = metar.substring(j+1, i+2);
		if (windString.contains("VRB")) 
		{
			isVariable = true;
			bearing = -1;
		}
		else 
		{
			isVariable = false;
			bearing = Integer.parseInt(windString.substring(0, 3));
		}
		
		velocity = Integer.parseInt(windString.substring(3, 5));
		
		if (windString.contains("G"))
		{
			int indexOfG = windString.indexOf('G');
			gustVelocity = Integer.parseInt(windString.substring(indexOfG + 1, indexOfG + 3));
		}
		
		
		readingTime = instant.toString().substring(0, 10) + "T" + hour + ":" + minute + ":00";
	}
	
	public String getAirportId() {
		return id;
	}
	
	public String getAirportName() {
		return airport.getName();
	}
	
	public float getAirportLat() {
		return airport.getLat();
	}
	
	public float getAirportLon() {
		return airport.getLon();
	}
	
	public String getAirportMunicipality() {
		return airport.getMunicipality();
	}
	
	public boolean bearingIsVariable() {
		return isVariable;
	}
	
	public boolean windHasGusts() {
		return hasGusts;
	}
	
	public int getWindBearing() {
		return bearing;
	}
	
	public int getWindVelocity() {
		return velocity;
	}
	
	public int getWindGustVelocity() {
		return gustVelocity;
	}
	
	public String getReadingTime() {
		return readingTime;
	}
}
