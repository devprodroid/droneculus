package core.oculusrift;

import java.util.EventObject;

/**
 * Definition of an Event fired by OculusRiftEventManager
 *
 */
public class OculusRiftEvent extends EventObject{
	
	private static final long serialVersionUID = 6275443355520233552L;
	
	private float yaw;
	private float pitch;
	private float roll;
	
	private int yawMult;
	private int pitchMult;
	private int rollMult;

	public OculusRiftEvent
	
	(		Object source, 
			float yaw, float pitch, float roll, 
			int yawMult, int pitchMult, int rollMult) 
	{
		super(source);
		this.yaw = yaw;
		this.pitch = pitch;
		this.roll = roll;
		this.yawMult = yawMult;
		this.pitchMult = pitchMult;
		this.rollMult = rollMult;
	}

	public float getYaw() {
		return yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public float getRoll() {
		return roll;
	}

	public int getYawMult() {
		return yawMult;
	}

	public int getPitchMult() {
		return pitchMult;
	}

	public int getRollMult() {
		return rollMult;
	}

}
