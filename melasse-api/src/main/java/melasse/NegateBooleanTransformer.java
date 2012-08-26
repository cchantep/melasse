package melasse;

/**
 * Negate input boolean value.
 *
 * @author Cedric Chantepie 
 */
public class NegateBooleanTransformer implements ValueTransformer<Boolean> {
    // --- Constants ---

    /**
     * Integer zero
     */
    private static final Integer ZERO = new Integer(0);

    // --- Shared ---

    /**
     * Non trimming transformer
     */
    private static NegateBooleanTransformer instance = null;

    // --- Constructors ---

    /**
     * No-arg constructor.
     */
    private NegateBooleanTransformer() {
    } // end of <init>

    /**
     * Returns trimming instance.
     */
    public static synchronized ValueTransformer getInstance() {
	if (instance == null) {
	    instance = new NegateBooleanTransformer();
	} // end of if

	return instance;
    } // end of getTrimmingInstance

    // ---

    /**
     * If given true, returns false.
     * If given false or null, returns true.
     *
     * @param value Boolean value
     * @return Boolean value
     */
    public Boolean transform(Boolean value) {
	if (Boolean.TRUE.equals(value)) {
	    return Boolean.FALSE;
	} // end of if

	return Boolean.TRUE;
    } // end of transform
} // end of class NegateBooleanTransformer
