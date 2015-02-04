package core.templates;

import core.commands.Commands;
import core.control.Control;
import core.oculusrift.OculusRiftEvent;

/**
 * Template Version 2
 *
 */
public class ORTemplate02 implements IORTemplate{

	@Override
	public void handleYaw(OculusRiftEvent e) {
		
		boolean useRift = Control.data.useRift();
		int multiplier = e.getYawMult();
		int speed = Control.data.getSpeed() * multiplier;
		
		 if(useRift && multiplier > 0) 
		   	{	    		   
			 	Commands.spinRight(speed);
	  	  	}
		 else if (useRift && multiplier < 0) 
		 	{
			 	speed = speed * -1;
			 	Commands.spinLeft(speed);
		 	}
		 
	}

	@Override
	public void handlePitch(OculusRiftEvent e) {
		
		boolean useRift = Control.data.useRift();
		int multiplier = e.getPitchMult();
		int speed = Control.data.getSpeed() * multiplier;
		
		 if(useRift && multiplier < 0) 
	  	   {
				speed = speed * -1;
				Commands.goUp(speed);
	  	   }	  	   
		 else if (useRift && multiplier > 0) 
	  	   {
				Commands.goDown(speed);
	  	   }
		 
	}

	@Override
	public void handleRoll(OculusRiftEvent e) {
		//Nothing TODO here ;)	
	}

}
