package melasse.swing;

import java.beans.PropertyChangeListener;

import javax.swing.DefaultListSelectionModel;

import melasse.PropertyChangeSupport;

/**
 * List selection model with property change support.
 *
 * @author Cedric Chantepie 
 */
public class ListSelectionModel extends DefaultListSelectionModel {
    // --- Properties ---

    /**
     */
    private final PropertyChangeSupport<Object> pcs;

    /**
     * Max selection index
     */
    private int maxSelectionIndex = -1;

    /**
     * Min selection index
     */
    private int minSelectionIndex = -1;

    /**
     * Select empty?
     */
    private boolean selectionEmpty = false;
	
    // --- Constructors ---

    /**
     * No-arg constructor.
     */
    public ListSelectionModel() {
	super();

	this.pcs = new PropertyChangeSupport<Object>(this);
	this.maxSelectionIndex = super.getMaxSelectionIndex();
	this.minSelectionIndex = super.getMinSelectionIndex();
	this.selectionEmpty = super.isSelectionEmpty();
    } // end of <init>

    // --- Property change support ---

    /**
     * Adds property change listener.
     *
     * @param listener Property change listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
	this.pcs.addPropertyChangeListener(listener);
    } // end of addPropertyChangeListener

    /**
     * Removes property change listener.
     *
     * @param listener Property change listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
	this.pcs.removePropertyChangeListener(listener);
    } // end of removePropertyChangeListener

    // ---

    /**
     * {@inheritDocs}
     */
    protected void fireValueChanged(boolean isAdjusting) {
	int id = System.identityHashCode(new Object());

	super.fireValueChanged(isAdjusting);

	pcs.firePropertyChange("minSelectionIndex", 
			       this.minSelectionIndex,
			       super.getMinSelectionIndex());

	pcs.firePropertyChange("maxSelectionIndex", 
			       this.maxSelectionIndex,
			       super.getMaxSelectionIndex());

	pcs.firePropertyChange("selectionEmpty", 
			       this.selectionEmpty,
			       super.isSelectionEmpty());

	this.minSelectionIndex = super.getMinSelectionIndex();
	this.maxSelectionIndex = super.getMaxSelectionIndex();
	this.selectionEmpty = super.isSelectionEmpty();
    } // end of fireValueChanged
    
    /**
     * {@inheritDocs}
     */
    protected void fireValueChanged(int firstIndex,
				    int lastIndex) {

	int id = System.identityHashCode(new Object());

	super.fireValueChanged(firstIndex, lastIndex);

	pcs.firePropertyChange("minSelectionIndex", 
			       this.minSelectionIndex,
			       super.getMinSelectionIndex());

	pcs.firePropertyChange("maxSelectionIndex", 
			       this.maxSelectionIndex,
			       super.getMaxSelectionIndex());

	pcs.firePropertyChange("selectionEmpty", 
			       this.selectionEmpty,
			       super.isSelectionEmpty());

	this.minSelectionIndex = super.getMinSelectionIndex();
	this.maxSelectionIndex = super.getMaxSelectionIndex();
	this.selectionEmpty = super.isSelectionEmpty();
    } // end of fireValueChanged
    
    /**
     * {@inheritDocs}
     */
    protected void fireValueChanged(int firstIndex,
				    int lastIndex,
				    boolean isAdjusting) {

	int id = System.identityHashCode(new Object());

	super.fireValueChanged(firstIndex, lastIndex, isAdjusting);

	pcs.firePropertyChange("minSelectionIndex", 
			       this.minSelectionIndex,
			       super.getMinSelectionIndex());

	pcs.firePropertyChange("maxSelectionIndex", 
			       this.maxSelectionIndex,
			       super.getMaxSelectionIndex());

	pcs.firePropertyChange("selectionEmpty", 
			       this.selectionEmpty,
			       super.isSelectionEmpty());

	this.minSelectionIndex = super.getMinSelectionIndex();
	this.maxSelectionIndex = super.getMaxSelectionIndex();
	this.selectionEmpty = super.isSelectionEmpty();
    } // end of fireValueChanged

    /**
     * {@inheritDocs}
     */
    public void setAnchorSelectionIndex(int anchorIndex) {
	PropertyChangeSupport.PropertyEditSession s = 
	    pcs.propertyWillChange("anchorSelectionIndex");

	super.setAnchorSelectionIndex(anchorIndex);

	s.propertyDidChange();
    } // end of setAnchorSelectionIndex

    /**
     * {@inheritDocs}
     */
    public void setLeadSelectionIndex(int leadIndex) {
	PropertyChangeSupport.PropertyEditSession s = 
	    pcs.propertyWillChange("leadSelectionIndex");

	super.setLeadSelectionIndex(leadIndex);

	s.propertyDidChange();
    } // end of setLeadSelectionIndex

    /**
     * {@inheritDocs}
     */
    public void setValueIsAdjusting(boolean valueIsAdjusting) {
	PropertyChangeSupport.PropertyEditSession s = 
	    pcs.propertyWillChange("valueIsAdjusting");

	super.setValueIsAdjusting(valueIsAdjusting);

	s.propertyDidChange();
    } // end of setValueIsAdjusting

    /**
     * {@inheritDocs}
     */
    public void setLeadAnchorNotificationEnabled(boolean flag) {
	PropertyChangeSupport.PropertyEditSession s = 
	    pcs.propertyWillChange("leadAnchorNotificationEnabled");

	super.setLeadAnchorNotificationEnabled(flag);

	s.propertyDidChange();
    } // end of setLeadAnchorNotificationEnabled

    /**
     * {@inheritDocs}
     */
    public void setSelectionMode(int selectionMode) {
	PropertyChangeSupport.PropertyEditSession s = 
	    pcs.propertyWillChange("selectionMode");

	super.setSelectionMode(selectionMode);

	s.propertyDidChange();
    } // end of setSelectionMode
} // end of class ListSelectionModel
