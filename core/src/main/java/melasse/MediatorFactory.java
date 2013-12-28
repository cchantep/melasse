package melasse;

/**
 * Factory for mediator arround object.
 *
 * @author Cedric Chantepie 
 */
public interface MediatorFactory {

    /**
     * Returns mediator for given |object|.
     *
     * @param object Object for which try to create a mediator
     * @return Mediator, or null if according this factory no one 
     * should be used for |object|.
     */
    public MediatorObject getMediator(Object object);

    // --- Inner classes ---

    /**
     * Mediator object.
     * Take care not to retain target object with hard reference.
     *
     * @author Cedric Chantepie 
     */
    public static interface MediatorObject {

	/**
	 * Returns mediated object, or null if not bound or cleared.
	 */
	public Object get();

	/**
	 * Releases underlying object.
	 */
	public void release();

    } // end of MediatorObject

} // end of interface BindingMediatorFactory
