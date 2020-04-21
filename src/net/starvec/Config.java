package net.starvec;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Config 
{
	private Connection dbConnection;
	private ArrayList<ConfigField> config = new ArrayList<>();
	
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
	
	private void addProperty(String property, String value) {
		config.add(new ConfigField(property, value));
	}
	
	public boolean updateValue(String property, String value)
	{
		for (int i = 0; i < config.size(); i++)
		{
			if (config.get(i).property.equals(property)) 
			{
				config.get(i).value = value;
				DBAction.executeQuery(dbConnection, "UPDATE config SET c_value = '" + value + "' WHERE c_property = '" + property + "';");
				return true;
			}
		}
		
		System.err.println("Config Error, Property " + property + " not found!");
		return false;
	}
	
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
	
	public boolean valueIs(String property, String value) {		
		return getValue(property).equals(value);
	}
}

class ConfigField
{
	public String property, value;
	
	public ConfigField(String property, String value)
	{
		this.property = property;
		this.value = value;
	}
}
