package melasse;

import java.text.Format;

/**
 * Transforms a Number to a String.
 *
 * @author Cedric Chantepie 
 */
public class NumberToStringTransformer implements UnaryFunction<Number,String> {

    // --- Properties ---

    /**
     * Number format
     */
    private final Format format;

    // --- Constructors ---

    /**
     * Bulk constructor.
     *
     * @param format Number format used for transformation
     */
    private NumberToStringTransformer(final Format format) {
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
    public static NumberToStringTransformer getInstance(Format format) {
	return new NumberToStringTransformer(format);
    } // end of getInstance

    // ---

    /**
     * Transforms a Number into an String.
     *
     * @param value Number value
     * @return String representation, according format
     */
    public String apply(final Number value) {
	if (value == null) {
	    return null;
	} // end of if

	return this.format.format(new Number[] { value });
    } // end of transform
} // end of class NumberToStringTransformer
