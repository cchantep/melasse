package melasse;

/**
 * Listener category.
 *
 * @author Cedric Chantepie 
 */
public enum BindingListenerCategory {
    // --- Instances ---

    /**
     * Category for PropertyChangeListener
     * @see java.beans.PropertyChangeListener
     */
    PROPERTY_CHANGE("propertyChange"),

    /**
     * Category for FocusListener
     * @see java.awt.event.FocusListener
     */
    FOCUS("focus"),

    /**
     * Category for ChangeListener
     * @see javax.swing.event.ChangeListener
     */
    CHANGE("change"),

    /**
     * Category for ItemListener
     * @see java.awt.event.ItemListener
     */
    ITEM("item"),

    /**
     * Category for ActionListener
     * @see java.awt.event.ActionListener
     */
    ACTION("action"),

    /**
     * Category for ComponentListener
     * @see java.awt.event.ComponentListener
     */
    COMPONENT("component"),

    /**
     * Category for KeyListener
     * @see java.awt.event.KeyListener
     */
    KEY("key");

    // --- Properties ---

    /**
     * Key name
     */
    private final String name;

    // --- Constructors ---

    /**
     * Bulk constructor.
     *
     * @param name Key name
     */
    private BindingListenerCategory(final String name) {
	this.name = name;
    } // end of <init>

    // ---

    /**
     * Returns name of method to add listener belonging to this category.
     */
    public String getAddMethodName() {
	String n = this.name;
	StringBuffer buff = new StringBuffer("add").
	    append(Character.toUpperCase(n.charAt(0))).
	    append(n.substring(1)).
	    append("Listener");

	return buff.toString();
    } // end of getAddMethodName

    /**
     * Returns name of method to remove listener belonging to this category.
     */
    public String getRemoveMethodName() {
	String n = this.name;
	StringBuffer buff = new StringBuffer("remove").
	    append(Character.toUpperCase(n.charAt(0))).
	    append(n.substring(1)).
	    append("Listener");

	return buff.toString();
    } // end of getRemoveMethodName

    /**
     * {@inheritDoc}
     */
    public String toString() {
	return this.name;
    } // end of toString
} // end of class BindingListenerCategory
