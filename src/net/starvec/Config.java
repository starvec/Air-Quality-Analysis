package net.starvec;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Config 
{
	private Connection dbConnection;
	private ArrayList<ConfigField> config = new ArrayList<>();
	
	// constructor for config takes the database connection and initializes the object with properties and values from the database
	public Config(Connection dbConnection) 
	{
		this.dbConnection = dbConnection;
		
		// query the database to get config values
		ResultSet result = DBAction.executeQuery(dbConnection, 
				"SELECT c.c_property AS property, c.c_value AS value " +
				"FROM config c;"
				);
		try
		{
			while(result.next()) {							
				addProperty(result.getString("property"), result.getString("value"));
			}
		}
		catch(SQLException sqle)
		{
			System.err.println(sqle.getMessage());
		}
	}
	
	// adds a new property and value to the config
	private void addProperty(String property, String value) {
		config.add(new ConfigField(property, value));
	}
	
	// updates the value of a specified property in the config
	public boolean updateValue(String property, String value)
	{
		for (int i = 0; i < config.size(); i++)
		{
			if (config.get(i).property.equals(property)) 
			{
				config.get(i).value = value;
				DBAction.executeUpdate(dbConnection, "UPDATE config SET c_value = '" + value + "' WHERE c_property = '" + property + "';");
				return true;
			}
		}
		
		System.err.println("Config Error, Property " + property + " not found!");
		return false;
	}
	
	// gets the value of a specified property from the config
	public String getValue(String property)
	{
		for (int i = 0; i < config.size(); i++)
		{
			if (config.get(i).property.equals(property)) 
			{
				return config.get(i).value;
			}
		}
		
		System.err.println("Config Error, Property " + property + " not found!");
		return null;
	}
	
	// returns true if the specified property has the specified value and false otherwise
	public boolean valueIs(String property, String value) {		
		return getValue(property).equals(value);
	}
}

// helper class for config
class ConfigField
{
	public String property, value;
	
	public ConfigField(String property, String value)
	{
		this.property = property;
		this.value = value;
	}
}
