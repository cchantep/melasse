package melasse;

/**
 * Transforms an array of char into a string.
 *
 * @author Cedric Chantepie 
 */
public class CharArrayToStringTransformer implements ValueTransformer<char[]> {

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
    public static synchronized ValueTransformer getInstance() {
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
    public String transform(char[] value) {
	if (value == null) {
	    return null;
	} // end of if

	return new String(value);
    } // end of transform
} // end of class CharArrayToStringTransformer
