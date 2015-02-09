package core.video;

import java.awt.image.BufferedImage;
import java.util.Observable;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Image;
import com.leapmotion.leap.Listener;

import core.control.Control;
import core.control.ControlManager;
import core.controller.ControllerManager;
import core.leapmotion.LeapMotionManager;
import de.yadrone.base.ARDrone;
import de.yadrone.base.video.ImageListener;

/**
 * Listens on every Image Update and processes the Image using the VideoPipe
 *
 */
public class VideoListener extends Observable {

	private BufferedImage image;
	private ARDrone drone;
	private Controller leapMan;

	public VideoListener() {

		leapMan = Control.leapController;
		leapMan.addListener(new Listener() {
			@Override
			public void onFrame(Controller controller) {

				// Sometimes YaDrone is not working as intended, so we can only
				// be sure, that we are connected to the Drone
				// if a Image has been send and is readable
				Control.isDroneConnected = true;
				if (controller.frame().images().count() > 0) {
					setChanged();
					try {
						notifyObservers(getImage(controller.frame()));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
	}

	// TODO: Evaluate current configuration
	// drone = Control.drone;
	// drone.getVideoManager().addImageListener(new ImageListener() {
	//
	// @Override
	// public void imageUpdated(BufferedImage newimage) {
	//
	// image = VideoPipe.process(newimage);
	// // Sometimes YaDrone is not working as intended, so we can only
	// // be sure, that we are connected to the Drone
	// // if a Image has been send and is readable
	// Control.isDroneConnected = true;
	// setChanged();
	// notifyObservers(image);
	//
	// }
	// });
	// }

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

}
