package core.leapmotion;

import java.util.Observable;

import com.leapmotion.leap.CircleGesture;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.FingerList;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Gesture;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.HandList;

import core.commands.Commands;
import core.commands.HoverInvoker;
import core.control.Control;
import core.templates.ILeapTemplate;
import core.templates.LeapTemplateFactory;
import core.templates.TemplateVersions.Template;
import core.utils.Config;

/**
 * Handler for LeapMotion Controller Analyzes the Gestures and handles the
 * connection
 * 
 * @author robert
 *
 */
public class LeapMotionHandler extends Observable implements Runnable {

	/**
	 * boolean, if Controller is connected
	 */
	private boolean isConnected = true;

	/**
	 * Template for Handling the Axis Values
	 */
	private ILeapTemplate template;

	/**
	 * if ControllerHandler is still running
	 */
	private boolean running = true;

	/**
	 * if ControllerHandler is still running
	 */
	private boolean isWaiting = false;

	/**
	 * local LeapMotion Controller instance
	 */
	private Controller controller = null;

	/**
	 * boolean if new frame ready
	 */
	public boolean frameProcessing = false;

	/**
	 * Circle Gesture active
	 */
	boolean Circle = false;

	private boolean hmdMode;

	/**
	 * Set Version and add the Observer for hovering We enable the used gestures
	 * fpr the controller
	 * 
	 * @param version
	 * @param hoverInv
	 * @param controller
	 */
	public LeapMotionHandler(Template version, HoverInvoker hoverInv, Controller controller) {
		this.controller = controller;
		this.controller.enableGesture(Gesture.Type.TYPE_CIRCLE);

		switchVersion(version);
		addObserver(hoverInv);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		Control.out.println("LeapMotion running...");

		// when running = false ControllerHandler will be shut down
		while ((running) && (!isWaiting)) {

			if (isConnected) {
				setChanged();

				if (!analyseFrame(controller.frame())) {

					sleep(Config.MILLIS_FOR_COMMANDS);
					notifyObservers(true);
				} else
					notifyObservers(false);
			} else {
				if (Control.data.isFlying()) {
					Commands.landing();
					Control.data.setFlying(false);
					Control.out.println("Lost Connection to Leap Motion Controller. Landing invoked!");
					sleep(200);

				}
			}
			// sleep(Config.MILLIS_FOR_COMMANDS);
		}
	}

	/**
	 * pauses the thread for a given time
	 * 
	 * @param millis
	 *            time in Milliseconds
	 */
	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace(Control.out);
		}
	}

	/**
	 * Determines the gestures and movements recognized by the LeapMotion in a
	 * Frame
	 * 
	 * @param frame
	 *            the Frame to analyze
	 * @param templateCopy
	 *            template for executing the action
	 */
	public boolean analyseFrame(Frame frame) {

		HandList hands = frame.hands();
		Hand firstHand = hands.get(0);

		if (parseGesture(frame)) {
			return true;
		} else if (controlWithPalmOrientation(firstHand)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * We try to find the hands thumb and the palms orientation for controlling
	 * forward/backward motion with pitch and roll of the hand, according to the
	 * normal of the palm
	 * 
	 * @param firstHand
	 *            the first detected hand
	 */
	private boolean controlWithPalmOrientation(Hand firstHand) {
		FingerList thumbFingerList = firstHand.fingers().fingerType(Finger.Type.TYPE_THUMB);
		Finger thumb = thumbFingerList.get(0);
		boolean result = false;

		// high confidence in the hand model is needed for accepting commands
		float confidence = firstHand.confidence();

		// high confidence and an extended thumb
		if ((confidence > 0.5) && (thumb.isExtended())) {

			float roll = firstHand.palmNormal().roll();
			float pitch = firstHand.palmNormal().pitch();
			float yaw = firstHand.direction().yaw();

			double pitchDeg = Math.toDegrees(pitch);
			Double yawDeg = Math.toDegrees(yaw);

			// pitch for forward/backward
			result = pitchCalculation(result, pitchDeg);

			// else move right left with the roll movement

			result = result || rollCalculation(result, roll);
			result = result || yawCalculation(result, yawDeg);
		}

		return result;
	}

	/**
	 * @param result
	 * @param pitchDeg
	 * @return
	 */
	private boolean yawCalculation(boolean result, Double yawDeg) {
		 Control.out.println("Yaw: "+yawDeg);

		if (hmdMode) {
			yawDeg = yawDeg * -1;
		}
		template.copy().handleYaw(yawDeg.intValue());
		if (Math.abs(yawDeg) > 10)
			result = true;

		return result;
	}

	/**
	 * @param result
	 * @param pitchDeg
	 * @return
	 */
	private boolean pitchCalculation(boolean result, double pitchDeg) {

		if (hmdMode) {
			if (pitchDeg < 85) {
				template.copy().handleBackward(1);
				result = true;
			}
			if (pitchDeg > 105) {
				template.copy().handleForward(1);
				result = true;
			}

		} else {
			if (pitchDeg > -70) {
				template.copy().handleBackward(1);
				result = true;
			}
			if (pitchDeg < -110) {
				template.copy().handleForward(1);
				result = true;
			}
		}
		return result;
	}

	/**
	 * @param result
	 * @param rollDeg
	 * @return
	 */
	private boolean rollCalculation(boolean result, double roll) {

		if (hmdMode) {
			roll = Math.sin(roll); // transform for ease of use
			roll = roll * -1;
		}
		double rollDeg = Math.toDegrees(roll);

		if (rollDeg < -20) {
			template.copy().handleRight(1);
			result = true;
		}
		if (rollDeg > 20) {
			template.copy().handleLeft(1);
			result = true;
		}

		// Control.out.println("rollDeg: " + rollDeg);
		return result;
	}

	/**
	 * Tries to recognize gestures Finger Circle for takeoff and landing a
	 * Circle gesture clockwise/counterclockwise for takeoff and landing
	 * 
	 * @param frame
	 */
	private boolean parseGesture(Frame frame) {

		if (frame.gestures().count() == 0) {
			return false;
		}

		if ((frame.gestures().get(0).type() == Gesture.Type.TYPE_CIRCLE && !Circle)) {

			Circle = true;
			CircleGesture circle = new CircleGesture(frame.gestures().get(0));
			float progress = circle.progress();

			if (progress > 2.0f) {
				// clockwise
				if (circle.pointable().direction().angleTo(circle.normal()) <= Math.PI / 2) {
					template.copy().handleStart();
				} else {
					// counterclockwise
					template.copy().handleLand();
				}

				Circle = false;
			}

		} else {
			Circle = false;

		}
		return Circle;

	}

	/**
	 * Switch used template and set the LeapMotion POLICY_OPTIMIZE_HMD if HMD
	 * Template is selected
	 * 
	 * @param version
	 *            = Version of Template to be used
	 */
	public void switchVersion(Template version) {
		setTemplate(LeapTemplateFactory.makeTemplate(version));
		if (version == Template.LeapMotionHMD) {
			hmdMode = true;

			controller.setPolicy(Controller.PolicyFlag.POLICY_OPTIMIZE_HMD);
		} else {
			hmdMode = false;
			controller.setPolicy(Controller.PolicyFlag.POLICY_DEFAULT);
		}
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
