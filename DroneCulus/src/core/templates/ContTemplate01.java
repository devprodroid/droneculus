package core.templates;

import core.commands.Commands;
import core.control.Control;

/**
 * Template Version 1
 *
 */
public class ContTemplate01 implements IContTemplate{

	@Override
	public void handleUp(double magnitude) {
		int speed = (int) (magnitude * Control.data.getSpeed()) ;
		Commands.goUp(speed);
	}

	@Override
	public void handleDown(double magnitude) {
		int speed = (int) (magnitude * Control.data.getSpeed()) ;
		Commands.goDown(speed);
		
	}

	@Override
	public void handleLeft(double magnitude) {
		//Nothing TODO here ;)
	}

	@Override
	public void handleRight(double magnitude) {
		//Nothing TODO here ;)
	}

	@Override
	public IContTemplate copy() {
		return new ContTemplate01();
	}
	
//	@Override
//	public void handleXAxis(JXInputAxisEvent arg0) {
//		//Nothing TODO here ;)
//			
//	}
//	
//	@Override
//	public void handleYAxis(JXInputAxisEvent arg0) {
//		
//		double value = arg0.getAxis().getValue();
//		int speed;
//		
//		if(value > 0.3)
//		{
//			int speed = (int) (value * Control.data.getSpeed()) ;
//			Commands.goDown(speed);
//		}
//		
//		
//		else if(value < -0.3)
//		{
//			speed = (int) (value * Control.data.getSpeed()) * -1 ;
//			Commands.goUp(speed);
//		}
//			
//	}
}
