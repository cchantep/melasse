package melasse;

import java.util.logging.Logger;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 * Quietly wraps an action, so that this wrapper is always enabled,
 * but it fires wrapped one only if this one is enabled.
 *
 * <code>
 * textField.setAction(new QuietWrapAction(anAction));
 * </code>
 *
 * @author Cedric Chantepie
 */
public final class QuietWrapAction extends AbstractAction {
    // --- Shared ---

    /**
     * Logger
     */
    private final Logger logger = Logger.getLogger("Melasse");

    // --- Properties ---

    /**
     * Wrapped action
     */
    public final Action wrapped;

    // --- Constructors ---

    /**
     * Bulk constructor.
     * Copies wrapped action properties, except enabled.
     *
     * @param action Action to be wrapped
     */
    public QuietWrapAction(final Action action) {
        if (action == null) {
            throw new IllegalArgumentException();
        } // end of if

        // ---

        this.wrapped = action;

        putValue(Action.ACCELERATOR_KEY, 
                 action.getValue(Action.ACCELERATOR_KEY));

        putValue(Action.ACTION_COMMAND_KEY,
                 action.getValue(Action.ACTION_COMMAND_KEY));

        putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY,
                 action.getValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY));

        putValue(Action.LARGE_ICON_KEY, action.getValue(Action.LARGE_ICON_KEY));

        putValue(Action.LONG_DESCRIPTION,
                 action.getValue(Action.LONG_DESCRIPTION));

        putValue(Action.MNEMONIC_KEY, action.getValue(Action.MNEMONIC_KEY));
        putValue(Action.NAME, action.getValue(Action.NAME));

        putValue(Action.SELECTED_KEY, action.getValue(Action.SELECTED_KEY));
        putValue(Action.SHORT_DESCRIPTION, 
                 action.getValue(Action.SHORT_DESCRIPTION));

        putValue(Action.SMALL_ICON, action.getValue(Action.SMALL_ICON));
    } // end of <init>

    // --- Action impl ---

    /**
     * {@inheritDoc}
     */
    public void actionPerformed(final java.awt.event.ActionEvent evt) {
        if (!this.wrapped.isEnabled()) {
            logger.warning("Skip wrapped action");

            return;
        } // end of if

        // ---

        this.wrapped.actionPerformed(evt);
    } // end of actionPerformed
} // end of class QuietWrapAction
        
