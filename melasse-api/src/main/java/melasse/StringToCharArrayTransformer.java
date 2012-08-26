package melasse;

/**
 * Transforms an array of char into a charArray.
 *
 * @author Cedric Chantepie 
 */
public class StringToCharArrayTransformer implements ValueTransformer<String> {

    // --- Shared ---

    /**
     * Single transformer
     */
    private static StringToCharArrayTransformer instance = null;

    // --- Constructors ---

    /**
     * No-arg constructor.
     */
    private StringToCharArrayTransformer() {
    } // end of <init>

    /**
     * Returns trimming instance.
     */
    public static synchronized ValueTransformer getInstance() {
	if (instance == null) {
	    instance = new StringToCharArrayTransformer();
	} // end of if

	return instance;
    } // end of getInstance

    // ---

    /**
     * Transforms a String into a char [].
     *
     * @param value String value
     * @return Array of char
     */
    public Object transform(String value) {
	if (value == null) {
	    return null;
	} // end of if

	return value.toCharArray();
    } // end of transform
} // end of class StringToCharArrayTransformer
