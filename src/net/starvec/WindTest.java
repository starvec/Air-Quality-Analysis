package net.starvec;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.Instant;

import org.apache.commons.io.IOUtils;

public class WindTest 
{
	public static void main(String[] args) 
	{
		int direction, velocity, gustVelocity = 0;
		int hour, minute = 0;
		
		Instant instant = Instant.now();
		
		/*
		String url2 = "https://ourairports.com/data/airports.csv";
		String webData2 = "";
		try {
			webData2 = IOUtils.toString(new URL(url2), Charset.forName("UTF-8"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(webData2);
		*/
		
		String url = "https://www.aviationweather.gov/metar/data?ids=AYPY,KMER,ABCD,KMCE&format=raw&hours=0&taf=off&layout=off";
		String webData = "";
		try {
			webData = IOUtils.toString(new URL(url), Charset.forName("UTF-8"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(webData);
		
		String windString = "";
		int i = webData.indexOf("KT");
		
		int j = i;
		while (j >= 0 && webData.charAt(j) != ' ') {
			j--;
		}
		
		int k = j;
		while (k>= 0 && webData.charAt(k) != 'Z') {
			k--;
		}
		
		hour = Integer.parseInt(webData.substring(k-4, k-2));
		minute = Integer.parseInt(webData.substring(k-2, k));
		
		
		windString = webData.substring(j+1, i+2);
		System.out.println(windString);
		
		if (windString.contains("VRB")) {
			direction = -1;
		}
		else {
			direction = Integer.parseInt(windString.substring(0, 3));
		}
		
		velocity = Integer.parseInt(windString.substring(3, 5));
		
		if (windString.contains("G"))
		{
			int indexOfG = windString.indexOf('G');
			gustVelocity = Integer.parseInt(windString.substring(indexOfG + 1, indexOfG + 3));
		}
		
		String dateTime = instant.toString().substring(0, 10) + "T" + hour + ":" + minute + ":00";
		
		System.out.println("Direction: " + direction + ", Velocity: " + velocity + ", Gusts: " + gustVelocity);
		System.out.println("Time: " + dateTime);
	}
}