package melasse;

/**
 * Negate input boolean value.
 *
 * @author Cedric Chantepie 
 */
public class NegateBooleanTransformer 
    implements UnaryFunction<Boolean,Boolean> {

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
    public static synchronized NegateBooleanTransformer getInstance() {
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
    public Boolean apply(Boolean value) {
	if (Boolean.TRUE.equals(value)) {
	    return Boolean.FALSE;
	} // end of if

	return Boolean.TRUE;
    } // end of transform

    /**
     * Transforms boolean value returned by given |transformer|, 
     * and returns the negated value.
     */
    public static <T> UnaryFunction<T,Boolean> negate(final UnaryFunction<T,Boolean> transformer) {
        return new WrapperTransformer<T>(transformer);
    } // end of negate

    // --- Inner classes ---

    /**
     * Wrapper implementation.
     */
    static final class WrapperTransformer<T> 
        implements UnaryFunction<T,Boolean> {

        // --- Properties ---

        /**
         * Wrapped transformer
         */
        private final UnaryFunction<T,Boolean> transformer;

        // --- Constructor ---

        /**
         */
        protected WrapperTransformer(final UnaryFunction<T,Boolean> t) {
            this.transformer = t;
        } // end of <init>

        // ---

        public Boolean apply(final T input) {
            return !transformer.apply(input);
        } // end of apply
    } // end of class WrapperTransformer
} // end of class NegateBooleanTransformer
