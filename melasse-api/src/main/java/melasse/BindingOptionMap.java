package melasse;

import java.util.HashMap;
import java.util.Map;

/**
 * Map of binding option.
 *
 * @author Cedric Chantepie 
 * @see melasse.BindingKey
 */
public class BindingOptionMap extends HashMap<Object,Object> {
    // --- Well known instances ---

    /**
     * Options for target mode
     */
    public static final BindingOptionMap targetModeOptions;

    static {
	BindingOptionMap options = new BindingOptionMap().
	    add(BindingKey.TARGET_MODE);

	targetModeOptions = new ImmutableBindingOptionMap(options);
    } // end of <static>

    // --- Constructors ---

    /**
     * No-arg constructor.
     */
    public BindingOptionMap() {
	super();
    } // end of <init>

    /**
     * Clone constructor.
     *
     * @param m Original map
     */
    protected BindingOptionMap(final Map<Object,Object> m) {
	super(m);
    } // end of <init>

    // ---

    /**
     * Adds |value| for option named with |key|.
     *
     * @param key Option key
     * @param value Option value
     * @return Current option map with modification
     */
    public BindingOptionMap add(final Object key, final Object value) {
	super.put(key, value);

	return this;
    } // end of add

    /**
     * Adds a boolean option named with |key|, and sets it to true.
     *
     * @param key Option key
     * @return Current option map with modification
     */
    public BindingOptionMap add(final Object key) {
	return this.add(key, Boolean.TRUE);
    } // end of add

    // --- Inner classes ---

    /**
     * Unmutable implementation.
     *
     * @author Cedric Chantepie 
     */
    static final class ImmutableBindingOptionMap extends BindingOptionMap {
	// --- Constructors ---

	/**
	 * Clone constructor.
	 *
	 * @param keys Option keys
	 */
	private ImmutableBindingOptionMap(BindingOptionMap keys) {
	    super((Map<Object,Object>) keys);
	} // end of <init>

	// ---
	
	/**
	 * {@inheritDoc}
	 */
	public void clear() {
	    throw new UnsupportedOperationException();
	} // end of clear

	/**
	 * {@inheritDoc}
	 */
	public Object put(Object key, Object value) {
	    throw new UnsupportedOperationException();
	} // end of put

	/**
	 * {@inheritDoc}
	 */
	public void putAll(Map<? extends Object,? extends Object> t) {
	    throw new UnsupportedOperationException();
	} // end of put

	/**
	 * {@inheritDoc}
	 */
	public Object remove(Object key) {
	    throw new UnsupportedOperationException();
	} // end of put
    } // end of class ImmutableBindingOptionMap
} // end of class BindingOptionMap
