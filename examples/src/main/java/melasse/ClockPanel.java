package melasse;

import java.util.Locale;

import java.util.logging.Logger;
import java.util.logging.Level;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.jdesktop.layout.GroupLayout;

import melasse.util.AppUtils;

/**
 * Clock panel.
 *
 * @author Cedric Chantepie ()
 */
public final class ClockPanel extends JPanel {
    // --- Properties ---

    /**
     * Owning application
     */
    private Object application = null;

    /**
     * Application logger
     */
    private Logger logger = null;

    /**
     * Analog clock
     */
    private AnalogClock analogClock = null;

    /**
     * Digital clock
     */
    private DigitalClock digitalClock = null;

    // --- Constructors ---

    /**
     * Bulk constructor.
     *
     * @param application Owning application
     */
    protected ClockPanel(Object application) {
	super();

	if (application == null) {
	    throw new IllegalArgumentException("Owner not specified");
	} // end of if

	// ---

	this.application = application;
	this.logger = AppUtils.getLogger(this.application);
    } // end of <init>

    // ---

    /**
     * Sets up UI.
     */
    protected void setUpUI() {
	logger.fine("Will set up clock panel");

	Locale locale = (Locale) AppUtils.
	    getProperty(this.application, "locale");

	logger.log(Level.FINER,
		   "locale = {0}", locale);

	// prepare analog clock
	this.analogClock = new AnalogClock();

	Image clockImg = 
	    Toolkit.getDefaultToolkit().
	    getImage(this.getClass().
		     getResource("/melasse/" +
				 "clock.jpg"));

	this.analogClock.setBackgroundImage(clockImg);
	
	// prepare labels
	String analogTitle = AppUtils.
	    localizedString("melasse.ClockPanel",
			    locale,
			    "analog.label");

	String digitalTitle = AppUtils.
	    localizedString("melasse.ClockPanel",
			    locale,
			    "digital.label");

	JPanel analog = new JPanel();
	JPanel digital = new JPanel();

	analog.setBorder(BorderFactory.
			 createTitledBorder(analogTitle));

	digital.setBorder(BorderFactory.
			 createTitledBorder(digitalTitle));

	// explanation sub-panel
	ExplanationPanel explain = 
	    new ExplanationPanel(this.application);

	explain.setTitleKey("clock.title");
	explain.setBodyKey("clock.body");
	explain.setCodeKey("clock.code");

	explain.setUp();

	// analog sub-panel
	analog.add(this.analogClock);

	// digital sub-panel
	this.digitalClock = new DigitalClock();

	digital.add(this.digitalClock);

	// sets layout
	GroupLayout layout = new GroupLayout(this);

	this.setLayout(layout);

	layout.setAutocreateGaps(true);
	layout.setAutocreateContainerGaps(true);

	// lays out
	GroupLayout.Group vgroup = 
	    layout.createParallelGroup(GroupLayout.LEADING).
	    add(layout.createSequentialGroup().
		add(analog,
		    GroupLayout.PREFERRED_SIZE,
		    GroupLayout.DEFAULT_SIZE,
		    GroupLayout.PREFERRED_SIZE).
		add(digital,
		    GroupLayout.PREFERRED_SIZE,
		    GroupLayout.DEFAULT_SIZE,
		    Short.MAX_VALUE)).
	    add(explain,
		0,
		GroupLayout.DEFAULT_SIZE,
		Short.MAX_VALUE);

	GroupLayout.Group hgroup = 
	    layout.createSequentialGroup().
	    add(layout.createParallelGroup(GroupLayout.CENTER).
		add(analog,
		    GroupLayout.PREFERRED_SIZE,
		    GroupLayout.DEFAULT_SIZE,
		    GroupLayout.PREFERRED_SIZE).
		add(digital,
		    GroupLayout.PREFERRED_SIZE,
		    GroupLayout.DEFAULT_SIZE,
		    GroupLayout.PREFERRED_SIZE)).
	    add(explain,
		0,
		GroupLayout.DEFAULT_SIZE,
		Short.MAX_VALUE);
	
	layout.setVerticalGroup(vgroup);
	layout.setHorizontalGroup(hgroup);

	layout.linkSize(new Component[] {
	    analog, digital
	}, GroupLayout.HORIZONTAL);
    } // end of setUpUI

    /**
     * Sets up bindings.
     */
    protected void setUpBindings() {
	Binder.bind("seconds", this.analogClock,
		    "secondsModel.value", this.digitalClock,
		    new BindingOptionMap());

	Binder.bind("minutes", this.analogClock,
		    "minutesModel.value", this.digitalClock,
		    new BindingOptionMap());

	Binder.bind("hours", this.analogClock,
		    "hoursModel.value", this.digitalClock,
		    new BindingOptionMap());

    } // end of setUpBindings
} // end of class ClockPanel
