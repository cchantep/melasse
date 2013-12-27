package melasse;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Support class for implementation listener used for binding.
 *
 * @author Cedric Chantepie 
 */
abstract class BindingListenerSupport {
    // --- Properties ---

    /**
     * Context logger
     */
    protected Logger logger = null;

    /**
     * Setter to propagate change
     */
    protected final Setter setter;

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
    protected BindingListenerSupport(final Setter setter,
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
    protected void setValue(final Object value) {
	this.setter.set(value);
    } // end of setValue

    // --- Inner classes ---

    /**
     * Listener setter.
     *
     * @author Cedric Chantepie
     */
    protected static class Setter {
	// --- Properties ---

	/**
	 * First element of object path
	 */
	protected final ObjectPathElement pathStart;

	/**
	 * Logger
	 */
	protected Logger logger = null;

	// --- Constructors ---

	/**
	 * Bulk constructor.
	 *
	 * @param pathStart Start of target object path
	 */
	public Setter(final ObjectPathElement pathStart) {
	    if (pathStart == null) {
		throw new IllegalArgumentException("Invalid object path: " +
						   pathStart);

	    } // end of if

	    this.pathStart = pathStart;
	    this.logger = Logger.getLogger("Melasse");
	} // end of <init>

	// --- 

	/**
	 * Sets new |value|.
	 *
	 * @param value New value
	 */
	public /*synchronized*/void set(final Object value) {
	    this.logger.log(Level.FINER, "value = {0}", value);

	    if (!this.pathStart.setAndTransformValue(value)) {
		this.logger.log(Level.WARNING,
				"Cannot set value as property " +
				"cannot be reached: {0} = {1}",
				new Object[] { 
				    pathStart, pathStart.getValue()
				});
	    
		return;
	    } // end of if
	} // end of set
    } // end of class Setter

    /**
     * Sets new value only if its differents from last set one.
     *
     * @author Cedric Chantepie 
     */
    protected static class ConditionalSetter extends Setter {
	// --- Properties ---

	/**
	 * Current value
	 */
	private Object value = null;

	// --- Constructors ---

	/**
	 * Bulk constructor.
	 *
	 * @param pathStart Start of target object path
	 * @param value Initial value (and current one)
	 */
	public ConditionalSetter(final ObjectPathElement pathStart,
				 final Object value) {

	    super(pathStart);

	    this.value = value;
	} // end of <init>

	// ---

	/**
	 * Sets if and only if current value is different 
	 * from given one. It avoids tiggering unneeded change event.
	 *
	 * @param value New value, only set if different.
	 */
	public void set(final Object value) {
	    this.logger.log(Level.FINER, "current value = {0}", this.value);

	    if ((this.value == null && value == null) ||
		(this.value != null && this.value.equals(value))) {

		this.logger.log(Level.FINER,
				"Does not set equals value: {0}, {1}",
				new Object[] { this.value, value });

		return;
	    } // end of if
	    
	    super.set(value);

	    this.value = value;
	} // end of if
    } // end of class ConditionalSetter
} // end of class BindingListenerSupport
