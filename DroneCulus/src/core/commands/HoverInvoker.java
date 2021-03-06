package core.commands;

import java.util.Observable;
import java.util.Observer;

import core.controller.ControllerHandler;
import core.leapmotion.LeapMotionHandler;
import core.oculusrift.OculusRiftAxisListener;

/**
 * Invokes the Hover Command, when OculusRift and Controller have nothing to do
 *
 */
public class HoverInvoker implements Observer {

	private static boolean OculusRiftSaysHover = false;
	private static boolean ControllerSaysHover = false;
	private static boolean LeapMotionSaysHover = false;

	private void compare() {

		if (OculusRiftSaysHover && ControllerSaysHover && LeapMotionSaysHover) {
			Commands.hover();
		}
	}

	@Override
	public synchronized void update(Observable obs, Object obj) {

		if (obs.getClass().getName() == OculusRiftAxisListener.class.getName()
				&& (boolean) obj)
			OculusRiftSaysHover = true;
		else if (obs.getClass().getName() == OculusRiftAxisListener.class
				.getName() && !(boolean) obj)
			OculusRiftSaysHover = false;

		if (obs.getClass().getName() == ControllerHandler.class.getName()
				&& (boolean) obj)
			ControllerSaysHover = true;
		else if (obs.getClass().getName() == ControllerHandler.class.getName()
				&& !(boolean) obj)
			ControllerSaysHover = false;
		
		if (obs.getClass().getName() == LeapMotionHandler.class.getName()
				&& (boolean) obj)
			LeapMotionSaysHover = true;
		else if (obs.getClass().getName() == LeapMotionHandler.class.getName()
				&& !(boolean) obj)
			LeapMotionSaysHover = false;

		compare();
	}
}
