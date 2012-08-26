package melasse;

import java.util.Locale;

import java.util.logging.Logger;
import java.util.logging.Level;

import java.awt.Dimension;

import javax.swing.JTabbedPane;
import javax.swing.JFrame;

import melasse.util.AppUtils;

/**
 * Main examples window.
 *
 * @author Cedric Chantepie ()
 */
public final class ExamplesWindow extends JFrame {
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
    public ExamplesWindow(Object application) {
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
	logger.fine("Will set up examples window");

	Locale locale = (Locale) AppUtils.
	    getProperty(this.application, "locale");

	logger.log(Level.FINER,
		   "locale = {0}", locale);

	// window.title
	String winTitle = AppUtils.
	    localizedString("melasse.ExamplesWindow",
			    locale,
			    "window.title");

	logger.log(Level.FINER,
		   "window title = {0}", winTitle);
	    
	setTitle(winTitle);
	setPreferredSize(new Dimension(640, 520));
	setSize(getPreferredSize());
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	JTabbedPane mainPane = new JTabbedPane(JTabbedPane.TOP);

	setContentPane(mainPane);

	// About panel
	String aboutTitle = AppUtils.
	    localizedString("melasse.ExamplesWindow",
			    locale,
			    "about.title");

	String aboutDescr = AppUtils.
	    localizedString("melasse.ExamplesWindow",
			    locale,
			    "about.description");

	int aboutMnemonic = 
	    AppUtils.
	    localizedKey("melasse.ExamplesWindow",
			 locale,
			 "about.tab.mnemonic");

	logger.log(Level.FINER,
		   "about title = {0}, description = {1}, tab mnemonic = {2}",
		   new Object[] {
		       aboutTitle,
		       aboutDescr,
		       new Integer(aboutMnemonic)
		   });

	AboutPanel aboutPanel = new AboutPanel(this.application);
	int tab = 0;

	mainPane.addTab(aboutTitle, aboutPanel);
	mainPane.setToolTipTextAt(tab, aboutDescr);
	mainPane.setMnemonicAt(tab++, aboutMnemonic);

	aboutPanel.setUp();

	pack();

	setLocationRelativeTo(null);

	// Lazilly run other tabs setup
	Runnable lazy = new Runnable() {
		public void run() {
		    lazySetUp();
		} 
	    };

	Thread th = new Thread(lazy);

	th.start();
    } // end of setUp

    /**
     * Lazy setup.
     */
    private void lazySetUp() {
	JTabbedPane contentPane = (JTabbedPane) getContentPane();
	Locale locale = (Locale) AppUtils.
	    getProperty(this.application, "locale");	
	int tab = 1;

	// Clock panel
	String clockTitle = AppUtils.
	    localizedString("melasse.ExamplesWindow",
			    locale,
			    "clock.title");

	String clockDescr = AppUtils.
	    localizedString("melasse.ExamplesWindow",
			    locale,
			    "clock.description");

	int clockMnemonic = 
	    AppUtils.
	    localizedKey("melasse.ExamplesWindow",
			 locale,
			 "clock.tab.mnemonic");

	logger.log(Level.FINER,
		   "clock title = {0}, description = {1}, tab mnemonic = {2}",
		   new Object[] {
		       clockTitle,
		       clockDescr,
		       new Integer(clockMnemonic)
		   });

	ClockPanel clockPanel = new ClockPanel(this.application);

	contentPane.addTab(clockTitle, clockPanel);
	contentPane.setToolTipTextAt(tab, clockDescr);
	contentPane.setMnemonicAt(tab++, clockMnemonic);

	clockPanel.setUpUI();

	// Convert panel
	String convertTitle = AppUtils.
	    localizedString("melasse.ExamplesWindow",
			    locale,
			    "convert.title");

	String convertDescr = AppUtils.
	    localizedString("melasse.ExamplesWindow",
			    locale,
			    "convert.description");

	int convertMnemonic = 
	    AppUtils.
	    localizedKey("melasse.ExamplesWindow",
			 locale,
			 "convert.tab.mnemonic");

	logger.log(Level.FINER,
		   "convert title = {0}, description = {1}, tab mnemonic = {2}",
		   new Object[] {
		       convertTitle,
		       convertDescr,
		       new Integer(convertMnemonic)
		   });

	ConvertPanel convertPanel = new ConvertPanel(this.application);

	contentPane.addTab(convertTitle, convertPanel);
	contentPane.setToolTipTextAt(tab, convertDescr);
	contentPane.setMnemonicAt(tab++, convertMnemonic);

	convertPanel.setUpUI();

	// Transfer panel
	String transferTitle = AppUtils.
	    localizedString("melasse.ExamplesWindow",
			    locale,
			    "transfer.title");

	String transferDescr = AppUtils.
	    localizedString("melasse.ExamplesWindow",
			    locale,
			    "transfer.description");

	int transferMnemonic = 
	    AppUtils.
	    localizedKey("melasse.ExamplesWindow",
			 locale,
			 "transfer.tab.mnemonic");

	logger.log(Level.FINER,
		   "transfer title = {0}, description = {1}, tab mnemonic = {2}",
		   new Object[] {
		       transferTitle,
		       transferDescr,
		       new Integer(transferMnemonic)
		   });

	TransferPanel transferPanel = new TransferPanel(this.application);

	contentPane.addTab(transferTitle, transferPanel);
	contentPane.setToolTipTextAt(tab, transferDescr);
	contentPane.setMnemonicAt(tab++, transferMnemonic);

	transferPanel.setUpUI();

	// Sources panel
	String sourcesTitle = AppUtils.
	    localizedString("melasse.ExamplesWindow",
			    locale,
			    "sources.title");

	String sourcesDescr = AppUtils.
	    localizedString("melasse.ExamplesWindow",
			    locale,
			    "sources.description");

	int sourcesMnemonic = 
	    AppUtils.
	    localizedKey("melasse.ExamplesWindow",
			 locale,
			 "sources.tab.mnemonic");

	logger.log(Level.FINER,
		   "sources title = {0}, description = {1}, tab mnemonic = {2}",
		   new Object[] {
		       sourcesTitle,
		       sourcesDescr,
		       new Integer(sourcesMnemonic)
		   });

	SourcesPanel sourcesPanel = new SourcesPanel(this.application);

	contentPane.addTab(sourcesTitle, sourcesPanel);
	contentPane.setToolTipTextAt(tab, sourcesDescr);
	contentPane.setMnemonicAt(tab++, sourcesMnemonic);

	sourcesPanel.setUpUI();

	// ---

	pack();

	clockPanel.setUpBindings();
	convertPanel.setUpBindings();
	transferPanel.setUpBindings();
	sourcesPanel.setUpBindings();
    } // end of lazySetUp
} // end of class ExamplesWindow
