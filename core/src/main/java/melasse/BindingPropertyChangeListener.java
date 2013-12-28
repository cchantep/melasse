package melasse;

import java.util.logging.Level;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * Property change listener for binding.
 *
 * @author Cedric Chantepie 
 */
class BindingPropertyChangeListener 
    extends BindingListenerSupport<Object> implements PropertyChangeListener {

    // --- Properties ---

    /**
     * Name of watched property
     */
    private final String property;

    /**
     * Allow null change
     * [false]
     */
    private boolean allowNullChange = false;

    // --- Constructors ---
    
    /**
     * Bulk constructor.
     *
     * @param setter Setter used to propagate value
     * @param watchedProperty Name of property watched by this listener
     */
    protected BindingPropertyChangeListener(final Setter<Object> setter, 
					    final String watchedProperty,
                                            final BindingOptionMap options) {

	super(setter, options);

	if (watchedProperty == null) {
	    throw new IllegalArgumentException("Invalid name of watched " +
					       "property: " + watchedProperty);

	} // end of if

	this.property = watchedProperty;
	this.allowNullChange = false;
    } // end of <init>

    // ---

    /**
     * Sets whether this listener should |propagate| event for null change.
     *
     * @param propagate true if should propagate
     * @see BindingKey#ALLOW_NULL_CHANGE
     */
    protected void setAllowNullChange(boolean propagate) {
	this.allowNullChange = propagate;
    } // end of setAllowNullChange

    /**
     * Changes object property, using transformer if given, 
     * from text component value change.
     */
    public void propertyChange(final PropertyChangeEvent evt) {
	final String name = evt.getPropertyName();

	this.logger.log(Level.FINER,
			"event = {0}, name = {1}, watched property = {2}", 
			new Object[] { evt, name, this.property });

	if (!this.property.equals(name)) {
	    this.logger.finer("Skip unwatched property");

	    return;
	} // end of if

	if (!this.allowNullChange &&
	    evt.getOldValue() == null && evt.getNewValue() == null) {

	    this.logger.finer("Skip null change");

	    return;
	} // end of if

	// ---

	final Object sourceValue = evt.getNewValue();

	this.logger.log(Level.FINER, "sourceValue = {0}", sourceValue );

	setValue(sourceValue);
    } // end of propertyChange
} // end of class BindingPropertyChangeListener
