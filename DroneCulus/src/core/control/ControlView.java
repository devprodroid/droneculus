package core.control;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.Timer;

import core.control.ControlManager.ColorizerListener;
import core.control.ControlManager.LogBoxListener;
import core.control.ControlManager.StartButtonListener;

/**
 * View for the Control instance, holds Container with every Button, CheckBox and Image
 *
 */
public class ControlView extends JFrame{

	private static final long serialVersionUID = 3306328165082242564L;
	
	//every Component is capsuled here
	private ControlContainer 		container;
	
	//timer and counter for Video waiting message
	private Timer 					timer;
	private int 					timerCounter = 10;
	
	public ControlView() {
				
		container = new ControlContainer();
		add(container);	
		
		setTitle("Please choose the configuration, you want to fly with!");	
		setSize(container.getImageWidth() + 25,container.getImageHeight() + 100);		
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);	
		addControlViewExitOptions();		
		
	}
	
	/**
	 * Checks for Drone-Connection and sets InitLabel to String
	 */
	public void startDroneConnectionCheck() {
		
		container.setInitLabel("Connecting to Drone...", Color.GRAY);		
		check();
		
		
	}
	
	/**
	 * starts a timer checking for Video Connection
	 */
	private void check() {
		
		timer = new Timer(1000, new ActionListener() {
		  @Override
		  public void actionPerformed(ActionEvent arg0) {
			  
			  Control.out.println("Checking Connection...");
			  if(timerCounter > 0) {
				  
				  if(Control.isDroneConnected) {
					  container.setInitLabel("Drone connected!", Color.BLUE);
					  Control.out.println("Drone connected!");
					  timer.stop();
				  } else {					  
					  container.setInitLabel("Waiting for Video... " + timerCounter + "s", Color.GRAY);	
					  timerCounter--;
				  }
				  
			  } else {
				  container.setInitLabel("Connection could not be established!", Color.RED);
				  Control.out.println("Connection could not be established!");
				  timer.stop();
			  }
		  } 
		});
		timer.setRepeats(true);
		timer.start();		
	}
	
	/**
	 * Exit Options of ControlView
	 */
	private void addControlViewExitOptions() {
		
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
        KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Cancel"); 
		getRootPane().getActionMap().put("Cancel", new AbstractAction(){ 
			private static final long serialVersionUID = -1585060199255639249L;

			public void actionPerformed(ActionEvent e)
	        {          	
				Control.drone.stop();
	        	System.exit(0);
	        }
	    });  
		
		addWindowListener(new WindowAdapter() {
	        public void windowClosing(WindowEvent e) 
	        {  
	        	Control.drone.stop();
	        	System.exit(0);
	        }
	    });	     
	}
	
	public void setBatteryTo(int value) {
		container.setBattery(value);
	}
	
	public String getSelectedItem() {
		return (String) container.getComboBox().getSelectedItem();
	}
	
	public boolean isLogBoxChecked() {
		return container.getLogCheckBox().isSelected();
	}
	
	public void addStartButtonListener(StartButtonListener listener) {
		container.getButton().addActionListener(listener);
	}
	
	public void addLogCheckBoxListener(LogBoxListener listener) {
		container.getLogCheckBox().addActionListener(listener);
	}
	
	public void addColorizerButtonListener(ColorizerListener listener) {
		container.getColorizerButton().addActionListener(listener);
	}
}
