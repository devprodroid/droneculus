package core.leapmotion;

import java.util.Observable;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Listener;

import core.commands.Commands;
import core.commands.HoverInvoker;
import core.control.Control;
import core.templates.ContTemplateFactory;
import core.templates.IContTemplate;
import core.templates.TemplateVersions.Template;

public class LeapMotionHandler extends Observable {
	
	private LeapListener listener = null;
	
	
	// boolean, if Controller is connected
	private boolean isConnected = true;

	// Template for Handling the Axis Values
	private IContTemplate template;

	// boolean if ControllerHandler is still running
	private boolean running = true;
	
	// boolean if new frame ready
	public boolean frameAvailable = false;


	public LeapMotionHandler(Template version, HoverInvoker hoverInv, Controller controller) {
		switchVersion(version);
		addObserver(hoverInv);
		listener = new LeapListener(this); 
		controller.addListener(listener);
		
	}


	/**
	 * switch used Template
	 * 
	 * @param version
	 *            = Version of Template to be used
	 */
	public void switchVersion(Template version) {
		template = ContTemplateFactory.makeTemplate(version);
	}

	/**
	 * set the ControllerHandler waiting or not
	 * 
	 * @param waiting
	 *            = boolean if waiting or not
	 */
	public void setWaiting(boolean waiting) {
		//this.waiting = waiting;
	}
}

class LeapListener extends Listener {

	LeapMotionHandler handler=null;
	
	public LeapListener(LeapMotionHandler handler){
		this.handler=handler;
	}
	
    public void onConnect(Controller controller) {
        System.out.println("Connected");
    }
    
    public void onDisconnect(Controller controller) {
        System.out.println("Leap motion Disconnected");
        
		if(Control.data.isFlying()) {
			Commands.landing();
			Control.data.setFlying(false);
			Control.out.println("Lost Connection to LeapMotionController. Landing invoked!");		
			
		}
    }

    public void onExit (Controller controller){
        System.out.println("Exited");        
    }
    
    public void onInit (Controller controller){
        System.out.println("Initialized");
    }   
    
    
    public void onFrame(Controller controller) {
    	handler.frameAvailable=true;
       // System.out.println("Frame available");
        
		if(controller.isConnected()) //controller is a Controller object
		{
		    Frame frame = controller.frame(); //The latest frame
		    //Frame previous = controller.frame(1); //The previous frame
		    //System.out.println(frame.timestamp());
		    
		    
		    if  (frame.hands().count()>0) {
		    	System.out.println("hand found");
				if(Control.data.isFlying()) {
					Commands.landing();
					Control.data.setFlying(false);
					Control.out.println("Lost Connection to LeapMotionController. Landing invoked!");		
					
				}
		    }
		    
		}
    }
}
