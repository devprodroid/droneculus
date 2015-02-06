package core.commands;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import core.control.Control;
import core.utils.Config;
import de.yadrone.base.command.CommandManager;

/**
 * Grants Access to Commands and builds Strings with TimeStamps for logging
 *
 */
public class Commands {

	private static StringBuilder sb = new StringBuilder();

	// Drone CommandManager
	private static CommandManager cmd = Control.drone.getCommandManager();
	private static Timestamp tstamp = new Timestamp(System.currentTimeMillis());

	// used for timestamps
	private static boolean hovering = false;

	/**
	 * Invokes the Drone to go Down
	 * 
	 * @param speed
	 *            = Percentage of Speed
	 */
	public static synchronized void goDown(int speed) {

		cmd.down(speed).doFor(Config.MILLIS_FOR_COMMANDS);
		buildString(speed, "Flying Down!");
	}

	/**
	 * Invokes the Drone to go Up
	 * 
	 * @param speed
	 *            = Percentage of Speed
	 */
	public static synchronized void goUp(int speed) {

		cmd.up(speed).doFor(Config.MILLIS_FOR_COMMANDS);
		buildString(speed, "Flying Up!");
	}

	/**
	 * Invokes the Drone to go Backward
	 * 
	 * @param speed
	 *            = Percentage of Speed
	 */
	public static synchronized void backward(int speed) {

		cmd.backward(speed).doFor(Config.MILLIS_FOR_COMMANDS);
		buildString(speed, "Flying Backward!");
	}

	/**
	 * Invokes the Drone to go Forward
	 * 
	 * @param speed
	 *            = Percentage of Speed
	 */
	public static synchronized void forward(int speed) {

		cmd.forward(speed).doFor(Config.MILLIS_FOR_COMMANDS);
		buildString(speed, "Flying Forward!");
	}

	/**
	 * Invokes the Drone to go Right
	 * 
	 * @param speed
	 *            = Percentage of Speed
	 */
	public static synchronized void goRight(int speed) {

		cmd.goRight(speed).doFor(Config.MILLIS_FOR_COMMANDS);
		buildString(speed, "Flying Right!");
	}

	/**
	 * Invokes the Drone to go Left
	 * 
	 * @param speed
	 *            = Percentage of Speed
	 */
	public static synchronized void goLeft(int speed) {

		cmd.goLeft(speed).doFor(Config.MILLIS_FOR_COMMANDS);
		buildString(speed, "Flying Left!");
	}

	/**
	 * Invokes the Drone to spin Right
	 * 
	 * @param speed
	 *            = Percentage of Speed
	 */
	public static synchronized void spinRight(int speed) {

		cmd.spinRight(speed).doFor(Config.MILLIS_FOR_COMMANDS);
		buildString(speed, "Rotating Right!");
	}

	/**
	 * Invokes the Drone to spin Left
	 * 
	 * @param speed
	 *            = Percentage of Speed
	 */
	public static synchronized void spinLeft(int speed) {

		cmd.spinLeft(speed).doFor(Config.MILLIS_FOR_COMMANDS);
		buildString(speed, "Rotating Left!");
	}

	/**
	 * Invokes the Drone to hover
	 */
	public static synchronized void hover() {
		hovering = true;
		cmd.hover().doFor(30);

	}

	/**
	 * Invokes the Drone to wait
	 * 
	 * @param millis
	 *            = time in ms to wait
	 */
	public static synchronized void waitFor(long millis) {
		cmd.waitFor(millis);
		Control.out.println("Waiting");
	}

	/** Invokes the Drone to freeze */
	public static synchronized void freeze() {
		cmd.freeze();
	}

	/** Invokes the Drone to land */
	public static synchronized void landing() {
		cmd.landing();
		Control.out.println("Land now");
	}

	/** Invokes the Drone to take off */
	public static synchronized void takeOff() {
		cmd.takeOff();
	}

	/** Invokes the Drone to fall down */
	public static synchronized void emergency() {
		cmd.emergency();
	}

	/** builds a String with TimeStamp for the Commands */
	private static synchronized void buildString(int speed, String text) {

		Timestamp last = tstamp;
		tstamp = new Timestamp(System.currentTimeMillis());
		long diff = diffTimestamps(last, tstamp);

		if (hovering) {
			hoveringString(text, diff, speed);
		} else {
			notHoveringString(text, diff, speed);
		}
		hovering = false;
		Control.out.println(sb.toString());
		sb = new StringBuilder();
	}

	/** builds the String, for when drone is hovering */
	private static void hoveringString(String text, long diff, int speed) {
		sb.append("Timestamp: ");
		sb.append(tstamp);
		sb.append(" - (");
		sb.append("hovered for ");
		sb.append(diff);
		sb.append(")");
		sb.append(" -- ");
		sb.append(text);
		if (speed > 0) {
			sb.append(" Value: ");
			sb.append(speed);
		}
	}

	/** builds the String, for when drone is not hovering */
	private static void notHoveringString(String text, long diff, int speed) {
		sb.append("Timestamp: ");
		sb.append(tstamp);
		sb.append(" - (");
		sb.append(diff);
		sb.append(")");
		sb.append(" -- ");
		sb.append(text);
		if (speed > 0) {
			sb.append(" Value: ");
			sb.append(speed);
		}
	}

	/**
	 * calculates the difference between to timestamps in ms
	 * 
	 * @param t1
	 *            = first Timestamp
	 * @param t2
	 *            = second Timestamp
	 * @return time in ms
	 */
	private static synchronized long diffTimestamps(Timestamp t1, Timestamp t2) {
		String date1 = t1.toString(), date2 = t2.toString();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		Date d1 = null;
		Date d2 = null;
		try {
			d1 = df.parse(date1);
			d2 = df.parse(date2);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		long diffInMilliseconds = Math.abs(d1.getTime() - d2.getTime());
		return diffInMilliseconds;
	}
}
