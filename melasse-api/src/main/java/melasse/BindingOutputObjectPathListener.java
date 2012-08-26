package melasse;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Object path to maintain binding.
 *
 * @author Cedric Chantepie 
 * @todo Weak ref for connected
 */
class BindingOutputObjectPathListener implements ObjectPathListener {

    // --- Properties ---

    /**
     * Path connected to observed one
     */
    private ObjectPathElement connected = null;

    /**
     * Transform value from source to target
     */
    private ValueTransformer transformer = null;

    /**
     * Logger 
     */
    private Logger logger = null;

    // --- Constructors ---

    /**
     * Bulk constructor.
     *
     * @param path Path connected to observed one
     */
    protected BindingOutputObjectPathListener(ObjectPathElement path) {
	if (path == null) {
	    throw new IllegalArgumentException("Invalid path: " + path);
	} // end of if

	this.connected = path;
	this.logger = Logger.getLogger(/*LibraSwing*/"Melasse");
    
	this.logger.finer("Inited");
    } // end of <init>

    // --- 

    /**
     * {@inheritDoc}
     */
    public void setTransformer(ValueTransformer transformer) {
	this.transformer = transformer;
    } // end of setTransformer

    /**
     * {@inheritDoc}
     */
    public void pathCompleted(ObjectPathElement element) {
	Object value = element.getValue();

	this.logger.log(Level.FINER,
			"element = {0}, value = {1}", 
			new Object[] { element, value });

	this.connected.setAndTransformValue(value);
    } // end of pathCompleted
} // end of class BindingOutputObjectPathListener
