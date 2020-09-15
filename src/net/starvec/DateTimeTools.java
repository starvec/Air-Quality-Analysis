package net.starvec;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeTools 
{
	// converts the date time format provided by the standard date and time spinner to the form stored in the database
		public static String spinnerDateTimeToDatabaseFormat(String datetime)
		{	
			String datetimeMonth = datetime.substring(4, 7);
			String formattedDatetime = datetime.substring(24);
			switch (datetimeMonth)
			{
				case "Jan":
					formattedDatetime += "-01-";
					break;
				case "Feb":
					formattedDatetime += "-02-";
					break;
				case "Mar":
					formattedDatetime += "-03-";
					break;
				case "Apr":
					formattedDatetime += "-04-";
					break;
				case "May":
					formattedDatetime += "-05-";
					break;
				case "Jun":
					formattedDatetime += "-06-";
					break;
				case "Jul":
					formattedDatetime += "-07-";
					break;
				case "Aug":
					formattedDatetime += "-08-";
					break;
				case "Sep":
					formattedDatetime += "-09-";
					break;
				case "Oct":
					formattedDatetime += "-10-";
					break;
				case "Nov":
					formattedDatetime += "-11-";
					break;
				case "Dec":
					formattedDatetime += "-12-";
					break;
			}
			
			formattedDatetime += datetime.substring(8, 10) + " " + datetime.substring(11, 19);
			
			LocalDateTime ldt = LocalDateTime.parse(formattedDatetime, DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss"));
			ZonedDateTime lzdt = ldt.atZone(ZoneId.systemDefault());	
			ZonedDateTime utczdt = lzdt.withZoneSameInstant(ZoneId.of("Etc/UTC"));
			
			String databaseDateTime = utczdt.toString().substring(0, 16) + ":00";
			return databaseDateTime;
		}
		
		// returns true if timeA in the format hh:mm:ss is less than timeB
		public static boolean timeLessThan(String timeA, String timeB)
		{
			if (timeA.charAt(1) == ':')
					timeA = "0" + timeA;
			
			if (timeB.charAt(1) == ':')
				timeB = "0" + timeB;
			
			int hourA = Integer.parseInt((String) timeA.substring(0, 2));
			int hourB = Integer.parseInt((String) timeB.substring(0, 2));
			
			int minuteA = Integer.parseInt((String) timeA.substring(3, 5));
			int minuteB = Integer.parseInt((String) timeB.substring(3, 5));
			
			int secondA = Integer.parseInt((String) timeA.substring(6));
			int secondB = Integer.parseInt((String) timeB.substring(6));
			
			if (hourA < hourB)
				return true;
			else if (hourA == hourB && minuteA < minuteB)
				return true;
			else if (hourA == hourB && minuteA == minuteB && secondA < secondB)
				return true;
			else
				return false;
		}
		
		// returns true if timeA in the format hh:mm:ss is greater than timeB
		public static boolean timeGreaterThan(String timeA, String timeB)
		{
			if (timeA.charAt(1) == ':')
				timeA = "0" + timeA;
		
			if (timeB.charAt(1) == ':')
				timeB = "0" + timeB;
		
			int hourA = Integer.parseInt((String) timeA.substring(0, 2));
			int hourB = Integer.parseInt((String) timeB.substring(0, 2));
			
			int minuteA = Integer.parseInt((String) timeA.substring(3, 5));
			int minuteB = Integer.parseInt((String) timeB.substring(3, 5));
			
			int secondA = Integer.parseInt((String) timeA.substring(6));
			int secondB = Integer.parseInt((String) timeB.substring(6));
			
			if (hourA > hourB)
				return true;
			else if (hourA == hourB && minuteA > minuteB)
				return true;
			else if (hourA == hourB && minuteA == minuteB && secondA > secondB)
				return true;
			else
				return false;
		}
}
