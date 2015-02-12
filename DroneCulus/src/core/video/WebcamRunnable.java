package core.video;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Observable;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamException;
import com.leapmotion.leap.Controller;

import core.control.Control;

/**
 * Searches for attached webcams and starts fetching images from up to 2 webcams
 * the images are fed into the videopipeline for the Occulus We are using
 * WebcamCapture by Bartosz Firyn (https://github.com/sarxos)
 **/
public class WebcamRunnable extends Observable implements Runnable {

	private Controller leapMan;
	private Webcam webcam0 = null;
	private Webcam webcam1 = null;
	private Webcam singleWebcam = null;
	private List<Webcam> webcams = null;
	private boolean dualVimicroCameraMode = false;

	public WebcamRunnable() {

		try {
			webcams = Webcam.getWebcams();
			
			for (Webcam webcam : webcams) {
				System.out.format("Opening %s\n", webcam.getName());

				if (webcam.getName().equalsIgnoreCase(
						"Vimicro USB2.0 UVC PC Camera 0")) {
					webcam0 = webcam;
					webcam0.getLock();
				//	webcam0.setViewSize(new java.awt.Dimension(640, 480));
				//	webcam0.
					
					webcam0.open(true);

				} else if (webcam.getName().equalsIgnoreCase(
						"Vimicro USB2.0 UVC PC Camera 1")) {
					webcam1 = webcam;
					webcam1.getLock();
					//webcam1.setViewSize(new java.awt.Dimension(640, 480));
					webcam1.open(true);
				}// else {
				//	singleWebcam = webcam;
				//	singleWebcam.setViewSize(new java.awt.Dimension(640, 480));
				//	singleWebcam.open();

				//}
				
				
				if ((webcam0 != null) && (webcam1 != null)) {
					dualVimicroCameraMode = true;
					break;

				}

			}
			 if ((webcam0 != null) && (webcam1 != null)) {
			 dualVimicroCameraMode = true;

			 }

		} catch (WebcamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		leapMan = Control.leapController;

	}

	// let sleep
	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace(Control.out);
		}
	}

	static {
		Webcam.setHandleTermSignal(true);
	}

	@Override
	public void run() {
		 dualVimicroCameraMode = true;
		if (dualVimicroCameraMode) {
			while ((webcam0.isOpen()) && (webcam1.isOpen())) {

				
					setChanged();

					BufferedImage image = VideoPipe.process(webcam0.getImage(),
							webcam1.getImage(), dualVimicroCameraMode);

					notifyObservers(image);
				

			}
		} else

			while (singleWebcam.isOpen()) {
				setChanged();

				BufferedImage image = VideoPipe.process(
						singleWebcam.getImage(), null, dualVimicroCameraMode);

				notifyObservers(image);

			}

		sleep(15);
	}
}
