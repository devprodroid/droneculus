package core.video;

import java.awt.image.BufferedImage;
import java.util.Observable;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.FingerList;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Image;
import com.leapmotion.leap.ImageList;
import com.leapmotion.leap.Vector;
import java.lang.Math;

import core.control.Control;
import core.utils.Config;

public class LeapImageListener extends Observable implements Runnable {

	private Controller leapMan;

	public LeapImageListener() {
		leapMan = Control.leapController;

	}

	private BufferedImage getStitchedImage(Frame frame) throws Exception {

		if (frame.isValid()) {
			if (frame.images().count() > 0) {
				// get Image from camera #0
				BufferedImage bufferedImage1 = getPartImage(frame, 0);

				//BufferedImage bufferedImage2 = getPartImage(frame, 1);
				int hei = bufferedImage1.getHeight();
				int wi = bufferedImage1.getWidth();

				System.out.println("width: " + wi + " height: " + hei);

				// return VideoPipe.process(bufferedImage1);
				return bufferedImage1;

			}

		} else {
			throw new Exception("Invalid Frame");
		}
		return null;
	}

	/**
	 * @param frame
	 * @return
	 */
	private BufferedImage getPartImage(Frame frame, Integer imageIndex) {
		int r;
		int g;
		int b;
		Image image = frame.images().get(0);
		Image image2 = frame.images().get(1);

		BufferedImage bufferedImage = new BufferedImage(image.width()*2,
				image.height(), BufferedImage.TYPE_BYTE_GRAY);

		// Get byte array containing the image data from Image object
		// Width*height*colordepth
		byte[] imageData = image.data();
		byte[] imageData2 = image2.data();

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

		// add second image
		i = 0;

		for (int j = 0; j < image2.height(); j++) {// spalte)
			for (int k = image2.width(); k < (image2.width() * 2); k++) {// zeile

				// convert pixel to unsigned and shift into place
				r = (imageData2[i] & 0xFF) << 16;
				g = (imageData2[i] & 0xFF) << 8;
				b = (imageData2[i] & 0xFF);

				int col = r | b | g;

				// set pixel at k,j to pixelcolor
				bufferedImage.setRGB(k, j, col);

				i++;
			}
		}

		image.delete();
		imageData = null;
		image2.delete();
		imageData2 = null;

		return bufferedImage;
	}

	public BufferedImage test(Frame frame) {
		// Draw the undistorted image using the warp() function
		int targetWidth = 200;
		int targetHeight = 200;

		Image image = frame.images().get(0);
		int[] brightness = { 0, 0, 0 }; // An array to hold the rgba color
										// components

		BufferedImage bufferedImage = new BufferedImage(image.width(),
				image.height(), BufferedImage.TYPE_BYTE_GRAY);

		// For each pixel in the target image...
		for (float y = 0; y < targetWidth; y++) {
			for (float x = 0; x < targetHeight; x++) {
				// Normalized slope for this pixel
				Vector input = new Vector(x / targetWidth, y / targetHeight, 0);
				// Convert from normalized [0..1] to slope [-4..4]
				input.setX((input.getX() - image.rayOffsetX())
						/ image.rayScaleX());
				input.setY((input.getY() - image.rayOffsetY())
						/ image.rayScaleY());

				// Look up the pixel coordinates in the raw image
				// corresponding to the slope values
				Vector pixel = image.warp(input);

				// Check that the coordinates are valid (i.e. within the
				// camera image)
				if (pixel.getX() >= 0 && pixel.getX() < image.width()
						&& pixel.getY() >= 0 && pixel.getY() < image.height()) {
					int data_index = (int) (Math.floor(pixel.getY())
							* image.width() + (Math.floor(pixel.getX())));
					brightness[0] = image.data()[data_index] & 0xff;
					brightness[2] = brightness[1] = brightness[0];
				} else {
					brightness[0] = 255; // Display invalid pixels as red
					brightness[2] = brightness[1] = 0;
				}

				int r = (brightness[0]);
				int g = (brightness[1]);
				int b = (brightness[2]);

				int col = r | b | g;

				int xi = Math.round(x);
				int yi = Math.round(y);

				bufferedImage.setRGB(xi, yi, col);
			}
		}
		return bufferedImage;

	}

	// let sleep
	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace(Control.out);
		}
	}

	@Override
	public void run() {

		while (leapMan.isConnected()) {
			if ((leapMan.isConnected())
					&& (leapMan.frame().images().count() > 0)) {
				setChanged();

				try {
					notifyObservers(getStitchedImage(leapMan.frame()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			sleep(50);
		}
	}

}
