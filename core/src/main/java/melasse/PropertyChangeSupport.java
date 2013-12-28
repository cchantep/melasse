package melasse;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Vector;

import java.util.logging.Logger;
import java.util.logging.Level;

import java.beans.PropertyChangeEvent;

/**
 * Property change support with dependent key management.
 *
 * @author Cedric Chantepie 
 * @todo Setter annotation propertyWillChange ... session.propertyDidChange
 */
public class PropertyChangeSupport<T> extends java.beans.PropertyChangeSupport {
    // --- Properties ---
    
    /**
     * Source bean
     */
    private final T sourceBean;

    /**
     * Dependent key registry : master property <-> dependent properties
     */
    private HashMap<String,HashSet<String>> dependencies = null;

    /**
     * Property value before change
     * @todo Remove when PropertyChangeSupport#propertyDidChange is removed
     */
    private HashMap<String,Object> values = null;

    /**
     * Logger
     */
    private Logger logger = null;

    // --- Constructors ---

    /**
     * Constructs a PropertyChangeSupport object.
     *
     * @param sourceBean The bean to be given as the source for any events
     */
    public PropertyChangeSupport(final T sourceBean) {
	super(sourceBean);

	this.sourceBean = sourceBean;
	this.dependencies = new HashMap<String,HashSet<String>>();
	this.values = new HashMap<String,Object>();
	this.logger = Logger.getLogger("Melasse");

	this.logger.log(Level.FINER, "Inited: {0}", this.sourceBean);
    } // end of PropertyChangeSupport

    // ---

    /**
     * Registers a dependency so that when |master| property is changed
     * a change notification (PropertyChangeEvent) is also fired for each
     * of |dependent| properties. Be aware that it is up to you to ensure 
     * that when |master| property is changed each |dependent| one is also 
     * changed (in |master| setter or by calculation in |dependent| getter).
     *
     * You should also call propertyWillChange(master) before changing 
     * master value, otherwise no valid notification for dependent properties
     * could be guaranted.
     *
     * <code>pcs.registerDependency("master", new String[] { "dependent1" });
     * [...]
     * pcs.propertyWillChange("master");
     * this.master = newVal;
     * pcs.firePropertyChange("master", oldVal, newVal);
     * // not recommanded if property can be used from different threads
     * </code>
     * or
     * <code>pcs.registerDependency("master", new String[] { "dependent1" });
     * [...]
     * PropertyEditSession s = pcs.propertyWillChange("master");
     * this.master = newVal;
     * s.propertyDidChange();</code>
     *
     * @param master Name of master property
     * @param dependent Name of dependent property
     * @see #propertyWillChange
     * @see PropertyEditSession#propertyDidChange
     */
    public void registerDependency(final String master, 
                                   final String[] dependent) {

	this.logger.log(Level.FINER,
			"Will register dependency: master property = {0}", 
			master);

	if (master == null || 
	    dependent == null || dependent.length == 0) {

	    return;
	} // end of if

	// ---

	synchronized(this.dependencies) {
	    HashSet<String> properties = this.dependencies.get(master);

	    if (properties == null) {
		properties = new HashSet<String>();

		for (int i = 0; i < dependent.length; i++) {
		    properties.add(dependent[i]);
		} // end of for

		this.dependencies.put(master, properties);
	    } else {
		for (int i = 0; i < dependent.length; i++) {
		    properties.add(dependent[i]);
		} // end of for
	    } // end of else
	} // end of sync
    } // end of registerDependency

    /**
     * Prepares change notification for properties depending 
     * on specified |propertyName|, if any.
     *
     * This should be called before assigning new value,
     * and so before calling firePropertyChange(property, old, new).
     *
     * This method is recursive, and does not put any lock.
     *
     * @param propertyName Name of property that will be change.
     * @see #registerDependency
     * @see #propertyDidChange
     */
    public PropertyEditSession propertyWillChange(final String propertyName) {
	this.logger.log(Level.FINER, "Property will change: {0}", propertyName);

	Object value = Binder.getValue(this.sourceBean, propertyName);
	
	PropertyEditSession session = 
	    new PropertyEditSession(propertyName, value);

	synchronized(this.values) {
	    this.logger.log(Level.FINER,
			    "Store property value before change: {0} = {1}",
			    new Object[] { propertyName, value });

	    this.values.put(propertyName, value);
	} // end of sync

	HashSet<String> properties = null;

	synchronized(this.dependencies) {
	    if (this.dependencies.isEmpty()) {
		return session;
	    } // end of if
		
	    // ---
		
	    properties = this.dependencies.
		get(propertyName);

	    if (properties == null) {
		return session;
	    } // end of if
		
	    properties = new HashSet<String>(properties);
	} // end of sync

	// ---

	HashMap<String,Object> pvals = new HashMap<String,Object>();

	for (String dependent : properties) {
	    this.logger.log(Level.FINER,
			    "Dependent property will change: {0}",
			    dependent);

	    session.chain(propertyWillChange(dependent));
	} // end of for

	return session;
    } // end of propertyWillChange

    /**
     * {@inheritDoc}
     */
    public void firePropertyChange(PropertyChangeEvent evt) {
	Object oldVal = evt.getOldValue();
	Object newVal = evt.getNewValue();

	if (oldVal != null && oldVal.equals(newVal)) {
	    return;
	} // end of if

	// ---

	String propertyName = evt.getPropertyName();

	super.firePropertyChange(evt);

	notifyDependentProperties(propertyName);

	synchronized(this.values) {
	    this.values.put(propertyName, newVal);
	} // end of sync
    } // end of firePropertyChange

    /**
     * {@inheritDoc}
     */
    public void firePropertyChange(String propertyName,
				   boolean oldValue,
				   boolean newValue) {

	if (oldValue == newValue) {
	    return;
	} // end of if

	super.firePropertyChange(propertyName, oldValue, newValue);

	notifyDependentProperties(propertyName);

	synchronized(this.values) {
	    Boolean newVal = newValue;

	    this.values.put(propertyName, newVal);
	} // end of sync
    } // end of firePropertyChange

    /**
     * {@inheritDoc}
     */
    public void firePropertyChange(String propertyName,
				   int oldValue,
				   int newValue) {

	if (oldValue == newValue) {
	    return;
	} // end of if

	super.firePropertyChange(propertyName, oldValue, newValue);
	
	notifyDependentProperties(propertyName);

	synchronized(this.values) {
	    Integer newVal = newValue;

	    this.values.put(propertyName, newVal);
	} // end of sync
    } // end of firePropertyChange

    /**
     * {@inheritDoc}
     */
    public void firePropertyChange(String propertyName,
				   Object oldValue,
				   Object newValue) {

	if (oldValue != null && oldValue.equals(newValue)) {
	    return;
	} // end of if

	super.firePropertyChange(propertyName, oldValue, newValue);
	
	notifyDependentProperties(propertyName);

	synchronized(this.values) {
	    this.values.put(propertyName, newValue);
	} // end of sync
    } // end of firePropertyChange

    /**
     * Fires a change notification for |propertyName|.
     * You should have called propertyWillChange(propertyName) before
     * assigned the new value so that change notification can be
     * automatically prepared.
     *
     * @param propertyName Name of changed property
     * @see #firePropertyChange(java.lang.String,java.lang.Object,java.lang.Object)
     * @see PropertyEditSession#propertyDidChange
     * @deprecated Use PropertyEditSession#propertyDidChange(java.lang.String)
     */
    public void propertyDidChange(String propertyName) {
	this.logger.log(Level.FINE,
			"Property did change: {0}", propertyName);

	Object oldValue = null;

	synchronized(this.values) {
	    if (!this.values.containsKey(propertyName)) {
		this.logger.log(Level.WARNING,
				"Cannot prepare change notification as it seems |propertyWillChange| has not been called before change: {0}", propertyName);

		return;
	    } // end of if

	    // ---

	    oldValue = this.values.get(propertyName);
	} // end of sync

	Object newValue = Binder.
	    getValue(this.sourceBean, propertyName);
	
	this.firePropertyChange(propertyName, oldValue, newValue);
    } // end of propertyDidChange

    /**
     * Fires change notification (PropertyChangeEvent) 
     * for properties depending on one specified by |propertyName|.
     *
     * @param propertyName Name of changed (master) property
     */
    private void notifyDependentProperties(String propertyName) {
	this.logger.log(Level.FINE,
			"Will notify dependent properties: {0}", propertyName);

	HashSet<String> properties = null;

	synchronized(this.dependencies) {
	    if (this.dependencies.isEmpty()) {
		return;
	    } // end of if

	    // ---

	    properties = this.dependencies.
		get(propertyName);

	    if (properties == null) {
		return;
	    } // end of if

	    // ---

	    properties = new HashSet<String>(properties);
	} // end of sync

	synchronized(this.values) {
	    if (this.values.isEmpty()) {
		return;
	    } // end of if

	    // ---

	    Object oldVal;
	    Object newVal;
	    for (String dependent : properties) {
		oldVal = this.values.get(dependent);
		newVal = Binder.getValue(this.sourceBean, dependent);

		this.logger.log(Level.FINER,
				"Will fire change notification for dependent property: name = {0}, old value = {1}, new value = {2}", new Object[] { dependent, oldVal, newVal });
		
		this.firePropertyChange(dependent, oldVal, newVal);
	    } // end of for
	} // end of sync
    } // end of notifyDependentProperties

    // --- Inner classes ---

    /**
     * Edit session about one property, used to ensure notification
     * are properly fired when several thread can touch the property.
     * You should prefer this way rather than propertyDidChange method
     * on PropertyChangeSupport, surer and quicker.
     *
     * @author Cedric Chantepie 
     */
    public class PropertyEditSession {
	// --- Properties ---

	/**
	 * Name of property
	 */
	private String propertyName = null;

	/**
	 * Initial value, before editing property
	 */
	private Object value = null;

	/**
	 * Chained session (dependent properties or included)
	 */
	private Vector<PropertyEditSession> sessions = null;

	// --- Constructors ---

	/**
	 * Bulk constructor.
	 *
	 * @param propertyName Name of property
	 * @param value Initial property value
	 */
	private PropertyEditSession(String propertyName,
				    Object value) {

	    this.propertyName = propertyName;
	    this.value = value;

	    this.sessions = new Vector<PropertyEditSession>();
	} // end of <init>

	// ---

	/**
	 * Fires a change notification property for this session,
	 * and chained sessions.
	 * You should have called propertyWillChange(propertyName) 
	 * before assigning the new value so that change notification 
	 * can be automatically prepared.
	 *
	 * @see PropertyChangeSupport#firePropertyChange(java.lang.String,java.lang.Object,java.lang.Object)
	 */
	public void propertyDidChange() {
	    logger.log(Level.FINE,
		       "Property did change: {0}", 
		       this.propertyName);

	    Object newValue = Binder.
		getValue(sourceBean, 
			 this.propertyName);
	    
	    firePropertyChange(this.propertyName, 
			       this.value, newValue);

	    for (PropertyEditSession s : this.sessions) {
		s.propertyDidChange();
	    } // end of for
	} // end of propertyDidChange

	/**
	 * Chains subsequent |session| with current,
	 * usefull if |session| is about a dependent property
	 * or to group change notification.
	 *
	 * @param session Session to be chained (after) this one
	 */
	public void chain(PropertyEditSession session) {
	    if (session == null) {
		logger.log(Level.WARNING,
			   "Cannot chain null edit session: {0}",
			   session);

		return;
	    } // end of if

	    this.sessions.add(session);
	} // end of chain
    } // end of class PropertyEditSession
} // end of class PropertyChangeSupport
