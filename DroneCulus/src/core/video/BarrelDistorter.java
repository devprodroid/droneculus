package core.video;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 * This class applies BarrelDistortion to a given Mat
 * ChromaCorrected or normal
 */
public class BarrelDistorter {
	
	//TODO: The OculusRifts Lens-Centers are not equal to the to the OculusRifts Screen-Centers for left and right eyes,
	//		this implementation however uses the Screen-Centers as the Center of Distortion.
	//		I had not enough Time to test, if the Results get better when the Lens-Centers are the
	//		Center of Distortion.
	
	//Integer for left or right eye
	public static final int LEFT_EYE = 0;
	public static final int RIGHT_EYE = 1;
	
	//offset for adjusting K1 and K2 Coefficients
	public static float offsetK1 = 0.00000001f;;
	public static float offsetK2 = 0.000000000001f;
	
	//Normally those Coefficients should be 1.0f, 0.22f, 0.24f and 0.0f for the OculusRift,
	//but my Implementation seems to differ a bit from the given Browns Model used in the Oculus-SDK, 
	//so i used other Coefficient-Values for K1 and K2, which can be manipulated using the offset above (look at FrameKeyListener.class). 
	//This way we can manually adjust the Distortion and can use the natural Rift-Distortion for Camera-Calibration, if wanted ;)
	public static float K0 = 1.0f;
	public static float K1 = 0.00000022f;
	public static float K2 = 0.000000000024f;
	public static float K3 = 0.0f;
	
	public static float BLUE_MAP_COEF = 1.014f;
	public static float GREEN_MAP_COEF = 1.0f;
	public static float RED_MAP_COEF = 0.996f;
	
	//This is only a Placeholder
	//The ChromaCorrection of the Oculus SDK uses this value for red correction, but when inserting the original Coefficients here
	//the results are incorrect
	public static float SEC_COEF = 0.0f;
	
	
	//Placeholder for the DistortionMaps, they are only here for efficiency reasons,
	//because it would be quite a heavy load, to calculate every Map on every single Frame.
	//Calculating these Maps is only needed, when the Coefficient-Values have changed or on initial setup!
	//---> Without ChromaCorrection
	private static Mat mapX;
	private static Mat mapY;
	
	
	//Placeholder for the DistortionMaps, they are only here for efficiency reasons,
	//because it would be quite a heavy load, to calculate every Map on every single Frame.
	//Calculating these Maps is only needed, when the Coefficient-Values have changed or on initial setup!
	//---> With ChromaCorrection
	private static Mat mapX_BLUE;
	private static Mat mapY_BLUE;	
	private static Mat mapX_GREEN;
	private static Mat mapY_GREEN;	
	private static Mat mapX_RED;
	private static Mat mapY_RED;
	
	/**
	 * Calculate normal Distortion Maps
	 * @param image = image used for calculation
	 */
	public static void calcMaps(Mat image) {
		
		int Cx = image.width()/2;
		int Cy = image.height()/2;
		
		mapX = Mat.zeros(image.size(),CvType.CV_32F);
		mapY = Mat.zeros(image.size(),CvType.CV_32F);
		
		float[] mapXArr = new float[(int) (mapX.total() * mapX.channels())];
		float[] mapYArr = new float[(int) (mapY.total() * mapY.channels())];
		
		int counter = 0;
		
		for (int y = 0; y < image.height(); y++)
	    {
	        for (int x = 0; x < image.width(); x++)
	        {   
	        	
	        	float rsq = (x-Cx)*(x-Cx)+(y-Cy)*(y-Cy);
	        	
	            float u = Cx+(x-Cx) * (K0 + K1 * rsq + K2 * rsq * rsq + K3 * rsq * rsq * rsq);
	            mapXArr[counter] = u;
	            
	            u = Cy+(y-Cy) * (K0 + K1 * rsq + K2 * rsq * rsq + K3 * rsq * rsq * rsq);
	            mapYArr[counter] = u;
	            counter++;
	        }
	    }
		
		mapX.put(0, 0, mapXArr);
		mapY.put(0, 0, mapYArr);	
	}
	
	/**
	 * remap image to calculated (normal) Maps
	 * @param image = image to be remapped
	 */
	public static void remap(Mat image) {
		Imgproc.remap(image, image, mapX, mapY, Imgproc.INTER_LINEAR);
	}
	
	/**
	 * init Maps for ChromaCorrection
	 * @param image
	 */
	private static void initChromaMaps(Mat image) {
		mapX_BLUE = Mat.zeros(image.size(),CvType.CV_32F);
		mapY_BLUE = Mat.zeros(image.size(),CvType.CV_32F);
		mapX_GREEN = Mat.zeros(image.size(),CvType.CV_32F);
		mapY_GREEN = Mat.zeros(image.size(),CvType.CV_32F);
		mapX_RED = Mat.zeros(image.size(),CvType.CV_32F);
		mapY_RED = Mat.zeros(image.size(),CvType.CV_32F);
	}
	
	/**
	 * put float-Arrays of Maps to Maps
	 * @param blueX = X-Map for blue
	 * @param blueY = Y-Map for blue
	 * @param greenX = X-Map for green
	 * @param greenY = Y-Map for green
	 * @param redX = X-Map for red
	 * @param redY = Y-Map for red
	 */
	private static void putArraysToMats(float[] blueX, float[] blueY, float[] greenX, float[] greenY, float[] redX, float[] redY) {
		mapX_BLUE.put(0, 0, blueX);
		mapY_BLUE.put(0, 0, blueY);	
		mapX_GREEN.put(0, 0, greenX);
		mapY_GREEN.put(0, 0, greenY);	
		mapX_RED.put(0, 0, redX);
		mapY_RED.put(0, 0, redY);	
	}
	
	/**
	 * Calculate Chromatic Aberration corrected Distortion Maps
	 * @param image = image used for calculation
	 */
	public static void calcMapsChromaCorrected(Mat image) {
		
		int Cx = image.width()/2;
		int Cy = image.height()/2;
		
		initChromaMaps(image);
		
		float[] mapXArr_BLUE = new float[(int) (mapX_BLUE.total() * mapX_BLUE.channels())];
		float[] mapYArr_BLUE = new float[(int) (mapY_BLUE.total() * mapY_BLUE.channels())];
		float[] mapXArr_GREEN = new float[(int) (mapX_GREEN.total() * mapX_GREEN.channels())];
		float[] mapYArr_GREEN = new float[(int) (mapY_GREEN.total() * mapY_GREEN.channels())];
		float[] mapXArr_RED = new float[(int) (mapX_RED.total() * mapX_RED.channels())];
		float[] mapYArr_RED = new float[(int) (mapY_RED.total() * mapY_RED.channels())];
		
		int counter = 0;
		
		for (int y = 0; y < image.height(); y++)
	    {
	        for (int x = 0; x < image.width(); x++)
	        {   
	        	
	        	float rsq = (x-Cx)*(x-Cx)+(y-Cy)*(y-Cy);
	        	
	            float b = Cx+(x-Cx) * (BLUE_MAP_COEF + SEC_COEF * rsq) * (K0 + K1 * rsq + K2 * rsq * rsq + K3 * rsq * rsq * rsq);
	            float g = Cx+(x-Cx) * (GREEN_MAP_COEF + SEC_COEF * rsq) * (K0 + K1 * rsq + K2 * rsq * rsq + K3 * rsq * rsq * rsq);
	            float r = Cx+(x-Cx) * (RED_MAP_COEF + SEC_COEF * rsq) * (K0 + K1 * rsq + K2 * rsq * rsq + K3 * rsq * rsq * rsq);
	            mapXArr_BLUE[counter] = b;
	            mapXArr_GREEN[counter] = g;
	            mapXArr_RED[counter] = r;
	            
	            b =  Cy+(y-Cy) * (BLUE_MAP_COEF + SEC_COEF * rsq) * (K0 + K1 * rsq + K2 * rsq * rsq + K3 * rsq * rsq * rsq);
	            g =  Cy+(y-Cy) * (GREEN_MAP_COEF + SEC_COEF * rsq) * (K0 + K1 * rsq + K2 * rsq * rsq + K3 * rsq * rsq * rsq);
	            r =  Cy+(y-Cy) * (RED_MAP_COEF + SEC_COEF * rsq) * (K0 + K1 * rsq + K2 * rsq * rsq + K3 * rsq * rsq * rsq);
	            mapYArr_BLUE[counter] = b;
	            mapYArr_GREEN[counter] = g;
	            mapYArr_RED[counter] = r;
	            counter++;
	        }
	    }
		
		putArraysToMats(mapXArr_BLUE, mapYArr_BLUE, mapXArr_GREEN, mapYArr_GREEN, mapXArr_RED, mapYArr_RED);
	}
	
	/**
	 * remap image to calculated (Chromatic Aberration corrected) Maps
	 * @param image = image to be remapped
	 */
	public static void remapChromaCorrected(Mat image) {
		List<Mat> mv = new ArrayList<Mat>();
		
		Core.split(image, mv);
		
		Mat ch1 = mv.get(0);	//B
		Mat ch2 = mv.get(1);	//G
		Mat ch3 = mv.get(2);	//R
		
		Imgproc.remap(ch1, ch1, mapX_BLUE, mapY_BLUE, Imgproc.INTER_LINEAR);
		Imgproc.remap(ch2, ch2, mapX_GREEN, mapY_GREEN, Imgproc.INTER_LINEAR);
		Imgproc.remap(ch3, ch3, mapX_RED, mapY_RED, Imgproc.INTER_LINEAR);
		
		Core.merge(mv, image);				
	}
}