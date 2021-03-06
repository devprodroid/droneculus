package core.video;

import java.awt.image.BufferedImage;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import core.control.Control;
import core.control.DataCenter;
import core.utils.Config;
import core.utils.ImgUtil;
import core.utils.ImgUtil.Direction;

public class VideoPipe {

	// Used for OpenCVs drawing functions
	private static final int fontFace = Core.FONT_HERSHEY_COMPLEX_SMALL;
	private static final double fontScale = 1;
	private static final int thickness = 1;
	private static final int lineType = Core.LINE_AA;

	// DataCenter
	private static DataCenter data = Control.data;

	// Stringbuilder for HUD-Strings
	private static StringBuilder speedBuilder = new StringBuilder("Speed: ");
	private static StringBuilder batteryBuilder = new StringBuilder();
	private static StringBuilder directionBuilder = new StringBuilder();
	private static StringBuilder fingerBuilder = new StringBuilder();

	/**
	 * Method for processing Images, is called from VideoListener and processes
	 * every Image on imageUpdated. Takes care of Generating an OpenCV-Mat and
	 * calls processMat.
	 * 
	 * @param image0
	 *            left Camera Image = BufferedImage
	 * @param image1
	 *            right Camera Image = BufferedImage
	 * @return = BufferedImage
	 */
	public static BufferedImage process(BufferedImage image0, BufferedImage image1, Boolean dualCamera) {

		// generate Mat from BufferedImages
		Mat imageMat0 = ImgUtil.makeMat(image0);

		Mat imageMat1 = null;
		if (dualCamera) {
			imageMat1 = ImgUtil.makeMat(image1);
		}

		return processMat(imageMat0, imageMat1, dualCamera);
	}

	/**
	 * Main Method for processing Images, is called from VideoListener and
	 * processes every Image on imageUpdated. Takes care of processing the image
	 * and transforming back to BufferdImage.
	 * 
	 * @param imageMat0
	 *            = Mat - left Camera Image
	 * @param imageMat1
	 *            = Mat - right Camera Image
	 * @param dualCamera
	 * 
	 * @return
	 */
	public static BufferedImage processMat(Mat imageMat0, Mat imageMat1, Boolean dualCamera) {
		// initial calculation or calculation of maps when a Coefficient has
		// changed (FrameKeyListener can change those)
		if (data.isRemapRecalculationWanted()) {
			if (!data.isChromaCorrectionEnabled())
				BarrelDistorter.calcMaps(imageMat0);
			else if (data.isChromaCorrectionEnabled())
				BarrelDistorter.calcMapsChromaCorrected(imageMat0);
			data.setRemapRecalculationWanted(false);
		}

		// show HUD only if wanted
		if (data.isHUDenabled()) {
			drawHud(imageMat0);

			if (dualCamera) {
				drawHud(imageMat1);
			}
		}

		// Distortion Correction, if wanted
		correctDistortion(imageMat0);
		if (dualCamera) {
			correctDistortion(imageMat1);
		}

		Mat combinedImage = null;
		Mat imageL = null;
		Mat imageR = null;

		// Shifting Image closer together to fix Parallax-Error
		imageL = ImgUtil.shiftFrame(imageMat0, data.getShift(), Direction.ShiftRight);
		// single camera mode - duplicate image
		if (!dualCamera) {
			imageR = ImgUtil.shiftFrame(imageMat0, data.getShift(), Direction.ShiftLeft);
		} else // dual camera mode - combine the image of two cameras
		{
			imageR = ImgUtil.shiftFrame(imageMat1, data.getShift(), Direction.ShiftLeft);
		}
		combinedImage = ImgUtil.makeOne(imageL, imageR);

		// make BufferedImage and return it
		return ImgUtil.makeBufferedImage(combinedImage);
	}

	/**
	 * @param imageMat
	 */
	private static void correctDistortion(Mat imageMat) {
		if (data.isDistortionCorrectionEnabled()) {

			if (!data.isChromaCorrectionEnabled()) {
				BarrelDistorter.remap(imageMat);
			}

			else if (data.isChromaCorrectionEnabled()) {
				BarrelDistorter.remapChromaCorrected(imageMat);
			}
		}
	}

	/**
	 * @param imageMat
	 */
	private static void drawHud(Mat imageMat) {
		drawText(data, imageMat);
		displayRollYawPitchAsHorizon(imageMat);
	}

	/**
	 * Drawing Method for Text that should be displayed on HUD, Battery Status,
	 * Speed and HUD-Information are displayed yet
	 * 
	 * @param data
	 * @param imageMat
	 */
	private static void drawText(DataCenter data, Mat imageMat) {

		int batteryPercentage = data.getBatteryPercentage();
		int speed = data.getSpeed();

		speedBuilder.append(speed);

		batteryBuilder.append(batteryPercentage);
		batteryBuilder.append("%");

		String info = data.getInfo();
		String batteryString = batteryBuilder.toString();

		String direction = data.getDirection();

		int w = imageMat.width();
		int h = imageMat.height();

		Scalar batteryColor;

		if (batteryPercentage > Config.BATTERY_ALERT_VALUE)
			batteryColor = HUDColorizer.NORMAL_HUD_COLOR;
		else
			batteryColor = HUDColorizer.ALERT_HUD_COLOR;

		Size sizeInfo = Imgproc.getTextSize(info, fontFace, fontScale, thickness, null);

		Size sizeBat = Imgproc.getTextSize(batteryString, fontFace, fontScale, thickness, null);
		
		Size sizeDirection = Imgproc.getTextSize(direction, fontFace, fontScale, thickness, null);

		int widthBat = (int) sizeBat.width + 5;
		int halfWidth = (int) (sizeInfo.width / 2);
		int widthDir = (int) sizeDirection.width + 5;
		

		put(imageMat, direction, new Point((w / 2) - widthDir, h - 20),  HUDColorizer.NORMAL_HUD_COLOR);
		put(imageMat, batteryString, new Point((w / 2) - widthBat, h - 5), batteryColor);
		put(imageMat, speedBuilder.toString(), new Point((w / 2) + 5, h - 5), HUDColorizer.NORMAL_HUD_COLOR);
		put(imageMat, info, new Point((w / 2) - halfWidth, 60), HUDColorizer.NORMAL_HUD_COLOR);

		// TODO: remove HardCoded Length for String "Speed: "
		speedBuilder.setLength(7);
		batteryBuilder = new StringBuilder();

	}

	/**
	 * Wrapper for putText-Method of OpenCV, because some parameters don't
	 * change.
	 * 
	 * @param image
	 *            = Image, the text should be displayed on
	 * @param text
	 *            = Text to be written
	 * @param point
	 *            = Point, were Text should be displayed
	 * @param Color
	 *            = the Color in which the the Text is displayed
	 */
	private static void put(Mat image, String text, Point point, Scalar Color) {

		Imgproc.putText(image, text, point, fontFace, fontScale, Color, thickness, lineType, false);
	}

	/**
	 * Method for Displaying the OculusRift Values as Horizon, so the user
	 * should be able to notice in which direction he is currently locking
	 * 
	 * @param imageMat
	 */
	private static void displayRollYawPitchAsHorizon(Mat imageMat) {

		double yaw = data.getCurYaw();
		double pitch = data.getCurPitch();

		int innerCircleRadius = Config.OFFSET_FOR_RIFT_ACTION / 3;
		int outerCircleRadius = Config.OFFSET_FOR_RIFT_ACTION;

		int w = imageMat.width();
		int h = imageMat.height();

		Point center = new Point(w / 2, h / 2);
		Point horizonCenter = new Point(w / 2 - yaw, h / 2 - pitch);

		drawCenterWithRollAt(imageMat, center, innerCircleRadius, 5);
		drawHorizonAt(imageMat, horizonCenter, outerCircleRadius);
		// drawHorizonWithRollAt(imageMat, horizonCenter, outerCircleRadius,
		// 10);
	}

	/**
	 * The Center for Orientation with extra line displaying the Rotation of the
	 * Head along the roll axis
	 * 
	 * @param imageMat
	 *            = Image
	 * @param center
	 *            = Centerpoint of Orientationcenter
	 * @param circleRadius
	 *            = Radius of Circle
	 * @param lineLength
	 *            = length of the line which is drawn to display rotation
	 */
	private static void drawCenterWithRollAt(Mat imageMat, Point center, int circleRadius, int lineLength) {

		double roll = data.getCurRoll();
		double radians = Math.toRadians(roll);

		int r = circleRadius + lineLength;
		int v = (int) (Math.sin(radians) * r);
		int u = (int) (Math.cos(radians) * r);
		Point rollOuterPoint = new Point(center.x + v, center.y - u);

		r = circleRadius;
		v = (int) (Math.sin(radians) * r);
		u = (int) (Math.cos(radians) * r);
		Point rollInnerPoint = new Point(center.x + v, center.y - u);

		Imgproc.line(imageMat, rollInnerPoint, rollOuterPoint, HUDColorizer.NORMAL_HUD_COLOR, 1, Core.LINE_AA, 0);
		Imgproc.circle(imageMat, center, circleRadius, HUDColorizer.NORMAL_HUD_COLOR, 1, Core.LINE_AA, 0);

	}

	/**
	 * Alternativ Test for static Horizon, the roll axis could be displayed
	 * there, too. But I checked, if this could work and it looked awful. A
	 * TODO: Display Roll of Drone, this could look nice.
	 * 
	 * @param imageMat
	 *            = Image
	 * @param center
	 *            = CenterPoint of Horizon
	 * @param circleRadius
	 *            = Radius of HorizonCircle
	 * @param lineLength
	 *            = length of the lines drawn left and right of the
	 *            HorizonCircle
	 */
	@SuppressWarnings("unused")
	private static void drawHorizonWithRollAt(Mat imageMat, Point center, int circleRadius, int lineLength) {

		double roll = data.getCurRoll();
		double radians = Math.toRadians(roll);

		int r = circleRadius + lineLength;
		int v = (int) (Math.sin(radians) * r);
		int u = (int) (Math.cos(radians) * r);
		Point leftOuterPoint = new Point(center.x + u, center.y - v);
		Point rightOuterPoint = new Point(center.x + u, center.y - v);

		r = circleRadius;
		v = (int) (Math.sin(radians) * r);
		u = (int) (Math.cos(radians) * r);
		Point leftInnerPoint = new Point(center.x + u, center.y - v);
		Point rightInnerPoint = new Point(center.x + u, center.y - v);

		Imgproc.line(imageMat, leftInnerPoint, leftOuterPoint, HUDColorizer.NORMAL_HUD_COLOR, 1, Core.LINE_AA, 0);
		Imgproc.line(imageMat, rightInnerPoint, rightOuterPoint, HUDColorizer.NORMAL_HUD_COLOR, 1, Core.LINE_AA, 0);
		Imgproc.circle(imageMat, center, circleRadius, HUDColorizer.NORMAL_HUD_COLOR, 1, Core.LINE_AA, 0);
	}

	/**
	 * draw Normal Horizon at Point
	 * 
	 * @param imageMat
	 *            = image
	 * @param center
	 *            = Centerpoint of Horizon
	 * @param circleRadius
	 *            = Radius of HorizonCircle
	 */
	private static void drawHorizonAt(Mat imageMat, Point center, int circleRadius) {

		Point left = new Point(center.x - 20, center.y);
		Point leftCircle = new Point(center.x - circleRadius, center.y);
		Point right = new Point(center.x + 20, center.y);
		Point rightCircle = new Point(center.x + circleRadius, center.y);

		Point up = new Point(center.x, center.y - circleRadius - 2);
		Point upCircle = new Point(center.x, center.y - circleRadius);
		Point down = new Point(center.x, center.y + circleRadius + 2);
		Point downCircle = new Point(center.x, center.y + circleRadius);

		Imgproc.line(imageMat, downCircle, down, HUDColorizer.NORMAL_HUD_COLOR, 1, Core.LINE_AA, 0);
		Imgproc.line(imageMat, upCircle, up, HUDColorizer.NORMAL_HUD_COLOR, 1, Core.LINE_AA, 0);
		Imgproc.line(imageMat, left, leftCircle, HUDColorizer.NORMAL_HUD_COLOR, 1, Core.LINE_AA, 0);
		Imgproc.line(imageMat, right, rightCircle, HUDColorizer.NORMAL_HUD_COLOR, 1, Core.LINE_AA, 0);
		Imgproc.circle(imageMat, center, circleRadius, HUDColorizer.NORMAL_HUD_COLOR, 1, Core.LINE_AA, 0);
	}

	/**
	 * Prototype for Orientation with OculusRift
	 * 
	 * @param imageMat
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private static void drawRYP(Mat imageMat) {

		double roll = data.getCurRoll();
		double yaw = data.getCurYaw();
		double pitch = data.getCurPitch();

		int w = imageMat.width();
		int h = imageMat.height();

		Point center = new Point(w / 2, h / 2);
		Point rightPoint;
		Point leftPoint;

		if (roll < 0) {
			rightPoint = new Point(w / 2 + roll, h / 2 - roll);
			leftPoint = new Point(w / 2 - roll, h / 2 + roll);
		} else {
			rightPoint = new Point(w / 2 + roll, h / 2 + roll);
			leftPoint = new Point(w / 2 - roll, h / 2 - roll);
		}

		Imgproc.line(imageMat, center, rightPoint, HUDColorizer.NORMAL_HUD_COLOR, 1, Core.LINE_AA, 0);
		Imgproc.line(imageMat, center, leftPoint, HUDColorizer.NORMAL_HUD_COLOR, 1, Core.LINE_AA, 0);

		rightPoint = new Point(w / 2 + pitch, h / 2);
		leftPoint = new Point(w / 2 - pitch, h / 2);

		Imgproc.line(imageMat, center, rightPoint, HUDColorizer.NORMAL_HUD_COLOR, 1, Core.LINE_AA, 0);
		Imgproc.line(imageMat, center, leftPoint, HUDColorizer.NORMAL_HUD_COLOR, 1, Core.LINE_AA, 0);

		rightPoint = new Point(w / 2, h / 2 + yaw);
		leftPoint = new Point(w / 2, h / 2 - yaw);

		Imgproc.line(imageMat, center, rightPoint, HUDColorizer.NORMAL_HUD_COLOR, 1, Core.LINE_AA, 0);
		Imgproc.line(imageMat, center, leftPoint, HUDColorizer.NORMAL_HUD_COLOR, 1, Core.LINE_AA, 0);

		Imgproc.circle(imageMat, center, Config.OFFSET_FOR_RIFT_ACTION, HUDColorizer.NORMAL_HUD_COLOR, 1, Core.LINE_AA,
				0);

	}
}
