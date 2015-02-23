package core.leapmotion;

import com.leapmotion.leap.Controller;

import core.commands.HoverInvoker;
import core.templates.TemplateVersions.Template;

/**
 * @author robert
 *Initializes the Thread for polling the LEapMotion in the Handler
 */
public class LeapMotionManager {
	LeapMotionHandler handler = null;
	Controller controller = null;



	/**
	 * @return LeapMotionController
	 */
	public Controller getController() {
		return controller;
	}

	/**
	 * Creates a LeapMotion Manager and tries to connect to the device
	 * @param version Version to start
	 * @param hoverInv Observer for Hovering
	 * @param controller Leap Motion Controller
	 * @throws Exception If Connect fails, exception is thrown
	 */
	public LeapMotionManager(Template version, HoverInvoker hoverInv, Controller controller) throws Exception {

		this.controller = controller;

		handler = new LeapMotionHandler(version, hoverInv, controller);
		if (!controller.isConnected())
			throw new NullPointerException("No LeapMotion Controller found!");
		initController();

	}

	/**
	 * Init the Thread for polling Frames
	 */
	private void initController() {

		new Thread(handler).start();
	}

	public void switchVersion(Template version) {
		handler.switchVersion(version);
	}

	public void stop() {
		handler.setWaiting(true);
	}

	public void start() {
		handler.setWaiting(false);
	}

	/**
	 * @return Connected state of the controller
	 */
	public boolean isConnected() {
		return controller.isConnected();

	}

}
