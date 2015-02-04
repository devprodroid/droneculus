package core.templates;

import core.commands.Commands;
import core.control.Control;

/**
 * Template Version 2
 *
 */
public class ContTemplate02 implements IContTemplate{

	@Override
	public void handleUp(double magnitude) {
		int speed = (int) (magnitude * Control.data.getSpeed()) ;
		Commands.forward(speed);
	}

	@Override
	public void handleDown(double magnitude) {
		int speed = (int) (magnitude * Control.data.getSpeed()) ;
		Commands.backward(speed);	
	}

	@Override
	public void handleLeft(double magnitude) {
		int speed = (int) (magnitude * Control.data.getSpeed()) ;
		Commands.goLeft(speed);
	}

	@Override
	public void handleRight(double magnitude) {
		int speed = (int) (magnitude * Control.data.getSpeed()) ;
		Commands.goRight(speed);
	}

	@Override
	public IContTemplate copy() {
		// TODO Auto-generated method stub
		return new ContTemplate02();
	}
	
//	@Override	
//	public void handleXAxis(JXInputAxisEvent arg0) {
//		
//		double value = arg0.getAxis().getValue();
//		int speed;
//		
//		if(value > 0.3)
//		{
//			speed = (int) (value * Control.data.getSpeed()) ;
//			Commands.goRight(speed);
//		}
//		
//		
//		else if(value < -0.3)
//		{
//			speed = (int) (value * Control.data.getSpeed()) * -1 ;
//			Commands.goLeft(speed);
//		}
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
//			speed = (int) (value * Control.data.getSpeed()) ;
//			Commands.backward(speed);
//		}
//		
//		
//		else if(value < -0.3)
//		{
//			speed = (int) (value * Control.data.getSpeed()) * -1 ;
//			Commands.forward(speed);
//		}			
//	}

}
