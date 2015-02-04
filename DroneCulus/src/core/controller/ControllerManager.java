package core.controller;

import ch.aplu.xboxcontroller.XboxController;
import core.commands.HoverInvoker;
import core.templates.TemplateVersions.Template;

/**
 * handles the Connection of the Controller and switches the IContTemplates used by the adapter
 *
 */
public class ControllerManager  {	
	
	
	private static XboxController xBox = null;
	
	private CustomXBOXControllerAdapter adapter;
	
	public ControllerManager(Template version, HoverInvoker hoverInv) throws Exception {	
		initController();
		adapter = new CustomXBOXControllerAdapter(version, hoverInv);
		xBox.addXboxControllerListener(adapter);
		xBox.setLeftTriggerDeadZone(0.3);

	}	
	
	private void initController() {
		if(xBox == null) xBox = new XboxController();
		if(!xBox.isConnected()) throw new NullPointerException("No Xbox-Controller found!");
	}
	
	public void switchVersion(Template version) {
		adapter.switchVersion(version);
	}
	
	public void stop() {
		adapter.setWaiting(true);
	}
	
	public void start() {
		adapter.setWaiting(false);
	}
}
