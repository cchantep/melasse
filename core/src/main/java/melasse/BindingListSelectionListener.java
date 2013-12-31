package melasse;

import java.util.logging.Level;

import javax.swing.ListSelectionModel;

/**
 * Binding listener for list selection.
 *
 * @see javax.swing.ListSelectionModel
 * @author Cedric Chantepie
 */
final class BindingListSelectionListener extends BindingListenerSupport<Object> 
    implements javax.swing.event.ListSelectionListener {

    // --- Properties ---

    /**
     * Name of watched property
     */
    private String property;

    // --- Constructors ---

    /**
     * Bulk constructor.
     *
     * @param setter Setter used to propagate value
     */
    protected BindingListSelectionListener(final Setter<Object> setter, 
                                           final String property, 
                                           final BindingOptionMap options) {

	super(setter, options);

        this.property = property;
    } // end of <init>

    // ---

    /**
     * {@inheritDoc}
     */
    public void valueChanged(final javax.swing.event.ListSelectionEvent evt) {
	this.logger.log(Level.FINER, "event = {0}", evt);

        if (evt.getValueIsAdjusting()) {
            return; // Skip - wait end of event serie
        } // end of if

        // ---

        final ListSelectionModel model = (ListSelectionModel) evt.getSource();

        if ("selectionEmpty".equals(property)) {
            setter.apply((Boolean) model.isSelectionEmpty());
        } else if ("minSelectionIndex".equals(property)) {
            setter.apply((Integer) model.getMinSelectionIndex());
        } else if ("maxSelectionIndex".equals(property)) {
            setter.apply((Integer) model.getMaxSelectionIndex());
        } // end of else
    } // end of valueChanged
} // end of class BindingListSelectionListener
