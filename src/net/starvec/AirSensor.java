package net.starvec;

public class AirSensor 
{
	private int id; 				// id of the air quality sensor
	private String label;			// label of the air quality sensor
	private String sensorType;		// name of the type of sensor
	
	private float p0_3Count;		// estimated number of particles <0.3um in size
	private float p0_5Count;		// estimated number of particles <0.5um in size
	private float p1Count;			// estimated number of particles <1um in size
	private float p2_5Count;		// estimated number of particles <2.5um in size
	private float p5Count;			// estimated number of particles <5um in size
	private float p10Count;			// estimated number of particles <10um in size
	private float pm1Value;			// micrograms of particles <1um in size per cubic meter of air
	private float pm2_5Value;		// micrograms of particles <2.5um in size per cubic meter of air
	private float pm10Value;		// micrograms of particles <10um in size per cubic meter of air
	
	private long lastReadingAge;	// age of the latest reading in minutes
	private long lastReadingTime;	// UTC time of the last reading that was taken
	
	public AirSensor(int id, String label, String sensorType)
	{
		this.id = id;
		this.label = label;
		this.sensorType = sensorType;
	}
	
	public void updateSensorData(String dataString)
	{
		lastReadingAge = Integer.parseInt(parseDataFromString(dataString, "AGE"));
		lastReadingTime = Integer.parseInt(parseDataFromString(dataString, "LastSeen"));
		p0_3Count = Float.parseFloat(parseDataFromString(dataString, "p_0_3_um"));
		p0_5Count = Float.parseFloat(parseDataFromString(dataString, "p_0_5_um"));
		p1Count = Float.parseFloat(parseDataFromString(dataString, "p_1_0_um"));
		p2_5Count = Float.parseFloat(parseDataFromString(dataString, "p_2_5_um"));
		p10Count = Float.parseFloat(parseDataFromString(dataString, "p_5_0_um"));
		pm1Value = Float.parseFloat(parseDataFromString(dataString, "pm10_0_atm"));
		pm2_5Value = Float.parseFloat(parseDataFromString(dataString, "pm2_5_atm"));
		pm10Value = Float.parseFloat(parseDataFromString(dataString, "pm10_0_atm"));
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

	public int getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public String getSensorType() {
		return sensorType;
	}

	public long getLastReadingAge() {
		return lastReadingAge;
	}

	public long getLastReadingTime() {
		return lastReadingTime;
	}

	public float getP0_3Count() {
		return p0_3Count;
	}

	public float getP0_5Count() {
		return p0_5Count;
	}

	public float getP1Count() {
		return p1Count;
	}

	public float getP2_5Count() {
		return p2_5Count;
	}

	public float getP5Count() {
		return p5Count;
	}

	public float getP10Count() {
		return p10Count;
	}

	public float getPm1Value() {
		return pm1Value;
	}

	public float getPm2_5Value() {
		return pm2_5Value;
	}

	public float getPm10Value() {
		return pm10Value;
	}

	@Override
	public String toString() {
		return "Sensor [id=" + id + ", label=" + label + ", sensorType=" + sensorType + ", p0_3Count=" + p0_3Count
				+ ", p0_5Count=" + p0_5Count + ", p1Count=" + p1Count + ", p2_5Count=" + p2_5Count + ", p5Count="
				+ p5Count + ", p10Count=" + p10Count + ", pm1Value=" + pm1Value + ", pm2_5Value=" + pm2_5Value
				+ ", pm10Value=" + pm10Value + ", lastReadingAge=" + lastReadingAge + ", lastReadingTime="
				+ lastReadingTime + "]";
	}
}
