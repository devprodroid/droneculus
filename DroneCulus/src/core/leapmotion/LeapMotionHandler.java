package core.leapmotion;

import java.util.Observable;

import org.jfree.data.time.Millisecond;

import com.leapmotion.leap.*;

import core.commands.Commands;
import core.commands.HoverInvoker;
import core.control.Control;
import core.templates.ILeapTemplate;
import core.templates.LeapTemplateFactory;
import core.templates.TemplateVersions.Template;
import core.utils.Config;

/**
 * @author robert Handler for LeapMotion Controller Analyzes the Gestures and
 *         handels the connection
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

	/**
	 * Set Version and add the Observer for hovering
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
	 * Determines the gesture recognized by the LeapMotion in a Frame
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
		} else if (parseFingers(firstHand)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Tries to find extended index fingers and react on commands
	 * 
	 * @param firstHand
	 */
	private boolean parseFingers(Hand firstHand) {
		// FingerList indexFingerList =
		// firstHand.fingers().fingerType(Finger.Type.TYPE_INDEX);
		// Finger indexFinger = indexFingerList.get(0);

		FingerList thumbFingerList = firstHand.fingers().fingerType(Finger.Type.TYPE_THUMB);
		Finger thumb = thumbFingerList.get(0);
		boolean result = false;

		// if (indexFinger.isExtended() && (!thumb.isExtended())) {
		// if (indexFinger.stabilizedTipPosition().getZ() < -50) {
		// // Move Forward
		// template.copy().handleForward(1);
		// result = true;
		//
		// } else if (indexFinger.stabilizedTipPosition().getZ() > 50) {
		// // Move Backwards
		// template.copy().handleBackward(1);
		// result = true;
		// }
		//
		// } else
		if (thumb.isExtended()) {

			float confidence = firstHand.confidence();
			if (confidence > 0.7) {

				float roll = firstHand.palmNormal().roll();
				float pitch = firstHand.palmNormal().pitch();

				double rollDeg = Math.toDegrees(roll);
				double pitchDeg = Math.toDegrees(pitch);

				System.out.println("rollDeg " + rollDeg);
				System.out.println("Pitchdeg " + pitchDeg);
				if (pitchDeg > -65) {
					template.copy().handleBackward(0.5 + (Math.abs(pitchDeg / 100)));
					result = true;
				}
				if (pitchDeg < -110) {
					template.copy().handleForward(0.5 + (Math.abs(pitchDeg / 100)));
					result = true;

				}

				if (rollDeg < -20) {
					template.copy().handleRight(0.5 + (Math.abs(rollDeg / 100)));
					result = true;
				}
				if (rollDeg > 20) {
					template.copy().handleLeft(0.5 + (Math.abs(rollDeg / 100)));
					result = true;
				}
				// if (thumb.stabilizedTipPosition().getX() < -50) {
				// // Move left
				// template.copy().handleLeft(1);
				// result = true;
				//
				// } else if (indexFinger.stabilizedTipPosition().getX() > 50) {
				// // Move right
				// template.copy().handleRight(1);

			}
		}

		return result;
	}

	/**
	 * Tries to recognize gestures Finger Circle for takeof
	 * 
	 * @param frame
	 */
	private boolean parseGesture(Frame frame) {

		if (frame.gestures().count() == 0) {
			return false;
		}

		if ((!Control.data.isFlying()) && (frame.gestures().get(0).type() == Gesture.Type.TYPE_CIRCLE && !Circle)) {

			Circle = true;
			CircleGesture circle = new CircleGesture(frame.gestures().get(0));
			float progress = circle.progress();

			if (progress > 2.0f) {
				template.copy().handleStart();
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
			controller.setPolicy(Controller.PolicyFlag.POLICY_OPTIMIZE_HMD);
		} else {
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
