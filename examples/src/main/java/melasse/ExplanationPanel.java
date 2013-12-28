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
 * Explanation panel (one per example).
 *
 * @author Cedric Chantepie ()
 */
public final class ExplanationPanel extends JPanel {
    // --- Properties ---
    
    /**
     * Owing application
     */
    private Object application = null;

    /**
     * Application logger
     */
    private Logger logger = null;

    /**
     */
    private String titleKey = null;

    /**
     */
    private String bodyKey = null;

    /**
     */
    private String codeKey = null;
    
    // --- Constructors ---

    /**
     * Bulk constructor.
     *
     * @param application Owing application
     */
    public ExplanationPanel(Object application) {
	super();

	if (application == null) {
	    throw new IllegalArgumentException("Owner not specified");
	} // end of if

	// ---

	this.application = application;
	this.logger = AppUtils.getLogger(this.application);
    } // end of <init>

    // --- Properties accessors ---

    /**
     * Sets |key| to find localized string for title.
     *
     * @param key New title key
     */
    public void setTitleKey(String key) {
	this.titleKey = key;
    } // end of setTitleKey

    /**
     * Sets |key| to find localized string for body text.
     *
     * @param key New body textkey
     */
    public void setBodyKey(String key) {
	this.bodyKey = key;
    } // end of setBodyKey

    /**
     * Sets |key| to find string for code.
     *
     * @param key New code key
     */
    public void setCodeKey(String key) {
	this.codeKey = key;
    } // end of setCodeKey

    // ---

    /**
     * Sets up UI
     */
    protected void setUp() {
	logger.fine("Will set up explanation panel");

	Locale locale = (Locale) AppUtils.
	    getProperty(this.application, "locale");

	logger.log(Level.FINER,
		   "locale = {0}, title key = {1}, body key = {2}, code key = {2}", new Object[] { locale, titleKey, bodyKey, codeKey });

	String title = AppUtils.
	    localizedString("melasse.ExplanationPanel",
			    locale,
			    titleKey);

	String body = AppUtils.
	    localizedString("melasse.ExplanationPanel",
			    locale,
			    bodyKey);

	String code = AppUtils.
	    localizedString("melasse.ExplanationPanel",
			    locale,
			    codeKey);
	
	logger.log(Level.FINER,
		   "title = {0}, body = {1}, code = {2}",
		   new Object[] { title, body, code });

	setBorder(BorderFactory.createTitledBorder(title));

	GroupLayout layout = new GroupLayout(this);

	this.setLayout(layout);

	layout.setAutocreateGaps(true);
	layout.setAutocreateContainerGaps(true);

	JLabel bodyLabel = new JLabel("<html><font face=\"Times New Roman\">" + body + "</font></html>");

	JLabel codeLabel = new JLabel("<html><code>" + code + 
				      "</code></html>");

	codeLabel.setForeground(java.awt.Color.BLUE);

	// Lays out
	GroupLayout.Group vgroup = 
	    layout.createSequentialGroup().
	    add(bodyLabel,
		GroupLayout.PREFERRED_SIZE,
		GroupLayout.DEFAULT_SIZE,
		GroupLayout.PREFERRED_SIZE).
	    add(codeLabel,
		GroupLayout.PREFERRED_SIZE,
		GroupLayout.DEFAULT_SIZE,
		Short.MAX_VALUE);

	GroupLayout.Group hgroup = 
	    layout.createParallelGroup(GroupLayout.CENTER).
	    add(bodyLabel,
		0,
		GroupLayout.DEFAULT_SIZE,
		Short.MAX_VALUE).
	    add(codeLabel,
		0,
		GroupLayout.DEFAULT_SIZE,
		Short.MAX_VALUE);

	layout.setVerticalGroup(vgroup);
	layout.setHorizontalGroup(hgroup);
    } // end of setUp
} // end of class ExplanationPanel
