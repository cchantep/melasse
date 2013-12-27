package melasse;

import java.util.logging.Level;

import java.awt.TextComponent;

import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;

import javax.swing.JSpinner;

import javax.swing.text.JTextComponent;

/**
 * Focus listener for binding. Use it only as alternate action listener,
 * not in case of continous-update-value listening.
 *
 * @author Cedric Chantepie 
 */
class BindingFocusListener 
    extends BindingListenerSupport implements FocusListener {

    // --- Constructors ---

    /**
     * Bulk constructor.
     *
     * @param setter Setter to propagate changes
     */
    protected BindingFocusListener(final Setter setter, 
                                   final BindingOptionMap options) {

	super(setter, options);
    } // end of <init>

    // ---

    /**
     * {@inheritDoc}
     */
    public void focusGained(FocusEvent e) { }

    /**
     * Sets property value on object when source lost focus.
     */
    public void focusLost(final FocusEvent evt) {
	this.logger.log(Level.FINER,
			"evt = {0}", evt);

	Object sourceValue = null;
	final Object source = evt.getSource();

	this.logger.log(Level.FINER, "source = {0}", source);

	if (source instanceof TextComponent) {
	    sourceValue = ((TextComponent) source).getText();
	} else if (source instanceof JTextComponent) {
	    sourceValue = ((JTextComponent) source).getText();
	} else if (source instanceof JSpinner) {
	    sourceValue = ((JSpinner) source).getValue();
	} else {
	    this.logger.log(Level.WARNING, 
                            "Unsupported component: {0}", source);

	    return;
	} // end of else

	setValue(sourceValue);
    } // end of focusLost
} // end of class BindingFocusListener
