package melasse;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Listener setter.
 *
 * @author Cedric Chantepie
 */
class Setter<A> implements UnaryFunction<A,Void> {
    // --- Properties ---

    /**
     * First element of object path
     */
    protected final ObjectPathElement pathStart;

    /**
     * Logger
     */
    protected final Logger logger;

    // --- Constructors ---

    /**
     * Bulk constructor.
     *
     * @param pathStart Start of target object path
     */
    protected Setter(final ObjectPathElement pathStart) {
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
    public /*synchronized*/Void apply(final A value) {
        this.logger.log(Level.FINER, "value = {0}", value);

        if (!this.pathStart.setAndTransformValue(value)) {
            this.logger.log(Level.WARNING,
                            "Cannot set value as property " +
                            "cannot be reached: {0} = {1}",
                            new Object[] { 
                                pathStart, pathStart.getValue()
                            });
	    
        } // end of if

        return null;
    } // end of set
} // end of class Setter
