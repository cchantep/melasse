package melasse;

import java.util.ArrayList;
import java.util.Map;

import java.util.logging.Logger;
import java.util.logging.Level;

import java.awt.TextComponent;
import java.awt.Component;

import javax.swing.ListSelectionModel;
import javax.swing.JComboBox;
import javax.swing.JSpinner;

import javax.swing.text.JTextComponent;

import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

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
    private final Binder.ObjectPath path;

    /**
     * Binding options
     */
    public final BindingOptionMap options;

    /**
     * Logger
     */
    private final Logger logger;

    // --- Constructors ---

    /**
     * Bulk constructor.
     *
     * @param path First element of associated object path
     * @param options Binding options
     */
    protected BindingPostAddListenerHook(final Binder.ObjectPath path,
					 final BindingOptionMap options) {

	this.path = path;
	this.options = options;
	this.logger = Binder.getLogger(options);
    } // end of <init>

    // ---

    /**
     * {@inheritDoc}
     */
    public void afterAddListeners(final ObjectPathElement observedElmt, final Map<BindingListenerCategory,ArrayList<Object>> registry) {

	this.logger.log(Level.FINER, "options = {0}, observed element = {1}, associated path = {2}", new Object[] { options, observedElmt, path });

	final Object targetObject = observedElmt.getTargetObject();
	final String property = observedElmt.getProperty();

	this.logger.log(Level.FINER, 
                        "target object = {0}, property = {1}", 
                        new Object[] { targetObject, property });

        if ((targetObject instanceof ListSelectionModel) &&
            ("selectionEmpty".equals(property) ||
             "minSelectionIndex".equals(property) ||
             "maxSelectionIndex".equals(property))) {

            addListSelectionListeners(observedElmt, registry);
            
            return;
        } // end of if

	if ((targetObject instanceof TextComponent) &&
	    "text".equals(property)) {

	    this.logger.fine("Will add text listeners");

            final TextComponent textComp = (TextComponent) targetObject;
            final boolean editable = textComp.isEditable();
            
            if (editable) {
                addTextListeners(observedElmt, registry);
            } else {
                final BindingTextEventListener.TextListener tl = 
                    new BindingTextEventListener.TextListener(textComp, 
                                                              this.options);

                textComp.addTextListener(tl);

                addPropertyListeners(observedElmt, registry);
            } // end of else

	    return;
	} // end of if

	if ((targetObject instanceof JTextComponent) && 
            "text".equals(property)) {

	    this.logger.fine("Will add text listeners");

            final JTextComponent textComp = (JTextComponent) targetObject;
            final boolean editable = textComp.isEditable();
            
            if (editable) {
                addTextListeners(observedElmt, registry);
            } else {
                final BindingTextEventListener.DocumentListener dl = 
                    new BindingTextEventListener.DocumentListener(textComp, 
                                                                  this.options);

                textComp.getDocument().addDocumentListener(dl);

                addPropertyListeners(observedElmt, registry);
            } // end of else

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
     * Adds listeners for list selection.
     *
     * @param pathElmt
     * @param registry Listeners registry
     */
    private void addListSelectionListeners(final ObjectPathElement pathElmt, final Map<BindingListenerCategory,ArrayList<Object>> registry) {

	this.logger.log(Level.FINER,
			"observed element = {0}, registry = {1}, path = {2}",
			new Object[] { pathElmt, registry, this.path });

	final Setter<Object> setter = new Setter<Object>(this.path.start);
        addListener(registry, BindingListenerCategory.LIST_SELECTION,
                    new BindingListSelectionListener(setter, 
                                                     pathElmt.getProperty(), 
                                                     this.options));

    } // end of addListSelectionListeners

    /**
     * Adds listeners for property change.
     *
     * @param pathElmt
     * @param registry Listeners registry
     */
    private void addPropertyListeners(final ObjectPathElement pathElmt, final Map<BindingListenerCategory,ArrayList<Object>> registry) {

	this.logger.log(Level.FINER,
			"observed element = {0}, registry = {1}, path = {2}",
			new Object[] { pathElmt, registry, this.path });

	final Setter<Object> setter = new Setter<Object>(this.path.start);
	final BindingPropertyChangeListener pl = 
	    new BindingPropertyChangeListener(setter,
					      pathElmt.getProperty(),
                                              this.options);

	if (options.containsKey(BindingKey.ALLOW_NULL_CHANGE)) {
	    pl.setAllowNullChange(true);
	} // end of if

        addListener(registry, BindingListenerCategory.PROPERTY_CHANGE, pl);
    } // end of addPropertyListeners

    /**
     * Adds listeners for combo box components.
     *
     * @param pathElmt
     * @param registry Listeners registry
     */
    private void addComboBoxListeners(final ObjectPathElement pathElmt, final Map<BindingListenerCategory,ArrayList<Object>> registry) {

	this.logger.log(Level.FINER,
			"path element = {0}, registry = {1}",
			new Object[] { pathElmt, registry });

	final String property = pathElmt.getProperty();

	this.logger.log(Level.FINER, "property = {0}", property);

	final Setter<Object> setter = new Setter<Object>(this.path.start);
        addListener(registry, BindingListenerCategory.ITEM,
                    new BindingComboBoxItemListener(setter, 
                                                    property, 
                                                    this.options));

    } // end of addComboBoxListeners

    /**
     * Adds listeners for spinner components.
     *
     * @param pathElmt
     * @param registry Listeners registry
     */
    private void addSpinnerListeners(final ObjectPathElement pathElmt, final Map<BindingListenerCategory,ArrayList<Object>> registry) {

	this.logger.log(Level.FINER,
			"path element = {0}, registry = {1}",
			new Object[] { pathElmt, registry });

	final Setter<Object> setter = new Setter<Object>(this.path.start);
        addListener(registry, BindingListenerCategory.CHANGE,
                    new BindingSpinnerChangeListener(setter, this.options));

    } // end of addSpinnerListeners

    /**
     * Adds listeners for component size.
     *
     * @param pathElmt
     * @param registry Listeners registry
     */
    private void addComponentSizeListeners(final ObjectPathElement pathElmt, final Map<BindingListenerCategory,ArrayList<Object>> registry) {

	this.logger.log(Level.FINER,
			"path element = {0}, registry = {1}",
			new Object[] { pathElmt, registry });

	final Setter<Object> setter = new Setter<Object>(this.path.start);
        addListener(registry, BindingListenerCategory.COMPONENT,
                    new BindingComponentSizeListener(setter, this.options));

    } // end of addComponentSizeListeners

    /**
     * Adds listeners for text components.
     *
     * @param pathElmt
     * @param registry Listeners registry
     */
    private void addTextListeners(final ObjectPathElement pathElmt, final Map<BindingListenerCategory,ArrayList<Object>> registry) {

	this.logger.log(Level.FINER,
			"path element = {0}, registry = {1}",
			new Object[] { pathElmt, registry });

	if (options.containsKey(TextBindingKey.
				CONTINUOUSLY_UPDATE_VALUE)) {

	    this.logger.finer("Add text listeners for continuous update");

	    final Setter<Object> setter = new Setter<Object>(this.path.start);
            addListener(registry, BindingListenerCategory.KEY,
                        new BindingKeyListener(setter, this.options));
	    
	    return;
	} // end of if

	// ---

	this.logger.finer("Add text listeners without continuous update");

	final ConditionalSetter<Object> setter = 
	    new ConditionalSetter<Object>(this.path.start, pathElmt.getValue());

        addListener(registry, BindingListenerCategory.ACTION, 
                    new BindingTextActionListener(setter, this.options));

        addListener(registry, BindingListenerCategory.FOCUS,
                    new BindingFocusListener(setter, this.options));

    } // end of addTextListeners

    /**
     * Adds |listener| of given |category| into in-memory |registry|.
     *
     * @return Listener list
     */
    private static ArrayList<Object> addListener(final Map<BindingListenerCategory,ArrayList<Object>> registry, final BindingListenerCategory category, final Object listener) {

        if (!registry.containsKey(category)) {
            registry.put(category, new ArrayList<Object>(1));
        } // end of if
        
        final ArrayList<Object> list = registry.
            get(category);

        list.add(listener);

        return list;
    } // end of addListener
} // end of class BindingPostAddListenerHook
