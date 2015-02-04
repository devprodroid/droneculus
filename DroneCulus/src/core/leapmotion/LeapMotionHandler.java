package core.leapmotion;

import java.util.Observable;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Listener;

import core.commands.Commands;
import core.commands.HoverInvoker;
import core.control.Control;
import core.templates.ContTemplateFactory;
import core.templates.IContTemplate;
import core.templates.TemplateVersions.Template;

public class LeapMotionHandler extends Observable implements Runnable {
	
	private SampleListener listener = null;
	
	
	// boolean, if Controller is connected
	private boolean isConnected = true;

	// Template for Handling the Axis Values
	private IContTemplate template;

	// boolean if ControllerHandler is still running
	private boolean running = true;

	// boolean if ControllerHandler is currently waiting
	private boolean waiting = true;

	public LeapMotionHandler(Template version, HoverInvoker hoverInv, Controller controller) {
		switchVersion(version);
		addObserver(hoverInv);
		listener = new SampleListener(); 
		controller.addListener(listener);
	}

	@SuppressWarnings("unused")
	@Override
	public void run() {

		while (running) {

			if (true) {
				Control.out
				.println("Lost Connection to LeapMotion Controller. Landing invoked!");
			} else {
				if (Control.data.isFlying()) {
					Commands.landing();
					Control.data.setFlying(false);
					Control.out
							.println("Lost Connection to LeapMotion Controller. Landing invoked!");
					sleep(200);
				}
			}
		}
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

	// let sleep
	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace(Control.out);
		}
	}

	/**
	 * set the ControllerHandler waiting or not
	 * 
	 * @param waiting
	 *            = boolean if waiting or not
	 */
	public void setWaiting(boolean waiting) {
		this.waiting = waiting;
	}
}

class SampleListener extends Listener {

    public void onConnect(Controller controller) {
        System.out.println("Connected");
    }

    public void onFrame(Controller controller) {
        System.out.println("Frame available");
    }
}
