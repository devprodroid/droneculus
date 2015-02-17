package core.leapmotion;

import java.awt.image.BufferedImage;
import java.util.Observable;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.HandList;
import com.leapmotion.leap.Image;

import core.commands.Commands;
import core.commands.HoverInvoker;
import core.control.Control;
import core.templates.ILeapTemplate;
import core.templates.LeapTemplateFactory;
import core.templates.TemplateVersions.Template;
import core.utils.Config;

/**
 * @author robert
 * Handler for LeapMotion Controller
 * Analyzes the Gestures and handels the connection
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
	 * Set Version and add the Observer for hovering
	 * 
	 * @param version
	 * @param hoverInv
	 * @param controller
	 */
	public LeapMotionHandler(Template version, HoverInvoker hoverInv,
			Controller controller) {
		this.controller = controller;
		switchVersion(version);
		addObserver(hoverInv);

		// enable image policy by demand
		// controller.setPolicy(Controller.PolicyFlag.POLICY_IMAGES);

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
		while (running) {

			// when waiting = true ControllerHandler is still running,
			// but should not react
			if (!isWaiting) {

				// only when the Controller is connected, input can be handled
				if (isConnected) {

					ILeapTemplate templateCopy = template.copy();

					setChanged();
					if (true) {
					}

					Frame frame = controller.frame(); // The latest frame

					whatGesture(frame, templateCopy);

				}

				// else sleep a moment and notify HoverInvoker, that
				// Controller wants to hover
				else {
					sleep(Config.MILLIS_FOR_EVENTMANAGER);
					notifyObservers(true);
				}

				// if the Controller is not connected, the Drone needs to
				// land, so that nothing
				// unexpected happens
			} else {
				if (Control.data.isFlying()) {
					Commands.landing();
					Control.data.setFlying(false);
					Control.out
							.println("Lost Connection to Leap Motion Controller. Landing invoked!");
					sleep(200);
				}
			}
		}

	}

	/**
	 * Returns an image from the Leapmotions camera deactivated because the
	 * image is unusable for flying the drone
	 * 
	 * @param frame
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private BufferedImage getImage(Frame frame) throws Exception {

		int r = 0;
		int g = 0;
		int b = 0;
		if (frame.isValid()) {
			if (frame.images().count() > 0) {
				// get Image from camera #0
				Image image = frame.images().get(0);

				BufferedImage bufferedImage = new BufferedImage(image.width(),
						image.height(), BufferedImage.TYPE_INT_RGB);

				// Get byte array containing the image data from Image object
				// Width*height*colordepth
				byte[] imageData = image.data();

				int i = 0;

				for (int j = 0; j < image.height(); j++) {// spalte
					for (int k = 0; k < image.width(); k++) {// zeile

						// convert pixel to unsigned and shift into place
						r = (imageData[i] & 0xFF) << 16;
						g = (imageData[i] & 0xFF) << 8;
						b = (imageData[i] & 0xFF);

						int col = r | b | g;

						// set pixel at k,j to pixelcolor
						bufferedImage.setRGB(k, j, col);

						i++;
					}
				}
				return bufferedImage;

			}

		} else {
			throw new Exception("Invalid Frame");
		}
		return null;
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
	public void whatGesture(Frame frame, ILeapTemplate templateCopy) {

		HandList hands = frame.hands();
		Hand leftHand = hands.leftmost();
		Hand rightHand = hands.rightmost();
		// System.out.println("X:"+rightHand.palmPosition().getX()+"Y:"+rightHand.palmPosition().getY()+"Z:"+rightHand.palmPosition().getZ());
		setChanged();
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
				// System.out.println("STOP");
				notifyObservers(true); // hover!
			}
		}
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
