package core.logging;

import core.control.Control;
import de.yadrone.base.exception.ARDroneException;
import de.yadrone.base.exception.IExceptionListener;

public class ExceptionListener implements IExceptionListener{

	@Override
	public void exeptionOccurred(ARDroneException arg0) {
		Control.out.println(arg0.toString());
	}

	
	
}
