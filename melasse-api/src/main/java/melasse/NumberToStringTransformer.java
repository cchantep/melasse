package melasse;

import java.text.NumberFormat;

/**
 * Transforms a Number to a String.
 *
 * @author Cedric Chantepie 
 */
public class NumberToStringTransformer implements ValueTransformer<Number> {

    // --- Properties ---

    /**
     * Number format
     */
    private NumberFormat format = null;

    // --- Constructors ---

    /**
     * Bulk constructor.
     *
     * @param format Number format used for transformation
     */
    private NumberToStringTransformer(NumberFormat format) {
	if (format == null) {
	    throw new IllegalArgumentException("Invalid number format: " +
					       format);

	} // end of if

	this.format = format;
    } // end of <init>

    /**
     * Returns instance using given number |format|.
     *
     * @param format Number format used for transformation
     */
    public static ValueTransformer getInstance(NumberFormat format) {
	return new NumberToStringTransformer(format);
    } // end of getInstance

    // ---

    /**
     * Transforms a Number into an String.
     *
     * @param value Number value
     * @return String representation, according format
     */
    public String transform(Number value) {
	if (value == null) {
	    return null;
	} // end of if

	return this.format.format(value);
    } // end of transform
} // end of class NumberToStringTransformer
