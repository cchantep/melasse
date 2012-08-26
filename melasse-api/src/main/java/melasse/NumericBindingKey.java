package melasse;

/**
 * Numeric component binding key names.
 *
 * @author Cedric Chantepie 
 */
public enum NumericBindingKey {
    // --- Instances ---

    /**
     * Expanded to transformers from BigInteger source to Integer target
     */
    BIGINTEGER_TO_INTEGER("bigIntegerToInteger"),

    /**
     * Expanded to transformers from Integer source to BigInteger target
     */
    INTEGER_TO_BIGINTEGER("integerToBigInteger");

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
    private NumericBindingKey(String name) {
	this.name = name;
    } // end of <init>

    // ---

    /**
     * {@inheritDoc}
     */
    public String toString() {
	return this.name;
    } // end of toString
} // end of class NumericBindingKey
