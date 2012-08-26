package melasse;

import java.util.ArrayList;
import java.util.Map;

import java.util.logging.Logger;
import java.util.logging.Level;

import java.awt.TextComponent;
import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JSpinner;

import javax.swing.text.JTextComponent;

/**
 * Add-listener hook for bindings.
 *
 * @author Cedric Chantepie 
 */
class BindingPostAddListenerHook 
    implements ObjectPathElement.PostAddListenerHook {

    // --- Properties ---

    /**
     * Associated path
     */
    private Binder.ObjectPath path = null;

    /**
     * Binding options
     */
    public BindingOptionMap options = null;

    /**
     * Logger
     */
    private Logger logger = null;

    // --- Constructors ---

    /**
     * Bulk constructor.
     *
     * @param path First element of associated object path
     * @param options Binding options
     */
    protected BindingPostAddListenerHook(Binder.ObjectPath path,
					 BindingOptionMap options) {

	this.path = path;
	this.options = options;

	this.logger = Logger.getLogger(/*LibraSwing*/"Melasse");
    } // end of <init>

    // ---

    /**
     * {@inheritDoc}
     */
    public void afterAddListeners(ObjectPathElement observedElmt,
				  final Map<BindingListenerCategory,ArrayList> registry) {

	this.logger.log(Level.FINER, 
			"options = {0}, observed element = {1}, associated path = {2}", 
			new Object[] { options, observedElmt, path });

	Object targetObject = observedElmt.getTargetObject();

	this.logger.log(Level.FINER,
			"target object = {0}", targetObject);

	String property = observedElmt.getProperty();

	this.logger.log(Level.FINER, "property = {0}", property);

	if (((targetObject instanceof TextComponent) ||
	     (targetObject instanceof JTextComponent)) &&
	    "text".equals(property)) {

	    this.logger.fine("Will add text listeners");

	    addTextListeners(observedElmt, registry);

	    return;
	} // end of if

	if ((targetObject instanceof JSpinner) &&
	    "value".equals(property)) {

	    this.logger.fine("Will add spinner listeners");

	    addSpinnerListeners(observedElmt, registry);

	    return;
	} // end of if

	if ((targetObject instanceof JComboBox) &&
	    ("selectedItem".equals(property) ||
	     "selectedObjects".equals(property))) {

	    this.logger.fine("Will add combobox listeners");

	    addComboBoxListeners(observedElmt, registry);

	    return;
	} // end of if

	if ((targetObject instanceof Component) &&
	    "size".equals(property)) {

	    this.logger.fine("Will add component size listener");

	    addComponentSizeListeners(observedElmt, registry);

	    return;
	} // end of if

	// ---

	if (!observedElmt.isEnd()) {
	    this.logger.finer("No extra listeners");
	    
	    return;
	} // end of if
	
	this.logger.fine("Will add property listeners");

	addPropertyListeners(observedElmt, registry);
    } // end of afterAddListeners

    /**
     * Adds listeners for property change.
     *
     * @param pathElmt
     * @param registry Listeners registry
     */
    private void addPropertyListeners(ObjectPathElement pathElmt,
				      final Map<BindingListenerCategory,ArrayList> registry) {

	this.logger.log(Level.FINER,
			"observed element = {0}, registry = {1}, path = {2}",
			new Object[] { pathElmt, registry, this.path });

	BindingListenerSupport.Setter setter = 
	    new BindingListenerSupport.Setter(this.path.start);

	BindingPropertyChangeListener plistener = 
	    new BindingPropertyChangeListener(setter,
					      pathElmt.getProperty());

	if (options.containsKey(BindingKey.ALLOW_NULL_CHANGE)) {
	    plistener.setAllowNullChange(true);
	} // end of if

	ArrayList list = registry.
	    get(BindingListenerCategory.PROPERTY_CHANGE);

	if (list != null) {
	    list.add(plistener);
	} else {
	    list = new ArrayList(1);

	    list.add(plistener);

	    registry.put(BindingListenerCategory.PROPERTY_CHANGE, list);
	} // end of else	
    } // end of addPropertyListeners

    /**
     * Adds listeners for combo box components.
     *
     * @param pathElmt
     * @param registry Listeners registry
     */
    private void addComboBoxListeners(ObjectPathElement pathElmt,
				      final Map<BindingListenerCategory,ArrayList> registry) {

	this.logger.log(Level.FINER,
			"path element = {0}, registry = {1}",
			new Object[] { pathElmt, registry });

	String property = pathElmt.getProperty();

	this.logger.log(Level.FINER,
			"property = {0}", property);

	BindingListenerSupport.Setter setter = 
	    new BindingListenerSupport.Setter(this.path.start);

	BindingComboBoxItemListener ilistener =
	    new BindingComboBoxItemListener(setter, property);

	ArrayList list = registry.
	    get(BindingListenerCategory.ITEM);

	if (list != null) {
	    list.add(ilistener);
	} else {
	    list = new ArrayList(1);

	    list.add(ilistener);

	    registry.put(BindingListenerCategory.ITEM, list);
	} // end of else	
    } // end of addComboBoxListeners

    /**
     * Adds listeners for spinner components.
     *
     * @param pathElmt
     * @param registry Listeners registry
     */
    private void addSpinnerListeners(ObjectPathElement pathElmt,
				     final Map<BindingListenerCategory,ArrayList> registry) {

	this.logger.log(Level.FINER,
			"path element = {0}, registry = {1}",
			new Object[] { pathElmt, registry });

	BindingListenerSupport.Setter setter = 
	    new BindingListenerSupport.Setter(this.path.start);

	BindingSpinnerChangeListener clistener =
	    new BindingSpinnerChangeListener(setter);

	ArrayList list = registry.
	    get(BindingListenerCategory.CHANGE);

	if (list != null) {
	    list.add(clistener);
	} else {
	    list = new ArrayList(1);

	    list.add(clistener);

	    registry.put(BindingListenerCategory.CHANGE, list);
	} // end of else	
    } // end of addSpinnerListeners

    /**
     * Adds listeners for component size.
     *
     * @param pathElmt
     * @param registry Listeners registry
     */
    private void addComponentSizeListeners(ObjectPathElement pathElmt,
					   final Map<BindingListenerCategory,ArrayList> registry) {

	this.logger.log(Level.FINER,
			"path element = {0}, registry = {1}",
			new Object[] { pathElmt, registry });

	BindingListenerSupport.Setter setter = 
	    new BindingListenerSupport.Setter(this.path.start);

	BindingComponentSizeListener slistener =
	    new BindingComponentSizeListener(setter);

	ArrayList list = registry.
	    get(BindingListenerCategory.COMPONENT);

	if (list != null) {
	    list.add(slistener);
	} else {
	    list = new ArrayList(1);

	    list.add(slistener);

	    registry.put(BindingListenerCategory.COMPONENT, list);
	} // end of else
    } // end of addComponentSizeListeners

    /**
     * Adds listeners for text components.
     *
     * @param pathElmt
     * @param registry Listeners registry
     */
    private void addTextListeners(ObjectPathElement pathElmt,
				  final Map<BindingListenerCategory,ArrayList> registry) {

	this.logger.log(Level.FINER,
			"path element = {0}, registry = {1}",
			new Object[] { pathElmt, registry });

	if (options.containsKey(TextBindingKey.
				CONTINUOUSLY_UPDATE_VALUE)) {

	    this.logger.finer("Add text listeners for continuous update");

	    BindingListenerSupport.Setter setter = 
		new BindingListenerSupport.Setter(this.path.start);

	    BindingKeyListener klistener = 
		new BindingKeyListener(setter);

	    ArrayList list = registry.
		get(BindingListenerCategory.KEY);

	    if (list != null) {
		list.add(klistener);
	    } else {
		list = new ArrayList(1);

		list.add(klistener);

		registry.put(BindingListenerCategory.KEY, list);
	    } // end of else
	    
	    return;
	} // end of if

	// ---

	this.logger.finer("Add text listeners without continuous update");

	BindingListenerSupport.ConditionalSetter setter = 
	    new BindingListenerSupport.ConditionalSetter(this.path.start,
							 pathElmt.getValue());

	BindingTextActionListener alistener =
	    new BindingTextActionListener(setter);

	BindingFocusListener flistener =
	    new BindingFocusListener(setter);

	ArrayList list = registry.
	    get(BindingListenerCategory.ACTION);

	if (list != null) {
	    list.add(alistener);
	} else {
	    list = new ArrayList(1);

	    list.add(alistener);

	    registry.put(BindingListenerCategory.ACTION, list);
	} // end of else

	list = registry.
	    get(BindingListenerCategory.FOCUS);

	if (list != null) {
	    list.add(flistener);
	} else {
	    list = new ArrayList(1);

	    list.add(flistener);

	    registry.put(BindingListenerCategory.FOCUS, list);
	} // end of else
    } // end of addTextListeners
} // end of class BindingPostAddListenerHook
