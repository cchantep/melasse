package melasse;

/**
 * Transformer object to boolean.
 *
 * @author Cedric Chantepie 
 */
public class NotNullTransformer implements UnaryFunction<Object,Boolean> {

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
    public static synchronized NotNullTransformer getInstance() {
	if (instance == null) {
	    instance = new NotNullTransformer();
	} // end of if

	return instance;
    } // end of getInstance

    // ---

    /**
     * Returns true if |object| is not null.
     */
    public Boolean apply(Object object) {
	return (object != null);
    } // end of apply
} // end of interface NotNullTransformer
