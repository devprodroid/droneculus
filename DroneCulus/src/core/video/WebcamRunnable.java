package core.video;

import java.awt.image.BufferedImage;
import java.util.Observable;

import javax.xml.crypto.Data;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import core.control.Control;
import core.utils.Config;

/**
 * Searches for attached webcams and starts fetching images from up to 2 webcams
 * the images are fed into the videopipeline for the Occulus. The Webcams are
 * fetched with openCV and for better synchronization we use grab() first and
 * then the slower retrieve() method.
 **/
public class WebcamRunnable extends Observable implements Runnable {

	private boolean dualVimicroCameraMode = false;

	/**
	 * Primary Webcam
	 */
	VideoCapture capture1 = null;
	/**
	 * Secondary Webcam
	 */
	VideoCapture capture2 = null;

	public WebcamRunnable() {
		

	}

	/**
	 * Creates and initializes the webcams with a given framerate Enables
	 * dualVimicroCameraMode if booth Devices are open and retrieve useable
	 * images
	 * 
	 * @param framerate
	 *            - framerate of the webcam
	 */
	private void initCaptureDevices(int framerate) {
		capture1 = new VideoCapture(Config.WEBCAM_PRIMARY_ID);
		capture1.set(5, framerate);

		if (capture1.isOpened()) {
			if (Control.data.getBatteryPercentage()>0)
			Control.isDroneConnected = true;

			capture2 = new VideoCapture(Config.WEBCAM_SECONDARY_ID);
			if (capture2.isOpened() && (capture2.grab())) {
				capture2.set(5, framerate);
				dualVimicroCameraMode = true;

			}
		}
	}

	@Override
	public void run() {
		initCaptureDevices(Config.WEBCAM_FRAMERATE);
		Mat webcam_image1 = new Mat();
		Mat webcam_image2 = new Mat();

		if (capture1.isOpened()) {
			while (true) {
				
				capture1.grab();

				if (dualVimicroCameraMode)
					capture2.grab();

				capture1.retrieve(webcam_image1);
				if (dualVimicroCameraMode)
					capture2.retrieve(webcam_image2);

				if (!webcam_image1.empty()) {
					setChanged();
					BufferedImage image = VideoPipe.processMat(webcam_image1,
							webcam_image2, dualVimicroCameraMode);

					notifyObservers(image);
				} else {

				}
			}
		}

	}
}
