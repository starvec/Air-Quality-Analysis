package net.starvec;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.io.IOUtils;

public class Airport 
{
	private String id;
	private String name;
	private float lat, lon;
	private String municipality;
	private String metar;
	
	// constructor takes airport identifier and populates the rest of the data
	public Airport(String id, Connection dbConnection)
	{	
		this.id = id;
		
		ResultSet result = DBAction.executeQuery(dbConnection, 
				"SELECT a.a_airport_name AS name, a.a_latitude AS lat, a.a_longitude AS lon, a.a_municipality AS municipality " +
				"FROM airport a " + 
				"WHERE a.a_airport_id = \"" + id + "\";"
				);
		try
		{
			if (result.next())
			{
				name = result.getString("name");
				lat = result.getFloat("lat");
				lon = result.getFloat("lon");
				municipality = result.getString("municipality");
			}
		}
		catch(SQLException sqle)
		{
			System.err.println(sqle.getMessage());
		}
		
		String url = "https://www.aviationweather.gov/metar/data?ids=" + id + "&format=raw&hours=0&taf=off&layout=off";
		String webData = "";
		try {
			webData = IOUtils.toString(new URL(url), Charset.forName("UTF-8"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int metarStart = webData.indexOf("<code>" + id) + 6;
		String webDataSub = webData.substring(metarStart);
		int metarEnd = webDataSub.indexOf("</code>");
		
		metar = webDataSub.substring(0, metarEnd);
	}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public float getLat() {
		return lat;
	}
	
	public float getLon() {
		return lon;
	}
	
	public String getMunicipality() {
		return municipality;
	}
	
	public void setMetar(String rawMetar) {
		metar = rawMetar;
	}

	public String getMetar() {
		return metar;
	}
}
