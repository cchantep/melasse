package melasse;

import java.math.BigInteger;

/**
 * Transforms a Integer to an BigInteger.
 *
 * @author Cedric Chantepie 
 */
public class IntegerToBigIntegerTransformer implements ValueTransformer<Integer> {

    // --- Shared ---

    /**
     * Single transformer
     */
    private static IntegerToBigIntegerTransformer instance = null;

    // --- Constructors ---

    /**
     * No-arg constructor.
     */
    private IntegerToBigIntegerTransformer() {
    } // end of <init>

    /**
     * Returns trimming instance.
     */
    public static synchronized ValueTransformer getInstance() {
	if (instance == null) {
	    instance = new IntegerToBigIntegerTransformer();
	} // end of if

	return instance;
    } // end of getInstance

    // ---

    /**
     * Transforms a Integer into an BigInteger.
     *
     * @param value Integer value
     * @return BigInteger value
     */
    public BigInteger transform(Integer value) {
	if (value == null) {
	    return BigInteger.ZERO;
	} // end of if

	return new BigInteger(value.toString());
    } // end of transform
} // end of class IntegerToBigIntegerTransformer
