package melasse.swing;

import java.util.Vector;

import java.beans.PropertyChangeListener;

import javax.swing.table.DefaultTableModel;

import melasse.PropertyChangeSupport;

/**
 * Table model with property change support.
 *
 * @author Cedric Chantepie 
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
    public void setColumnCount(int columnCount) {
	PropertyChangeSupport.PropertyEditSession s = 
	    pcs.propertyWillChange("columnCount");
	
	super.setColumnCount(columnCount);

	s.propertyDidChange();	
    } // end of setColumnCount

    /**
     * {@inheritDocs}
     */
    public void addColumn(Object columnName) {
	PropertyChangeSupport.PropertyEditSession s = 
	    pcs.propertyWillChange("columnCount");	

	super.addColumn(columnName);

	s.propertyDidChange();	
    } // end of setColumnCount

    /**
     * {@inheritDocs}
     */
    public void addColumn(Object columnName,
			  Vector columnData) {

	PropertyChangeSupport.PropertyEditSession s = 
	    pcs.propertyWillChange("columnCount");

	super.addColumn(columnName, columnData);

	s.propertyDidChange();
    } // end of setColumnCount

    /**
     * {@inheritDocs}
     */
    public void addColumn(Object columnName,
			  Object[] columnData) {

	PropertyChangeSupport.PropertyEditSession s = 
	    pcs.propertyWillChange("columnCount");

	super.addColumn(columnName, columnData);

	s.propertyDidChange();
    } // end of setColumnCount

    /**
     * {@inheritDocs}
     */
    public void fireTableRowsInserted(int firstRow,
				      int lastRow) {

	PropertyChangeSupport.PropertyEditSession s = 
	    pcs.propertyWillChange("rowCount");

	super.fireTableRowsInserted(firstRow, lastRow);

	s.propertyDidChange();
    } // end of fireTableRowsInserted

    /**
     * {@inheritDocs}
     */
    public void fireTableDataChanged() {
	PropertyChangeSupport.PropertyEditSession s = 
	    pcs.propertyWillChange("dataVector");

	super.fireTableDataChanged();

	s.propertyDidChange();
    } // end of fireTableDataChanged
} // end of class TableModel
