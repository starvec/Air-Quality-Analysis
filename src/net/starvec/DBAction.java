package net.starvec;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBAction
{
	public static Connection openDatabaseConnection(String path)
	{
		Connection connection = null;
		
		File dbFile = new File(path);
		if (!dbFile.exists())
		{
			System.err.println("Database file '" + path + "' could not be found");
			return connection;
		}
		
		try
		{
			// Create TPCH database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + path);
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
		}
		return connection;
	}
	
	public static boolean closeDatabaseConnection(Connection conn)
	{
		try
		{
			if(conn != null)
			{
				conn.close();
				return true;
			}
		}
		catch(SQLException e)
		{
			// connection close failed.
			System.err.println(e.getMessage());
		}
		return false;
	}
	
	public static ResultSet executeQuery(Connection connection, String queryStr)
	{
		try
		{
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(10);  // set timeout to 10 sec.

			ResultSet rs = statement.executeQuery(queryStr);
			return rs;
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
			System.err.println("Error on -> " + queryStr);
			ResultSet rs = null;
			return rs ;
		}
	}
	
	public static void executeUpdate(Connection connection, String queryStr)
	{
		try
		{
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(10);  // set timeout to 10 sec.
			statement.executeUpdate(queryStr);
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
			System.err.println("Error on -> " + queryStr);
		}
	}	
}
