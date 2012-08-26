package melasse;

/**
 * Transformer object to boolean.
 *
 * @author Cedric Chantepie 
 */
public class NotNullTransformer implements ValueTransformer<Object> {
    // --- Shared ---

    /**
     * Single instance
     */
    private static NotNullTransformer instance = null;

    // --- Constructors ---

    /**
     * No-arg constructor.
     */
    private NotNullTransformer() {
    } // end of <init>

    /**
     * Returns instance transformer.
     */
    public static synchronized ValueTransformer getInstance() {
	if (instance == null) {
	    instance = new NotNullTransformer();
	} // end of if

	return instance;
    } // end of getInstance

    // ---

    /**
     * Returns true if |object| is not null.
     */
    public Boolean transform(Object object) {
	return (object != null);
    } // end of transform

} // end of interface ValueTransformer
