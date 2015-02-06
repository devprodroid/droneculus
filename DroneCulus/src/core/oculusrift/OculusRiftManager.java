package core.oculusrift;

import core.commands.HoverInvoker;
import core.control.Control;
import core.templates.TemplateVersions.Template;
import de.fruitfly.ovr.OculusRift;


/**
 * Manages Connection between Listener and EventManager and
 * invokes switching the IORTemplates
 *
 */
public class OculusRiftManager{
	
	 private OculusRiftAxisListener listener;
	

	 public OculusRiftManager( Template version , HoverInvoker hoverInv) {
	    initOculusRiftListener(version, hoverInv);
	 }
	 
	 private void initOculusRiftListener(Template version, HoverInvoker hoverInv) {
		 
		OculusRift rift = OculusRiftEventManager.getOculusRift();
		
		if(!rift.isInitialized()) {rift.init(); Control.out.println("OculusRift initialised: " + rift.isInitialized());}
		listener = new OculusRiftAxisListener(version, hoverInv);
		//else throw new NullPointerException("No OculusRift Device found!");
		
	 }
	 
	 public void switchVersion(Template version) {
		 listener.switchVersion(version);
	 }
	
	 public void start() {
		 OculusRiftEventManager.addEventListener(listener);
	 }
		
	 public void stop() {
		 OculusRiftEventManager.removeEventListener(listener);
	 }

}
