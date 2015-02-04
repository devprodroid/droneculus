package core.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import core.commands.HoverInvoker;
import core.controller.ControllerManager;
import core.leapmotion.LeapMotionManager;
import core.logging.FlightOutput;
import core.oculusrift.OculusRiftManager;
import core.templates.TemplateVersions.Template;
import core.video.HUDColorizer;
import core.video.VideoView;
import de.yadrone.base.navdata.BatteryListener;

/**
 * Startpoint for everything
 * Manages Controller and OculusRiftManager
 *
 */
public class ControlManager {
		
	//Log-View
	private FlightOutput			flightOut;
	
	//Control and Video-View
	private ControlView 			controlView;
	private VideoView 				videoView;
	
	//DeviceManager
	private OculusRiftManager 		curORManager;
	private ControllerManager 		curContManager;
	private LeapMotionManager 	    curLeapManager;
	
	//holds HUDColorizer, so that it can be displayed on Buttonpress
	private HUDColorizer			colorizer;
	
	//initializes HoverInvoker
	private HoverInvoker			hoverInvoker;
	
	public ControlManager(FlightOutput flightOut) {
		
		colorizer = new HUDColorizer();
		hoverInvoker = new HoverInvoker();
		
		this.flightOut = flightOut;
		
		controlView = new ControlView();
		controlView.addStartButtonListener(new StartButtonListener());
		controlView.addLogCheckBoxListener(new LogBoxListener());
		controlView.addColorizerButtonListener(new ColorizerListener());
		
		Control.drone.getNavDataManager().addBatteryListener(new CustomBatteryListener());
		Control.drone.start();
		controlView.startDroneConnectionCheck();
		
		videoView = new VideoView();		
		addVideoViewExitOptions();
	}

	
	/**
	 * starts OculusRiftManager and ControllerManager
	 * @param version = Version to start with
	 * @throws Exception = NullPointerException, if a Device could not be found
	 */
	private void startWithConfig(Template version) throws Exception {
				
		Control.out.println("Running " + version.toString());
		startLeapMotionManager(version);
		startOculusRiftManager(version);		
		startControllerManager(version);
		
		
		
		
		videoView.setVisible(true);
	}
	
	/**
	 * starts OculusRift Manager
	 * @param version = Version to start with
	 * @throws Exception = NullPointerException, if OculusRift Device could not be found
	 */
	private void startOculusRiftManager(Template version) throws Exception {
		if(curORManager == null) {
			curORManager = new OculusRiftManager(version, hoverInvoker);	
			curORManager.start();
		}
		else {
			curORManager.switchVersion(version);
			curORManager.start();
		}
	}
	
	/**
	 * starts Controller Manager
	 * @param version = Version to start with
	 * @throws Exception = NullPointerException, if Controller Device could not be found
	 */
	private void startControllerManager(Template version) throws Exception {
		if(curContManager == null) {
			curContManager = new ControllerManager(version, hoverInvoker);	
			curContManager.start();
		}
		else {
			curContManager.switchVersion(version);
			curContManager.start();
		}
	}
	
	/**
	 * starts Controller Manager
	 * @param version = Version to start with
	 * @throws Exception = NullPointerException, if Controller Device could not be found
	 */
	private void startLeapMotionManager(Template version) throws Exception {
		if(curLeapManager == null) {
			curLeapManager = new LeapMotionManager(version, hoverInvoker);	
			curLeapManager.start();
		}
		else {
			curLeapManager.switchVersion(version);
			curLeapManager.start();
		}
	}
	
	/**
	 * add Exit Options for VideoView
	 */
	private void addVideoViewExitOptions() {
		
		 videoView.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
			        KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Cancel"); 
		 videoView.getRootPane().getActionMap().put("Cancel", new AbstractAction(){ 
				private static final long serialVersionUID = 8762923978776186290L;
		
				public void actionPerformed(ActionEvent e)
		        {          	
					controlView.setVisible(true);
					videoView.setVisible(false);
					curORManager.stop();
					curContManager.stop();
		        }
		 });  
		 
		 videoView.addWindowListener(new WindowAdapter() {
		        public void windowClosing(WindowEvent e) 
		        {     
		        	controlView.setVisible(true);
		        	videoView.setVisible(false);
		        	curORManager.stop();
		        	curContManager.stop();
		        }
		 });	   
		 
	}
	
	/**
	 * stop Manager on Error
	 * @param message
	 */
	private void onError(String message) {
		
		if(curContManager != null) curContManager.stop();
		if(curORManager != null) curORManager.stop();
		showError(message);		
	}
	
	/**
	 * Display Message of Error
	 * @param message
	 */
	private void showError(String message) {
		JOptionPane.showMessageDialog(new JFrame(),
				message,
			    "Error",
			    JOptionPane.ERROR_MESSAGE);		
	}
	
	
	/**
	 * Inner Classes for individual handling of the Events from the ControlView Components  
	 *	 
	 * Start Button Reaction */
	class StartButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			if(Control.isDroneConnected) {	
				
				String configString = controlView.getSelectedItem();
				try{
					switch(configString) {
					
						case "Version1": 		startWithConfig(Template.Version1);											
												break;
											
						case "Version2": 		startWithConfig(Template.Version2);
												break;
												
						default: break;					
					}
					
				} catch(Exception exc) {onError(exc.getMessage());exc.printStackTrace();}
				
			}
		}
		
	}
	
	/** Log Box Reaction */
	class LogBoxListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) { flightOut.setVisible(controlView.isLogBoxChecked()); }
		
	}
	
	/** Colorizer Button Reaction */
	class ColorizerListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) { colorizer.setVisible(true); }
		
	}
	
	/** Listen for BatteryLevelChanged and inform View */
	class CustomBatteryListener implements BatteryListener {

		@Override
		public void batteryLevelChanged(int arg0) {	controlView.setBatteryTo(arg0);	}

		@Override
		public void voltageChanged(int arg0) {}
		
	}
}
