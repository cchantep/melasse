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
    extends BindingListenerSupport 
    implements ItemListener {

    // --- Properties ---

    /**
     * Property name
     */
    private String property = null;

    // --- Constructors ---

    /**
     * Bulk constructor.
     *
     * @param setter Setter used to propagate value
     * @param property Either "selectedItem" or "selectedObjects"
     */
    protected BindingComboBoxItemListener(Setter setter,
					  String property) {

	super(setter);

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
    public void itemStateChanged(ItemEvent evt) {
	this.logger.log(Level.FINER,
			"evt = {0}", evt);

	Object sourceValue = null;
	Object source = evt.getSource();

	this.logger.log(Level.FINER, 
			"source = {0}", source);

	if (!(source instanceof JComboBox)) {
	    this.logger.log(Level.WARNING,
			    "Unsupported spinner component: {0}",
			    source);

	    return;
	} // end of else

	sourceValue = Binder.getValue(source, this.property);

	this.logger.log(Level.FINER, 
			"sourceValue = {0}", sourceValue);

	setValue(sourceValue);
    } // end of stateChanged
} // end of class BindingComboBoxItemListener
