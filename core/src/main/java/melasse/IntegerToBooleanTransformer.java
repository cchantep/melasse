package melasse;

/**
 * Transforms an integer to a boolean value.
 * Returns false if null or 0.
 *
 * @author Cedric Chantepie 
 */
public class IntegerToBooleanTransformer 
    implements UnaryFunction<Integer,Boolean> {

    // --- Constants ---

    /**
     * Integer zero
     */
    private static final Integer ZERO = new Integer(0);

    // --- Shared ---

    /**
     * Non trimming transformer
     */
    private static IntegerToBooleanTransformer instance = null;

    // --- Constructors ---

    /**
     * No-arg constructor.
     */
    private IntegerToBooleanTransformer() {
    } // end of <init>

    /**
     * Returns trimming instance.
     */
    public static synchronized IntegerToBooleanTransformer getInstance() {
	if (instance == null) {
	    instance = new IntegerToBooleanTransformer();
	} // end of if

	return instance;
    } // end of getTrimmingInstance

    // ---

    /**
     * Transforms a given integer |value| and returns a boolean one.
     * Integer <= 0 gives false, otherwise it gives true.
     *
     * @param value Integer value
     * @return Boolean value
     */
    public Boolean apply(final Integer value) {
	if (value == null) {
	    return Boolean.FALSE;
	} // end of if

	if (ZERO.compareTo(value) >= 0) {
	    return Boolean.FALSE;
	} // end of if

	return Boolean.TRUE;
    } // end of transform

    /**
     * Returns negative transformer.
     */
    public UnaryFunction<Integer,Boolean> negate() {
        return NegateBooleanTransformer.negate(this);
    } // end of negate
} // end of class IntegerToBooleanTransformer
