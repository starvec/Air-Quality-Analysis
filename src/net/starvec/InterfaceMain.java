package net.starvec;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.internal.chartpart.Chart;
import org.knowm.xchart.style.Styler.LegendLayout;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.RadarChart;
import org.knowm.xchart.RadarChartBuilder;
import org.knowm.xchart.XChartPanel;

import javax.swing.JScrollPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.awt.event.ActionEvent;
import javax.swing.JList;
import java.awt.Image;

import javax.swing.AbstractListModel;
import javax.swing.JPanel;
import javax.swing.ScrollPaneConstants;
import java.awt.Toolkit;
import javax.swing.JTabbedPane;
import javax.swing.JSplitPane;
import javax.swing.JRadioButton;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import java.util.Calendar;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;


public class InterfaceMain 
{
	private final long MILLIS_IN_DAY = 86400000;
	
	private JFrame frmMercedCollege;
	private SpringLayout springLayout;
	private JList<String> listSensors = new JList<String>();
	
	JTabbedPane tabbedPaneGraph;
	int tabbedPaneGraphLastSelected;
	
	XChartPanel panelParticleCountGraph;
	XChartPanel panelParticleMassGraph;
	XChartPanel panelTPH;
	XChartPanel panelWind;
	
	JSpinner spinnerChartDateTimeStartCustom;
	JSpinner spinnerChartDateTimeEndCustom;
	
	JCheckBox chckbxUseEarliestData;
	JCheckBox chckbxUseLatestData;
	
	Connection dbConnection;
	
	static Config config;
	
	private static ArrayList<PurpleAir> airSensors;
	private static ArrayList<String> airSensorDisplayNames;
	private static ArrayList<WindSensor> windSensors;
	
	String chartDateTimeStart = "";
	String chartDateTimeEnd = "";
	
	String chartDateTimeStartLast = "";
	String chartDateTimeEndLast = "";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) 
	{
		Connection dbConnection = DBAction.openDatabaseConnection("data.db");
		
		config = new Config(dbConnection);
		
		ArrayList<String> sensorDisplayNames = new ArrayList<>();
		sensorDisplayNames.add("Sensor Alpha");
		sensorDisplayNames.add("Sensor Beta");
		sensorDisplayNames.add("Sensor Gamma");
		sensorDisplayNames.add("Sensor Delta");
		
		EventQueue.invokeLater(new Runnable()
		{
			public void run() 
			{
				try {
					InterfaceMain window = new InterfaceMain(dbConnection, config, airSensors, windSensors, sensorDisplayNames);
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public InterfaceMain(Connection dbConnection, Config config, ArrayList<PurpleAir> sensors, ArrayList<WindSensor> windSensors, ArrayList<String> sensorDisplayNames) 
	{
		this.dbConnection = dbConnection;
		this.config = config;
		this.airSensors = sensors;
		this.airSensorDisplayNames = sensorDisplayNames;
		this.windSensors = windSensors;
		
		initialize(sensors, sensorDisplayNames);
		frmMercedCollege.setVisible(true);
	}

	@SuppressWarnings("serial")
	private void initialize(ArrayList<PurpleAir> sensors, ArrayList<String> sensorDisplayNames) 
	{
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		
		List<Image> icons = new ArrayList<Image>();
		icons.add(Toolkit.getDefaultToolkit().getImage(InterfaceMain.class.getResource("/net/starvec/mclogo16.png")));
		icons.add(Toolkit.getDefaultToolkit().getImage(InterfaceMain.class.getResource("/net/starvec/mclogo20.png")));
		icons.add(Toolkit.getDefaultToolkit().getImage(InterfaceMain.class.getResource("/net/starvec/mclogo24.png")));
		icons.add(Toolkit.getDefaultToolkit().getImage(InterfaceMain.class.getResource("/net/starvec/mclogo32.png")));
		icons.add(Toolkit.getDefaultToolkit().getImage(InterfaceMain.class.getResource("/net/starvec/mclogo40.png")));
		icons.add(Toolkit.getDefaultToolkit().getImage(InterfaceMain.class.getResource("/net/starvec/mclogo48.png")));
		icons.add(Toolkit.getDefaultToolkit().getImage(InterfaceMain.class.getResource("/net/starvec/mclogo60.png")));
		icons.add(Toolkit.getDefaultToolkit().getImage(InterfaceMain.class.getResource("/net/starvec/mclogo64.png")));
		icons.add(Toolkit.getDefaultToolkit().getImage(InterfaceMain.class.getResource("/net/starvec/mclogo72.png")));
		icons.add(Toolkit.getDefaultToolkit().getImage(InterfaceMain.class.getResource("/net/starvec/mclogo128.png")));
		
		frmMercedCollege = new JFrame();
		frmMercedCollege.setTitle("Merced College - Air Quality Monitor");
		frmMercedCollege.setIconImages(icons);
		frmMercedCollege.setBounds(100, 100, 1280, 720);
		frmMercedCollege.setLocationRelativeTo(null);
		frmMercedCollege.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		springLayout = new SpringLayout();
		frmMercedCollege.getContentPane().setLayout(springLayout);
		frmMercedCollege.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	    frmMercedCollege.addWindowListener(new WindowAdapter() {
	        @Override
	        public void windowClosing(WindowEvent event) 
	        {
	        	DBAction.closeDatabaseConnection(dbConnection);
	        	frmMercedCollege.dispose();
	        	System.exit(0);
	        }
	    });
		
		JScrollPane scrollPaneSensors = new JScrollPane();
		scrollPaneSensors.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		springLayout.putConstraint(SpringLayout.NORTH, scrollPaneSensors, 32, SpringLayout.NORTH, frmMercedCollege.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, scrollPaneSensors, 0, SpringLayout.WEST, frmMercedCollege.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPaneSensors, 0, SpringLayout.SOUTH, frmMercedCollege.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, scrollPaneSensors, 256, SpringLayout.WEST, frmMercedCollege.getContentPane());
		frmMercedCollege.getContentPane().add(scrollPaneSensors);
			
		JButton btnAddSensor = new JButton("Add");
		springLayout.putConstraint(SpringLayout.EAST, btnAddSensor, 84, SpringLayout.WEST, frmMercedCollege.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, btnAddSensor, 0, SpringLayout.NORTH, scrollPaneSensors);
		springLayout.putConstraint(SpringLayout.NORTH, btnAddSensor, 0, SpringLayout.NORTH, frmMercedCollege.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, btnAddSensor, 0, SpringLayout.WEST, frmMercedCollege.getContentPane());
		frmMercedCollege.getContentPane().add(btnAddSensor);
		
		btnAddSensor.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				
				InterfaceAddSensor window = new InterfaceAddSensor(sensors, sensorDisplayNames, windSensors, dbConnection, frmMercedCollege);

				// thread to watch for when the add sensor interface finishes and update the sensor list when it does
				new Thread(new Runnable() {
				    public void run() 
				    {
				    	while (!window.isFinished()) {
							sleep(100);
						}
				    	
				    	listSensors.setModel(new AbstractListModel<String>() 
						{
							public int getSize() {
								return sensorDisplayNames.size();
							}
							
							public String getElementAt(int index) {
								return sensorDisplayNames.get(index);
							}
						});
				    }
				}).start();	
			}
		});
		
		JButton btnEditSensor = new JButton("Edit");
		springLayout.putConstraint(SpringLayout.NORTH, btnEditSensor, 0, SpringLayout.NORTH, frmMercedCollege.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, btnEditSensor, 0, SpringLayout.EAST, btnAddSensor);
		springLayout.putConstraint(SpringLayout.SOUTH, btnEditSensor, 0, SpringLayout.NORTH, scrollPaneSensors);
		frmMercedCollege.getContentPane().add(btnEditSensor);
		
		listSensors.setModel(new AbstractListModel<String>() 
		{
			public int getSize() {
				return sensorDisplayNames.size();
			}
			
			public String getElementAt(int index) {
				return sensorDisplayNames.get(index);
			}
		});
		
		scrollPaneSensors.setViewportView(listSensors);
		
		JButton btnDeleteSensor = new JButton("Delete");
		springLayout.putConstraint(SpringLayout.EAST, btnEditSensor, 0, SpringLayout.WEST, btnDeleteSensor);
		springLayout.putConstraint(SpringLayout.WEST, btnDeleteSensor, -85, SpringLayout.EAST, scrollPaneSensors);
		springLayout.putConstraint(SpringLayout.EAST, btnDeleteSensor, -1, SpringLayout.EAST, scrollPaneSensors);
		springLayout.putConstraint(SpringLayout.EAST, btnEditSensor, 0, SpringLayout.WEST, btnDeleteSensor);
		springLayout.putConstraint(SpringLayout.NORTH, btnDeleteSensor, 0, SpringLayout.NORTH, frmMercedCollege.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, btnDeleteSensor, 0, SpringLayout.NORTH, scrollPaneSensors);
		frmMercedCollege.getContentPane().add(btnDeleteSensor);
		
		tabbedPaneGraph = new JTabbedPane(JTabbedPane.TOP);
		springLayout.putConstraint(SpringLayout.SOUTH, tabbedPaneGraph, -240, SpringLayout.SOUTH, frmMercedCollege.getContentPane());
		springLayout.putConstraint(SpringLayout.NORTH, tabbedPaneGraph, 0, SpringLayout.NORTH, frmMercedCollege.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, tabbedPaneGraph, 0, SpringLayout.EAST, scrollPaneSensors);
		springLayout.putConstraint(SpringLayout.EAST, tabbedPaneGraph, 0, SpringLayout.EAST, frmMercedCollege.getContentPane());
		frmMercedCollege.getContentPane().add(tabbedPaneGraph);
		
		JLabel lblChartDateTimeRange = new JLabel("Chart Date/Time Range");
		springLayout.putConstraint(SpringLayout.WEST, lblChartDateTimeRange, 4, SpringLayout.EAST, scrollPaneSensors);
		lblChartDateTimeRange.setHorizontalAlignment(SwingConstants.CENTER);
		springLayout.putConstraint(SpringLayout.NORTH, lblChartDateTimeRange, 4, SpringLayout.SOUTH, tabbedPaneGraph);
		frmMercedCollege.getContentPane().add(lblChartDateTimeRange);
		
		chckbxUseEarliestData = new JCheckBox("Use earliest data");
		chckbxUseEarliestData.setHorizontalAlignment(SwingConstants.LEFT);
		chckbxUseEarliestData.setSelected(true);
		frmMercedCollege.getContentPane().add(chckbxUseEarliestData);
		chckbxUseEarliestData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				if (chckbxUseEarliestData.isSelected())
					spinnerChartDateTimeStartCustom.setEnabled(false);
				else
					spinnerChartDateTimeStartCustom.setEnabled(true);
			}
		});
		
		chckbxUseLatestData = new JCheckBox("Use latest data");
		chckbxUseLatestData.setSelected(true);
		frmMercedCollege.getContentPane().add(chckbxUseLatestData);
		chckbxUseLatestData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				if (chckbxUseLatestData.isSelected())
					spinnerChartDateTimeEndCustom.setEnabled(false);
				else
					spinnerChartDateTimeEndCustom.setEnabled(true);
			}
		});
		
		JLabel lblChartDateTimeStart = new JLabel("Start Date/Time");
		springLayout.putConstraint(SpringLayout.NORTH, chckbxUseEarliestData, 4, SpringLayout.SOUTH, lblChartDateTimeStart);
		lblChartDateTimeStart.setHorizontalAlignment(SwingConstants.CENTER);
		springLayout.putConstraint(SpringLayout.NORTH, lblChartDateTimeStart, 8, SpringLayout.SOUTH, lblChartDateTimeRange);
		frmMercedCollege.getContentPane().add(lblChartDateTimeStart);
		
		JLabel lblChartDateTimeEnd = new JLabel("End Date/Time");
		springLayout.putConstraint(SpringLayout.WEST, lblChartDateTimeEnd, 0, SpringLayout.WEST, chckbxUseLatestData);
		springLayout.putConstraint(SpringLayout.EAST, lblChartDateTimeEnd, 0, SpringLayout.EAST, chckbxUseLatestData);
		springLayout.putConstraint(SpringLayout.NORTH, chckbxUseLatestData, 4, SpringLayout.SOUTH, lblChartDateTimeEnd);
		lblChartDateTimeEnd.setHorizontalAlignment(SwingConstants.CENTER);
		springLayout.putConstraint(SpringLayout.NORTH, lblChartDateTimeEnd, 8, SpringLayout.SOUTH, lblChartDateTimeRange);
		frmMercedCollege.getContentPane().add(lblChartDateTimeEnd);
		
		JSeparator separator = new JSeparator();
		springLayout.putConstraint(SpringLayout.WEST, separator, 256, SpringLayout.EAST, scrollPaneSensors);
		springLayout.putConstraint(SpringLayout.EAST, lblChartDateTimeRange, -4, SpringLayout.EAST, separator);
		springLayout.putConstraint(SpringLayout.NORTH, separator, 4, SpringLayout.SOUTH, tabbedPaneGraph);
		springLayout.putConstraint(SpringLayout.SOUTH, separator, -4, SpringLayout.SOUTH, frmMercedCollege.getContentPane());
		separator.setOrientation(SwingConstants.VERTICAL);
		frmMercedCollege.getContentPane().add(separator);
		
		JLabel lblChartDateTimeStartCustom = new JLabel("Custom");
		springLayout.putConstraint(SpringLayout.NORTH, lblChartDateTimeStartCustom, 8, SpringLayout.SOUTH, chckbxUseEarliestData);
		frmMercedCollege.getContentPane().add(lblChartDateTimeStartCustom);
		
		spinnerChartDateTimeStartCustom = new JSpinner();
		springLayout.putConstraint(SpringLayout.WEST, lblChartDateTimeStart, 0, SpringLayout.WEST, spinnerChartDateTimeStartCustom);
		springLayout.putConstraint(SpringLayout.EAST, lblChartDateTimeStart, 0, SpringLayout.EAST, spinnerChartDateTimeStartCustom);
		springLayout.putConstraint(SpringLayout.EAST, lblChartDateTimeStartCustom, 0, SpringLayout.EAST, spinnerChartDateTimeStartCustom);
		springLayout.putConstraint(SpringLayout.EAST, chckbxUseEarliestData, 0, SpringLayout.EAST, spinnerChartDateTimeStartCustom);
		spinnerChartDateTimeStartCustom.setEnabled(false);
		springLayout.putConstraint(SpringLayout.WEST, lblChartDateTimeStartCustom, 0, SpringLayout.WEST, spinnerChartDateTimeStartCustom);
		springLayout.putConstraint(SpringLayout.NORTH, spinnerChartDateTimeStartCustom, 4, SpringLayout.SOUTH, lblChartDateTimeStartCustom);
		springLayout.putConstraint(SpringLayout.WEST, spinnerChartDateTimeStartCustom, 8, SpringLayout.EAST, scrollPaneSensors);
		spinnerChartDateTimeStartCustom.setModel(new SpinnerDateModel(new Date(Instant.now().getEpochSecond()*1000 - MILLIS_IN_DAY), null, null, Calendar.DAY_OF_YEAR));
		frmMercedCollege.getContentPane().add(spinnerChartDateTimeStartCustom);
		
		spinnerChartDateTimeEndCustom = new JSpinner();
		springLayout.putConstraint(SpringLayout.WEST, chckbxUseLatestData, 0, SpringLayout.WEST, spinnerChartDateTimeEndCustom);
		springLayout.putConstraint(SpringLayout.EAST, chckbxUseLatestData, 0, SpringLayout.EAST, spinnerChartDateTimeEndCustom);
		spinnerChartDateTimeEndCustom.setEnabled(false);
		springLayout.putConstraint(SpringLayout.EAST, spinnerChartDateTimeEndCustom, -8, SpringLayout.WEST, separator);
		spinnerChartDateTimeEndCustom.setModel(new SpinnerDateModel(new Date(Instant.now().getEpochSecond()*1000), null, null, Calendar.DAY_OF_YEAR));
		frmMercedCollege.getContentPane().add(spinnerChartDateTimeEndCustom);
		
		JLabel lblChartDateTimeEndCustom = new JLabel("Custom");
		springLayout.putConstraint(SpringLayout.NORTH, spinnerChartDateTimeEndCustom, 4, SpringLayout.SOUTH, lblChartDateTimeEndCustom);
		springLayout.putConstraint(SpringLayout.NORTH, lblChartDateTimeEndCustom, 8, SpringLayout.SOUTH, chckbxUseLatestData);
		springLayout.putConstraint(SpringLayout.WEST, lblChartDateTimeEndCustom, 0, SpringLayout.WEST, spinnerChartDateTimeEndCustom);
		springLayout.putConstraint(SpringLayout.EAST, lblChartDateTimeEndCustom, 0, SpringLayout.EAST, spinnerChartDateTimeEndCustom);
		frmMercedCollege.getContentPane().add(lblChartDateTimeEndCustom);
		
		if (chckbxUseEarliestData.isSelected())
			chartDateTimeStart = "0001-01-01T00:00:00";
		else
			chartDateTimeStart = DateTimeTools.spinnerDateTimeToDatabaseFormat(spinnerChartDateTimeStartCustom.getValue().toString());
		
		if (chckbxUseLatestData.isSelected())
			chartDateTimeEnd = "9999-12-31T23:59:59";
		else
			chartDateTimeEnd = DateTimeTools.spinnerDateTimeToDatabaseFormat(spinnerChartDateTimeEndCustom.getValue().toString());
		
		chartDateTimeStartLast = chartDateTimeStart;
		chartDateTimeEndLast = chartDateTimeEnd;
		
		String particleCountQuery = "SELECT ar.ar_datetime, ar.ar_p0_3_count, ar.ar_p0_5_count, ar.ar_p1_count, ar.ar_p2_5_count, ar.ar_p5_count, ar.ar_p10_count " + 
				"FROM air_reading ar " + 
				"WHERE ar.ar_sensor_id = 14617 AND ar.ar_datetime >= \"" + chartDateTimeStart + "\" AND ar.ar_datetime <= \"" + chartDateTimeEnd + "\";";

		String[] particleCountGraphSeriesLabels = {"0.3µm", "0.5µm", "1.0µm", "2.5µm", "5.0µm", "10.0µm"};
		
		panelParticleCountGraph = new XChartPanel<XYChart>(ChartTools.getXYChart(dbConnection, particleCountQuery, particleCountGraphSeriesLabels, "Date/Time", "Particles per m^3"));
		panelParticleCountGraph.setBorder(null);
		tabbedPaneGraph.addTab("Particle Count", null, panelParticleCountGraph, null);
		tabbedPaneGraph.setEnabledAt(0, true);
		panelParticleCountGraph.setLayout(new SpringLayout());
		
		String particleMassQuery = "SELECT ar.ar_datetime, ar.ar_pm1_value, ar.ar_pm2_5_value, ar.ar_pm10_value " + 
		"FROM air_reading ar " + 
		"WHERE ar.ar_sensor_id = 14617 AND ar.ar_datetime >= \"" + chartDateTimeStart + "\" AND ar.ar_datetime <= \"" + chartDateTimeEnd + "\";";
		
		String[] particleMassGraphSeriesLabels = {"1.0µm", "2.5µm", "10.0µm"};
		
		panelParticleMassGraph = new XChartPanel<XYChart>(ChartTools.getXYChart(dbConnection, particleMassQuery, particleMassGraphSeriesLabels, "Date/Time", "Particle mass per m^3"));
		panelParticleMassGraph.setBorder(null);
		tabbedPaneGraph.addTab("Particle Mass", null, panelParticleMassGraph, null);
		panelParticleMassGraph.setLayout(new SpringLayout());
		
		String TPHQuery = "SELECT ar.ar_datetime, ar.ar_temperature_f, ar.ar_pressure, ar.ar_humidity " + 
		"FROM air_reading ar " + 
		"WHERE ar.ar_sensor_id = 14617 AND ar.ar_datetime >= \"" + chartDateTimeStart + "\" AND ar.ar_datetime <= \"" + chartDateTimeEnd + "\";";
		
		String[] TPHGraphSeriesLabels = {"Temperature", "Pressure", "Humidity"};
		
		panelTPH = new XChartPanel<XYChart>(ChartTools.getXYChart(dbConnection, TPHQuery, TPHGraphSeriesLabels, "Date/Time", "Temperature (F)/Pressure (hPa)/Humidity (%)"));
		panelTPH.setBorder(null);
		tabbedPaneGraph.addTab("Temperature/Pressure/Humidity", null, panelTPH, null);
		panelTPH.setLayout(new SpringLayout());
		
		String WindQuery = "SELECT wr.wr_bearing AS bearing, wr.wr_velocity AS velocity, wr.wr_datetime AS datetime " + 
				"FROM wind_reading wr, sensor s " + 
				"WHERE s.s_primary_sensor_id = 14617 AND s.s_airport_id == wr.wr_airport_id " +
				"AND wr.wr_datetime >= \"" + chartDateTimeStart + "\" AND wr.wr_datetime <= \"" + chartDateTimeEnd + "\" AND wr.wr_variable == \"false\"";
		
		panelWind = new XChartPanel<RadarChart>(ChartTools.getWindRadarChart(dbConnection, WindQuery, 18));
		panelWind.setBorder(null);
		tabbedPaneGraph.addTab("Wind", null, panelWind, null);
		panelWind.setLayout(new SpringLayout());
		
		JButton btnChartDateTimeSubmit = new JButton("Submit");
		springLayout.putConstraint(SpringLayout.WEST, btnChartDateTimeSubmit, 8, SpringLayout.WEST, tabbedPaneGraph);
		btnChartDateTimeSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				tabbedPaneGraphLastSelected = tabbedPaneGraph.getSelectedIndex();
				setCharts(14617);
			}
		});
		springLayout.putConstraint(SpringLayout.SOUTH, btnChartDateTimeSubmit, -10, SpringLayout.SOUTH, frmMercedCollege.getContentPane());
		frmMercedCollege.getContentPane().add(btnChartDateTimeSubmit);
		
		JCheckBox chckbxChartDateTimeAutoSubmit = new JCheckBox("Auto-submit");
		springLayout.putConstraint(SpringLayout.EAST, btnChartDateTimeSubmit, -8, SpringLayout.WEST, chckbxChartDateTimeAutoSubmit);
		
		// check the config to see if the check box was last check or not and set it appropriately
		if (config.valueIs("chart_date_time_range_auto_submit", "1"))
			chckbxChartDateTimeAutoSubmit.setSelected(true);
		else
			chckbxChartDateTimeAutoSubmit.setSelected(false);
		
		chckbxChartDateTimeAutoSubmit.setToolTipText("Auto-refresh charts on date/time range change");
		springLayout.putConstraint(SpringLayout.SOUTH, chckbxChartDateTimeAutoSubmit, 0, SpringLayout.SOUTH, btnChartDateTimeSubmit);
		springLayout.putConstraint(SpringLayout.EAST, chckbxChartDateTimeAutoSubmit, -8, SpringLayout.EAST, separator);
		frmMercedCollege.getContentPane().add(chckbxChartDateTimeAutoSubmit);
		chckbxChartDateTimeAutoSubmit.addItemListener(new ItemListener() {
		    @Override
		    public void itemStateChanged(ItemEvent e) {
		        if(e.getStateChange() == ItemEvent.SELECTED) 
		        {
		        	config.updateValue("chart_date_time_range_auto_submit", "1");
		        } else {
		        	config.updateValue("chart_date_time_range_auto_submit", "0");
		        };
		    }
		});
		
		spinnerChartDateTimeStartCustom.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				if (chckbxChartDateTimeAutoSubmit.isSelected())
					setCharts(14617);
			}
		});
		
		frmMercedCollege.addComponentListener(new ComponentAdapter() 
		{
		    public void componentResized(ComponentEvent componentEvent) {
		        handleWindowResize();
		    }
		    
		    public void componentMoved(ComponentEvent componentEvent) {
		        handleWindowResize();
		    }
		});
	}
	
	private void setCharts(int sensorId)
	{
		if (chckbxUseEarliestData.isSelected())
			chartDateTimeStart = "0001-01-01T00:00:00";
		else
			chartDateTimeStart = DateTimeTools.spinnerDateTimeToDatabaseFormat(spinnerChartDateTimeStartCustom.getValue().toString());
		
		if (chckbxUseLatestData.isSelected())
			chartDateTimeEnd = "9999-12-31T23:59:59";
		else
			chartDateTimeEnd = DateTimeTools.spinnerDateTimeToDatabaseFormat(spinnerChartDateTimeEndCustom.getValue().toString());
		
		if (chartDateTimeStartLast.equals(chartDateTimeStart) && chartDateTimeEndLast.equals(chartDateTimeEnd) && !chckbxUseLatestData.isSelected())
			return;
		
		chartDateTimeStartLast = chartDateTimeStart;
		chartDateTimeEndLast = chartDateTimeEnd;
		
		// particle count chart
		String particleCountQuery = "SELECT ar.ar_datetime, ar.ar_p0_3_count, ar.ar_p0_5_count, ar.ar_p1_count, ar.ar_p2_5_count, ar.ar_p5_count, ar.ar_p10_count " + 
				"FROM air_reading ar " + 
				"WHERE ar.ar_sensor_id = 14617 AND ar.ar_datetime >= \"" + chartDateTimeStart + "\" AND ar.ar_datetime <= \"" + chartDateTimeEnd + "\";";

		String[] particleCountGraphSeriesLabels = {"0.3µm", "0.5µm", "1.0µm", "2.5µm", "5.0µm", "10.0µm"};
		
		XYChart particleCountChart = ChartTools.getXYChart(dbConnection, particleCountQuery, particleCountGraphSeriesLabels, "Date/Time", "Particles per m^3");
		// if chart has been returned as null, abort the update of charts
		if (particleCountChart == null)
			return;
		panelParticleCountGraph = new XChartPanel<XYChart>(particleCountChart);
		panelParticleCountGraph.setBorder(null);
		panelParticleCountGraph.setLayout(new SpringLayout());
		
		// particle mass chart
		String particleMassQuery = "SELECT ar.ar_datetime, ar.ar_pm1_value, ar.ar_pm2_5_value, ar.ar_pm10_value " + 
				"FROM air_reading ar " + 
				"WHERE ar.ar_sensor_id = 14617 AND ar.ar_datetime >= \"" + chartDateTimeStart + "\" AND ar.ar_datetime <= \"" + chartDateTimeEnd + "\";";
		
		String[] particleMassGraphSeriesLabels = {"1.0µm", "2.5µm", "10.0µm"};
		
		XYChart particleMassChart = ChartTools.getXYChart(dbConnection, particleMassQuery, particleMassGraphSeriesLabels, "Date/Time", "Particle mass per m^3");
		if (particleMassChart == null)
			return;
		panelParticleMassGraph = new XChartPanel<XYChart>(particleMassChart);
		panelParticleMassGraph.setBorder(null);
		panelParticleMassGraph.setLayout(new SpringLayout());
		
		// temperature, pressure, humidity chart
		String TPHQuery = "SELECT ar.ar_datetime, ar.ar_temperature_f, ar.ar_pressure, ar.ar_humidity " + 
				"FROM air_reading ar " + 
				"WHERE ar.ar_sensor_id = 14617 AND ar.ar_datetime >= \"" + chartDateTimeStart + "\" AND ar.ar_datetime <= \"" + chartDateTimeEnd + "\";";
		
		String[] TPHGraphSeriesLabels = {"Temperature", "Pressure", "Humidity"};
		
		XYChart tphChart = ChartTools.getXYChart(dbConnection, TPHQuery, TPHGraphSeriesLabels, "Date/Time", "Temperature (F)/Pressure (hPa)/Humidity (%)");
		if (tphChart == null)
			return;
		panelTPH = new XChartPanel<XYChart>(tphChart);
		panelTPH.setBorder(null);
		panelTPH.setLayout(new SpringLayout());
		
		// wind chart
		String WindQuery = "SELECT wr.wr_bearing AS bearing, wr.wr_velocity AS velocity, wr.wr_datetime AS datetime " + 
				"FROM wind_reading wr, sensor s " + 
				"WHERE s.s_primary_sensor_id = 14617 AND s.s_airport_id == wr.wr_airport_id " +
				"AND wr.wr_datetime >= \"" + chartDateTimeStart + "\" AND wr.wr_datetime <= \"" + chartDateTimeEnd + "\" AND wr.wr_variable == \"false\"";
		
		RadarChart windChart = ChartTools.getWindRadarChart(dbConnection, WindQuery, 18);
		if (windChart == null)
			return;
		panelWind = new XChartPanel<RadarChart>(windChart);
		panelWind.setBorder(null);
		panelWind.setLayout(new SpringLayout());
		
		// remove old charts and add new charts
		tabbedPaneGraph.removeAll();
		tabbedPaneGraph.addTab("Particle Count", null, panelParticleCountGraph, null);
		tabbedPaneGraph.addTab("Particle Mass", null, panelParticleMassGraph, null);
		tabbedPaneGraph.addTab("Temperature/Pressure/Humidity", null, panelTPH, null);
		tabbedPaneGraph.addTab("Wind", null, panelWind, null);
		tabbedPaneGraph.setSelectedIndex(tabbedPaneGraphLastSelected);
	}
	
	public void handleWindowResize()
	{
		int frameWidth = frmMercedCollege.getSize().width;
        int frameHeight = frmMercedCollege.getSize().height;
        
        int chartPanelWidth = panelParticleCountGraph.getSize().width;
        int chartPanelHeight = panelParticleCountGraph.getSize().height;
        
        //springLayout.putConstraint(SpringLayout.SOUTH, panelParticleCount, (int)(height/-2.0), SpringLayout.SOUTH, frmMercedCollege.getContentPane());
		//springLayout.putConstraint(SpringLayout.EAST, panelParticleCount, (int)((width - 256)/-2.0), SpringLayout.EAST, frmMercedCollege.getContentPane());
		
		//springLayout.putConstraint(SpringLayout.WEST, panelParticleMass, (int)((width - 256)/-2.0), SpringLayout.EAST, frmMercedCollege.getContentPane());
		//springLayout.putConstraint(SpringLayout.SOUTH, panelParticleMass, (int)(height/-2.0), SpringLayout.SOUTH, frmMercedCollege.getContentPane());
	}
	
	private static void sleep(int millis)
	{
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
