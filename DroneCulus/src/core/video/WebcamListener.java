package core.video;



import java.awt.image.BufferedImage;
import java.util.Observable;

import com.github.sarxos.webcam.Webcam;
import com.leapmotion.leap.Controller;

import core.control.Control;

public class WebcamListener extends Observable implements Runnable {

	private Controller leapMan;
	private Webcam webcam = Webcam.getDefault();
	
	public WebcamListener() {
		leapMan = Control.leapController;
		
		webcam.setViewSize(new java.awt.Dimension(640, 480));
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

		
		
		webcam.open();
		// 

		while(webcam.isOpen()){
				setChanged();

				BufferedImage image =VideoPipe.process(webcam.getImage());
				try {
					notifyObservers(image);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			sleep(15);
		}
	

}
