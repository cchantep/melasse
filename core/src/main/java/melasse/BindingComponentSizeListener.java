package melasse;

import java.util.logging.Level;

import java.awt.Dimension;
import java.awt.Component;

import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;

/**
 * Listens change of bound size property change for a component.
 *
 * @author Cedric Chantepie 
 */
class BindingComponentSizeListener 
    extends BindingListenerSupport<Object> implements ComponentListener {

    // --- Properties ---

    /**
     * Current size
     * [null]
     */
    private Dimension size = null;

    // --- Constructors ---

    /**
     * Bulk constructor.
     *
     * @param setter Setter used to propagate value
     */
    protected BindingComponentSizeListener(final Setter<Object> setter,
                                           final BindingOptionMap options) {

	super(setter, options);
    } // end of <init>

    // ---

    /**
     * Listens initial size at showing.
     */
    public void componentShown(final ComponentEvent evt) {
	logger.log(Level.FINER, "event = {0}", evt);

	final Component component = evt.getComponent();

	logger.log(Level.FINER, "component = {0}", component);

	final Dimension s = component.getSize();

	logger.log(Level.FINER, "component size = {0}", s);

	setValue(this.size = s);
    } // end of componentShown
    
    /**
     * Size has changed.
     */
    public void componentResized(final ComponentEvent evt) {
	logger.log(Level.FINER, "event = {0}", evt);

	final Component component = evt.getComponent();

	logger.log(Level.FINER, "component = {0}", component);

	final Dimension s = component.getSize();

	logger.log(Level.FINER, "component size = {0}", s);

	if (s.equals(this.size)) {
	    logger.finer("Skip unchanged component size");

	    return;
	} // end of if

	setValue(this.size = s);
    } // end of componentResized

    /**
     * {@inheritDoc}
     */
    public void componentHidden(ComponentEvent evt) { }

    /**
     * {@inheritDoc}
     */
    public void componentMoved(ComponentEvent evt) { }

} // end of class BindingComponentSizeListener
