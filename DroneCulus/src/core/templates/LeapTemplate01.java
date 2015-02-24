package core.templates;

import core.commands.Commands;
import core.control.Control;

/**
 * Template LeapMotion Desktop
 *
 */
public class LeapTemplate01 implements ILeapTemplate {

	@Override
	public void handleForward(double magnitude) {
		int speed = (int) (magnitude * Control.data.getSpeed());
		// Commands.spinLeft(speed);
		Commands.forward(speed);
	}

	@Override
	public void handleBackward(double magnitude) {
		int speed = (int) (magnitude * Control.data.getSpeed());
		// Commands.spinRight(speed);
		Commands.backward(speed);
	}

	@Override
	public void handleLeft(double magnitude) {
		int speed = (int) (magnitude * Control.data.getSpeed());
		Commands.goLeft(speed);
	}

	@Override
	public void handleRight(double magnitude) {
		int speed = (int) (magnitude * Control.data.getSpeed());
		Commands.goRight(speed);
	}

	@Override
	public void handleUp(double magnitude) {
		int speed = (int) (magnitude * Control.data.getSpeed());
		Commands.goUp(speed);
	}

	@Override
	public void handleDown(double magnitude) {
		int speed = (int) (magnitude * Control.data.getSpeed());
		Commands.goDown(speed);
	}

	@Override
	public void handleStart() {
		if (!Control.data.isFlying()) {
			Commands.takeOff();

			Control.data.setFlying(true);
			Commands.hover();
		} else {
			// Commands.landing();

			// Control.data.setFlying(false);
		}
	}

	@Override
	public void handleLand() {
		if (Control.data.isFlying()) {
			Commands.landing();
			Control.data.setFlying(false);
		}
	}

	@Override
	public void handleYaw(int Yaw) {

		int speed = Control.data.getSpeed() * Yaw / 5;

		if (Yaw > 10) {
			Commands.spinRight(speed);
		} else if (Yaw < -10) {
			speed=speed*-1;
			Commands.spinLeft(speed);
		}

	}

	@Override
	public ILeapTemplate copy() {
		// TODO Auto-generated method stub
		return new LeapTemplate01();
	}

}
