package melasse;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Support class for implementation listener used for binding.
 *
 * @author Cedric Chantepie 
 */
abstract class BindingListenerSupport<A> {
    // --- Properties ---

    /**
     * Context logger
     */
    protected Logger logger = null;

    /**
     * Setter to propagate change
     */
    protected final Setter<A> setter;

    /**
     * Binding options
     */
    protected final BindingOptionMap options;

    // --- Constructors ---

    /**
     * Bulk constructor.
     *
     * @param setter Setter to propagate change from source to target object
     */
    protected BindingListenerSupport(final Setter<A> setter,
                                     final BindingOptionMap options) {

	if (setter == null) {
	    throw new IllegalArgumentException("Invalid setter: " + setter);
	} // end of if

	this.setter = setter;
        this.options = options;
	this.logger = Binder.getLogger(this.options);

	this.logger.log(Level.FINER, "setter = {0}", setter);
    } // end of <init>

    /**
     * Sets value transformed from |value| for property on object,
     * only if |value| is different from last set value.
     *
     * @param value New property value
     */
    protected void setValue(final A value) {
	this.setter.apply(value);
    } // end of setValue
} // end of class BindingListenerSupport
