package core.leapmotion;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Observable;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;

import processing.core.PImage;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.HandList;
import com.leapmotion.leap.Image;
import com.leapmotion.leap.ImageList;
import com.leapmotion.leap.Leap;
import com.leapmotion.leap.Listener;

import core.commands.Commands;
import core.commands.HoverInvoker;
import core.control.Control;
import core.templates.ILeapTemplate;
import core.templates.LeapTemplateFactory;
import core.templates.TemplateVersions.Template;
import core.utils.Config;

public class LeapMotionHandler extends Observable implements Runnable {

	// boolean, if Controller is connected
	private boolean isConnected = true;

	// Template for Handling the Axis Values
	private ILeapTemplate template;

	// boolean if ControllerHandler is still running
	private boolean running = true;

	// boolean if ControllerHandler is still running
	private boolean isWaiting = false;

	private Controller controller = null;

	// boolean if new frame ready
	public boolean frameProcessing = false;

	public LeapMotionHandler(Template version, HoverInvoker hoverInv,
			Controller controller) {
		this.controller = controller;
		switchVersion(version);
		addObserver(hoverInv);

		// TODO: enable image policy by demand
		controller.setPolicy(Controller.PolicyFlag.POLICY_IMAGES);

	}

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
					// try {
					// // getImage(frame);
					// } catch (Exception e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// }

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

	/*
	 * returns BufferedImage from one of the LeapMotion cameras
	 */
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
				// File f = new File("c:/MyFile.png");
				// ImageIO.write(bufferedImage, "PNG", f);
				// System.out.println("image saved");

			}

		} else {
			throw new Exception("Invalid Frame");
		}
		return null;
	}

	// let sleep
	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace(Control.out);
		}
	}

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
	 * switch used Template
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

// class LeapListener extends Listener {
//
// LeapMotionHandler handler = null;
//
// public LeapListener(LeapMotionHandler handler) {
// this.handler = handler;
// }
//
// public void onConnect(Controller controller) {
// System.out.println("Connected");
// }
//
// public void onDisconnect(Controller controller) {
// System.out.println("Leap motion Disconnected");
//
// if (Control.data.isFlying()) {
// Commands.landing();
// Control.data.setFlying(false);
// Control.out
// .println("Lost Connection to LeapMotionController. Landing invoked!");
//
// }
// }
//
// public void onExit(Controller controller) {
// System.out.println("Exited");
// }
//
// public void onInit(Controller controller) {
// System.out.println("Initialized");
// handler.setWaiting(false);
// }
//
// public void onFrame(Controller controller) {
//
// if (!handler.frameProcessing) { // skip frames as long as another is
// // being processed
// handler.frameProcessing = true;
// // if (!handler.isWaiting()) {
// if (controller.isConnected()) {
// Frame frame = controller.frame(); // The latest frame
//
// ILeapTemplate templateCopy = handler.getTemplate().copy();
//
// whatGesture(frame, templateCopy);
//
// // templateCopy.handleForward(0.3);
//
// }
// // }
// handler.frameProcessing = false;
// }
// }
//
// private void whatGesture(Frame frame, ILeapTemplate templateCopy) {
// // TODO Auto-generated method stub
// // moved to thread
// }
//
// }
