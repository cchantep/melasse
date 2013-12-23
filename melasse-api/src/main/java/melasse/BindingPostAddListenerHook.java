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
    private Logger logger = null;

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

	this.logger = Logger.getLogger("Melasse");
    } // end of <init>

    // ---

    /**
     * {@inheritDoc}
     */
    public void afterAddListeners(final ObjectPathElement observedElmt, final Map<BindingListenerCategory,ArrayList> registry) {

	this.logger.log(Level.FINER, "options = {0}, observed element = {1}, associated path = {2}", new Object[] { options, observedElmt, path });

	final Object targetObject = observedElmt.getTargetObject();

	this.logger.log(Level.FINER,
			"target object = {0}", targetObject);

	final String property = observedElmt.getProperty();

	this.logger.log(Level.FINER, "property = {0}", property);

	if ((targetObject instanceof TextComponent) &&
	    "text".equals(property)) {

	    this.logger.fine("Will add text listeners");

            final TextComponent textComp = (TextComponent) targetObject;
            final boolean editable = textComp.isEditable();
            
            if (editable) {
                addTextListeners(observedElmt, registry);
            } else {
                // TODO: Workaround - convert text event to property one
                final BindingTextListener tl = 
                    new BindingTextListener(textComp);

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
                // Workaround - redirect text event to property change
                final BindingDocumentListener dl = 
                    new BindingDocumentListener(textComp);

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
     * Adds listeners for property change.
     *
     * @param pathElmt
     * @param registry Listeners registry
     */
    private void addPropertyListeners(final ObjectPathElement pathElmt,
				      final Map<BindingListenerCategory,ArrayList> registry) {

	this.logger.log(Level.FINER,
			"observed element = {0}, registry = {1}, path = {2}",
			new Object[] { pathElmt, registry, this.path });

	final BindingListenerSupport.Setter setter = 
	    new BindingListenerSupport.Setter(this.path.start);

	final BindingPropertyChangeListener plistener = 
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
    private void addComboBoxListeners(final ObjectPathElement pathElmt,
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

	    final BindingListenerSupport.Setter setter = 
		new BindingListenerSupport.Setter(this.path.start);

	    final BindingKeyListener klistener = new BindingKeyListener(setter);
	    ArrayList list = registry.get(BindingListenerCategory.KEY);

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

	final BindingListenerSupport.ConditionalSetter setter = 
	    new BindingListenerSupport.ConditionalSetter(this.path.start,
							 pathElmt.getValue());

	final BindingTextActionListener alistener =
	    new BindingTextActionListener(setter);

	final BindingFocusListener flistener =
	    new BindingFocusListener(setter);

	ArrayList list = registry.get(BindingListenerCategory.ACTION);

	if (list != null) {
	    list.add(alistener);
	} else {
	    list = new ArrayList(1);

	    list.add(alistener);

	    registry.put(BindingListenerCategory.ACTION, list);
	} // end of else

	list = registry.get(BindingListenerCategory.FOCUS);

	if (list != null) {
	    list.add(flistener);
	} else {
	    list = new ArrayList(1);

	    list.add(flistener);

	    registry.put(BindingListenerCategory.FOCUS, list);
	} // end of else
    } // end of addTextListeners

    // --- Inner classes ---

    /**
     * Base for listener of converted text event.
     */
    private abstract class ConvertedTextEventListener<C extends Component> {

        // --- Properties ---

        /**
         * Text component
         */
        protected final C component;

        /**
         * Text before change
         */
        private String text;

        // --- Constructor ---

        /**
         * Bulk constructor.
         *
         * @param component Observed text component
         */
        ConvertedTextEventListener(final C component, final String text) {
            this.component = component;
            this.text = text;
        } // end of <init>

        // ---

        /**
         * Fire property change according document one.
         */
        protected void firePropertyChange(final String newText) {
            java.beans.PropertyChangeEvent event = null;

            synchronized (this) {
                event = new java.beans.
                    PropertyChangeEvent(this.component, "text", 
                                        this.text, newText);
                
                this.text = newText;
            } // end of sync

            for (final java.beans.PropertyChangeListener l : this.component.
                     getPropertyChangeListeners()) {

                l.propertyChange(event);
            } // end of for
        } // end of firePropertyChange
    } // end of class ConvertedTextEventListener

    /**
     * Listener for text event - converted as property change event.
     */
    private final class BindingTextListener 
        extends ConvertedTextEventListener<TextComponent>  
        implements java.awt.event.TextListener {

        // --- Constructors ---

        /**
         * Bulk constructor.
         *
         * @param component Observed text component
         */
        BindingTextListener(final TextComponent component) {
            super(component, component.getText());
        } // end of <init>

        // ---

        /**
         * {@inheritDoc}
         */
        public void textValueChanged(java.awt.event.TextEvent e) {
            firePropertyChange(this.component.getText());
        } // end of textValueChanged
    } // end of class BindingTextListener

    /**
     * Listener for document event - converted as property change event.
     */
    private final class BindingDocumentListener 
        extends ConvertedTextEventListener<JTextComponent> 
        implements javax.swing.event.DocumentListener {

        // --- Constructors ---

        /**
         * Bulk constructor.
         *
         * @param component Observed text component
         */
        BindingDocumentListener(final JTextComponent component) {
            super(component, component.getText());
        } // end of <init>

        // ---

        /**
         * {@inheritDoc}
         */
        public void insertUpdate(javax.swing.event.DocumentEvent e) {
            firePropertyChange(this.component.getText());
        } // end of insertUpdate

        /**
         * {@inheritDoc}
         */
        public void removeUpdate(javax.swing.event.DocumentEvent e) {
            firePropertyChange(this.component.getText());
        } // end of removeUpdate

        /**
         * {@inheritDoc}
         */
        public void changedUpdate(javax.swing.event.DocumentEvent e) {
            firePropertyChange(this.component.getText());
        } // end of changedUpdate
    } // end of BindingDocumentListener
} // end of class BindingPostAddListenerHook
