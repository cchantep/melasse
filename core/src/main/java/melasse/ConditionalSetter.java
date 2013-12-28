package melasse;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Sets new value only if its differents from last set one.
 *
 * @author Cedric Chantepie 
 */
final class ConditionalSetter<A> extends Setter<A> {
    // --- Properties ---

    /**
     * Current value
     */
    private A value = null;

    // --- Constructors ---

    /**
     * Bulk constructor.
     *
     * @param pathStart Start of target object path
     * @param value Initial value (and current one)
     */
    protected ConditionalSetter(final ObjectPathElement pathStart,
                                final A value) {
        
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
    public Void apply(final A value) {
        logger.log(Level.FINER, "current value = {0}", this.value);

        if ((this.value == null && value == null) ||
            (this.value != null && this.value.equals(value))) {

            logger.log(Level.FINER, "Does not set equals value: {0}, {1}",
                       new Object[] { this.value, value });

            return null;
        } // end of if
	    
        super.apply(value);

        this.value = value;

        return null;
    } // end of if
} // end of class ConditionalSetter
