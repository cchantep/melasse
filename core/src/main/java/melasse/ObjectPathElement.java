package melasse;

import java.lang.ref.WeakReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import java.util.concurrent.CopyOnWriteArrayList;

import java.util.logging.Logger;
import java.util.logging.Level;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Maintains an element of path to an object property.
 *
 * @author Cedric Chantepie 
 * @todo Allows to have component in path and so use special listeners
 */
class ObjectPathElement implements PropertyChangeListener {
    // --- Properties ---

    /**
     * Weak reference to object
     */
    private WeakReference<Object> objectRef = null;

    /**
     * Object property
     */
    private final String property;

    /**
     * Previous element
     */
    protected ObjectPathElement previous = null;

    /**
     * Next element
     */
    private ObjectPathElement next = null;

    /**
     * Binding options
     */
    private final BindingOptionMap options;

    /**
     * Logger
     */
    private final Logger logger;

    /**
     * Listeners
     */
    private CopyOnWriteArrayList<ObjectPathListener> listeners = null;

    /**
     * Post add-listener hooks
     */
    private ArrayList<PostAddListenerHook> hooks = null;

    /**
     * Listeners maintained on target object
     */
    private HashMap<BindingListenerCategory,ArrayList<Object>> addedListeners = null;

    /**
     * Event dispatcher (to |listeners|)
     */
    private EventDispatcher eventDispatcher = null;

    /**
     * Input value transformer
     */
    private UnaryFunction<Object,Object> inTransformer = null;

    /**
     * Input aggregate transformer.
     */
    private UnaryFunction<Boolean,Boolean> inAggregateTransformer = null;

    /**
     * Output value transformer
     */
    private UnaryFunction<Object,Object> outTransformer = null;

    /**
     * Mediator factory
     */
    private MediatorFactory mediatorFactory = null;

    /**
     * Many input value expected to one value at end of this path
     * [false]
     */
    private boolean manyToOne = false;

    // --- Constructors ---

    /**
     * Bulk constructor.
     *
     * @param object Target object
     * @param property Object property
     */
    public ObjectPathElement(final Object object,
			     final String property,
                             final BindingOptionMap options) {

	if (property == null || property.length() == 0) {
	    throw new IllegalArgumentException("Invalid property: " + 
					       property);

	} // end of if

	if (property.endsWith("[]")) {
	    this.manyToOne = true;
	} // end of if

	this.property = normalizeProperty(property);
	this.logger = Binder.getLogger(options);
        this.options = options;
	this.listeners = new CopyOnWriteArrayList<ObjectPathListener>();
	this.addedListeners = 
            new HashMap<BindingListenerCategory,ArrayList<Object>>();
	this.hooks = new ArrayList<PostAddListenerHook>();

	setTargetObject(object);

	this.logger.finer("Inited");
    } // end of <init>

    // ---

    /**
     * Normalizes property |name|.
     *
     * @param name Input property name
     * @return Normalized property name
     */
    protected static String normalizeProperty(final String name) {
	if (name == null) {
	    return null;
	} // end of if

	// ---

        return !name.endsWith("[]") ? name : name.substring(0, name.length()-2);
    } // end of normalizeProperty

    /**
     * Sets input value |transformer|, applied when setting value 
     * with <tt>setAndTransform</tt>.
     *
     * @param transformer Input transformer
     * @see #setAndTransform
     */
    public void setInputTransformer(final UnaryFunction<Object,Object> transformer) {
	this.inTransformer = transformer;
    } // end of setInputTransformer

    /**
     * Sets output value |transformer|, applied when get value 
     * with <tt>getTransformedValue</tt>.
     *
     * @param transformer output transformer
     * @see #getTransformedValue
     */
    public void setOutputTransformer(final UnaryFunction<Object,Object> transformer) {
	this.outTransformer = transformer;
    } // end of setOutputTransformer

    /**
     * Sets mediator |factory|.
     *
     * @param factory Mediator factory
     */
    public void setMediatorFactory(final MediatorFactory factory) {
	this.mediatorFactory = factory;

	if (this.next != null) {
	    this.next.setMediatorFactory(factory);

	    return;
	} // end of if

	if (!isTargetReachable()) {
	    return;
	} // end of if

	final Object target = this.objectRef.get();

	this.logger.log(Level.FINER, 
			"Will set target as mediator: {0} (factory = {1})", 
                        new Object[] { target, this.mediatorFactory });

	final MediatorFactory.MediatorObject obj = 
            (this.mediatorFactory == null) ? null 
            : this.mediatorFactory.getMediator(target);
	
	if (obj == null) {
	    this.logger.finer("No mediator for object");

	    return;
	} // end of if

	releaseObjectRef();

	this.objectRef = new WeakReference<Object>(obj);
    } // end of setMediatorFactory

    /**
     * Releases object reference.
     */
    private void releaseObjectRef() {
	if (this.objectRef == null) {
	    return;
	} // end of if

	this.objectRef.clear();
	
	this.objectRef = null;
    } // end of releaseObjectRef

    /**
     * Forward unset.
     */
    private void forwardUnset() {
	removeListeners();

	releaseObjectRef();

	this.logger.log(Level.FINER, "next = {0}", this.next);

	if (this.next != null) {
	    this.next.forwardUnset();
	} // end of if
    } // end of forwardUnset

    /**
     * Checks whether target object is still reachable.
     *
     * @return true if target object is reachable, or false
     */
    private boolean isTargetReachable() {
	if (this.objectRef == null) {
	    return false;
	} // end of if

	final Object target = this.objectRef.get();

	if (target == null) {
	    return false;
	} // end of if

	if (!(target instanceof MediatorFactory.MediatorObject)) {
	    return true;
	} // end of if

        // ---

	return ((MediatorFactory.MediatorObject) target).get() != null;
    } // end of isTargetReachable

    /**
     * Forward reset.
     *
     * @param target New target object
     */
    private void forwardReset(final Object target) {
	if (target == null) {
	    releaseObjectRef();
	} else {
	    MediatorFactory.MediatorObject obj = null;

	    this.logger.log(Level.FINER,
			    "mediator factory = {0}", this.mediatorFactory);

	    if (this.mediatorFactory != null) {
		obj = this.mediatorFactory.getMediator(target);

		this.logger.log(Level.FINER,
				"mediator object = {0}", obj);

	    } // end of if

            this.objectRef = (obj != null) 
                ? new WeakReference<Object>(obj) 
                : new WeakReference<Object>(target);

	} // end of else

	if (!isTargetReachable()) {
	    return;
	} // end of if

	// ---

	if (this.manyToOne) {
	    final Object value = Binder.getValue(target, this.property);
	    
	    this.logger.log(Level.FINER, "value = {0}", value);
	
	    resetAggregator(value);
	} // end of if

	addListeners();

	if (this.next == null) {
	    return;
	} // end of if

	// ---

	this.next.forwardReset(Binder.getValue(target, this.property));
    } // end of forwardReset

    /**
     * Removes registered listeners from current object.
     */
    private void removeListeners() {
	if (!isTargetReachable()) {
	    logger.warning("Cannot remove listeners on garbage object");

	    return;
	} // end of if

	// ---

	final Object target = this.objectRef.get();

	Binder.removeListener(target,
			      BindingListenerCategory.PROPERTY_CHANGE,
			      this, this.options);

	// CHANGE LISTENER ???

	synchronized(this.addedListeners) {
	    ArrayList list;
	    for (final BindingListenerCategory cat : this.
                     addedListeners.keySet()) {

		list = this.addedListeners.get(cat);

		synchronized(list) {
		    for (final Object l : list) {
			Binder.removeListener(target, cat, l, this.options);
		    } // end of for
		} // end of sync
	    } // end of for
	} // end of if
    } // end of removeListeners

    /**
     * Puts listeners from |registry| in added list 
     * and them to target object also.
     */
    private void pushListenerRegistry(Map<BindingListenerCategory,ArrayList<Object>> registry) {

	if (!isTargetReachable()) {
	    this.logger.warning("Cannot add listeners on garbage object");

	    return;
	} // end of if

	// ---

	final Object target = this.objectRef.get();

	this.logger.log(Level.FINER, "registry = {0}", registry);

	synchronized(this.addedListeners) {
	    // should have been cleared before
	    ArrayList<Object> rl;
	    ArrayList<Object> list;
	    for (final BindingListenerCategory cat : registry.keySet()) {
		rl = registry.get(cat);

		this.logger.log(Level.FINER, "category = {0} -> {1}", 
                                new Object[] { cat, rl });

		for (final Object l : rl) {
		    Binder.addListener(target, cat, l, this.options);
		} // end of for

		list = this.addedListeners.get(cat);

		if (list == null) {
		    list = rl;
		    
		    this.addedListeners.put(cat, list);
		} else {
		    synchronized(list) {
			list.addAll(rl);
		    } // end of sync
		} // end of else

		this.logger.log(Level.FINER, "category listeners = {0}", list);
	    } // end of for
	} // end of sync
    } // end of pushListenerRegistry

    /**
     * Adds registered listeners to current object.
     */
    private void addListeners() {
	if (!isTargetReachable()) {
	    this.logger.warning("Cannot add listeners on garbage listeners");

	    return;
	} // end of if

	// ---

	final Object target = this.objectRef.get();
	final boolean end = isEnd();

	Binder.addListener(target,
			   BindingListenerCategory.PROPERTY_CHANGE,
			   this, this.options);
	
	// CHANGE LISTENER ???

	if (!end) { // @todo Only at end ; strategy pattern
	    return;
	} // end of if

	final HashMap<BindingListenerCategory,ArrayList<Object>> registry = 
	    new HashMap<BindingListenerCategory,ArrayList<Object>>();
	
	synchronized(this.hooks) {
	    for (final PostAddListenerHook h : this.hooks) {
		h.afterAddListeners(this, registry);
	    } // end of for
	} // end of if

	pushListenerRegistry(registry);
    } // end of addListeners

    /**
     * Registers a add-listener |hook|.
     *
     * @param hook Add-listener hook
     */
    public void registerPostAddListenerHook(final PostAddListenerHook hook) {
	synchronized(this.hooks) {
	    this.hooks.add(hook);
	} // end of sync

	if (!isTargetReachable()) {
	    return;
	} // end of if

	// ... else applies it directly
	final HashMap<BindingListenerCategory,ArrayList<Object>> hreg = 
	    new HashMap<BindingListenerCategory,ArrayList<Object>>();
	
	hook.afterAddListeners(this, hreg);

	pushListenerRegistry(hreg);
    } // end of registerPostAddListenerHook

    /**
     * Adds a path |listener|.
     *
     * @param listener Listener to be added
     */
    public void addObjectPathListener(final ObjectPathListener listener) {
	synchronized(this.listeners) {
	    this.listeners.add(listener);
	} // end of sync
    } // end of addObjectPathListener

    /**
     * Removes path |listener|.
     *
     * @param listener Listener to be removed
     */
    public void removeObjectPathListener(final ObjectPathListener listener) {
	synchronized(this.listeners) {
	    this.listeners.remove(listener);
	} // end of sync
    } // end of removeObjectPathListener

    /**
     * Returns next element in path.
     */
    public ObjectPathElement getNext() {
	return this.next;
    } // end of getNext

    /**
     * Returns true if this element is end of the path.
     */
    public boolean isEnd() {
	return (this.next == null);
    } // end of isEnd

    /**
     * Returns target object.
     */
    public Object getTargetObject() {
	if (this.objectRef == null) {
	    return null;
	} // end of if

	return this.objectRef.get();
    } // end of getTargetObject

    /**
     * Returns name of property on target object.
     *
     * @see #getTargetObject
     */
    public String getProperty() {
	return this.property;
    } // end of getProperty
    
    /**
     * Sets target |object|.
     * Removes this element as property listener from old target object.
     * Adds this element as property listener to new target object.
     *
     * @param object New target object
     */
    public void setTargetObject(final Object object) {
	Object target = null;

	if (this.objectRef != null &&
	    ((target = this.objectRef.get()) == object ||
	     ((target instanceof MediatorFactory.MediatorObject) &&
	      ((MediatorFactory.MediatorObject) target).get() == object))) {

	    return; // is the same
	} // end of if

	// ---

	if (target != null) {
	    forwardUnset();
	} // end of if

	forwardReset(object);

	if (this.next == null) { // path is now complete
	    this.firePathCompleted();
	} // end of if
    } // end of setTargetObject

    /**
     * Sets value, transformed with input transformed if not null, 
     * from input |value|.
     *
     * @param value Input value
     * @see #setValue
     */
    public boolean setAndTransformValue(final Object value) {
	this.logger.log(Level.FINER, "this = {0}, value = {1}", 
			new Object[] { this, value });

	if (!isTargetReachable()) {
	    this.logger.log(Level.WARNING,
			    "Cannot set value on null target object: {0} -> {1}",
			    new Object [] {
				this.objectRef, 
				(this.objectRef != null) 
				? this.objectRef.get() : null 
			    });

	    return false;
	} // end of if

	if (this.next != null) {
	    this.logger.finer("Pass set query to next element");
	    
	    return this.next.setAndTransformValue(value);
	} // end of if

	if (this.inTransformer != null) {
	    this.logger.finer("Apply input transformer");

	    return this.setValue(this.inTransformer.apply(value));
	} // end of if

	return this.setValue(value);
    } // end of setAndTransformValue

    /**
     * Sets value to object property at end of path.
     *
     * @param value New property value
     * @return true if property has been set, 
     * false if property cannot be reached
     */
    public boolean setValue(final Object value) {
	this.logger.log(Level.FINER, "property = {0}, next = {1}, inAggregateTransformer = {2}", new Object[] { this.property, this.next, this.inAggregateTransformer });

	if (!isTargetReachable()) {
	    this.logger.log(Level.WARNING,
			    "Cannot set value on null target object: {0}",
			    this.objectRef);

	    return false;
	} // end of if

	if (this.next != null) {
	    return this.next.setValue(value);
	} // end of if

	this.logger.finer("Sets property value");

	if (this.inAggregateTransformer != null) {
	    this.logger.finer("Set value with aggregate transformer");

	    final Boolean val = inAggregateTransformer.apply((Boolean) value);

	    this.logger.log(Level.FINER, "aggregated value = {0}", val);

            Binder.setValue(this.objectRef.get(), 
                            this.property, val, this.options);

	} else {
            Binder.setValue(this.objectRef.get(), 
                            this.property, value, this.options);

        } // end of else

	return true;
    } // end of setValue

    /**
     * Returns value to object property at end of path.
     *
     * @return Value from pointed by current path, or null
     */
    public Object getValue() {
	this.logger.log(Level.FINER,
			"object = {0}, next = {1}",
			new Object[] { (this.objectRef != null) 
				       ? this.objectRef.get()
				       : null, next });


	if (!isTargetReachable()) {
	    this.logger.warning("Cannot get value from path element with unset target object");
	    this.logger.log(Level.FINER,
			    "Unbounded object path element: previous = {0}, property = {1}", new Object[] { this.previous, this.property });
	    
	    return null;
	} // end of if

	if (this.next != null) {
	    return this.next.getValue();
	} // end of if

	this.logger.finer("Gets property value");

	return Binder.getValue(this.objectRef.get(), this.property);
    } // end of getValue

    /**
     * Sets next |element| in object path.
     *
     * @param element Next element
     */
    public void setNextElement(ObjectPathElement element) {
	this.next = element;
	
	element.previous = this;
    } // end of setNextElement

    /**
     * Fires event when property at end of path has changed.
     */
    private void firePathCompleted() {
	if (this.eventDispatcher != null) {
	    return;
	} // end of if

	this.eventDispatcher = new EventDispatcher();
	this.eventDispatcher.owner = this;

	Thread th = new Thread(this.eventDispatcher);

	th.start();
    } // end of firePathCompleted

    /**
     * {@inheritDoc}
     */
    public void propertyChange(PropertyChangeEvent evt) {
	String name = evt.getPropertyName();

	this.logger.log(Level.FINER, 
			"source = {0}, name = {1}", 
			new Object[] { evt.getSource(), name });

	if (!this.property.equals(name)) {
	    this.logger.finer("Skip unwatched property");

	    return;
	} // end of if

	// ---

	Object newValue = evt.getNewValue();

	this.logger.log(Level.FINER,
			"next = {0}, newValue = {1}",
			new Object[] { next, newValue });

	if (this.manyToOne) {
	    resetAggregator(newValue);
	} // end of if

	if (this.next != null) {
	    this.next.setTargetObject(newValue);
	} // end of if
    } // end of propertyChange

    /**
     * Resets aggregate value transformer, if required.
     *
     * @param value Current property value
     */
    private void resetAggregator(final Object value) {
	if ((value instanceof Boolean) ||
	    (value != null && Boolean.TYPE.equals(value.getClass()))) {

	    this.logger.log(Level.FINER, "Add group transformer for boolean");
	    
	    this.inAggregateTransformer = Binder.
		getBooleanAndAggregateTransformer(this, this.options);
	    
	} // end of if
    } // end of resetAggregator

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
	return new HashCodeBuilder(3, 17).
	    append(this.getTargetObject()).
	    append(this.property).
	    toHashCode();

    } // end of hashCode

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object o) {
	if (o == null || !(o instanceof ObjectPathElement)) {
	    return false;
	} // end of if

	final ObjectPathElement other = (ObjectPathElement) o;

	return new EqualsBuilder().
	    append(this.getTargetObject(), other.getTargetObject()).
	    append(this.property, other.property).
	    isEquals();

    } // end of equals

    /**
     * {@inheritDoc}
     */
    public String toString() {
	Object obj = this.getTargetObject();

	if (obj != null) {
	    int hc = System.identityHashCode(obj);
	    String cn = obj.getClass().getName();

	    obj = cn + '@' + hc;
	} // end of if

	return new ToStringBuilder(this).
	    append("object", obj).
	    append("property", this.property).
	    toString();

    } // end of toString

    // --- Inner classes ---

    /**
     * Dispatch object path event.
     *
     * @author Cedric Chantepie 
     */
    private class EventDispatcher implements Runnable {
	// --- Properties ---

	/**
	 * Owning element
	 */
	public ObjectPathElement owner = null;

	// ---

	/**
	 * Dispatches events.
	 */
	public void run() {
	    for (ObjectPathListener l : listeners) {
		l.pathCompleted(this.owner);
	    } // end of for
	    
	    if (previous != null) {
		previous.firePathCompleted();
	    } // end of if

	    eventDispatcher = null;
	} // end of run
    } // end of class EventDispatcher

    /**
     * Hook to add custom listeners after standard one were added.
     *
     * @author Cedric Chantepie 
     */
    public static interface PostAddListenerHook {
	
	/**
	 * Adds custom listeners through passed |registry| 
	 * after required listeners were added.
	 *
	 * @param observedElmt Object path element at which listeners are added
	 * @param registry Listener registry (inout)
	 */
	public void afterAddListeners(ObjectPathElement observedElmt, Map<BindingListenerCategory,ArrayList<Object>> registry);

    } // end of class PostAddListenerHook
} // end of class ObjectPathElement
