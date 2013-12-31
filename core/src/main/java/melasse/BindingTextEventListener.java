package melasse;

import java.beans.PropertyChangeEvent;

import java.awt.TextComponent;
import java.awt.Component;

import javax.swing.text.JTextComponent;

import javax.swing.event.DocumentEvent;

/**
 * Base for listener of converted text event.
 *
 * @author Cedric Chantepie
 */
abstract class BindingTextEventListener<C extends Component> {

    // --- Properties ---

    /**
     * Text component
     */
    protected final C component;

    /**
     * Binding options
     */
    private final BindingOptionMap options;

    /**
     * Current text
     */
    private String text = null;

    // --- Constructor ---

    /**
     * Bulk constructor.
     *
     * @param component Observed text component
     */
    protected BindingTextEventListener(final C component, 
                                       final String text,
                                       final BindingOptionMap options) {

        this.component = component;
        this.text = text;
        this.options = options;
    } // end of <init>

    // ---

    /**
     * Fire property change according document one.
     */
    protected void firePropertyChange(final String newText) {
        PropertyChangeEvent event = null;

        synchronized (this) {
            event = new java.beans.
                PropertyChangeEvent(this.component, "text", 
                                    this.text, newText);
                
            this.text = newText;
        } // end of sync

        for (final java.beans.PropertyChangeListener l : this.component.
                 getPropertyChangeListeners()) {

            l.propertyChange(event);
        } // end of for
    } // end of firePropertyChange

    /**
     * Listener for text event - converted as property change event.
     */
    protected static final class TextListener 
        extends BindingTextEventListener<TextComponent>  
        implements java.awt.event.TextListener {

        // --- Constructors ---

        /**
         * Bulk constructor.
         *
         * @param component Observed text component
         */
        TextListener(final TextComponent component, 
                     final BindingOptionMap options) {

            super(component, component.getText(), options);
        } // end of <init>

        // ---

        /**
         * {@inheritDoc}
         */
        public void textValueChanged(java.awt.event.TextEvent e) {
            firePropertyChange(this.component.getText());
        } // end of textValueChanged
    } // end of class TextListener

    /**
     * Listener for document event - converted as property change event.
     */
    protected static final class DocumentListener 
        extends BindingTextEventListener<JTextComponent> 
        implements javax.swing.event.DocumentListener {

        // --- Constructors ---

        /**
         * Bulk constructor.
         *
         * @param component Observed text component
         */
        DocumentListener(final JTextComponent component, 
                         final BindingOptionMap options) {

            super(component, component.getText(), options);
        } // end of <init>

        // ---

        /**
         * {@inheritDoc}
         */
        public void insertUpdate(DocumentEvent e) {
            firePropertyChange(this.component.getText());
        } // end of insertUpdate

        /**
         * {@inheritDoc}
         */
        public void removeUpdate(DocumentEvent e) {
            firePropertyChange(this.component.getText());
        } // end of removeUpdate

        /**
         * {@inheritDoc}
         */
        public void changedUpdate(DocumentEvent e) {
            firePropertyChange(this.component.getText());
        } // end of changedUpdate
    } // end of DocumentListener
} // end of class BindingTextEventListener
