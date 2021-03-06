package melasse;

import java.util.logging.Level;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import javax.swing.JSpinner;

/**
 * Spinner change listener binding value from spinner value 
 * to an object property.
 *
 * @author Cedric Chantepie 
 */
class BindingSpinnerChangeListener 
    extends BindingListenerSupport<Object> implements ChangeListener {

    // --- Constructors ---

    /**
     * Bulk constructor.
     *
     * @param setter Setter used to propagate value
     */
    protected BindingSpinnerChangeListener(final Setter<Object> setter, 
                                           final BindingOptionMap options) {

	super(setter, options);
    } // end of <init>

    // ---

    /**
     * Changes object property, using transformer if given, 
     * from spinner value change.
     */
    public void stateChanged(ChangeEvent evt) {
	this.logger.log(Level.FINER, "event = {0}", evt);

	final Object source = evt.getSource();

	this.logger.log(Level.FINER, "source = {0}", source);

	if (!(source instanceof JSpinner)) {
	    this.logger.log(Level.WARNING,
			    "Unsupported spinner component: {0}", source);

            return;
        } // end of if

        // ---

        final Object sourceValue = ((JSpinner) source).getValue();

	this.logger.log(Level.FINER, "sourceValue = {0}", sourceValue);

	setValue(sourceValue);
    } // end of stateChanged
} // end of class BindingSpinnerChangeListener
