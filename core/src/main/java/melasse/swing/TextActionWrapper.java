package melasse.swing;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.Action;

/**
 * Wrapper action to allow it to be plugged as action listener on text component
 * (triggered by <tt>VK_ENTER</tt>).
 *
 * @author Cedric Chantepie 
 */
public class TextActionWrapper implements ActionListener {
    // --- Properties ---

    /**
     * Wrapped action
     */
    private Action action = null;

    // --- Constructors ---

    /**
     * Bulk constructor.
     *
     * @param action Wrapped action
     */
    public TextActionWrapper(Action action) {
	if (action == null) {
	    throw new IllegalArgumentException();
	} // end of if

	this.action = action;
    } // end of <init>


    /**
     * Fires wrapped action, if this one is enabled.
     */
    public void actionPerformed(ActionEvent evt) {
	if (!this.action.isEnabled()) {
	    return;
	} // end of if

	this.action.actionPerformed(evt);
    } // end of actionPerformed
} // end of TextActionWrapper
