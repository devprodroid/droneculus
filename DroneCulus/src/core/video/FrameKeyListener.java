package core.video;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import core.control.Control;
import core.control.DataCenter;
import de.yadrone.base.command.CommandManager;

public class FrameKeyListener implements KeyListener {
	
	private DataCenter data = Control.data;
	private CommandManager cmd = Control.drone.getCommandManager();
	
	/** react on key pressed */
	@Override
	public void keyPressed(KeyEvent arg0) 
	{
		
		shiftKeys(arg0);
		barrelDistortionKeys(arg0);
		chromaCorrectionKeys(arg0);
		keyboardControlKeys(arg0);
	}
	
	/** react on key released */
	@Override
	public void keyReleased(KeyEvent arg0) {		
	
		if(		arg0.getKeyCode() == KeyEvent.VK_UP || 
				arg0.getKeyCode() == KeyEvent.VK_DOWN ||
				arg0.getKeyCode() == KeyEvent.VK_LEFT || 
				arg0.getKeyCode() == KeyEvent.VK_RIGHT||
				arg0.getKeyCode() == KeyEvent.VK_SHIFT ||
				arg0.getKeyCode() == KeyEvent.VK_CAPS_LOCK) {
			
			cmd.hover();
		}
	}
	
	/** react on KeyEvents, used for gaining KeyBoard-Control */
	private void keyboardControlKeys(KeyEvent arg0) {
		if(arg0.getKeyCode() == KeyEvent.VK_SPACE) {
			if(!data.isDisableControllerAndRift()) {
				data.setDisableControllerAndRift(true);
				data.setInfo("Control kicked!");
				cmd.hover();
			} else {
				data.setDisableControllerAndRift(false);
				data.setInfo("Control granted!");
			}
		}
		
		if(arg0.getKeyCode() == KeyEvent.VK_UP) {
			cmd.forward(5);
		}
		
		if(arg0.getKeyCode() == KeyEvent.VK_DOWN) {
			cmd.backward(5);
		}
		
		if(arg0.getKeyCode() == KeyEvent.VK_RIGHT) {
			cmd.goRight(5);
		}
		
		if(arg0.getKeyCode() == KeyEvent.VK_LEFT) {
			cmd.goLeft(5);
		}
		
		if(arg0.getKeyCode() == KeyEvent.VK_ENTER) {
			cmd.landing();
		}
		
		if(arg0.getKeyCode() == KeyEvent.VK_SHIFT) {
			cmd.up(5);
		}
		
		if(arg0.getKeyCode() == KeyEvent.VK_CAPS_LOCK) {
			cmd.down(5);
		}
		
	}
	
	/** react on KeyEvents, used for shifting Frames together */
	private void shiftKeys(KeyEvent arg0) {
		//Increase or Decrease Shifting of Images
		if(arg0.getKeyChar() == 'd')
			data.setShift(data.getShift()+1);
		
		if(arg0.getKeyChar() == 'a') 
			data.setShift(data.getShift()-1);
	}
	
	/** react on KeyEvents, used for controlling the Distortion-Coefficients */
	private void barrelDistortionKeys(KeyEvent arg0) {
		
		if(arg0.getKeyChar() == 'b') {
			data.setDistortionCorrectionEnabled(!data.isDistortionCorrectionEnabled());
		}
		
		
		//Increase or Decrease K1 Coefficient
		if(arg0.getKeyChar() == 'j') {
			
			BarrelDistorter.K1 = BarrelDistorter.K1 + BarrelDistorter.offsetK1;
			Control.out.println("K1 now: " + BarrelDistorter.K1);
			
			data.setRemapRecalculationWanted(true);
		}
		
		if(arg0.getKeyChar() == 'l')  {
			
			BarrelDistorter.K1 = BarrelDistorter.K1 - BarrelDistorter.offsetK1;
			Control.out.println("K1 now: " + BarrelDistorter.K1);
			
			data.setRemapRecalculationWanted(true);
		}
		
		//Increase or Decrease K2 Coefficient
		if(arg0.getKeyChar() == 'i')  {
			
			BarrelDistorter.K2 = BarrelDistorter.K2 + BarrelDistorter.offsetK2;
			Control.out.println("K2 now: " + BarrelDistorter.K2);
			
			data.setRemapRecalculationWanted(true);
		}
		
		if(arg0.getKeyChar() == 'k')  {
			
			BarrelDistorter.K2 = BarrelDistorter.K2 - BarrelDistorter.offsetK2;
			Control.out.println("K2 now: " + BarrelDistorter.K2);
			
			data.setRemapRecalculationWanted(true);
		}
	}
	
	/** react on KeyEvents, used for toggling Chromatic Aberration Correction */
	private void chromaCorrectionKeys(KeyEvent arg0) {
		//Toggle Chromatic Aberration Correction
		if(arg0.getKeyChar() == 'c')  {
			
			data.setChromaCorrectionEnabled(!data.isChromaCorrectionEnabled());
			
			data.setRemapRecalculationWanted(true);
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {}

}
