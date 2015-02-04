package core.controller;

import ch.aplu.xboxcontroller.XboxControllerAdapter;
import core.commands.HoverInvoker;
import core.templates.TemplateVersions.Template;
/**
 * Extension of XBoxControllerAdapter to react on Button and Axis Events
 *
 */
public class CustomXBOXControllerAdapter extends XboxControllerAdapter {
	
	private ButtonReaction reaction = new ButtonReaction();
	
	private ControllerHandler handler;
	
	private boolean waiting = true;
	
	
	public CustomXBOXControllerAdapter(Template version, HoverInvoker hoverInv) {
		handler = new ControllerHandler(version, hoverInv);
		new Thread(handler).start();
	}	
	
	public void switchVersion(Template version) {
		handler.switchVersion(version);
	}
	
	public void setWaiting(boolean waiting) {
		this.waiting = waiting;
		handler.setWaiting(waiting);
	}

    public void leftThumbMagnitude(double magnitude)
    {
    	handler.updateMagnitude(magnitude);
    }

    public void leftThumbDirection(double direction)
    {
    	handler.updateDirection(direction);
    }   
    
    public void isConnected(boolean connected)
    {
    	handler.updateConnection(connected);
    }      
  
    
    // Reactions, when Buttons are pressed
    //---------------------------------------------------------------
    //---------------------------------------------------------------
    public void buttonA(boolean pressed)
	{ if(pressed && !waiting) { reaction.pressedA(); } }

    public void buttonB(boolean pressed)
    { if(pressed && !waiting) { reaction.pressedB(); } }

    public void buttonX(boolean pressed)
    { if(pressed && !waiting) { reaction.pressedX(); } }

    public void buttonY(boolean pressed)
    { if(pressed && !waiting) { reaction.pressedY(); } }

    public void back(boolean pressed)
    { if(pressed && !waiting) { reaction.pressedBACK(); } }

    public void start(boolean pressed)
    { if(pressed && !waiting) { reaction.pressedSTART(); } }

    public void leftShoulder(boolean pressed)
    { if(pressed && !waiting) { reaction.pressedLB(); } }

    public void rightShoulder(boolean pressed)
    { if(pressed && !waiting) {	reaction.pressedRB(); } }
    //------------------------------------------------------------------

}
