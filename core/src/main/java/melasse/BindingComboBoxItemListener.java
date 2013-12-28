package melasse;

import java.util.logging.Level;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

import javax.swing.JComboBox;

/**
 * Combo box item listener binding selectedItem/selectedObjects 
 * from combo box to an object property.
 *
 * @author Cedric Chantepie 
 */
class BindingComboBoxItemListener 
    extends BindingListenerSupport<Object> implements ItemListener {

    // --- Properties ---

    /**
     * Property name
     */
    private final String property;

    // --- Constructors ---

    /**
     * Bulk constructor.
     *
     * @param setter Setter used to propagate value
     * @param property Either "selectedItem" or "selectedObjects"
     */
    protected BindingComboBoxItemListener(final Setter<Object> setter,
					  final String property,
                                          final BindingOptionMap options) {

	super(setter, options);

	if (!"selectedItem".equals(property) && 
	    !"selectedObjects".equals(property)) {

	    throw new IllegalArgumentException("Unsupport combo box " +
					       "property: " + property);

	} // end of if

	this.property = property;
    } // end of <init>

    // ---

    /**
     * Changes object property, using transformer if given, 
     * from spinner value change.
     */
    public void itemStateChanged(final ItemEvent evt) {
	final Object source = evt.getSource();

	this.logger.log(Level.FINER, "event = {0}, source = {1}", 
                        new Object[] { evt, source });

	if (!(source instanceof JComboBox)) {
	    this.logger.log(Level.WARNING,
			    "Unsupported spinner component: {0}", source);

	    return;
	} // end of else

        // ---

	final Object sourceValue = Binder.getValue(source, this.property);

	this.logger.log(Level.FINER, "sourceValue = {0}", sourceValue);

	setValue(sourceValue);
    } // end of stateChanged
} // end of class BindingComboBoxItemListener
