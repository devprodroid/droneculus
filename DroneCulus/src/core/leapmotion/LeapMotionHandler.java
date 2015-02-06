package core.leapmotion;

import java.util.Observable;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.HandList;
import com.leapmotion.leap.Listener;

import core.commands.Commands;
import core.commands.HoverInvoker;
import core.control.Control;
import core.templates.ILeapTemplate;
import core.templates.LeapTemplateFactory;
import core.templates.TemplateVersions.Template;

public class LeapMotionHandler extends Observable {

	private LeapListener listener = null;

	// boolean, if Controller is connected
	private boolean isConnected = true;

	// Template for Handling the Axis Values
	private ILeapTemplate template;

	// boolean if ControllerHandler is still running
	private boolean running = true;

	// boolean if ControllerHandler is still running
	private boolean isWaiting = false;
	
	public void setChangedFlag(){
		setChanged();
	}

	// boolean if new frame ready
	public boolean frameProcessing = false;

	public LeapMotionHandler(Template version, HoverInvoker hoverInv,
			Controller controller) {
		switchVersion(version);
		addObserver(hoverInv);

		listener = new LeapListener(this);
		controller.addListener(listener);
		controller.setPolicy(Controller.PolicyFlag.POLICY_IMAGES);
	}

	/**
	 * switch used Template
	 * 
	 * @param version
	 *            = Version of Template to be used
	 */
	public void switchVersion(Template version) {
		setTemplate(LeapTemplateFactory.makeTemplate(version));
	}



	/**
	 * set the ControllerHandler waiting or not
	 * 
	 * @param waiting
	 *            = boolean if waiting or not
	 */
	public void setWaiting(boolean waiting) {
		// this.waiting = waiting;
	}

	public ILeapTemplate getTemplate() {
		return template;
	}

	public void setTemplate(ILeapTemplate template) {
		this.template = template;
	}

	public boolean isWaiting() {
		return isWaiting;
	}

	public void stop() {
		setWaiting(true);
	}

	public void start() {
		setWaiting(false);
	}
}

class LeapListener extends Listener {

	LeapMotionHandler handler = null;

	public LeapListener(LeapMotionHandler handler) {
		this.handler = handler;
	}

	public void onConnect(Controller controller) {
		System.out.println("Connected");
	}

	public void onDisconnect(Controller controller) {
		System.out.println("Leap motion Disconnected");

		if (Control.data.isFlying()) {
			Commands.landing();
			Control.data.setFlying(false);
			Control.out
					.println("Lost Connection to LeapMotionController. Landing invoked!");

		}
	}

	public void onExit(Controller controller) {
		System.out.println("Exited");
	}

	public void onInit(Controller controller) {
		System.out.println("Initialized");
		handler.setWaiting(false);
	}

	public void onFrame(Controller controller) {

		if (!handler.frameProcessing) { // skip frames as long as another is
										// being processed
			handler.frameProcessing = true;
			// if (!handler.isWaiting()) {
			if (controller.isConnected()) {
				Frame frame = controller.frame(); // The latest frame

				ILeapTemplate templateCopy = handler.getTemplate().copy();

				whatGesture(frame, templateCopy);

				// templateCopy.handleForward(0.3);

			}
			// }
			handler.frameProcessing = false;
		}
	}

	public void whatGesture(Frame frame, ILeapTemplate templateCopy) {

		HandList hands = frame.hands();
		Hand leftHand = hands.leftmost();
		Hand rightHand = hands.rightmost();
		// System.out.println("X:"+rightHand.palmPosition().getX()+"Y:"+rightHand.palmPosition().getY()+"Z:"+rightHand.palmPosition().getZ());
		handler.setChangedFlag();
		if (rightHand.isValid() && leftHand.isValid()) {
			if (leftHand.palmPosition().getZ() < -85
					&& rightHand.palmPosition().getZ() < -85) {
				// Move Forward
				templateCopy.handleForward(2);

				System.out.println("FORWARD");

			} else if (leftHand.palmPosition().getZ() > 50
					&& rightHand.palmPosition().getZ() > 50) {
				// Move Backwards
				templateCopy.handleBackward(2);
				System.out.println("BACKWARD");
			} else {
				//System.out.println("STOP");
				handler.notifyObservers(true); // hover!
			}
		}
	}

}
