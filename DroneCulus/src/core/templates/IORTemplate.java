package core.templates;

import core.oculusrift.OculusRiftEvent;

/**
 * Interface for Templates, which want to handle OculusRift AxisEvents
 *
 */
public interface IORTemplate {
	void handleYaw(OculusRiftEvent e);
	void handlePitch(OculusRiftEvent e);
	void handleRoll(OculusRiftEvent e);
}
