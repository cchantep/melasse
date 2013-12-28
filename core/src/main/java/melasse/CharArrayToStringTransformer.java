package melasse;

/**
 * Transforms an array of char into a string.
 *
 * @author Cedric Chantepie 
 */
public class CharArrayToStringTransformer 
    implements UnaryFunction<char[],String> {

    // --- Shared ---

    /**
     * Single transformer
     */
    private static CharArrayToStringTransformer instance = null;

    // --- Constructors ---

    /**
     * No-arg constructor.
     */
    private CharArrayToStringTransformer() {
    } // end of <init>

    /**
     * Returns trimming instance.
     */
    public static synchronized CharArrayToStringTransformer getInstance() {
	if (instance == null) {
	    instance = new CharArrayToStringTransformer();
	} // end of if

	return instance;
    } // end of getInstance

    // ---

    /**
     * Transforms a char[] into a String.
     *
     * @param value Array of char
     * @return String value
     */
    public String apply(final char[] value) {
	if (value == null) {
	    return null;
	} // end of if

	return new String(value);
    } // end of apply
} // end of class CharArrayToStringTransformer
