package melasse;

import java.awt.Dimension;

import javax.swing.JSpinner;
import javax.swing.JPanel;
import javax.swing.JLabel;

import melasse.swing.SpinnerNumberModel;

/**
 * Digital clock component.
 *
 * @author Cedric Chantepie ()
 */
public class DigitalClock extends JPanel {
    // --- Properties ---

    /**
     * Digital hours model
     */
    private SpinnerNumberModel hoursModel = null;

    /**
     * Digital minutes model
     */
    private SpinnerNumberModel minutesModel = null;

    /**
     * Digital seconds model
     */
    private SpinnerNumberModel secondsModel = null;

    // --- Constructors ---

    /**
     * No-arg constructor.
     */
    public DigitalClock() {
	this.hoursModel = new SpinnerNumberModel();
	this.minutesModel = new SpinnerNumberModel();
	this.secondsModel = new SpinnerNumberModel();

	JSpinner hoursSpinner = new JSpinner(this.hoursModel);
	JSpinner minutesSpinner = new JSpinner(this.minutesModel);
	JSpinner secondsSpinner = new JSpinner(this.secondsModel);

	add(hoursSpinner);
	add(new JLabel(":", JLabel.CENTER));
	add(minutesSpinner);
	add(new JLabel(":", JLabel.CENTER));
	add(secondsSpinner);

	// sizes
	Dimension spinnerSize = hoursSpinner.getPreferredSize();

	spinnerSize.setSize(spinnerSize.getWidth() * 1.8f,
			    spinnerSize.getHeight());

	hoursSpinner.setPreferredSize(spinnerSize);
	minutesSpinner.setPreferredSize(spinnerSize);
	secondsSpinner.setPreferredSize(spinnerSize);

	// models
	this.hoursModel.setValue(new Integer(0));
	this.hoursModel.setMinimum(new Integer(0));
	this.hoursModel.setMaximum(new Integer(23));
	this.hoursModel.setStepSize(new Integer(1));

	this.minutesModel.setValue(new Integer(0));
	this.minutesModel.setMinimum(new Integer(0));
	this.minutesModel.setMaximum(new Integer(59));
	this.minutesModel.setStepSize(new Integer(1));

	this.secondsModel.setValue(new Integer(0));
	this.secondsModel.setMinimum(new Integer(0));
	this.secondsModel.setMaximum(new Integer(59));
	this.secondsModel.setStepSize(new Integer(1));
    } // end of <init>

    // --- Properties accessors ---

    /**
     * Returns hour model.
     */
    public SpinnerNumberModel getHoursModel() {
	return this.hoursModel;
    } // end of getHoursModel

    /**
     * Returns hour model.
     */
    public SpinnerNumberModel getMinutesModel() {
	return this.minutesModel;
    } // end of getMinutesModel

    /**
     * Returns hour model.
     */
    public SpinnerNumberModel getSecondsModel() {
	return this.secondsModel;
    } // end of getSecondsModel
} // end of class DigitalClock
