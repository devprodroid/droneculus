package core.detection;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import core.video.HUDColorizer;

/**
 * Experiment for FaceDetection, works but is not used ;)
 *
 */
public class FaceDetector {
	
	private static CascadeClassifier faceDetector = 
			new CascadeClassifier(FaceDetector.class.getResource("/resources/haarcascade_frontalface_alt.xml").toString());
	
	private static int 				fontFace = Core.FONT_HERSHEY_COMPLEX_SMALL;
	private static double 			fontScale = 1;
	private static int 				thickness = 1;
	private static int 				lineType = Core.LINE_AA;
	
	private static String 			alertString = "Close Human!";
	
	public static void detect(Mat image) {
		
		double scale = 6;
		
		Mat resized = new Mat();
		Mat gray = new Mat();
		
		Imgproc.resize(image, resized, new Size(image.width()/scale,image.height()/scale));        
		
		Imgproc.cvtColor(resized, gray, Imgproc.COLOR_BGR2GRAY);
		Imgproc.equalizeHist(gray, gray);
        
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(gray, faceDetections);
        
        for (Rect rect : faceDetections.toArray()) 
        {        	
        	drawRectangle(
        			image, 
        			new Point(rect.x*scale, rect.y*scale), 
        			new Point((rect.x + rect.width)*scale, (rect.y + rect.height)*scale), 
        			new Point(rect.x*scale, (rect.y + rect.height + 4)*scale));
        	
        }  
        
	}
	
	private static void drawRectangle(Mat image, Point p1, Point p2, Point stringPoint) {
		
		Core.rectangle(
        		image, 
        		p1, 
        		p2,
                HUDColorizer.ALERT_HUD_COLOR);
        
        Core.putText(
        		image, 
        		alertString, 
        		stringPoint, 
        		fontFace, 
        		fontScale, 
        		HUDColorizer.ALERT_HUD_COLOR, 
        		thickness, 
        		lineType, 
        		false);
	}
}
