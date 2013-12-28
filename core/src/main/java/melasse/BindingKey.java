package melasse;

/**
 * Common binding key names.
 *
 * @author Cedric Chantepie 
 */
public enum BindingKey {
    // --- Instances ---

    /**
     * Transformer from source property to target one
     */
    INPUT_TRANSFORMER("inputTransformer"),

    /**
     * Transformer from target property to source one
     */
    OUTPUT_TRANSFORMER("outputTransformer"),

    /**
     * Mediator factory for target object
     * @see melasse.BindingMediatorFactory
     */
    INPUT_MEDIATOR_FACTORY("inputMediatorFactory"),

    /**
     * Mediator factory for target object
     * @see melasse.BindingMediatorFactory
     */
    OUTPUT_MEDIATOR_FACTORY("outputMediatorFactory"),

    /**
     * Allow to consider as change setting null as an already null property
     * (like default behavious of PropertyChangeSupport).
     * Default behaviour of binding is to ignore this kind of "change".
     * Allowing it can cause some event cycle (and StackOverFlowError).
     *
     * @see java.beans.PropertyChangeSupport#firePropertyChange(java.lang.String,java.lang.Object,java.lang.Object)
     */
    ALLOW_NULL_CHANGE("allowNullChange"),

    /**
     * Do not propage change from object A to object B
     */
    TARGET_MODE("targetMode"),

    /**
     * Debug category
     * @see java.util.logging.Logger
     */
    DEBUG_CATEGORY("debugCategory");

    // --- Properties ---

    /**
     * Key name
     */
    private String name = null;

    // --- Constructors ---

    /**
     * Bulk constructor.
     *
     * @param name Key name
     */
    private BindingKey(String name) {
	this.name = name;
    } // end of <init>

    // ---

    /**
     * {@inheritDoc}
     */
    public String toString() {
	return this.name;
    } // end of toString
} // end of class BindingKey
