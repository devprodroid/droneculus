package core.logging;

import core.control.Control;
import de.yadrone.base.exception.ARDroneException;
import de.yadrone.base.exception.CommandException;
import de.yadrone.base.exception.ConfigurationException;
import de.yadrone.base.exception.IExceptionListener;
import de.yadrone.base.exception.NavDataException;
import de.yadrone.base.exception.VideoException;

public class ExceptionListener implements IExceptionListener {

	@Override
	public void exeptionOccurred(ARDroneException arg0) {


		if (arg0 instanceof ConfigurationException) {
			Control.out.println("ConfigurationException");
		} else if (arg0 instanceof CommandException) {
			Control.out.println("CommandException");
		} else if (arg0 instanceof NavDataException) {
			Control.out.println("NavDataException");
		} else if (arg0 instanceof VideoException) {
			Control.out.println("VideoException");
		} else {
			Control.out.println(arg0.toString());

		}

	}

}
