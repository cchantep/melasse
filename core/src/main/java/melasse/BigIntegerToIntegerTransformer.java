package melasse;

import java.math.BigInteger;

/**
 * Transforms a BigInteger to an Integer.
 *
 * @author Cedric Chantepie 
 */
public class BigIntegerToIntegerTransformer 
    implements UnaryFunction<BigInteger,Integer> {

    // --- Shared ---

    /**
     * Zero integer
     */
    private static final Integer ZERO = new Integer(0);

    /**
     * Single transformer
     */
    private static BigIntegerToIntegerTransformer instance = null;

    // --- Constructors ---

    /**
     * No-arg constructor.
     */
    private BigIntegerToIntegerTransformer() {
    } // end of <init>

    /**
     * Returns transformer instance.
     */
    public static synchronized BigIntegerToIntegerTransformer getInstance() {
	if (instance == null) {
	    instance = new BigIntegerToIntegerTransformer();
	} // end of if

	return instance;
    } // end of getInstance

    // ---

    /**
     * Transforms a BigInteger into an Integer.
     *
     * @param value BigInteger value
     * @return Integer value
     */
    public Integer apply(final BigInteger value) {
	if (value == null) {
	    return ZERO;
	} // end of if

	return new Integer(value.intValue());
    } // end of transform
} // end of class BigIntegerToIntegerTransformer
