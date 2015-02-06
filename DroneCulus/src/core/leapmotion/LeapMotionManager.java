package core.leapmotion;

import com.leapmotion.leap.Controller;

import core.commands.HoverInvoker;
import core.templates.TemplateVersions.Template;

public class LeapMotionManager {
	LeapMotionHandler handler = null;

	Controller controller = null;

	// listener etc

	public LeapMotionManager(Template version, HoverInvoker hoverInv)
			throws Exception {

		controller = new Controller();

		handler = new LeapMotionHandler(version, hoverInv, controller);
		initController();
	}

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

}
