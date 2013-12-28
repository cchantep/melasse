package melasse;

import java.util.Locale;

import java.util.logging.Logger;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.GroupLayout;

import melasse.util.AppUtils;

/**
 * About panel.
 *
 * @author Cedric Chantepie
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

        final String appDescr = AppUtils.
            localizedString("melasse.melasse-examples",
                            locale,
                            "description");

        logger.log(Level.FINER,
                   "application title = {0}, description = {1}",
                   new Object[] { appTitle, appDescr });

        // adds labels
        final JLabel descrLabel = 
            new JLabel("<html>" + appDescr + "</html>", JLabel.LEFT);

        final JLabel titleLabel =
            new JLabel("<html>" + appTitle + "</html>", JLabel.CENTER);

        descrLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        descrLabel.setVerticalTextPosition(JLabel.TOP);

        // Sets layout
        final GroupLayout layout = new GroupLayout(this);

        this.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        // Lays out
        final GroupLayout.Group vgroup = 
            layout.createSequentialGroup().
            addComponent(titleLabel,
                         GroupLayout.PREFERRED_SIZE,
                         GroupLayout.DEFAULT_SIZE,
                         GroupLayout.PREFERRED_SIZE).
            addComponent(descrLabel,
                         GroupLayout.PREFERRED_SIZE,
                         GroupLayout.DEFAULT_SIZE,
                         Short.MAX_VALUE);

        final GroupLayout.Group hgroup = 
            layout.createParallelGroup(GroupLayout.Alignment.LEADING).
            addComponent(titleLabel,
                         0,
                         GroupLayout.DEFAULT_SIZE,
                         Short.MAX_VALUE).
            addComponent(descrLabel,
                         0,
                         GroupLayout.DEFAULT_SIZE,
                         Short.MAX_VALUE);
        
        layout.setVerticalGroup(vgroup);
        layout.setHorizontalGroup(hgroup);
    } // end of setUp
} // end of class AboutPanel
