package net.starvec;

import java.io.IOException;
import java.net.NoRouteToHostException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import org.apache.commons.io.IOUtils;

public class PurpleAir 
{
	static private final Boolean SENSOR_A = true;
	static private final Boolean SENSOR_B = false;
	
	private boolean justCreated;
	
	private int id;
	private String rawJSONString;
	private float lat, lon;
	private String tertiarySensorType;
	private int humidity, tempF;
	private float pressure;
	private String locationType;
	private String firmwareVersion;
	private long uptime;
	private long lastUpdateCheck;
	private int rssi;
	
	private Sensor sensorA, sensorB;

	public PurpleAir(int id)
	{	
		this.id = id;
		justCreated = true;
		try {
			refreshJSONString();
		}
		catch(UnknownHostException e) {
			System.err.println("Unknown Host - Check Internet Connection");
		}
		catch(NoRouteToHostException e) {
			System.err.println("No Route To Host - Check Internet Connection");
		}
		
		
		int sensorAId = getSensorID(rawJSONString, SENSOR_A);
		int sensorBId = getSensorID(rawJSONString, SENSOR_B);
		String sensorALabel = getSensorLabel(rawJSONString, SENSOR_A);
		String sensorBLabel = getSensorLabel(rawJSONString, SENSOR_B);
		String sensorAType = getSensorType(rawJSONString, SENSOR_A);
		String sensorBType = getSensorType(rawJSONString, SENSOR_B);
		
		tertiarySensorType = getSensorType(rawJSONString, null);
		
		sensorA = new Sensor(sensorAId, sensorALabel, sensorAType);
		sensorB = new Sensor(sensorBId, sensorBLabel, sensorBType);
		
		refreshSensorData();
	}
	
	private void refreshJSONString() throws UnknownHostException, NoRouteToHostException
	{
		String json = "";
		try 
		{
			// get the JSON data string from the purple air web site using the sensor id
			json = IOUtils.toString(new URL("https://www.purpleair.com/json?show=" + id), Charset.forName("UTF-8"));
			
			if (json.length() < 1000 || json.length() > 4000 || json.isEmpty())
			{
				System.err.println("Invalid Sensor ID, JSON String not updated");
			}
			
			rawJSONString = json;
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	
	public void refreshSensorData()
	{
		if (!justCreated)
		{
			try {
				refreshJSONString();
			}
			catch(UnknownHostException e) {
				System.err.println("Unknown Host - Check Internet Connection");
			}
			catch(NoRouteToHostException e) {
				System.err.println("No Route To Host - Check Internet Connection");
			}
		}
		
		String dataStringA = getSensorDataString(rawJSONString, SENSOR_A);
		String dataStringB = getSensorDataString(rawJSONString, SENSOR_B);
		sensorA.updateSensorData(dataStringA);
		sensorB.updateSensorData(dataStringB);
		
		// all the additional data not from either of the air quality sensors is found in sensor A's data string for some reason
		lat = Float.parseFloat(parseDataFromString(dataStringA, "Lat"));
		lon = Float.parseFloat(parseDataFromString(dataStringA, "Lon"));
		humidity = Integer.parseInt(parseDataFromString(dataStringA, "humidity"));
		tempF = Integer.parseInt(parseDataFromString(dataStringA, "temp_f"));
		pressure = Float.parseFloat(parseDataFromString(dataStringA, "pressure"));
		locationType = parseDataFromString(dataStringA, "DEVICE_LOCATIONTYPE");
		firmwareVersion = parseDataFromString(dataStringA, "Version");
		lastUpdateCheck = Long.parseLong(parseDataFromString(dataStringA, "LastUpdateCheck"));
		uptime = Long.parseLong(parseDataFromString(dataStringA, "Uptime"));
		rssi = Integer.parseInt(parseDataFromString(dataStringA, "RSSI"));
		
		justCreated = false;
	}

	private String parseDataFromString(String dataString, String label)
	{
		// find the index of the character after the end of the label string
				String targetString = label + ":";
				int i = dataString.indexOf(targetString) + targetString.length();
				
				// from the index i, scan the string to find where the data ends
				int j = i;
				while (dataString.charAt(j) != ',') {
					j++;
				}
				
				// return the substring between those two indices
				return dataString.substring(i, j);
	}
	
	private int getSensorID(String rawJSONString, boolean sensor)
	{
		String dataString = getSensorDataString(rawJSONString, sensor);
		String data = parseDataFromString(dataString, "ID");
		return Integer.parseInt(data);
	}
	
	private String getSensorLabel(String rawJSONString, boolean sensor)
	{
		String dataString = getSensorDataString(rawJSONString, sensor);
		return parseDataFromString(dataString, "Label");
	}
	
	private String getSensorType(String rawJSONString, Boolean sensor)
	{
		String dataString = getSensorDataString(rawJSONString, SENSOR_A);
		String typeString = parseDataFromString(dataString, "Type");
		
		// split the string on the + character
		String[] sensors = typeString.split("\\+");
		
		// return the correct sensor type
		if (sensor == null)
			return sensors[2];
		
		if (sensor == SENSOR_A)
			return sensors[0];
		else
			return sensors[1];
	}
	
	private String getSensorDataString(String rawString, boolean sensor)
	{
		String dataString = rawString.replace("\"", "");
		
		// sensor A
		if (sensor == SENSOR_A)
		{
			// scan the string until we find a certain character
			int i = 0;
			while (dataString.charAt(i) != '[') {
				i++;
			}
			
			// from the index i, continue to scan the string until we find another particular character
			int j = i+1;
			while (dataString.charAt(j) != '}') {
				j++;
			}
			
			// return the substring between those two indices
			return dataString.substring(i+1, j+1);
		}
		// sensor B
		else
		{
			// scan the string until we find a two particular characters in a row
			int i = 0;
			while (!(dataString.charAt(i) == '}' && dataString.charAt(i+1) == ',')) {
				i++;
			}
			
			// from the index i, continue to scan the string until we find another particular character
			int j = i+1;
			while (dataString.charAt(j) != '}') {
				j++;
			}
			
			// return the substring between those two indices
			return dataString.substring(i+2, j+1);
		}
	}
	
	public int getPrimaryId() {
		return id;
	}
	
	public int getSecondaryId() {
		return sensorB.getId();
	}
	
	public String getPrimaryName() {
		return sensorA.getLabel();
	}
	
	public String getSecondaryName() {
		return sensorB.getLabel();
	}
	
	public String getPrimarySensorType() {
		return sensorA.getSensorType();
	}
	
	public String getSecondarySensorType() {
		return sensorB.getSensorType();
	}
	
	public String getTertiarySensorType() {
		return tertiarySensorType;
	}
	
	public float getLat() {
		return lat;
	}
	
	public float getLon() {
		return lon;
	}
	
	public String getLocationType() {
		return locationType;
	}
	
	public String getFirmwareVersion() {
		return firmwareVersion;
	}
	
	public long getLastUpdateCheckTime() {
		return lastUpdateCheck;
	}
	
	public long getUptime() {
		return uptime;
	}
	
	public int getRSSI() {
		return rssi;
	}
	
	public float getPrimaryP0_3Count() {
		return sensorA.getP0_3Count();
	}
	
	public float getSecondaryP0_3Count() {
		return sensorB.getP0_3Count();
	}
	
	public float getPrimaryP0_5Count() {
		return sensorA.getP0_5Count();
	}
	
	public float getSecondaryP0_5Count() {
		return sensorB.getP0_5Count();
	}
	
	public float getPrimaryP1Count() {
		return sensorA.getP1Count();
	}
	
	public float getSecondaryP1Count() {
		return sensorB.getP1Count();
	}
	
	public float getPrimaryP2_5Count() {
		return sensorA.getP2_5Count();
	}
	
	public float getSecondaryP2_5Count() {
		return sensorB.getP2_5Count();
	}
	
	public float getPrimaryP5Count() {
		return sensorA.getP5Count();
	}
	
	public float getSecondaryP5Count() {
		return sensorB.getP5Count();
	}
	
	public float getPrimaryP10Count() {
		return sensorA.getP10Count();
	}
	
	public float getSecondaryP10Count() {
		return sensorB.getP10Count();
	}
	
	public float getPrimaryPm1Value() {
		return sensorA.getPm1Value();
	}
	
	public float getSecondaryPm1Value() {
		return sensorB.getPm1Value();
	}
	
	public float getPrimaryPm2_5Value() {
		return sensorA.getPm2_5Value();
	}
	
	public float getSecondaryPm2_5Value() {
		return sensorB.getPm2_5Value();
	}
	
	public float getPrimaryPm10Value() {
		return sensorA.getPm10Value();
	}
	
	public float getSecondaryPm10Value() {
		return sensorB.getPm10Value();
	}
	
	public int getTemperatureF() {
		return tempF;
	}
	
	public int getHumidity() {
		return humidity;
	}
	
	public float getPressure() {
		return pressure;
	}
	
	public long getLastPrimaryReadingTime() {
		return sensorA.getLastReadingTime();
	}
	
	public long getLastSecondaryReadingTime() {
		return sensorB.getLastReadingTime();
	}
	
	@Override
	public String toString() 
	{
		return "PurpleAir [rawJSONString=" + rawJSONString + ", id=" + id + ",\n\tsensorA=" + sensorA + ",\n\tsensorB="
				+ sensorB + ",\n\thumidity=" + humidity + ", tempF=" + tempF + ", pressure=" + pressure + ", lat=" + lat
				+ ", lon=" + lon + ", locationType=" + locationType + ", firmwareVersion=" + firmwareVersion
				+ ", lastUpdateCheck=" + lastUpdateCheck + ", uptime=" + uptime + ", rssi=" + rssi + "]";
	}
}
