package core.utils;

public class Config {
	
	/**
	 * Length in Milliseconds for each Command started in CommandThread
	 */
	public static long MILLIS_FOR_COMMANDS = 200;
	
	public static long MILLIS_FOR_EVENTMANAGER = 200; 
	
	/**
	 * Offset, for when the OculusRift should recognize Input as a Command
	 */
	public static int OFFSET_FOR_RIFT_ACTION = 10;
	
	/**
	 * Time in Milliseconds for displaying Info-Text on HUD
	 */
	public static int INFO_LENGTH_VALUE = 2000;
	
	/**
	 * Underneath this Value Battery-Percentage is displayed in red
	 */
	public static int BATTERY_ALERT_VALUE = 20;		
	
	/**
	 * Framerate for initializing the Webcamera
	 */
	public static int WEBCAM_FRAMERATE = 30;		
	
	/**
	 * Id for the left/primary webcam, 
	 */
	public static  int WEBCAM_PRIMARY_ID = 0;
	
	/**
	 * Id for the right/secondary webcam, 
	 */
	public static int WEBCAM_SECONDARY_ID = 1;
	
	
}
