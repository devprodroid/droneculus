package core.video;

import java.awt.image.BufferedImage;
import java.util.Observable;



import core.control.Control;
import de.yadrone.base.ARDrone;
import de.yadrone.base.video.ImageListener;

/**
 * Listens on every Image Update and processes the Image using the VideoPipe
 *
 */
public class VideoListener extends Observable {

	private ARDrone drone;

	public VideoListener() {
		Control.isDroneConnected = true;
		
		

	
		
		
		// TODO: Evaluate current configuration
		drone = Control.drone;
		drone.getVideoManager().addImageListener(new ImageListener() {
			
			@Override
			public void imageUpdated(BufferedImage newimage) {

				// image = VideoPipe.process(newimage);
				// Sometimes YaDrone is not working as intended, so we can only
				// be sure, that we are connected to the Drone
				// if a Image has been send and is readable
				Control.isDroneConnected = true;
				// setChanged();
				// notifyObservers(image);

			}
		});
	}

}
