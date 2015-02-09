package core.control;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import core.templates.TemplateVersions;
import core.utils.Config;
import edu.cmu.relativelayout.Binding;
import edu.cmu.relativelayout.BindingFactory;
import edu.cmu.relativelayout.RelativeConstraints;
import edu.cmu.relativelayout.RelativeLayout;

/**
 * holds every Component of Control-View and grants Access to them
 *
 */
public class ControlContainer extends JPanel {

	private static final long serialVersionUID = -7769593462458318702L;

	// Start-Button and Version-ComboBox
	private JButton btnOK;
	private JComboBox<String> comboBox;

	// initialize Label
	private JLabel initLabel;

	// Image for Template
	private ImageIcon image;
	private JLabel imageLabel;

	// log CheckBox
	private JCheckBox logCheckBox;

	// Button to show Colorizer
	private JButton colorizerButton;

	// ProgressBar for Battery State
	private JProgressBar battery;

	// Constraints for Relative Layout
	private RelativeConstraints comboBoxConstraints;
	private RelativeConstraints buttonOKConstraints;
	private RelativeConstraints imageConstraints;
	private RelativeConstraints initLabelConsraints;
	private RelativeConstraints batteryConstraints;
	private RelativeConstraints logCheckBoxConstraints;
	private RelativeConstraints colorizerButtonConstraints;

	// Bindings for Relative Layout
	private Binding leftEdge;
	private Binding rightEdge;
	private Binding topEdge;
	private Binding bottomEdge;
	private Binding rightofCBOX;
	private Binding leftofLogCheck;
	private Binding leftofColorizerButton;
	private Binding directlyUnderCBox;

	public ControlContainer() {
		super(new RelativeLayout());
		initComponents();
		initImage();
		initConstraints();
		generateBindings(new BindingFactory());
		addBindings();
		addComponents();
	}

	/** initializes every Component */
	private void initComponents() {

		String comboBoxList[] = TemplateVersions.templatesToString();

		comboBox = new JComboBox<String>(comboBoxList);
		btnOK = new JButton("Start");
		battery = new JProgressBar();
		initLabel = new JLabel();
		imageLabel = new JLabel();
		logCheckBox = new JCheckBox("Show Log?");
		colorizerButton = new JButton("Show Colorizer");
		addComboBoxListener();
	}

	/** initializes Image Components */
	private void initImage() {

		image = new ImageIcon(getClass().getResource(
				"/resources/Xbox-Rift-Template01.png"));
		Border b1 = new BevelBorder(BevelBorder.LOWERED, Color.LIGHT_GRAY,
				Color.LIGHT_GRAY);
		imageLabel = new JLabel("", image, JLabel.CENTER);
		imageLabel.setBorder(b1);
	}

	/** initializes Constraints for Relative Layout */
	private void initConstraints() {

		comboBoxConstraints = new RelativeConstraints();
		buttonOKConstraints = new RelativeConstraints();
		imageConstraints = new RelativeConstraints();
		initLabelConsraints = new RelativeConstraints();
		batteryConstraints = new RelativeConstraints();
		logCheckBoxConstraints = new RelativeConstraints();
		colorizerButtonConstraints = new RelativeConstraints();
	}

	/** generates Bindings for Relative Layout */
	private void generateBindings(BindingFactory bf) {

		leftEdge = bf.leftEdge();
		rightEdge = bf.rightEdge();
		topEdge = bf.topEdge();
		bottomEdge = bf.bottomEdge();

		rightofCBOX = bf.rightOf(comboBox);
		leftofLogCheck = bf.leftOf(logCheckBox);
		directlyUnderCBox = bf.below(comboBox);
		leftofColorizerButton = bf.leftOf(colorizerButton);
	}

	/** adds Bindings to Constraints */
	private void addBindings() {

		comboBoxConstraints.addBinding(leftEdge);
		comboBoxConstraints.addBinding(topEdge);
		buttonOKConstraints.addBinding(rightofCBOX);
		buttonOKConstraints.addBinding(topEdge);
		buttonOKConstraints.addBinding(leftofColorizerButton);
		batteryConstraints.addBinding(rightEdge);
		batteryConstraints.addBinding(bottomEdge);
		imageConstraints.addBinding(leftEdge);
		imageConstraints.addBinding(directlyUnderCBox);
		initLabelConsraints.addBinding(bottomEdge);
		initLabelConsraints.addBinding(leftEdge);
		logCheckBoxConstraints.addBinding(topEdge);
		logCheckBoxConstraints.addBinding(rightEdge);
		colorizerButtonConstraints.addBinding(leftofLogCheck);
		colorizerButtonConstraints.addBinding(topEdge);
	}

	/** adds all Components to Panel */
	private void addComponents() {

		add(comboBox, comboBoxConstraints);
		add(btnOK, buttonOKConstraints);
		add(logCheckBox, logCheckBoxConstraints);
		add(imageLabel, imageConstraints);
		add(battery, batteryConstraints);
		add(initLabel, initLabelConsraints);
		add(colorizerButton, colorizerButtonConstraints);
	}

	/**
	 * listen on Version changed ComboBox, to display correct Image for Template
	 */
	private void addComboBoxListener() {
		comboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				String selected = (String) comboBox.getSelectedItem();

				switch (selected) {

				case "Version1":
					image = new ImageIcon(getClass().getResource(
							"/resources/Xbox-Rift-Template01.png"));
					break;

				case "Version2":
					image = new ImageIcon(getClass().getResource(
							"/resources/Xbox-Rift-Template02.png"));
					break;
				case "LeapMotion": //TODO: Insert a new Image
					image = new ImageIcon(getClass().getResource(
							"/resources/Xbox-Rift-Template02.png"));
					break;
				case "LeapMotionHMD":
					image = new ImageIcon(getClass().getResource(
							"/resources/Xbox-Rift-Template02.png"));
					break;

				default:
					break;
				}

				imageLabel.setIcon(image);

			}
		});
	}

	/** returns Version ComboBox */
	public JComboBox<String> getComboBox() {
		return this.comboBox;
	}

	/** returns Start Button */
	public JButton getButton() {
		return this.btnOK;
	}

	/** returns Log CheckBox */
	public JCheckBox getLogCheckBox() {
		return this.logCheckBox;
	}

	/** returns Image Width */
	public int getImageWidth() {
		return image.getIconWidth();
	}

	/** returns Image Height */
	public int getImageHeight() {
		return image.getIconHeight();
	}

	/**
	 * sets Text of InitLabel to
	 * 
	 * @param text
	 *            = new String of InitLabel
	 * @param color
	 *            = Color of Text
	 */
	public void setInitLabel(String text, Color color) {
		initLabel.setText(text);
		initLabel.setForeground(color);
	}

	/**
	 * sets Battery Progress to new value
	 * 
	 * @param batteryLoad
	 *            = new Value
	 */
	public void setBattery(int batteryLoad) {
		battery.setStringPainted(true);
		setBatteryColor(batteryLoad);
		battery.setValue(batteryLoad);
		battery.setString(batteryLoad + "%");
	}

	/** color of Battery ProgressBar */
	private void setBatteryColor(int batteryLoad) {

		if (batteryLoad < Config.BATTERY_ALERT_VALUE) {
			battery.setForeground(Color.RED);
		}
	}

	/** returns Button for Colorizer Display */
	public JButton getColorizerButton() {
		return colorizerButton;
	}

}
