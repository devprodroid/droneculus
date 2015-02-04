package core.oculusrift;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import core.control.Control;
import core.utils.Config;
import de.fruitfly.ovr.OculusRift;

/**
 * EventManager for OculusRift Events
 * Listeners can be added and are informed, when relevant changes occur
 *
 */
public class OculusRiftEventManager implements Runnable {
	
	//Listener List
	private static List<IOculusRiftListener> 	listeners = new ArrayList<IOculusRiftListener>();
	private static OculusRift 					oculusRift = new OculusRift();	
	private static long 						triggerIntervall = 50;
	
	//OculusRift Event Values in Degree
	private static float 						yaw;
	private static float 						pitch;
	private static float 						roll;
	
	//OculusRift Event Values in Multiplier, every Config.OFFSET_FOR_RIFT_ACTION they are increased
	private static int							yawMult;
	private static int 							pitchMult;
	private static int 							rollMult;	
	
	//Boolean if the EventManager should be shut down or not
	private static volatile boolean 			running = true;
	
	static {
		new Thread(new OculusRiftEventManager()).start();		
	}
	
	/**
	 * add Listener to OculusRiftEventManager, who get informed on changes.
	 * @param listener = should implement IOculusRiftListener
	 */
	public static synchronized void addEventListener(IOculusRiftListener listener)  {
		Control.out.println("OculusRiftManager -- Listener: " + listener.getClass().getName() + " added!");
		listeners.add(listener);
	}
	/**
	 * remove Listener from OculusRiftEventManager.
	 * @param listener = should implement IOculusRiftListener
	 */
	public static synchronized void removeEventListener(IOculusRiftListener listener)   {	
		Control.out.println("OculusRiftManager -- Listener: " + listener.getClass().getName() + " removed!");
	    listeners.remove(listener);
	}
	
	/**
	 * running Method
	 */
	@Override
	public void run() {
		
		Control.out.println("OculusRiftEventManager running...");	
		oculusRift.init();
		
		Control.out.println("OculusRift initialised: " + oculusRift.isInitialized());
		
		while(running) {
			
			oculusRift.poll();		  
			
			yaw = oculusRift.getYawDegrees_LH();
			pitch = oculusRift.getPitchDegrees_LH();
			roll = oculusRift.getRollDegrees_LH();
		  	
			yawMult = (int) (yaw / Config.OFFSET_FOR_RIFT_ACTION);
		   	pitchMult = (int) (pitch / Config.OFFSET_FOR_RIFT_ACTION);
		   	rollMult = (int) (roll / Config.OFFSET_FOR_RIFT_ACTION);
		   	
			fireEvent();
			
			//sleep if every Multiplier is zero, so that HoverInvoker has less stuff to do
			if(yawMult == 0 && pitchMult == 0 && rollMult == 0) {
				sleep();
			}
		}		
	}
	
	/**
	 * informs every Listener about Event
	 */
	private synchronized void fireEvent() {
		
	   OculusRiftEvent event = new OculusRiftEvent(this, yaw, pitch, roll, yawMult, pitchMult, rollMult);
	   
	   List<IOculusRiftListener> listenersCopy = new ArrayList<IOculusRiftListener>(listeners);
	   
	   Iterator<IOculusRiftListener> i = listenersCopy.iterator();
	   while(i.hasNext())  {
	      ((IOculusRiftListener) i.next()).changed(event);
	   }
	}
	
	/**
	 * sleep
	 */
	private void sleep() {
		try {
			Thread.sleep(triggerIntervall);
		} catch (InterruptedException e) {
			e.printStackTrace(Control.out);
		}
	}
	
	/**
	 * get Intervall for Sleeping
	 * @return
	 */
	public synchronized static long getTriggerIntervall() {
		return triggerIntervall;
	}
	
	/**
	 * set Intervall for Sleeping
	 * @param triggerIntervall = time in ms to sleep
	 */
	public synchronized static void setTriggerIntervall(long triggerIntervall) {
		OculusRiftEventManager.triggerIntervall = triggerIntervall;
	}
	
	/**
	 * get OculusRift device
	 * @return
	 */
	public static OculusRift getOculusRift() {
		return oculusRift;
	}
	
	/**
	 * set OculusRift device
	 * @param rift = new device
	 */
	public static void setOculusRift(OculusRift rift) {
		oculusRift = rift;
	}
	
	/**
	 * reset OculusRift
	 */
	public static synchronized void resetOculusRift() {
		oculusRift.destroy();
		oculusRift.init();
	}
	
	/**
	 * stop EventManager from running
	 */
	public static void stop() {	running = false; }
}
	 