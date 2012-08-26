package melasse;

import java.util.logging.Level;

import java.awt.TextComponent;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.text.JTextComponent;

/**
 * Action listener for text components.
 *
 * @author Cedric Chantepie 
 */
class BindingTextActionListener 
    extends BindingListenerSupport 
    implements ActionListener {

    // --- Constructors ---

    /**
     * Bulk constructor.
     *
     * @param setter Setter to propagate changes
     */
    protected BindingTextActionListener(Setter setter) {
	super(setter);
    } // end of <init>

    // ---

    /**
     * When text is validated (usually with ENTER key).
     */
    public void actionPerformed(ActionEvent evt) {
	this.logger.log(Level.FINER,
			"evt = {0}", evt);

	Object sourceValue = null;
	Object source = evt.getSource();

	this.logger.log(Level.FINER, "source = {0}", source);

	if (source instanceof TextComponent) {
	    sourceValue = ((TextComponent) source).getText();
	} else if (source instanceof JTextComponent) {
	    sourceValue = ((JTextComponent) source).getText();
	} else {
	    this.logger.log(Level.WARNING,
			    "Unsupported text component: {0}",
			    source);

	    return;
	} // end of else

	setValue(sourceValue);
    } // end of actionPerformed
} // end of class BindingTextActionListener
