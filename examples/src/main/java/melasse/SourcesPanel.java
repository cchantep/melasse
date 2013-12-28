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

import javax.swing.GroupLayout;

import melasse.swing.ComboBoxModel;

import melasse.util.AppUtils;

/**
 * Sources panel.
 *
 * @author Cedric Chantepie
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
    private ComboBoxModel<String> filesModel = null;

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

        final Locale locale = (Locale) AppUtils.
            getProperty(this.application, "locale");

        logger.log(Level.FINER,
                   "locale = {0}", locale);

        // Prepare example names
        final String clockTitle = AppUtils.
            localizedString("melasse.ExamplesWindow",
                            locale,
                            "clock.title");

        final String convertTitle = AppUtils.
            localizedString("melasse.ExamplesWindow",
                            locale,
                            "convert.title");

        final String transferTitle = AppUtils.
            localizedString("melasse.ExamplesWindow",
                            locale,
                            "transfer.title");

        // Files combo box
        final String[] titles = new String[] {
            clockTitle, convertTitle, transferTitle
        };
        this.titleList = (List<String>) Arrays.asList(titles);
        this.filesModel = new ComboBoxModel<String>(titles);

        final JComboBox filesBox = new JComboBox(this.filesModel);
        
        // text pane
        this.textPane = new JTextPane();
        this.textPane.setEditable(false);

        final JScrollPane scrollPane = new JScrollPane(this.textPane);

        this.textPane.setContentType("text/html");

        // sets layout
        final GroupLayout layout = new GroupLayout(this);

        this.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        // lays out
        final GroupLayout.Group vgroup = 
            layout.createSequentialGroup().
            addComponent(filesBox,
                         GroupLayout.PREFERRED_SIZE,
                         GroupLayout.DEFAULT_SIZE,
                         GroupLayout.PREFERRED_SIZE).
            addComponent(scrollPane,
                         0,
                         GroupLayout.DEFAULT_SIZE,
                         Short.MAX_VALUE);

        final GroupLayout.Group hgroup = 
            layout.createParallelGroup(GroupLayout.Alignment.CENTER).
            addComponent(filesBox,
                         0,
                         GroupLayout.DEFAULT_SIZE,
                         Short.MAX_VALUE).
            addComponent(scrollPane,
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
