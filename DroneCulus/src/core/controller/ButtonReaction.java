package core.controller;

import core.commands.Commands;
import core.control.Control;
import core.oculusrift.OculusRiftEventManager;

/**
 * react on normal Button Input, this is equal for every Controller-Template
 *
 */
public class ButtonReaction {
	
	/** calibrates OculusRift */
	public void pressedA() {
		OculusRiftEventManager.resetOculusRift();
		message("OR reset!");
	}
	
	/** toggles Distortion Correction */
	public void pressedX() {
		
//		if(Control.data.isDistortionCorrectionEnabled()) 
//   		{
//   			Control.data.setDistortionCorrectionEnabled(false);
//   			message("Correction: OFF!");
//   		}
//   		else 
//   		{
//   			Control.data.setDistortionCorrectionEnabled(true);
//   			message("Correction: ON!");
//   		}  	   		
	}
	
	/** toggles if HUD is shown or not */
	public void pressedB() {
		
		Control.data.setHUDenabled(!Control.data.isHUDenabled());
	}
	
	/** toggles if Rift is used for controlling or not*/
	public void pressedY() {
		
   		if(Control.data.useRift() == true) 
   		{
   			Control.data.setUseRift(false);
   			message("Rift-Control: OFF!");
   			Commands.hover();
   		}
   		else
   		{
   			Control.data.setUseRift(true);
   			message("Rift-Control: ON!");
   		}

	}
	
	/** Decreases Speed */
	public void pressedLB() {
		
		int speed = Control.data.getSpeed();
		
   		if(speed > 0) {
   			Control.data.setSpeed(speed-1);
   			Control.out.println("Speed changed to: " + (speed-1));   		
   		}   		
	}
	
	/** Increases Speed */
	public void pressedRB() {
		
		int speed = Control.data.getSpeed();
		
   		if(speed < 10) {
   			Control.data.setSpeed(speed+1);
   			Control.out.println("Speed changed to: " + (speed+1));
   		}   		
	}
	
	/** Invokes the Take-Off or the Landing Command */
	public void pressedSTART() {
		
		if(!Control.data.isFlying()) 
   		{
   			Commands.takeOff();	
   			message("Take-Off!");
   			Control.data.setFlying(true);
   		} 
   		else 
   		{
   			Commands.landing();
   			message("Landing!");
   			Control.data.setFlying(false);
   		}
	}
	
	/** Emergency Button to shut down everything, if drone is flying, this causes her to fall down.
	 * USE WITH CARE!!! */
	public void pressedBACK() {
		
//   		Control.out.println("Back gedrueckt, beende.");
//   		
//   		if(!Control.data.isFlying()) 
//   		{
//   			Commands.emergency();		    			   
//   		}   		
//   		System.exit(0);
	}
	
	/** Display Message on HUD and PrintStream */
	private void message(String text) {
		Control.out.println(text);
		Control.data.setInfo(text);
	}
}
