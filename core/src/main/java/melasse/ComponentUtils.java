package melasse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Arrays;

import java.util.logging.Logger;
import java.util.logging.Level;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.lang.ref.WeakReference;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import java.awt.Component;
import java.awt.Dimension;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.swing.GroupLayout;

import static javax.swing.SwingConstants.HORIZONTAL;
import static javax.swing.SwingConstants.VERTICAL;

/**
 * Utility class for UI components.
 *
 * @author Cedric Chantepie 
 */
public class ComponentUtils {
    // --- Shared ---

    /**
     * Linking group
     */
    private static final ArrayList<ComponentGroup> groups;

    /**
     * Mediator factory
     */
    private static ComponentMediatorFactory mediatorFactory = null;

    static {
	groups = new ArrayList<ComponentGroup>();
    } // end of <static>

    // ---

    /**
     * Links UI components pointed by |paths| so that they their size 
     * keep the same max one.
     *
     * @param flags Linking flags
     * @param pathBases Base objects of |paths|
     * @param paths Component paths
     * @see #HORIZONTAL
     * @see #VERTICAL
     * @todo MINIMIZE flag
     */
    public static void linkSize(final int flags, 
				final Object[] pathBases,
				final String[] paths) {

	Logger logger = Logger.getLogger(/*LibraSwing*/"Melasse");

	logger.log(Level.FINER, "flags = {0}", flags);

	if (pathBases.length != paths.length) {
	    throw new IllegalArgumentException("Paths and bases do not " +
					       "correspond: " + pathBases.length +
					       ", " + paths.length);

	} // end of if

	// ---

	final ArrayList<WeakReference<Object>> baseRefs = 
	    new ArrayList<WeakReference<Object>>(pathBases.length);

	final ArrayList<String> pathList = new ArrayList<String>(paths.length);

	for (int i = 0; i < pathBases.length; i++) {
	    baseRefs.add(new WeakReference<Object>(pathBases[i]));
	    pathList.add(paths[i]);
	} // end of for

	final ComponentGroup grp = 
            new ComponentGroup(flags, baseRefs, pathList);

	logger.log(Level.FINER, "component group = {0}", grp);

	groups.add(grp);

	logger.finer("Will install component group");

	grp.install();
    } // end of linkSize

    // --- Inner classes ---

    /**
     * Component group to link sizes.
     *
     * @author Cedric Chantepie 
     */
    public static class ComponentGroup 
	extends ComponentAdapter implements ObjectPathListener {

	// --- Properties ---

	/**
	 */
	private final int flags;

	/**
	 * Weak references to objects used as path bases
	 */
	private ArrayList<WeakReference<Object>> baseRefs = null;

	/**
	 * Paths evaluated from base objects
	 */
	private ArrayList<String> paths = null;

	/**
	 * Component for which initial size is still waited
	 */
	private ArrayList<Integer> waitedComponents = null;

	/**
	 * Observed paths
	 */
	private ArrayList<Binder.ObjectPath> observedPaths = null;

	/**
	 * Size common to all components,
	 * the greater one
	 */
	private Dimension commonSize = null;

	/**
	 * Component whose initial size is common one
	 */
	private WeakReference<Object> componentRef = null;

	/**
	 * Group logger
	 */
	private final Logger logger;

	/**
	 * Property change support
	 */
	private final PropertyChangeSupport pcs;

	// --- Constructors ---

	/**
	 * Bulk constructor.
	 *
	 * @param flags Linking flags
	 * @param baseRefs Weak references to base objects
	 * @paral paths Paths to target objects, from base ones
	 * @see #HORIZONTAL
	 * @see #VERTICAL
	 */
	protected ComponentGroup(int flags,
				 ArrayList<WeakReference<Object>> baseRefs,
				 ArrayList<String> paths) {

	    if (baseRefs == null) {
		throw new IllegalArgumentException("Invalid component references: " +
						   baseRefs);

	    } // end of if

	    if (paths == null) {
		throw new IllegalArgumentException("Invalid paths: " + paths);
	    } // end of if

	    // ---

	    this.baseRefs = baseRefs;
	    this.paths = paths;
	    this.flags = flags;

	    this.pcs = new PropertyChangeSupport(this);
	    this.logger = Logger.getLogger("Melasse");

	    this.logger.finer("Inited");
	} // end of <init>

	// --- Properties accessors ---

	/**
	 * Returns size commons to all components.
	 */
	public Dimension getCommonSize() {
	    return this.commonSize;
	} // end of getCommonSize

	/**
	 * Returns width from common size.
	 *
	 * @see #getCommonSize
	 */
	public double getCommonWidth() {
	    if (this.commonSize == null) {
		return (double) 0;
	    } // end of if

	    return this.commonSize.getWidth();
	} // end of getCommonWidth

	/**
	 * Returns height from common size.
	 *
	 * @see #getCommonSize
	 */
	public double getCommonHeight() {
	    if (this.commonSize == null) {
		return (double) 0;
	    } // end of if

	    return this.commonSize.getHeight();
	} // end of getCommonHeight

	// ---

	/**
	 * Install this group.
	 */
	protected void install() {
	    int observed = 0;

	    synchronized(this.baseRefs) {
		synchronized(this.paths) {
		    final Iterator<WeakReference<Object>> iter = 
                        this.baseRefs.iterator();
		    
		    this.observedPaths = 
			new ArrayList<Binder.ObjectPath>(this.paths.size());
		    
		    WeakReference<Object> r;
		    Object base;
		    String path;
		    String[] ps;
		    Binder.ObjectPath objectPath;
		    while (iter.hasNext()) {
			r = iter.next();
			base = r.get();
			iter.remove();

			this.logger.log(Level.FINER, "base = {0}", base);

			path = this.paths.get(0);
			this.paths.remove(0);

			this.logger.log(Level.FINER, "path = {0}", path);

			objectPath = Binder.makeObjectPath(base, path, null);

			this.logger.log(Level.FINER,
					"object path = {0}", objectPath);
			
			this.observedPaths.add(objectPath);

			objectPath.end.addObjectPathListener(this);

			observed++;
		    } // end of for
		} // end of sync
	    } // end of sync

	    // Finalize
	    this.baseRefs = null;
	    this.paths = null;

	    if (observed == 0) {
		groups.remove(this);

		return;
	    } // end of if

	    // ---

	    this.waitedComponents = new ArrayList<Integer>(observed);

	    for (Binder.ObjectPath p : this.observedPaths) {
		if (p.end.getValue() != null) {
		    this.pathCompleted(p.end);
		} // end of if
	    } // end of for
	} // end of install

	/**
	 * Path to size property, |element| belongs to, is completed.
	 */
	public final void pathCompleted(ObjectPathElement element) {
	    this.logger.log(Level.FINER, "element = {0}", element);

	    element.removeObjectPathListener(this);

	    if (this.waitedComponents == null) {
		this.logger.finer("All waited components are already known");

		return;
	    } // end of if

	    // ---

	    Object component = element.getValue();
	    // assert component != null;

	    Integer h = new Integer(System.identityHashCode(component));

	    this.logger.log(Level.FINER, 
			    "component = {0}, hash code = {1}", 
			    new Object[] { component, h });

	    this.waitedComponents.add(h);

	    addComponentSizeListeners(component);
	} // end of pathCompleted

	// --- Initial size observation ---

	/**
	 * Adds listeners on |component| so that this group was notified
	 * when size changes.
	 * Can be overriden for specific component
	 *
	 * @param component Observed component
	 */
	protected void addComponentSizeListeners(Object component) {
	    if (!Binder.addListener(component,
				    BindingListenerCategory.COMPONENT,
				    this, null)) {

		this.logger.log(Level.FINER,
				"Cannot add listener on component: {0}",
				component);

		return;
	    } // end of if
	} // end of addComponentSizeListeners

	/**
	 * Listens initial size at showing.
	 */
	public void componentShown(ComponentEvent evt) {
	    logger.log(Level.FINER, "evt = {0}", evt);
	
	    sizeMayHaveChanged(evt.getComponent());
	} // end of componentShown
    
	/**
	 * Size has changed.
	 */
	public void componentResized(ComponentEvent evt) {
	    logger.log(Level.FINER, "evt = {0}", evt);

	    sizeMayHaveChanged(evt.getComponent());
	} // end of componentResized

	/**
	 * A |component| may have change its size.
	 */
	protected void sizeMayHaveChanged(Component component) {
	    logger.log(Level.FINER, "component = {0}", component);

	    // assert component != null
	    Integer h = new Integer(System.identityHashCode(component));

	    this.logger.log(Level.FINER, 
			    "component = {0}, hash code = {1}", 
			    new Object[] { component, h });

	    synchronized(this.waitedComponents) {
		if (!this.waitedComponents.contains(h)) {
		    this.logger.log(Level.FINER,
				    "Skip already known component: {0}", h);

		    return;
		} // end of if

		Dimension s = component.getSize();

		logger.log(Level.FINER, "component size = {0}", s);

		this.waitedComponents.remove(h);
		component.removeComponentListener(this);

		if (isGreaterThan(s, this.commonSize)) {
		    this.logger.finer("Skip lesser size");

		    this.commonSize = s;

		    if (this.componentRef != null) {
			this.componentRef.clear();
		    } // end of if

		    this.componentRef = new WeakReference<Object>(component);

		    this.logger.log(Level.FINER, "Set new common size: {0}", 
                                    this.commonSize);

		} // end of if	    
	    } // end of sync

	    // Finalize
	    if (!this.waitedComponents.isEmpty()) {
		this.logger.log(Level.FINER, "Wait more component: {0}", 
				this.waitedComponents.size());

		return;
	    } // end of if

	    this.waitedComponents.clear();

	    bind();
	} // end of componentResized

	// --- Binding ---
	
	/**
	 * Binds component sizes, according flags, when initial sizes
	 * are known for all components.
	 */
	private void bind() {
	    // Binding options
	    MediatorFactory mediatorFactory =
		ComponentMediatorFactory.getInstance();

	    BindingOptionMap options = new BindingOptionMap().
		add(BindingKey.TARGET_MODE).
		add(BindingKey.OUTPUT_MEDIATOR_FACTORY, mediatorFactory);

	    // Self path
	    Binder.ObjectPath selfPath = null;

	    this.logger.log(Level.FINER, "common size = {0}", this.commonSize);

	    if ((this.flags & HORIZONTAL) == HORIZONTAL &&
		(this.flags & VERTICAL) == VERTICAL) {

		this.logger.finer("Will bind in two dimension");

		selfPath = Binder.makeObjectPath(this, "commonSize", null);
	    } else if ((this.flags & HORIZONTAL) == HORIZONTAL) {
		this.logger.finer("Will only bind width");

		selfPath = Binder.makeObjectPath(this, "commonWidth", null);
	    } else {
		this.logger.finer("Wil only bind height");

		selfPath = Binder.makeObjectPath(this, "commonHeight", null);
	    } // end of else

	    this.logger.log(Level.FINER, "self path = {0}", selfPath);

	    // Observed paths
	    Object refComp = this.componentRef.get();

	    this.logger.log(Level.FINER, 
			    "reference component = {0}", refComp);

	    Iterator<Binder.ObjectPath> iter = this.observedPaths.iterator();

	    Binder.ObjectPath path;
	    String cp;
	    Object base;
	    Object component;
	    ObjectPathElement sizeElmt;
	    String[] ps;
	    String property;
	    while (iter.hasNext()) {
		path = iter.next();
		
		this.logger.log(Level.FINER, "path = {0}", path);
		
		iter.remove();

		component = path.end.getValue();

		if (component.equals(refComp)) {
		    this.logger.finer("Skip reference component");

		    continue;
		} // end of if

		// ---

		base = path.start.getTargetObject();
		cp = componentPath(path);

		this.logger.log(Level.FINER, 
				"base = {0}, component = {1}, component path = {2}", new Object[] { base, component, cp });

		ps = sizeProperties(component);

		for (int i = 0; i < ps.length; i++) {
		    this.logger.log(Level.FINER, "size path = {0}", ps[i]);

		    if ((this.flags & HORIZONTAL) == HORIZONTAL &&
			(this.flags & VERTICAL) == VERTICAL) {

			property = ps[i];
		    } else if ((this.flags & HORIZONTAL) == HORIZONTAL) {
			property = widthPath(ps[i]);
		    } else {
			property = heightPath(ps[i]);
		    } // end of else

		    this.logger.log(Level.FINER, 
				    "size property = {0}", property);

		    if (i == 0) { 
			sizeElmt = 
                            new ObjectPathElement(component, property, null);

			path.end.setNextElement(sizeElmt);
		    } else {
			path = Binder.
                            makeObjectPath(base, cp + '.' + property, null);

			sizeElmt = path.end;
		    } // end of else

		    this.logger.log(Level.FINER, 
				    "path = {0}, size element = {1}", 
				    new Object[] { path, sizeElmt });

		    Binder.bind(selfPath, path, options);
		} // end of for
	    } // end of while
	} // end of bind

	/**
	 * Returns path to component from property |path|.
	 *
	 * @param path Path to property
	 * @return Path as string to component
	 */
	private String componentPath(Binder.ObjectPath path) {
	    this.logger.log(Level.FINER, "path = {0}", path);

	    StringBuffer buff = new StringBuffer(path.start.getProperty());
	    ObjectPathElement elmt = path.start.getNext();

	    while (elmt != null) {
		buff.append('.').append(elmt.getProperty());

		elmt = elmt.getNext();
	    } // end of while

	    this.logger.log(Level.FINER, "buff = {0}", buff);

	    return buff.toString();
	} // end of componentPath

	/**
	 * Returns name of size properties on |component|.
	 *
	 * @param component Component
	 * @return Size properties names
	 */
	protected String[] sizeProperties(Object component) {
	    return new String[] { "size", "preferredSize" };
	} // end of sizeProperties

	/**
	 * Returns path to width from |path| to size property.
	 *
	 * @param path Path to size property
	 */
	protected String widthPath(String path) {
	    if ("preferredSize".equals(path)) {
		return "preferredWidth";
	    } // end of if

	    return "width";
	} // end of widthPath

	/**
	 * Returns path to height from |path| to size property.
	 *
	 * @param path Path to size property
	 */
	protected String heightPath(String path) {
	    if ("preferredSize".equals(path)) {
		return "preferredHeight";
	    } // end of if

	    return "height";
	} // end of heightPath

	/**
	 * Checks whether |size| needs change according |common| one.
	 * Change is needed if and only if |size| is greater than |common| one,
	 * according linking flags.
	 *
	 * @param common Common size
	 * @param size New size
	 */
	protected boolean needChange(Dimension common,
				     Dimension size) {

	    this.logger.log(Level.FINER,
			    "common = {0}, size = {1}",
			    new Object[] { common, size });

	    if ((this.flags & HORIZONTAL) == HORIZONTAL ||
		(this.flags & VERTICAL) == VERTICAL) {

		this.logger.finer("Will check change need in two dimensions");

		if ((common == null && size == null) ||
		    (common != null && common.equals(size))) {

		    return false;
		} else {
		    return true;
		} // end of else
	    } else if ((this.flags & HORIZONTAL) == HORIZONTAL) {
		double sw = (size != null) ? size.getWidth() : (double) -1;
		double cw = (common != null) ? common.getWidth() : (double) -1;

		this.logger.log(Level.FINER,
				"common width = {0}, new width = {1}",
				new Double[] { 
				    new Double(cw), new Double(sw)
				});

		return (sw != cw);
	    } else {
		double sh = (size != null) ? size.getHeight() : (double) -1;
		double ch = (common != null) ? common.getHeight() : (double) -1;

		this.logger.log(Level.FINER,
				"common height = {0}, new height = {1}",
				new Double[] { 
				    new Double(ch), new Double(sh)
				});

		return (sh != ch);
	    } // end of else
	} // end of needChange

	/**
	 * Checks whether |s1| is greater than |s1|, 
	 * according linking flags.
	 *
	 * @param s1 First size
	 * @param s2 Second size
	 * @return true if s1 is greater than s2
	 */
	protected boolean isGreaterThan(Dimension s1, Dimension s2) {
	    this.logger.log(Level.FINER, 
			    "first size = {0}, second size = {1}",
			    new Object[] { s1, s2 });

	    if (s1 == null) {
		this.logger.finer("First size is null");

		return false;
	    } // end of if

	    if (s2 == null) {
		this.logger.finer("Second size is null");

		return true;
	    } // end of if

	    double a1 = 0;
	    double a2 = 0;

	    if ((this.flags & HORIZONTAL) == HORIZONTAL ||
		(this.flags & VERTICAL) == VERTICAL) {

		this.logger.finer("Determines surface according two dimensions");

		a1 = s1.getWidth() * s1.getHeight();
		a2 = s2.getWidth() * s2.getHeight();
	    } else if ((this.flags & HORIZONTAL) == HORIZONTAL) {

		this.logger.finer("Determines surface horizontally");

		a1 = s1.getWidth();
		a2 = s2.getWidth();
	    } else {
		this.logger.finer("Determines surface vertically");

		a1 = s1.getHeight();
		a2 = s2.getHeight();
	    } // end of else if

	    this.logger.log(Level.FINER,
			    "First surface = {0}, second surface = {1}",
			    new Double[] {
				new Double(a1), new Double(a2)
			    });

	    return (a1 > a2);
	} // end of isGreaterThan

	// --- Property change support ---

	/**
	 * Adds property change |listener|.
	 *
	 * @param listener Property change listener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
	    this.pcs.addPropertyChangeListener(listener);
	} // end of addPropertyChangeListener

	/**
	 * Removes property change |listener|.
	 *
	 * @param listener Property change listener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
	    this.pcs.removePropertyChangeListener(listener);
	} // end of removePropertyChangeListener
    } // end of class ComponentGroup

    /**
     * Binding mediator factory for component utility.
     *
     * @author Cedric Chantepie 
     */
    private static class ComponentMediatorFactory implements MediatorFactory {
	// --- Constructors ---

	/**
	 * No-arg constructor.
	 */
	private ComponentMediatorFactory() {
	} // end of <init>
	
	/**
	 * {@inheritDoc}
	 */
	public static synchronized MediatorFactory getInstance() {
	    if (mediatorFactory == null) {
		mediatorFactory = new ComponentMediatorFactory();
	    } // end of if

	    return mediatorFactory;
	} // end of getInstance

	// ---

	/**
	 * Returns mediator for given |object|.
	 *
	 * @param object Object for which try to create a mediator
	 * @return Mediator, or null if according this factory no one 
	 * should be used for |object|.
	 */
	public MediatorFactory.MediatorObject getMediator(Object object) {
	    Logger logger = Logger.getLogger(/*LibraSwing*/"Melasse");

	    logger.log(Level.FINER, "object = {0}", object);

	    if (!(object instanceof Component)) {
		return null;
	    } // end of if

	    // ---

	    ComponentMediator m = new ComponentMediator((Component) object);
	    ArrayList<Class> ifaces = new ArrayList<Class>();

	    ifaces.add(MediatorFactory.MediatorObject.class);
	    ifaces.add(MComponent.class);
	    ifaces.addAll(Arrays.asList(object.getClass().getInterfaces()));

	    return (MediatorFactory.MediatorObject) Proxy.
		newProxyInstance(MediatorFactory.MediatorObject.class.
				 getClassLoader(),
				 ifaces.toArray(new Class[ifaces.size()]), m);
				 
	} // end of getMediator
    } // end of class ComponentMediatorFactory

    /**
     * Component mediator.
     *
     * @author Cedric Chantepie 
     */
    protected static class ComponentMediator 
	implements MediatorFactory.MediatorObject, InvocationHandler {

	// --- Properties ---

	/**
	 * Proxyfied component
	 */
	private final WeakReference<Component> compRef;

	/**
	 * Logger
	 */
	private final Logger logger;

	// --- Constructors ---

	/**
	 * Bulk constructor.
	 *
	 * @param component Target component
	 */
	public ComponentMediator(final Component component) {
	    if (component == null) {
		throw new IllegalArgumentException("Invalid component: " + 
						   component);

	    } // end of if

	    this.compRef = new WeakReference<Component>(component);
	    this.logger = Logger.getLogger("Melasse");

	    this.logger.finer("Inited");
	} // end of <init>

	// ---

	/**
	 * {@inheritDoc}
	 */
	public Component get() {
	    return (Component) this.compRef.get();
	} // end of get

	/**
	 * {@inheritDoc}
	 */
	public void release() {
	    this.compRef.clear();
	} // end of release

	/**
	 * {@inheritDoc}
	 */
	public Object invoke(Object proxy,
			     Method method,
			     Object[] args)
	    throws Throwable {

	    Object target = this.compRef.get();
	    
	    this.logger.log(Level.FINER, 
			    "method = {0}, target = {1}", 
			    new Object[] { method, target });

	    if (target == null) {
		throw new RuntimeException("Unbound component: " + target);
	    } // end of if

	    // ---

	    final String methodName = method.getName();

	    if ("get".equals(methodName)) {
		return get();
	    } else if ("release".equals(methodName)) {
		release();
		return null;
	    } else if ("setWidth".equals(methodName)) {
		setWidth(((Double) args[0]).doubleValue());
		return null;
	    } else if ("setPreferredWidth".equals(methodName)) {
		setPreferredWidth(((Double) args[0]).doubleValue());
		return null;
	    } else if ("setHeight".equals(methodName)) {
		setHeight(((Double) args[0]).doubleValue());
		return null;
	    } else if ("setPreferredHeight".equals(methodName)) {
		setPreferredHeight(((Double) args[0]).doubleValue());
		return null;
	    } else if ("toString".equals(methodName)) {
		return toString();
	    } // end of else if

	    this.logger.finer("Simply delegate method to component");

	    return method.invoke(target, args);
	} // end of invoke

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
	    return new ToStringBuilder(this).
		append("component", this.compRef.get()).
		toString();

	} // end of toString

	/**
	 * Sets |width|.
	 */
	public void setWidth(double width) {
	    Component target = get();
	    Dimension size = target.getSize();
	    
	    this.logger.log(Level.FINER, 
			    "width = {0}, target = {1}, size = {2}", 
			    new Object[] { new Double(width), target, size });

	    if (size == null) {
		this.logger.finer("Cannot set width on null size");

		return;
	    } // end of if

	    Dimension newSize = new Dimension((int) width, (int) size.getHeight());

	    this.logger.log(Level.FINER, "new size = {0}", newSize);

	    target.setSize(newSize);
	} // end of setWidth

	/**
	 * Sets |height|.
	 */
	public void setHeight(double height) {
	    Component target = get();
	    Dimension size = target.getSize();
	    
	    this.logger.log(Level.FINER, 
			    "height = {0}, target = {1}, size = {2}", 
			    new Object[] { new Double(height), target, size });

	    if (size == null) {
		this.logger.finer("Cannot set height on null size");

		return;
	    } // end of if

	    Dimension newSize = new Dimension((int) size.getWidth(), (int) height);

	    this.logger.log(Level.FINER, "new size = {0}", newSize);

	    target.setSize(newSize);
	} // end of setHeight

	/**
	 * Sets preferred |width|.
	 */
	public void setPreferredWidth(double width) {
	    Component target = get();
	    Dimension size = target.getPreferredSize();
	    
	    this.logger.log(Level.FINER, 
			    "width = {0}, target = {1}, size = {2}", 
			    new Object[] { new Double(width), target, size });

	    if (size == null) {
		this.logger.finer("Cannot set width on null size");

		return;
	    } // end of if

	    Dimension newPreferredSize = 
		new Dimension((int) width, (int) size.getHeight());

	    this.logger.log(Level.FINER, "new size = {0}", newPreferredSize);

	    target.setPreferredSize(newPreferredSize);
	} // end of setPreferredWidth

	/**
	 * Sets preferred |height|.
	 */
	public void setPreferredHeight(double height) {
	    Component target = get();
	    Dimension size = target.getPreferredSize();
	    
	    this.logger.log(Level.FINER, 
			    "height = {0}, target = {1}, size = {2}", 
			    new Object[] { new Double(height), target, size });

	    if (size == null) {
		this.logger.finer("Cannot set height on null size");

		return;
	    } // end of if

	    Dimension newPreferredSize = 
		new Dimension((int) size.getWidth(), (int) height);

	    this.logger.log(Level.FINER, "new size = {0}", newPreferredSize);

	    target.setPreferredSize(newPreferredSize);
	} // end of setPreferredHeight
    } // end of class ComponentMediator

    /**
     * Interface declaring publicily extra methods for proxified components.
     *
     * @author Cedric Chantepie 
     */
    public static interface MComponent extends IComponent {
	/**
	 * Sets |width|.
	 */
	public void setWidth(double width);

	/**
	 * Sets |height|.
	 */
	public void setHeight(double height);

	/**
	 * Sets preferred |width|.
	 */
	public void setPreferredWidth(double width);

	/**
	 * Sets preferred |height|.
	 */
	public void setPreferredHeight(double height);

    } // end of interface MComponent
} // end of class ComponentUtils
