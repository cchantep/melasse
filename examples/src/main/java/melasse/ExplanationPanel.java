package melasse;

import java.util.Locale;

import java.util.logging.Logger;
import java.util.logging.Level;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JLabel;

import javax.swing.GroupLayout;

import melasse.util.AppUtils;

/**
 * Explanation panel (one per example).
 *
 * @author Cedric Chantepie
 */
public final class ExplanationPanel extends JPanel {
    // --- Properties ---
    
    /**
     * Owing application
     */
    private final Object application;

    /**
     * Application logger
     */
    private final Logger logger;

    /**
     * Scroll pane
     */
    private JScrollPane pane = null;

    /**
     */
    private String titleKey = null;

    /**
     */
    private String bodyKey = null;

    /**
     */
    private String codeKey = null;

    /**
     */
    private String body = null;

    /**
     */
    private String code = null;

    /**
     */
    private JLabel bodyLabel = null;

    /**
     */
    private JLabel codeLabel = null;
    
    // --- Constructors ---

    /**
     * Bulk constructor.
     *
     * @param application Owing application
     */
    public ExplanationPanel(final Object application) {
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

        final Locale locale = (Locale) AppUtils.
            getProperty(this.application, "locale");

        logger.log(Level.FINER, "locale = {0}, title key = {1}, body key = {2}, code key = {2}", new Object[] { locale, titleKey, bodyKey, codeKey });

        final String title = AppUtils.
            localizedString("melasse.ExplanationPanel", locale, titleKey);

        this.body = AppUtils.
            localizedString("melasse.ExplanationPanel", locale, bodyKey);

        this.code = AppUtils.
            localizedString("melasse.ExplanationPanel", locale, codeKey);
        
        logger.log(Level.FINER, "title = {0}, body = {1}, code = {2}",
                   new Object[] { title, body, code });

        setBorder(BorderFactory.createTitledBorder(title));

        final JPanel content = new JPanel();
        this.pane = new JScrollPane(content);

        pane.setBackground(java.awt.Color.WHITE);
        pane.setBorder(null);
        add(pane);

        final GroupLayout layout = new GroupLayout(content);

        content.setBorder(null);
        content.setLayout(layout);
        content.setBackground(java.awt.Color.WHITE);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        this.bodyLabel = 
            new JLabel("<html><body><font face=\"Times New Roman\">" + 
                       this.body + "</font></body></html>");

        this.codeLabel = 
            new JLabel("<html><code>" + this.code + "</code></html>");

        codeLabel.setForeground(java.awt.Color.BLUE);

        // Lays out
        final GroupLayout.Group vgroup = 
            layout.createSequentialGroup().
            addComponent(this.bodyLabel,
                         GroupLayout.PREFERRED_SIZE,
                         GroupLayout.DEFAULT_SIZE,
                         GroupLayout.PREFERRED_SIZE).
            addComponent(this.codeLabel,
                         GroupLayout.PREFERRED_SIZE,
                         GroupLayout.DEFAULT_SIZE,
                         Short.MAX_VALUE);

        final GroupLayout.Group hgroup = 
            layout.createParallelGroup(GroupLayout.Alignment.CENTER).
            addComponent(this.bodyLabel, 0,
                         GroupLayout.DEFAULT_SIZE,
                         Short.MAX_VALUE).
            addComponent(this.codeLabel, 0,
                         GroupLayout.DEFAULT_SIZE,
                         Short.MAX_VALUE);

        layout.setVerticalGroup(vgroup);
        layout.setHorizontalGroup(hgroup);

        // Bindings
        Binder.bind("size", this, "componentSize", this, 
                    BindingOptionMap.targetModeOptions);

    } // end of setUp

    /**
     * Takes |size| of this component on screen.
     */
    public void setComponentSize(final Dimension size) {
        final java.awt.Insets insets = getBorder().getBorderInsets(this);
        final int w = size.width - insets.left - insets.right;
        final int h = size.height - insets.top - insets.bottom;

        this.pane.setPreferredSize(new Dimension(w, h));

        System.out.println("W => " + this.pane.getViewport().getWidth());

        this.bodyLabel.setText("<html><body style=\"width:"+(w-100)+"px\"><font face=\"Times New Roman\">" + this.body + "</font></body></html>");

        this.codeLabel.setText("<html><code style=\"width:"+w+"px\">" + this.code + "</code></html>");
        
    } // end of setComponentSize
} // end of class ExplanationPanel
