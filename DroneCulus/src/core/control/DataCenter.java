package core.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import core.oculusrift.IOculusRiftListener;
import core.oculusrift.OculusRiftEvent;
import core.utils.Config;
import de.yadrone.base.navdata.BatteryListener;

public class DataCenter implements IOculusRiftListener, BatteryListener {

	private static DataCenter uniqueInstance;

	// Current Yaw, Pitch and Roll Values of OculusRift-Device
	private double curYaw = 0;
	private double curPitch = 0;
	private double curRoll = 0;

	// Multiplier for Speed
	// Currently only used in OculusRiftHandler, but could be interesting
	// in VideoPipe as well
	private int yawMult = 0;
	private int pitchMult = 0;
	private int rollMult = 0;

	// Booleans, for:
	// Is Rift-Control enabled?
	// Is Drone already flying?
	// Is HUD enabled?
	// Is detection of Faces enabled?
	private boolean useRift = false;
	private boolean flying = false;
	private boolean HUDenabled = false;
	private boolean distortionCorrectionEnabled = true;
	private boolean remapRecalculationWanted = true;
	private boolean chromaCorrectionEnabled = true;

	private boolean disableControllerAndRift = false;

	// Shift-Value, to get images for both eyes closer together (Parallax)
	private int shift = 50;

	// Battery-Percentage of Drone
	private int batteryPercentage = 100;

	// fundamental Speed-Value
	private int speed = 5;

	// Info displayed on HUD
	private String info = "";

	// UserSelected webcam id
	private int primaryWebcamId;
	private int secondaryWebcamId;
	
	private String direction="hover";
	private String finger;
	
	
	

	public int getPrimaryWebcamId() {
		return primaryWebcamId;
	}

	public void setPrimaryWebcamId(int primaryWebcamId) {
		this.primaryWebcamId = primaryWebcamId;
	}

	public int getSecondaryWebcamId() {
		return secondaryWebcamId;
	}

	public void setSecondaryWebcamId(int secondaryWebcamId) {
		this.secondaryWebcamId = secondaryWebcamId;
	}

	/**
	 * Used to Store Data, so that every Thread can get some Information, about
	 * what is currently happening, if needed. For example: The VideoPipe uses
	 * some Information from the OculusRift to display those.
	 */
	private DataCenter() {
	}

	/**
	 * Current Yaw Value of OculusRift-Device
	 * 
	 * @return
	 */
	public double getCurYaw() {
		return curYaw;
	}

	/**
	 * Current Yaw Value of OculusRift-Device
	 * 
	 * @param curYaw
	 */
	public synchronized void setCurYaw(double curYaw) {
		this.curYaw = curYaw;
	}

	/**
	 * Current Pitch Value of OculusRift-Device
	 * 
	 * @return
	 */
	public double getCurPitch() {
		return curPitch;
	}

	/**
	 * Current Pitch Value of OculusRift-Device
	 * 
	 * @param curPitch
	 */
	public synchronized void setCurPitch(double curPitch) {
		this.curPitch = curPitch;
	}

	/**
	 * Current Roll Value of OculusRift-Device
	 * 
	 * @return
	 */
	public double getCurRoll() {
		return curRoll;
	}

	/**
	 * Current Roll Value of OculusRift-Device
	 * 
	 * @param curRoll
	 */
	public synchronized void setCurRoll(double curRoll) {
		this.curRoll = curRoll;
	}

	/**
	 * Shift Value for bringing the two images closer to each other
	 * 
	 * @return
	 */
	public int getShift() {
		return shift;
	}

	/**
	 * Shift Value for bringing the two images closer to each other
	 * 
	 * @param shift
	 */
	public synchronized void setShift(int shift) {
		this.shift = shift;
	}

	/**
	 * Battery-Percentage of Drone
	 * 
	 * @return
	 */
	public int getBatteryPercentage() {
		return batteryPercentage;
	}

	/**
	 * Battery-Percentage of Drone
	 * 
	 * @param batteryPercentage
	 */
	public synchronized void setBatteryPercentage(int batteryPercentage) {
		this.batteryPercentage = batteryPercentage;
	}

	/**
	 * fundamental Speed-Value for Commands
	 * 
	 * @return
	 */
	public int getSpeed() {
		return speed;
	}

	/**
	 * fundamental Speed-Value for Commands
	 * 
	 * @param speed
	 */
	public synchronized void setSpeed(int speed) {
		this.speed = speed;
	}

	/**
	 * Yaw multiplier for Rotation-Speed enhancement
	 * 
	 * @return
	 */
	public int getYawMult() {
		return yawMult;
	}

	/**
	 * Yaw multiplier for Rotation-Speed enhancement
	 * 
	 * @param yawMult
	 */
	public synchronized void setYawMult(int yawMult) {
		this.yawMult = yawMult;
	}

	/**
	 * Pitch multiplier for Speed enhancement
	 * 
	 * @return
	 */
	public int getPitchMult() {
		return pitchMult;
	}

	/**
	 * Pitch multiplier for Speed enhancement
	 * 
	 * @param pitchMult
	 */
	public synchronized void setPitchMult(int pitchMult) {
		this.pitchMult = pitchMult;
	}

	/**
	 * Roll multiplier for Sideways-Speed enhancement
	 * 
	 * @return
	 */
	public int getRollMult() {
		return rollMult;
	}

	/**
	 * Roll multiplier for Sideways-Speed enhancement
	 * 
	 * @param rollMult
	 */

	public synchronized void setRollMult(int rollMult) {
		this.rollMult = rollMult;
	}

	/**
	 * Returns, whether Rift-Control is enabled or not
	 * 
	 * @return
	 */

	public boolean useRift() {
		return useRift;
	}

	public synchronized void setUseRift(boolean useRift) {
		this.useRift = useRift;
	}

	/**
	 * Returns, whether the Drone is already flying or not
	 * 
	 * @return
	 */

	public boolean isFlying() {
		return flying;
	}

	public synchronized void setFlying(boolean isFlying) {
		this.flying = isFlying;
	}

	/** Returns, whether the HUD should be displayed or not */
	public boolean isHUDenabled() {
		return HUDenabled;
	}

	public synchronized void setHUDenabled(boolean hUDenabled) {
		HUDenabled = hUDenabled;
	}

	/** Info-String displayed by HUD for {@link Config#INFO_LENGTH_VALUE} millis */
	public String getInfo() {
		return info;
	}

	/** Info-String displayed by HUD for {@link Config#INFO_LENGTH_VALUE} millis */
	public synchronized void setInfo(String info) {
		this.info = info;
		resetInfo();
	}

	/**
	 * Method for resetting the Info-String after
	 * {@link Config#INFO_LENGTH_VALUE} millis
	 */
	private void resetInfo() {
		Timer timer = new Timer(Config.INFO_LENGTH_VALUE, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				info = "";
			}
		});
		timer.setRepeats(false); // Only execute once
		timer.start();
	}

	/** Returns, whether Distortion Correction is enabled or not */
	public boolean isDistortionCorrectionEnabled() {
		return distortionCorrectionEnabled;
	}

	public synchronized void setDistortionCorrectionEnabled(boolean enabled) {
		this.distortionCorrectionEnabled = enabled;
	}

	/**
	 * Listen to OculusRift Events from EventManager, so that those information
	 * can be stored
	 */
	@Override
	public void changed(OculusRiftEvent e) {
		curYaw = e.getYaw();
		curPitch = e.getPitch();
		curRoll = e.getRoll();

		yawMult = e.getYawMult();
		pitchMult = e.getPitchMult();
		rollMult = e.getRollMult();
	}

	/**
	 * Listen to Battery changed Events from Drone, so that those information
	 * can be stored
	 */
	@Override
	public void batteryLevelChanged(int percentage) {
		batteryPercentage = percentage;
	}

	@Override
	public void voltageChanged(int arg0) {
	}

	/** Returns, whether Recalculation of Distortion Maps is wanted or not */
	public boolean isRemapRecalculationWanted() {
		return remapRecalculationWanted;
	}

	public void setRemapRecalculationWanted(boolean remapRecalculationWanted) {
		this.remapRecalculationWanted = remapRecalculationWanted;
	}

	/** Returns, whether Chromatic Aberration Correction is enabled or not */
	public boolean isChromaCorrectionEnabled() {
		return chromaCorrectionEnabled;
	}

	public void setChromaCorrectionEnabled(boolean chromaCorrectionEnabled) {
		this.chromaCorrectionEnabled = chromaCorrectionEnabled;
	}

	public boolean isDisableControllerAndRift() {
		return disableControllerAndRift;
	}

	public void setDisableControllerAndRift(boolean disableControllerAndRift) {
		this.disableControllerAndRift = disableControllerAndRift;
	}

	public static DataCenter getInstance() {

		if (uniqueInstance == null) {
			uniqueInstance = new DataCenter();
		}

		return uniqueInstance;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getFinger() {
		return finger;
	}

	public void setFinger(String finger) {
		this.finger = finger;
	}
}
