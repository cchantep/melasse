package melasse;

/**
 * Listener for path change.
 *
 * @author Cedric Chantepie 
 * @todo Weak ref
 */
public interface ObjectPathListener {

    /**
     * Fired when path is completed, applied to its end.
     *
     * @param element Path element raising up completion event.
     */
    public void pathCompleted(final ObjectPathElement element);

} // end of interface ObjectPathListener
