package melasse;

/**
 * Text component binding key names.
 *
 * @author Cedric Chantepie 
 */
public enum TextBindingKey {
    // --- Instances ---

    /**
     * Expanded to transformers from char array source to string target
     */
    CHAR_ARRAY_TO_STRING("charArrayToString"),

    /**
     * Expanded to transformers from string source to char array target
     */
    STRING_TO_CHAR_ARRAY("stringToCharArray"),

    /**
     * Update value for each change, not only on validate action.
     */
    CONTINUOUSLY_UPDATE_VALUE("continuouslyUpdateValue");

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
    private TextBindingKey(String name) {
	this.name = name;
    } // end of <init>

    // ---

    /**
     * {@inheritDoc}
     */
    public String toString() {
	return this.name;
    } // end of toString
} // end of class TextBindingKey
