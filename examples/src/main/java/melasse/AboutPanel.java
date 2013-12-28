package melasse;

import java.util.Locale;

import java.util.logging.Logger;
import java.util.logging.Level;


import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jdesktop.layout.GroupLayout;

import melasse.util.AppUtils;

/**
 * About panel.
 *
 * @author Cedric Chantepie ()
 */
public final class AboutPanel extends JPanel {
    // --- Properties ---

    /**
     * Owning application
     */
    private Object application = null;

    /**
     * Application logger
     */
    private Logger logger = null;

    // --- Constructors ---

    /**
     * Bulk constructor.
     *
     * @param application Owning application
     */
    protected AboutPanel(Object application) {
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
    protected void setUp() {
	logger.fine("Will set up about panel");

	Locale locale = (Locale) AppUtils.
	    getProperty(this.application, "locale");

	logger.log(Level.FINER,
		   "locale = {0}", locale);

	String appTitle = AppUtils.
	    localizedString("melasse.melasse-examples",
			    locale,
			    "title");

	String appDescr = AppUtils.
	    localizedString("melasse.melasse-examples",
			    locale,
			    "description");

	logger.log(Level.FINER,
		   "application title = {0}, description = {1}",
		   new Object[] { appTitle, appDescr });

	// adds labels
	JLabel descrLabel = 
	    new JLabel("<html>" + appDescr + "</html>", 
		       JLabel.LEFT);

	JLabel titleLabel =
	    new JLabel("<html>" + appTitle + "</html>", 
		       JLabel.CENTER);

	descrLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
	descrLabel.setVerticalTextPosition(JLabel.TOP);

	// Sets layout
	GroupLayout layout = new GroupLayout(this);

	this.setLayout(layout);

	layout.setAutocreateGaps(true);
	layout.setAutocreateContainerGaps(true);

	// Lays out
	GroupLayout.Group vgroup = 
	    layout.createSequentialGroup().
	    add(titleLabel,
		GroupLayout.PREFERRED_SIZE,
		GroupLayout.DEFAULT_SIZE,
		GroupLayout.PREFERRED_SIZE).
	    add(descrLabel,
		GroupLayout.PREFERRED_SIZE,
		GroupLayout.DEFAULT_SIZE,
		Short.MAX_VALUE);

	GroupLayout.Group hgroup = 
	    layout.createParallelGroup(GroupLayout.LEADING).
	    add(titleLabel,
		0,
		GroupLayout.DEFAULT_SIZE,
		Short.MAX_VALUE).
	    add(descrLabel,
		0,
		GroupLayout.DEFAULT_SIZE,
		Short.MAX_VALUE);
	
	layout.setVerticalGroup(vgroup);
	layout.setHorizontalGroup(hgroup);
    } // end of setUp
} // end of class AboutPanel
