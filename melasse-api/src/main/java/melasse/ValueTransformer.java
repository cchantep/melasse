package melasse;

/**
 * Transforms a value into another.
 *
 * @author Cedric Chantepie 
 */
public interface ValueTransformer<T> {

    /**
     * Transforms given |value| and returns new one.
     *
     * @param value Input value
     * @return Transformed value
     */
    public Object transform(T value);

} // end of interface ValueTransformer
