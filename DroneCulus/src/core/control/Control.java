package core.control;

import java.io.PrintStream;

import core.logging.ExceptionListener;
import core.logging.FlightOutput;
import core.oculusrift.OculusRiftEventManager;
import core.utils.Config;
import de.yadrone.base.ARDrone;

/**
 * This Class grants Access to the PrintStream for displaying Information relevant for Flight, 
 * because the YaDrone CommandManager makes it impossible to read the Console. It also grants Access
 * to the DataStorage and the Drone. Furthermore it starts the ControlView.
 * 
 */
public class Control {
	
	//Output for Flight related Messages
	private static FlightOutput 				flightOut = new FlightOutput();
	
	//grants fast Access to the Drone, the Output-Stream or the DataCenter,
	//this way they don't have to be passed through every Constructor
	//TODO: DataCenter is secured as a Singleton and could be accessed static from every Class, 
	//		but drone and PrintStream are just final, that's why everything is listed here.
	//		Special Security for the Drone-Instance could be considered.
	public final static PrintStream 			out  = flightOut.getPrintstream();
	public final static ARDrone		 			drone = new ARDrone();
	public final static DataCenter 				data = DataCenter.getInstance();
	
	
	
	//is used for checking, if the Drone is connected
	public static boolean 						isDroneConnected = false;
		
	/**
	 * starts the ControlManager and initializes a few Listeners
	 */
	public static void start(){		
		
		out.println("Control started!");	
		drone.addExceptionListener(new ExceptionListener());
		
		OculusRiftEventManager.setTriggerIntervall( Config.MILLIS_FOR_EVENTMANAGER );
		
		new ControlManager(flightOut);	
				
		OculusRiftEventManager.addEventListener(data);
		drone.getNavDataManager().addBatteryListener(data);		
	}
}
