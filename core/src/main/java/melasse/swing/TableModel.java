package melasse.swing;

import java.util.Vector;

import java.beans.PropertyChangeListener;

import javax.swing.table.DefaultTableModel;

import melasse.PropertyChangeSupport;

/**
 * Table model with property change support.
 *
 * @author Cedric Chantepie 
 * @todo Deprecate by implementing Table*ModelListener
 */
public class TableModel extends DefaultTableModel {
    // --- Properties ---

    /**
     * Property change support
     */
    private final PropertyChangeSupport<TableModel> pcs;

    // --- Constructors ---

    /**
     * {@inheritDocs}
     */
    public TableModel() {
	super();

	this.pcs = new PropertyChangeSupport<TableModel>(this);
    } // end of <init>

    /**
     * {@inheritDocs}
     */
    public TableModel(final int rowCount, final int columnCount) {
	super(rowCount, columnCount);

	this.pcs = new PropertyChangeSupport<TableModel>(this);
    } // end of <init>

    /**
     * {@inheritDocs}
     */
    public TableModel(final Vector columnNames, final int rowCount) {
	super(columnNames, rowCount);

	this.pcs = new PropertyChangeSupport<TableModel>(this);
    } // end of <init>

    /**
     * {@inheritDocs}
     */
    public TableModel(final Object[] columnNames, final int rowCount) {
	super(columnNames, rowCount);

	this.pcs = new PropertyChangeSupport<TableModel>(this);
    } // end of <init>

    /**
     * {@inheritDocs}
     */
    public TableModel(final Vector data, final Vector columnNames) {
	super(data, columnNames);

	this.pcs = new PropertyChangeSupport<TableModel>(this);
    } // end of <init>

    /**
     * {@inheritDocs}
     */
    public TableModel(final Object[][] data, final Object[] columnNames) {
	super(data, columnNames);

	this.pcs = new PropertyChangeSupport<TableModel>(this);
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

    // --- Properties accessors ---
    
    /**
     * {@inheritDocs}
     */
    public void setColumnCount(final int columnCount) {
	final PropertyChangeSupport.PropertyEditSession s = 
	    pcs.propertyWillChange("columnCount");
	
	super.setColumnCount(columnCount);

	s.propertyDidChange();	
    } // end of setColumnCount

    /**
     * {@inheritDocs}
     */
    public void addColumn(final Object columnName) {
	final PropertyChangeSupport.PropertyEditSession s = 
	    pcs.propertyWillChange("columnCount");	

	super.addColumn(columnName);

	s.propertyDidChange();	
    } // end of setColumnCount

    /**
     * {@inheritDocs}
     */
    public void addColumn(final Object columnName, final Vector columnData) {
	final PropertyChangeSupport.PropertyEditSession s = 
	    pcs.propertyWillChange("columnCount");

	super.addColumn(columnName, columnData);

	s.propertyDidChange();
    } // end of setColumnCount

    /**
     * {@inheritDocs}
     */
    public void addColumn(final Object columnName,
			  final Object[] columnData) {

	final PropertyChangeSupport.PropertyEditSession s = 
	    pcs.propertyWillChange("columnCount");

	super.addColumn(columnName, columnData);

	s.propertyDidChange();
    } // end of setColumnCount

    /**
     * Call this before changing either structure or data of table.
     *
     * <code>
     * final PropertyChangeSupport.PropertyEditSession s = 
     *   tableModel.willChange();
     *
     * // Apply data/struct changes
     *
     * s.propertyDidChange(); // fire properties events
     * </code>
     */
    public PropertyChangeSupport.PropertyEditSession willChange() {
        return pcs.propertyWillChange("columnCount").
            chain(pcs.propertyWillChange("rowCount")).
            chain(pcs.propertyWillChange("dataVector"));
        
    } // end of willChange
} // end of class TableModel
