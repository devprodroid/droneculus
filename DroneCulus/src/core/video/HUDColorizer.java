package core.video;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import core.utils.ImgUtil;

/**
 * This is just a Colorizer so you can adjust the HUD-Color
 *
 */
public class HUDColorizer extends JFrame implements ChangeListener, ActionListener{
	private static final long serialVersionUID = -5509536636194171461L;
	
	private JPanel 				panel;	
	private JSlider 			slider1;
	private JSlider 			slider2;
	private JSlider 			slider3;
	
	private JButton 			applyButton;
	
	private JLabel 				colorLabel;
	private ImageIcon 			image;
	
	private int 				blue = 100;
	private int 				green = 255;
	private int 				red = 0;
	
	private Mat 				colorMat = new Mat(20,200,CvType.CV_8UC3);
	
	public static Scalar 		NORMAL_HUD_COLOR = new Scalar(100, 255, 0);
	public static Scalar 		ALERT_HUD_COLOR = new Scalar(0,100,255);
	
	public HUDColorizer() {
		setTitle("HUD-Colorizer");
		setSize(250, 160);
		setLocationRelativeTo(null);
		setResizable(false);
		init();
		setValues();		
		addComponents();				
	}
	
	
	private void init() {
		panel = new JPanel();
		slider1 = new JSlider();
		slider2 = new JSlider();
		slider3 = new JSlider();		
		image = new ImageIcon();
		colorLabel = new JLabel(image);	
		applyButton = new JButton("Apply");
		
		slider1.setToolTipText("Blue");
		slider2.setToolTipText("Green");
		slider3.setToolTipText("Red");
	}
	
	private void setValues() {
		
		colorMat.setTo(new Scalar(blue,green,red));		
		image.setImage(ImgUtil.makeBufferedImage(colorMat));
		
		slider1.setMaximum(255);
		slider2.setMaximum(255);
		slider3.setMaximum(255);		
		slider1.setValue(blue);
		slider2.setValue(green);
		slider3.setValue(red);
	}
	
	private void addComponents() {
		panel.add(slider1);
		panel.add(slider2);
		panel.add(slider3);	
		panel.add(colorLabel);
		panel.add(applyButton);
		this.add(panel);
		
		slider1.addChangeListener(this);
		slider2.addChangeListener(this);
		slider3.addChangeListener(this);		
		applyButton.addActionListener(this);
	}
	
	public JSlider getSlider1() {
		return slider1;
	}

	public void setSlider1(JSlider slider1) {
		this.slider1 = slider1;
	}
	
	public JSlider getSlider2() {
		return slider2;
	}


	public void setSlider2(JSlider slider2) {
		this.slider2 = slider2;
	}	

	public JSlider getSlider3() {
		return slider3;
	}


	public void setSlider3(JSlider slider3) {
		this.slider3 = slider3;
	}
	
	@Override
	public void stateChanged(ChangeEvent arg0) {
		
		JSlider source = (JSlider) arg0.getSource();
		int value = source.getValue();
		
		if(source == slider1) {
			blue = value;
		}
		
		if(source == slider2) {
			green = value;
		}
		
		if(source == slider3) {
			red = value;
		}
		
		colorMat.setTo(new Scalar(blue, green, red));			
		image.setImage(ImgUtil.makeBufferedImage(colorMat));
		panel.repaint();
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		if(arg0.getSource() == applyButton) {
			
			NORMAL_HUD_COLOR = new Scalar(blue, green, red);
			setVisible(false);
		}		
	}



}
