package melasse.swing;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

/**
 * Spinner number model with property change support for value.
 * 
 * @author Cedric Chantepie 
 */
public class SpinnerNumberModel 
    extends javax.swing.SpinnerNumberModel
    implements ChangeListener {

    // --- Properties ---

    /**
     * Property change support
     */
    private PropertyChangeSupport pcs = null;
    
    /**
     * Previous value
     */
    private Object previous = null;

    // --- Constructors ---

    /**
     * No-arg constructor.
     */
    public SpinnerNumberModel() {
	super();

	this.pcs = new PropertyChangeSupport(this);

	this.addChangeListener(this);

	this.previous = getValue();
    } // end of <init>

    // ---

    /**
     * Adds property change |listener|.
     *
     * @param listener Listener ed to be added
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
	this.pcs.addPropertyChangeListener(listener);
    } // end of addPropertyChangeListener

    /**
     * Removes property change |listener|.
     *
     * @param listener Listener ed to be removeed
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
	this.pcs.removePropertyChangeListener(listener);
    } // end of removePropertyChangeListener

    /**
     * {@inheritDoc}
     */
    public void stateChanged(ChangeEvent e) {
	Object newValue = getValue();

	this.pcs.firePropertyChange("value", previous, newValue);

	this.previous = newValue;
    } // end of stateChanged
} // end of class SpinnerNumberModel
