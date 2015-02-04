package core.utils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

public class ImgUtil {

	/**
	 * Enum for directions in which shifting a Frame is possible
	 *
	 */
	public enum Direction{
		ShiftUp, ShiftRight, ShiftDown, ShiftLeft
	};

	/**
	 * Shift a Frame.
	 * @param frame = Frame, which should be shifted
	 * @param pixels = pixels-offset for shift
	 * @param direction = Direction for the Shift
	 * @return Shifted Mat
	 */
	public static Mat shiftFrame(Mat frame, int pixels, Direction direction)
	{
		//create a same sized temporary Mat with all the pixels flagged as invalid (-1)
		Mat temp = Mat.zeros(frame.size(), frame.type());

		switch (direction)
		{
		case ShiftUp :		
			frame.submat((new Rect(0, pixels, frame.cols(), frame.rows() - pixels))).copyTo(temp.submat(new Rect(0, 0, temp.cols(), temp.rows() - pixels)));
			break;
		case ShiftRight :
			frame.submat(new Rect(0, 0, frame.cols() - pixels, frame.rows())).copyTo(temp.submat(new Rect(pixels, 0, frame.cols() - pixels, frame.rows())));
			break;
		case ShiftDown :
			frame.submat(new Rect(0, 0, frame.cols(), frame.rows() - pixels)).copyTo(temp.submat(new Rect(0, pixels, frame.cols(), frame.rows() - pixels)));
			break;
		case ShiftLeft :
			frame.submat(new Rect(pixels, 0, frame.cols() - pixels, frame.rows())).copyTo(temp.submat(new Rect(0, 0, frame.cols() - pixels, frame.rows())));
			break;
		default:
			System.out.println("Shift direction is not set properly");
		}

		return temp;
	}

	/**
	 * Make one Mat out of two.
	 * @param matL = left image
	 * @param matR = right image
	 * @return combined Images in one
	 */
	public static Mat makeOne(Mat matL, Mat matR) {
		
		int w = matL.width();
		int h = matR.height();
		
		Mat img_matches = new Mat(h, w * 2, matL.type());

		Mat left = new Mat(img_matches,new Rect(0, 0, w, h)); // Copy constructor
		matL.copyTo(left);
		Mat right = new Mat(img_matches,new Rect(w, 0, w, h)); // Copy constructor
		matR.copyTo(right);

		return img_matches;
	}

	/**
	 * generates a OpenCV-Mat from a given BufferedImage
	 * @param image = Input BufferedImage
	 * @return Output OpenCV-Mat
	 */
	public static Mat makeMat(BufferedImage image) {
    	
   	 byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        
     Mat imageMat = new Mat(image.getHeight(),image.getWidth(),CvType.CV_8UC3);
        
     imageMat.put(0, 0, pixels);
     
     return imageMat;
   }
   
	/**
	 * generates a BufferedImage from a given OpenCV-Mat
	 * @param image = Input OpenCV-Mat
	 * @return Output BufferedImage
	 */
   public static BufferedImage makeBufferedImage(Mat image) {
	   
	      byte[] data = new byte[image.rows()*image.cols()*(int)(image.elemSize())];
	      image.get(0, 0, data);
	      if (image.channels() == 3) {
	          for (int i = 0; i < data.length; i += 3) {
	              byte temp = data[i];
	              data[i] = data[i + 2];
	              data[i + 2] = temp;
	          }
	      }
	      
	      BufferedImage bufimage = new BufferedImage(image.cols(), image.rows(), BufferedImage.TYPE_3BYTE_BGR);
	      bufimage.getRaster().setDataElements(0, 0, image.cols(), image.rows(), data); 
	      
	      return bufimage;
   }
	
}
