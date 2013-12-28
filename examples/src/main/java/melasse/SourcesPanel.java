package melasse;

import java.util.Arrays;
import java.util.Locale;
import java.util.List;

import java.util.logging.Logger;
import java.util.logging.Level;

import java.text.NumberFormat;

import java.net.URL;

import java.awt.Dimension;
import java.awt.Component;
import java.awt.Color;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jdesktop.layout.GroupLayout;

import melasse.swing.ComboBoxModel;

import melasse.util.AppUtils;

/**
 * Sources panel.
 *
 * @author Cedric Chantepie ()
 */
public final class SourcesPanel extends JPanel {
    // --- Shared ---

    /**
     * Examples url
     */
    private static final URL[] exampleUrls = new URL[3];

    static {
	try {
	    exampleUrls[0] = SourcesPanel.class.
		getResource("/melasse/ClockPanel.html");

	    exampleUrls[1] = SourcesPanel.class.
		getResource("/melasse/ConvertPanel.html");

	    exampleUrls[2] = SourcesPanel.class.
		getResource("/melasse/TransferPanel.html");

	} catch (Exception e) {
	    e.printStackTrace();
	} // end of catch
    } // end of <static>

    // --- Properties ---

    /**
     * Owning application
     */
    private Object application = null;

    /**
     * Combo box model
     */
    private ComboBoxModel filesModel = null;

    /**
     * Application logger
     */
    private Logger logger = null;

    /**
     * Title list
     */
    private List<String> titleList = null;

    /**
     * Display text pane
     */
    private JTextPane textPane = null;

    // --- Constructors ---

    /**
     * Bulk constructor.
     *
     * @param application Owning application
     */
    protected SourcesPanel(Object application) {
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
     * Sets selected example.
     */
    public void setSelectedExample(String selected) {
	int idx = this.titleList.indexOf(selected);
	URL selectedUrl = exampleUrls[idx];

	try {
	    this.textPane.setPage(selectedUrl);
	} catch (Exception e) {
	    logger.log(Level.WARNING,
		       "Fails to open example page", e);

	} // end of catch
    } // end of setSelectedExample

    /**
     * Returns selected example.
     */
    public String getSelectedExample() {
	return (String) this.filesModel.getSelectedItem();
    } // end of getSelectedExample

    /**
     * Sets up UI.
     */
    protected void setUpUI() {
	logger.fine("Will set up convert panel");

	Locale locale = (Locale) AppUtils.
	    getProperty(this.application, "locale");

	logger.log(Level.FINER,
		   "locale = {0}", locale);

	// Prepare example names
	String clockTitle = AppUtils.
	    localizedString("melasse.ExamplesWindow",
			    locale,
			    "clock.title");

	String convertTitle = AppUtils.
	    localizedString("melasse.ExamplesWindow",
			    locale,
			    "convert.title");

	String transferTitle = AppUtils.
	    localizedString("melasse.ExamplesWindow",
			    locale,
			    "transfer.title");

	// Files combo box
	String[] titles = new String[] {
	    clockTitle, convertTitle, transferTitle
	};
	this.titleList = (List<String>) Arrays.asList(titles);
	this.filesModel = new ComboBoxModel(titles);

	JComboBox filesBox = new JComboBox(this.filesModel);
	
	// text pane
	this.textPane = new JTextPane();
	JScrollPane scrollPane = new JScrollPane(this.textPane);

	this.textPane.setContentType("text/html");

	// sets layout
	GroupLayout layout = new GroupLayout(this);

	this.setLayout(layout);

	layout.setAutocreateGaps(true);
	layout.setAutocreateContainerGaps(true);

	// lays out
	GroupLayout.Group vgroup = 
	    layout.createSequentialGroup().
	    add(filesBox,
		GroupLayout.PREFERRED_SIZE,
		GroupLayout.DEFAULT_SIZE,
		GroupLayout.PREFERRED_SIZE).
	    add(scrollPane,
		0,
		GroupLayout.DEFAULT_SIZE,
		Short.MAX_VALUE);

	GroupLayout.Group hgroup = 
	    layout.createParallelGroup(GroupLayout.CENTER).
	    add(filesBox,
		0,
		GroupLayout.DEFAULT_SIZE,
		Short.MAX_VALUE).
	    add(scrollPane,
		0,
		GroupLayout.DEFAULT_SIZE,
		Short.MAX_VALUE);
	
	layout.setVerticalGroup(vgroup);
	layout.setHorizontalGroup(hgroup);
    } // end of setUpUI

    /**
     * Sets up bindings.
     */
    protected void setUpBindings() {
	Binder.bind("selectedItem", this.filesModel,
		    "selectedExample", this,
		    BindingOptionMap.targetModeOptions);

    } // end of setUpBindings
} // end of class SourcesPanel
