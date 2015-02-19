package core.controller;

import java.util.Observable;

import core.commands.Commands;
import core.commands.HoverInvoker;
import core.control.Control;
import core.templates.ContTemplateFactory;
import core.templates.IContTemplate;
import core.templates.TemplateVersions.Template;
import core.utils.Config;

/**
 * holds IContTemplates and handles input based on these Templates
 *
 */
public class ControllerHandler extends Observable implements Runnable {
	
	//Value of LeftThumb Axis
	private double 		magnitude;
	
	//Direction in Degree in which LeftThumb Axis is pointing
	private double 		direction;
	
	//boolean, if Controller is connected
	private boolean 	isConnected = true;
	
	//boolean if ControllerHandler is still running
	private boolean		running = true;
	
	//boolean if ControllerHandler is currently waiting
	private boolean 	waiting = true;
	
	//Template for Handling the Axis Values
	private IContTemplate template;
	
	public ControllerHandler(Template version, HoverInvoker hoverInv) {
		switchVersion(version);
		addObserver(hoverInv);
	}
	
	/**
	 * update the Magnitude Value of the Left Thumb
	 * @param value = new Value
	 */
	public void updateMagnitude(double value) {
		magnitude = value;
	}
	
	/**
	 * update the Direction Value of the Left Thumb
	 * @param value = new Value
	 */
	public void updateDirection(double value) {
		direction = value;
	}
	
	/**
	 * update if Controller is still connected or not
	 * @param connected = boolean for connection
	 */
	public void updateConnection(boolean connected) {
		isConnected = connected;
	}
	
	/**
	 * switch used Template
	 * @param version = Version of Template to be used
	 */
	public void switchVersion(Template version) {		
		template = ContTemplateFactory.makeTemplate(version);
	}
	
	/**
	 * set the ControllerHandler waiting or not
	 * @param waiting = boolean if waiting or not
	 */
	public void setWaiting(boolean waiting) {
		this.waiting = waiting;
	}

	@Override
	public void run() {
    	Control.out.println("XBOXAdapter running...");
    	
    	//when running = false ControllerHandler will be shut down
    	while(running) {
    		
    		//when waiting = true ControllerHandler is still running,
    		//but should not react
    		if(!waiting && !Control.data.isDisableControllerAndRift()) {
    		
    			//only when the Controller is connected, input can be handled
	    		if(isConnected) {
	    			
	    			IContTemplate templateCopy = template.copy();
	    			
		    		setChanged();	 
		    		//If Direction of LeftThumb-Axis is currently Up
					if(		magnitude > 0.3 && 
							(direction > 330 && direction < 360) || 
							(direction > 0 && direction < 30)) {
						templateCopy.handleUp(magnitude);
						notifyObservers(false);
					}
					
					//If Direction of LeftThumb-Axis is currently Right
					else if(magnitude > 0.3 && direction > 60 && direction < 120) {
						templateCopy.handleRight(magnitude);
						notifyObservers(false);
					}
					
					//If Direction of LeftThumb-Axis is currently Down
					else if(magnitude > 0.3 && direction > 150 && direction < 210) {
						templateCopy.handleDown(magnitude);
						notifyObservers(false);
					}
					
					//If Direction of LeftThumb-Axis is currently Left
					else if(magnitude > 0.3 && direction > 240 && direction < 300) {
						templateCopy.handleLeft(magnitude);
						notifyObservers(false);
					}
					
					//else sleep a moment and notify HoverInvoker, that Controller wants to hover
					else {
						sleep(Config.MILLIS_FOR_EVENTMANAGER);
						//hover only if not controlled by LeapMotion
						//TODO: ask for leap control
					
						notifyObservers(true);
					}
					
					
				//if the Controller is not connected, the Drone needs to land, so that nothing
				//unexpected happens
	    		} else {
	    			if(Control.data.isFlying()) {
		    			Commands.landing();
		    			Control.data.setFlying(false);
		    			Control.out.println("Lost Connection to XBOX Controller. Landing invoked!");
		    			sleep(200);
	    			}
	    		}
	    	}
    	}
	}
	
	//let sleep
	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace(Control.out);
		}
	}
	
}
