package melasse.swing;

import java.util.Vector;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.DefaultComboBoxModel;

/**
 * Combo box model with property change support for "selectedItem".
 *
 * @author Cedric Chantepie 
 */
public class ComboBoxModel<E> extends DefaultComboBoxModel<E> {
    // --- Properties ---

    /**
     * Property change support
     */
    private final PropertyChangeSupport pcs;

    // --- Constructors ---

    /**
     * No-arg constructor
     */
    public ComboBoxModel() {
	super();

	this.pcs = new PropertyChangeSupport(this);
    } // end of <init>

    /**
     * Constructs model from object |array|.
     *
     * @param array Model object array
     */
    public ComboBoxModel(final E[] array) {
	super(array);

	this.pcs = new PropertyChangeSupport(this);
    } // end of <init>

    /**
     * Constructs model from |vector|.
     *
     * @param vector Model vector
     */
    public ComboBoxModel(Vector<E> vector) {
	super(vector);

	this.pcs = new PropertyChangeSupport(this);
    } // end of <init>

    // ---

    /**
     * Adds property change |listener|.
     *
     * @param listener Listener to be added
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
	this.pcs.addPropertyChangeListener(listener);
    } // end of addPropertyChangeListener

    /**
     * Removes property change |listener|.
     *
     * @param listener Listener to be removed
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
	this.pcs.removePropertyChangeListener(listener);
    } // end of removePropertyChangeListener

    /**
     * {@inheritDoc}
     */
    public void setSelectedItem(Object anObject) {
	Object old = this.getSelectedItem();

	super.setSelectedItem(anObject);

	this.pcs.firePropertyChange("selectedItem", old, anObject);
    } // end of setSelectedItem
} // end of class ComboBoxModel
