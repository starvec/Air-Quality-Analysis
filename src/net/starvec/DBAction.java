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
			return rs;
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
	
	public static void executeUpdateAsTransaction(Connection connection, String queryStr)
	{
		try
		{
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(10);  // set timeout to 10 sec.
			connection.setAutoCommit(false);
			statement.executeUpdate(queryStr);
			connection.commit();
			connection.setAutoCommit(true);
		}
		catch(SQLException e)
		{
			try {
				connection.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.err.println(e.getMessage());
			System.err.println("Error on -> " + queryStr);
		}
		
		
	}	
	
	public static void executeUpdateUncaught(Connection connection, String queryStr) throws SQLException
	{
		Statement statement = connection.createStatement();
		statement.setQueryTimeout(10);  // set timeout to 10 sec.
		statement.executeUpdate(queryStr);
	}
	
	public static void executeUpdateRetry(Connection connection, int waitTime, int timeOutTime, String queryStr)
	{
		boolean tryAgain = true;
		int timeLeft = timeOutTime;
		int timesTried = 0;
		
		while(tryAgain)
		{
			try
			{
				Statement statement = connection.createStatement();
				statement.setQueryTimeout(10);  // set timeout to 10 sec.
				statement.executeUpdate(queryStr);
				tryAgain = false;
			}
			catch(SQLException e)
			{	
				if (timeLeft > 0)
				{
					try {
						Thread.sleep(Math.min(waitTime, timeLeft));
					} catch (InterruptedException se) {
						se.printStackTrace();
					}
					
					timeLeft -= waitTime;
					timesTried++;
				}
				else
				{
					System.err.println(e.getMessage());
					System.err.println("Error on -> " + queryStr);
					System.err.println("Update tried " + timesTried + " times over " + timeOutTime + " milliseconds but timed out");
					tryAgain = false;
				}
			}
		}	
	}	
}
