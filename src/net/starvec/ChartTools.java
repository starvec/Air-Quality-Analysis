package net.starvec;

import java.awt.Color;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.knowm.xchart.RadarChart;
import org.knowm.xchart.RadarChartBuilder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler.LegendLayout;
import org.knowm.xchart.style.Styler.LegendPosition;

public class ChartTools 
{
	public static XYChart getXYChart(Connection dbConnection, String query, String[] seriesLabels, String xLabel, String yLabel)
	{	
		ArrayList<XYSeries> series = new ArrayList<>();
		ArrayList<List<Date>> xData = new ArrayList<>();
		ArrayList<List<Double>> yData = new ArrayList<>();
		
		for (int i = 0; i < seriesLabels.length; i++)
		{
			xData.add(new ArrayList<Date>());
			yData.add(new ArrayList<Double>());
		}
		
		ResultSet result = DBAction.executeQuery(dbConnection, query);
		try
		{	
			while (result.next())
			{
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				Date date;
				try
				{
					date = dateFormat.parse(result.getString(1).replace('T', ' '));
				} 
				catch (ParseException e)
				{
					e.printStackTrace();
					return null;
				}
				
				for (int i = 0; i < seriesLabels.length; i++)
				{
					xData.get(i).add(date);
					yData.get(i).add(result.getDouble(i+2));
				}
			}
		}
		catch(SQLException sqle)
		{
			System.err.println(sqle.getMessage());
			return null;
		}
		
		// set up chart
		XYChart chart = new XYChartBuilder().xAxisTitle(xLabel).yAxisTitle(yLabel).build();
	    chart.getStyler().setChartBackgroundColor(Color.WHITE);
	    chart.getStyler().setLegendPosition(LegendPosition.OutsideS);
	    chart.getStyler().setLegendLayout(LegendLayout.Horizontal);
	    chart.getStyler().setDatePattern("d-MMM-YY hh:mm");
	    chart.getStyler().setDecimalPattern("#0.0");
	    chart.getStyler().setXAxisLabelRotation(-45);
	    chart.getStyler().setLocale(Locale.US);
	    chart.getStyler().setToolTipsEnabled(true);
		
	    // check for empty series and return a null chart if there are any
	    // this usually occurs if data is queried for an empty time period
	    for (int i = 0; i < yData.size(); i++)
	    {
	    	if (yData.get(i).size() == 0)
	    		return null;
	    }
	    
	    // add any built series to the chart
		for (int i = 0; i < seriesLabels.length; i++) {
			series.add(chart.addSeries(seriesLabels[i], xData.get(i), yData.get(i)));
		}
		
		return chart;
	}
	
	public static RadarChart getWindRadarChart(Connection dbConnection, String query, int divisions)
	{	
		ArrayList<Double> amData = new ArrayList<Double>();
		ArrayList<Double> pmData = new ArrayList<Double>();
		ArrayList<String> variableLabels = new ArrayList<String>();
		
		double vDist = 360.0/divisions;
		
		variableLabels.add("0");
		
		for (int i = 0; i < divisions; i++)
		{
			amData.add(0.0);
			pmData.add(0.0);
			
			if (i > 0) 
				variableLabels.add(String.valueOf((divisions - i)*vDist));
		}
		
		ResultSet result = DBAction.executeQuery(dbConnection, query);
		try
		{	
			String databaseNoonDateTime = DateTimeTools.spinnerDateTimeToDatabaseFormat("Mon Sep 14 12:00:00 PDT 2020");
			String databaseMidnightDateTime = DateTimeTools.spinnerDateTimeToDatabaseFormat("Mon Sep 14 00:00:00 PDT 2020");
			String databaseNoon = databaseNoonDateTime.substring(11);
			String databaseMidnight = databaseMidnightDateTime.substring(11);
			//System.out.println(databaseNoon);
			//System.out.println(databaseMidnight);
			
			while (result.next())
			{	
				double bearing = result.getDouble("bearing");
				int velocity = result.getInt("velocity");
				String datetime = result.getString("datetime");
				String databaseTime = datetime.substring(11);
				
				for (int i = 0; i < divisions-1; i++)
				{
					if (Math.abs(i*vDist - bearing) <= vDist && Math.abs((i+1)*vDist - bearing) <= vDist)
					{
						double lowBearing = i*vDist;
						double highBearing = (i+1)*vDist;
						
						double lowDist = bearing - lowBearing;
						double highDist = highBearing - bearing;
						
						double lowRatio = (vDist - lowDist) / vDist;
						double highRatio = (vDist - highDist) / vDist;
						
						if (DateTimeTools.timeGreaterThan(databaseNoon, databaseMidnight))
						{
							// after noon, before midnight
							if (DateTimeTools.timeGreaterThan(databaseTime, databaseNoon) || DateTimeTools.timeLessThan(databaseTime, databaseMidnight))
							{
								pmData.set(i, pmData.get(i) + lowRatio*velocity);
								pmData.set(i+1, pmData.get(i+1) + highRatio*velocity);
								//System.out.println("pm: " + databaseTime);
							}
							// after midnight, before noon
							else
							{
								amData.set(i, amData.get(i) + lowRatio*velocity);
								amData.set(i+1, amData.get(i+1) + highRatio*velocity);
								//System.out.println("am: " + databaseTime);
							}
						}
						else
						{
							if (DateTimeTools.timeGreaterThan(databaseTime, databaseNoon) || DateTimeTools.timeLessThan(databaseTime, databaseMidnight))
							{
								amData.set(i, amData.get(i) + lowRatio*velocity);
								amData.set(i+1, amData.get(i+1) + highRatio*velocity);
								//System.out.println("am: " + databaseTime);
							}
							else
							{
								pmData.set(i, pmData.get(i) + lowRatio*velocity);
								pmData.set(i+1, pmData.get(i+1) + highRatio*velocity);
								//System.out.println("pm: " + databaseTime);
							}
						}
						break;
					}
				}
			}
			
			double maxValue = 0;
			
			for (int i = 0; i < divisions; i++) 
			{
				maxValue = Math.max(maxValue, amData.get(i));
				maxValue = Math.max(maxValue, pmData.get(i));
			}
			
			for (int i = 0; i < divisions; i++)
			{
				amData.set(i, amData.get(i)/maxValue);
				pmData.set(i, pmData.get(i)/maxValue);
			}
		}
		catch(SQLException sqle)
		{
			System.err.println(sqle.getMessage());
			return null;
		}
		
		RadarChart chart = new RadarChartBuilder().title("Wind").build();
	    chart.getStyler().setChartBackgroundColor(Color.WHITE);
	    chart.getStyler().setLegendPosition(LegendPosition.InsideSW);
	    chart.getStyler().setDecimalPattern("#0.0");
	    
	    chart.setVariableLabels(Arrays.asList(variableLabels.toArray()).toArray(new String[divisions]));
		chart.addSeries("12:00 am - 11:59 am", 
				Arrays.stream(reverseAndOffsetArrayList(amData).toArray()).mapToDouble(num -> Double.parseDouble(num.toString())).toArray());
		chart.addSeries("12:00 pm - 11:59 pm", 
				Arrays.stream(reverseAndOffsetArrayList(pmData).toArray()).mapToDouble(num -> Double.parseDouble(num.toString())).toArray());
		
		return chart;
	}
	
	private static ArrayList<Double> reverseAndOffsetArrayList(ArrayList<Double> list)
	{
		ArrayList<Double> reverse = new ArrayList<Double>();
		ArrayList<Double> offset = new ArrayList<Double>();
		
		for (int i = list.size()-1; i >= 0; i--) {
			reverse.add(list.get(i));
		}
		
		offset.add(reverse.get(reverse.size()-1));
		
		for (int i = 0; i < reverse.size()-1; i++) {
			offset.add(reverse.get(i));
		}
		
		return offset;
	}
}
