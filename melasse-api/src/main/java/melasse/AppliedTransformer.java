package melasse;

/**
 * Returns fixed value, whatever is the given argument.
 *
 * @author Cedric Chantepie 
 */
public class AppliedTransformer<A,B> implements UnaryFunction<A,B> {

    // --- Properties ---

    /**
     * Fixed output value
     */
    private B output;

    // --- Constructors ---

    /**
     * Bulk constructor.
     */
    public AppliedTransformer(final B output) {
        this.output = output;
    } // end of <init>

    // ---

    /**
     * Returns fixed output value.
     */
    public B apply(A input) {
        return this.output;
    } // end of apply
} // end of class AppliedTransformer
