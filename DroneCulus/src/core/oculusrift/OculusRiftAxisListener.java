package core.oculusrift;

import java.util.Observable;

import core.commands.HoverInvoker;
import core.control.Control;
import core.templates.IORTemplate;
import core.templates.ORTemplateFactory;
import core.templates.TemplateVersions.Template;

/**
 * Listener on Events from OculusRiftEventManager.
 * holds and switches IORTemplates, which handle those Events in predefined ways
 *
 */
public class OculusRiftAxisListener extends Observable	implements IOculusRiftListener {
		
	private IORTemplate template;

	public OculusRiftAxisListener( Template version , HoverInvoker hover) {
		switchVersion(version);
	    addObserver(hover);
	}
	 
	public void switchVersion( Template version ) {
     	template = ORTemplateFactory.makeTemplate(version);
	}
	
	/**
	 * react on Event
	 * notifyObservers, notifies the HoverInvoker, if the OculusRift-Values are correct
	 * for hovering (true = hovering is wanted, false = hovering is not wanted)
	 */
	@Override
	public void changed(OculusRiftEvent e) {
					
		int yawMult = e.getYawMult();
		int pitchMult = e.getPitchMult();
		int rollMult = e.getRollMult();
		setChanged();
		
		if(!Control.data.isDisableControllerAndRift()) {
		
			if(		yawMult == 0 && 
					pitchMult == 0 && 
					rollMult == 0 && 
					Control.data.useRift()
					
			) notifyObservers(true);	
			
			else if(!Control.data.useRift()) 
				notifyObservers(true);
			
			else {			
				notifyObservers(false);
				template.handleYaw(e);
				template.handlePitch(e);
				template.handleRoll(e);
			}
		}
		
	}
}
