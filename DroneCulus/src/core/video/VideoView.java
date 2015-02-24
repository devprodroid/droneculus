package core.video;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * View for Displaying the VideoFeed
 *
 */
public class VideoView extends JFrame implements Observer {

	private static final long serialVersionUID = -572341852243342361L;
	private BufferedImage image;
	
	
	/**
	 * Instance of the WebcamRunnable 
	 */
	private WebcamRunnable webcamRunnable;
public boolean enableWebcam;
	public VideoView(boolean enableWebcam) {
		this.enableWebcam=enableWebcam;
		// GraphicsEnvironment ge =
		// GraphicsEnvironment.getLocalGraphicsEnvironment();
		// GraphicsDevice gs = ge.getDefaultScreenDevice();
		// gs.setFullScreenWindow( this );

		setSize(1280, 800);
		setBackground(Color.BLACK);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setVisible(false);
		addKeyListener(new FrameKeyListener());
		// select the videosource

		if (enableWebcam) {

			webcamRunnable = new WebcamRunnable();
			webcamRunnable.addObserver(this);
			new Thread(webcamRunnable).start();
			new VideoListener(enableWebcam).addObserver(this);
		} else {

			new VideoListener(enableWebcam).addObserver(this);
		}

	}


	public void paint(Graphics g) {
		if (image != null) {
			g.drawImage(image, 0, 220, image.getWidth(), image.getHeight(),
					null);
		}
	}

	@Override
	public void update(Observable arg0, Object arg1) {

		try {
			image = (BufferedImage) arg1;
		} catch (Exception e) {
			e.printStackTrace();
		}

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				repaint();
			}
		});
	}

	public void closeWebcams() {

		// leapImageListener.closeCaptureDevices();

	}

}
