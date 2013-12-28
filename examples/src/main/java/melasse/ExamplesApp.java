package melasse;

import java.util.Locale;

import java.util.logging.Logger;

/**
 * Melasse examples application.
 *
 * @author Cedric Chantepie ()
 * @todo Transformer
 * @todo Dependent
 */
public final class ExamplesApp {
    // --- Shared ---

    /**
     * Application logger
     */
    private static Logger logger = Logger.
	getLogger(ExamplesApp.class.getName());

    // --- Properties ---

    /**
     * Application locale
     */
    private Locale locale = null;

    /**
     * Main window of examples application
     */
    private ExamplesWindow mainWin = null;

    // ---

    /**
     * Application main entry point.
     */
    public static void main(String[] args) {
	ExamplesApp app = new ExamplesApp();

	// @todo medium Get from arguments or env
	app.locale = /*Locale.FRANCE*/Locale.ENGLISH;

	app.start();
    } // end of <main>

    // --- Constructors ---

    /**
     * No-arg constructor.
     */
    private ExamplesApp() {
    } // end of ExamplesApp

    // --- Properties accessors ---

    /**
     * Returns application logger.
     */
    public Logger getLogger() {
	return logger;
    } // end of getLogger

    /**
     * Returns application locale.
     */
    public Locale getLocale() {
	return this.locale;
    } // end of getLocale

    // ---

    /**
     * Launch examples application.
     */
    private void start() {
	this.mainWin = new ExamplesWindow(this);

	this.mainWin.setUp();

	this.mainWin.setVisible(true);
    } // end of start
} // end of class ExamplesApp
