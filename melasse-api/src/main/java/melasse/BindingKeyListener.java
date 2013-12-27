package melasse;

import java.util.logging.Level;

import java.awt.TextComponent;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import javax.swing.text.JTextComponent;

/**
 * Key listener binding value from text component value to an object property.
 * Used when binding options include CONTINUOUSLY_UPDATE_VALUE
 *
 * @author Cedric Chantepie 
 * @see TextBindingKey#CONTINUOUSLY_UPDATE_VALUE
 */
class BindingKeyListener 
    extends BindingListenerSupport 
    implements KeyListener {

    // --- Constructors ---

    /**
     * Bulk constructor.
     *
     * @param setter Setter used to propagate value
     */
    protected BindingKeyListener(final Setter setter, 
                                 final BindingOptionMap options) {

	super(setter, options);
    } // end of <init>

    // ---

    /**
     * {@inheritDoc}
     */
    public void keyPressed(KeyEvent evt) { }

    /**
     * {@inheritDoc}
     */
    public void keyTyped(KeyEvent evt) { }

    /**
     * Changes object property, using transformer if given, 
     * from text component value change.
     */
    public void keyReleased(final KeyEvent evt) {
	this.logger.log(Level.FINER, "event = {0}", evt);

	String sourceValue = null;
	final Object source = evt.getSource();

	this.logger.log(Level.FINER, 
			"source = {0}, sourceValue = {1}", 
			new Object[] { source, sourceValue });

	if (source instanceof TextComponent) {
	    sourceValue = ((TextComponent) source).getText();
	} else if (source instanceof JTextComponent) {
	    sourceValue = ((JTextComponent) source).getText();
	} else {
	    this.logger.log(Level.WARNING,
			    "Unsupported text component: {0}", source);

	    return;
	} // end of else

	setValue(sourceValue);
    } // end of keyReleased
} // end of class BindingKeyListener
