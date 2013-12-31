package melasse;

import java.util.StringTokenizer;
import java.util.HashMap;
import java.util.Map;

import java.util.logging.Logger;
import java.util.logging.Level;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * UI/Object property binder.
 * Objects in path to final properties should support 
 * addPropertyChangeListener and removePropertyChangeListener, 
 * and fire property changes for concerned properties and objects.
 *
 * @author Cedric Chantepie 
 */
public class Binder {
    // --- Shared ---

    /**
     * Logger
     */
    private final static Logger defaultLogger = Logger.getLogger("Melasse");

    /**
     * Setters cache
     */
    private static final HashMap<String,HashMap<String,Method>> setters;

    /**
     * Getters cache
     */
    private static final HashMap<String,HashMap<String,Method>> getters;

    /**
     * Add listeners cache
     */
    private static final HashMap<String,HashMap<BindingListenerCategory,Method>> addListeners;

    /**
     * Remove listeners cache
     */
    private static final HashMap<String,HashMap<BindingListenerCategory,Method>> removeListeners;

    /**
     * Aggregators
     */
    private static final HashMap<Integer,UnaryFunction<Boolean,Boolean>> aggregators;
    
    /**
     * Value history for aggregating
     */
    private static final HashMap<Object,HashMap<String,Integer>> aggregateValues;

    /**
     * Single instance
     */
    private static Binder instance = null;

    static {
        setters = new HashMap<String,HashMap<String,Method>>();
        getters = new HashMap<String,HashMap<String,Method>>();
        addListeners = new HashMap<String,HashMap<BindingListenerCategory,Method>>();
        removeListeners = new HashMap<String,HashMap<BindingListenerCategory,Method>>();
        aggregateValues = new HashMap<Object,HashMap<String,Integer>>();
        aggregators = new HashMap<Integer,UnaryFunction<Boolean,Boolean>>();
    } // end of <static>

    // --- Constructors ---

    /**
     * No-arg constructor
     */
    private Binder() {
    } // end of <init>

    // ---

    /**
     * Returns binding logger.
     */
    static Logger getLogger(final BindingOptionMap options) {
        String category = null;

        if (options == null || (category = (String) options.
                                get(BindingKey.DEBUG_CATEGORY)) == null) {

            return defaultLogger;
        } // end of if

        return Logger.getLogger(category);
    } // end of getLogger

    /**
     * Binds |sourcePath| about |source| 
     * with |targetPath| about |target|.
     *
     * @param sourcePath Path to |source| property
     * @param source Involved source
     * @param targetPath Path to |target| property
     * @param target Involved target
     * @param options Binding options
     */
    public static void bind(final String sourcePath,
                            final Object source,
                            final String targetPath,
                            final Object target,
                            final BindingOptionMap options) {

        getLogger(options).log(Level.FINER,
                   "Binds {0}@{1}.{2} to {3}@{4}.{5}",
                   new Object[] {
                       source.getClass().getName(),
                       new Integer(System.identityHashCode(source)),
                       sourcePath,
                       target.getClass().getName(), 
                       new Integer(System.identityHashCode(target)),
                       targetPath
                   });

        // Object paths
        final ObjectPath spath = makeObjectPath(source, sourcePath, options);
        final ObjectPath tpath = makeObjectPath(target, targetPath, options);

        bind(spath, tpath, options);
    } // end of bind

    /**
     * Binds source |spath| with target |tpath|.
     *
     * @param spath Source object path
     * @param tpath Target object path
     * @param options Binding options
     */
    public static void bind(final ObjectPath spath,
                            final ObjectPath tpath,
                            final BindingOptionMap options) {

        final Logger logger = getLogger(options);

        logger.log(Level.FINER, 
                   "source path = {0}, target path = {1}",
                   new Object[] { spath, tpath });

        final BindingOptionMap opts = (options != null) ? expandKeys(options)
            : new BindingOptionMap();

        // Mediator factories
        final MediatorFactory inMediatorFactory = 
            (MediatorFactory) opts.get(BindingKey.INPUT_MEDIATOR_FACTORY);

        final MediatorFactory outMediatorFactory =
            (MediatorFactory) opts.get(BindingKey.OUTPUT_MEDIATOR_FACTORY);

        if (inMediatorFactory != null) {
            logger.finer("Will set mediator factory on source path");

            spath.start.setMediatorFactory(inMediatorFactory);
        } // end of if

        if (outMediatorFactory != null) {
            logger.finer("Will set mediator factory on target path");

            tpath.start.setMediatorFactory(outMediatorFactory);
        } // end of if

        // Transformers from options
        UnaryFunction<Object,Object> soutTransformer = null;

        @SuppressWarnings("unchecked")
        final UnaryFunction<Object,Object> toutTransformer = (UnaryFunction<Object,Object>) opts.get(BindingKey.OUTPUT_TRANSFORMER);

        if (opts.containsKey(BindingKey.INPUT_TRANSFORMER)) {
            @SuppressWarnings("unchecked")
            final UnaryFunction<Object,Object> f = 
                (UnaryFunction<Object,Object>) opts.
                get(BindingKey.INPUT_TRANSFORMER);

            soutTransformer = f;

            logger.log(Level.FINER, "Found source output transformer = {0}", 
                       soutTransformer);

            spath.end.setOutputTransformer(soutTransformer);
            tpath.end.setInputTransformer(soutTransformer);
        } else if (toutTransformer != null) {
            logger.log(Level.WARNING, "Bad unidirectional binder, should not found transformer: {0}", opts.get(BindingKey.OUTPUT_TRANSFORMER));

            return;
        } // end of if

        // Target mode
        boolean targetMode = (soutTransformer != null);

        if (targetMode && toutTransformer != null) {
            logger.log(Level.FINER,
                       "Found object output transformer = {0}",
                       soutTransformer);

            targetMode = false; // input and output transformers
        } // end of if

        if (opts.containsKey(BindingKey.TARGET_MODE)) {
            logger.finer("Target mode forced to true");

            targetMode = true;
        } // end of if

        logger.log(Level.FINER, "target mode = {0}", targetMode);

        // Add-listener hook
        final BindingPostAddListenerHook shook = 
            new BindingPostAddListenerHook(tpath, opts);        

        spath.end.registerPostAddListenerHook(shook);

        if (!targetMode) {
            if (toutTransformer != null) {
                tpath.end.setOutputTransformer(toutTransformer);
                spath.end.setInputTransformer(toutTransformer);
            } // end of if

            logger.finer("Register binding hook on object path");

            final BindingPostAddListenerHook thook = 
                new BindingPostAddListenerHook(spath, opts);
            
            tpath.end.registerPostAddListenerHook(thook);
        } // end of if

        // Object path listener
        final BindingExchangeObjectPathListener sxPathListener =
            new BindingExchangeObjectPathListener(spath.start);

        final BindingOutputObjectPathListener soPathListener =
            new BindingOutputObjectPathListener(tpath.start);

        // automatically set value from source to target,
        // when path to object property is completed
        // or when path to source property is completed
        tpath.start.addObjectPathListener(sxPathListener);
        spath.start.addObjectPathListener(soPathListener);

        // Initial value
        // set source value to target if possible
        final Object iniVal = spath.end.getValue();
        
        logger.log(Level.FINER, "source initial value = {0}", iniVal);
        
        // Sets directly from last element
        tpath.end.setAndTransformValue(iniVal);
    } // end of bind

    /**
     * Expands binding keys in |options|.
     * 
     * @param options Binding options
     */
    private static BindingOptionMap expandKeys(final BindingOptionMap options) {
        synchronized(options) {
            // Numeric
            if (options.containsKey(NumericBindingKey.BIGINTEGER_TO_INTEGER)) {
                options.remove(NumericBindingKey.BIGINTEGER_TO_INTEGER);

                options.add(BindingKey.INPUT_TRANSFORMER,
                            BigIntegerToIntegerTransformer.getInstance()).
                    add(BindingKey.OUTPUT_TRANSFORMER,
                        IntegerToBigIntegerTransformer.getInstance());

            } // end of if

            if (options.containsKey(NumericBindingKey.INTEGER_TO_BIGINTEGER)) {
                options.remove(NumericBindingKey.INTEGER_TO_BIGINTEGER);

                options.add(BindingKey.INPUT_TRANSFORMER,
                            IntegerToBigIntegerTransformer.getInstance()).
                    add(BindingKey.OUTPUT_TRANSFORMER,
                        BigIntegerToIntegerTransformer.getInstance());

            } // end of if

            // Text
            if (options.containsKey(TextBindingKey.CHAR_ARRAY_TO_STRING)) {
                options.remove(TextBindingKey.CHAR_ARRAY_TO_STRING);

                options.add(BindingKey.INPUT_TRANSFORMER,
                            CharArrayToStringTransformer.getInstance()).
                    add(BindingKey.OUTPUT_TRANSFORMER,
                        StringToCharArrayTransformer.getInstance());

            } // end of if

            if (options.containsKey(TextBindingKey.STRING_TO_CHAR_ARRAY)) {
                options.remove(TextBindingKey.STRING_TO_CHAR_ARRAY);

                options.add(BindingKey.INPUT_TRANSFORMER,
                            StringToCharArrayTransformer.getInstance()).
                    add(BindingKey.OUTPUT_TRANSFORMER,
                        CharArrayToStringTransformer.getInstance());

            } // end of if
        } // end of sync

        return options;
    } // end of expandKeys

    /**
     * Returns value transformed to aggregate various input boolean values
     * as one, using a AND clause. So final set boolean is true if and only if
     * all input booleans are true. Transformer is not retained.
     *
     * @param pathElmt Path element
     * @param options Binding options
     * @return Aggregating value transformer
     */
    protected static UnaryFunction<Boolean,Boolean> getBooleanAndAggregateTransformer(final ObjectPathElement pathElmt, final BindingOptionMap options) {

        final Logger logger = getLogger(options);
        final Integer key = System.identityHashCode(pathElmt);

        logger.log(Level.FINER, "path element = {0}, key = {1}", 
                   new Object[] { pathElmt, key });

        UnaryFunction<Boolean,Boolean> aggregator = null;
        
        synchronized(aggregators) {
            aggregator = aggregators.get(key);

            logger.log(Level.FINER, "aggregator = {0}", aggregator);

            if (aggregator != null) {
                return aggregator;
            } // end of if
        } // end of sync

        final Object object = pathElmt.getTargetObject();
        final String property = pathElmt.getProperty();

        logger.log(Level.FINER,
                   "target object = {0}, property = {1}",
                   new Object[] { object, property });

        HashMap<String,Integer> group = null;

        synchronized(aggregateValues) {
            final Integer okey = new Integer(System.identityHashCode(object));
            group = aggregateValues.get(okey);

            if (group == null) {
                group = new HashMap<String,Integer>();

                group.put(property, new Integer(0));

                aggregateValues.put(okey, group);
            } // end of if
        } // end of sync

        aggregator = new AndClauseTransformer(group, property);
        
        aggregators.put(key, aggregator);

        return aggregator;
    } // end of getBooleanAndAggregateTransformer

    /**
     * Makes object path.
     *
     * @param object Source of object |path|
     * @param path Object path, with part separated by '.'
     * @param options Binding options
     * @return Object path
     */
    protected static ObjectPath makeObjectPath(final Object object, 
                                               final String path,
                                               final BindingOptionMap options) {
        
        final Logger logger = getLogger(options);

        logger.log(Level.FINER, "object = {0}, path = {1}",
                   new Object[] { object, path });

        ObjectPathElement firstObjectElement = null;
        ObjectPathElement previousElmt = null;
        Object parentObject = object;
        String objectPart = path;
        final StringTokenizer tok = new StringTokenizer(objectPart, ".");
        int toklen = tok.countTokens();
        
        ObjectPathElement ope;
        String pname;
        for (int i = 1; tok.hasMoreTokens(); i++) {
            objectPart = tok.nextToken();
            ope = new ObjectPathElement(parentObject, objectPart, options);

            if (i == 1) { // keep first
                firstObjectElement = ope;
            } // end of if

            if (previousElmt != null) {
                previousElmt.setNextElement(ope);
            } // end of if

            previousElmt = ope;

            if (parentObject != null) {
                pname = ObjectPathElement.normalizeProperty(objectPart);

                parentObject = getValue(parentObject, pname);
            } // end of if
        } // end of for

        logger.log(Level.FINER, "first = {0}, last = {1}",
                   new Object[] { firstObjectElement, previousElmt });

        return new ObjectPath(firstObjectElement, previousElmt);
    } // end of makeObjectPath

    /**
     * Adds |listener| with |category| on |object|.
     *
     * @param object Target object
     * @param category Listener category
     * @param listener Listener to be added
     * @see BindingListenerCategory
     * @todo Restricted addPropertyChangeListener
     */
    protected static boolean addListener(final Object object,
                                         final BindingListenerCategory category,
                                         final Object listener,
                                         final BindingOptionMap options) {

        final Logger logger = getLogger(options);
        
        logger.log(Level.FINER,
                   "object = {0}, category = {1}, listener = {2}",
                   new Object[] { object, category, listener });

        final Class objectClass = object.getClass();
        final String className = objectClass.getName();

        logger.log(Level.FINER, "class name = {0}", className);

        Method meth = null;

        synchronized(addListeners) {
            HashMap<BindingListenerCategory,Method> categories = null;

            if (!addListeners.containsKey(className)) {
                categories = new HashMap<BindingListenerCategory,Method>();

                addListeners.put(className, categories);
            } else {
                categories = addListeners.get(className);
            } // end of if

            if (!categories.containsKey(category)) {
                final String methodName = category.getAddMethodName();

                logger.log(Level.FINER, 
                           "expected method name = {0}", methodName);

                try {
                    Method[] methods = objectClass.getMethods();

                    for (int i = 0; i < methods.length && 
                             meth == null; i++) {

                        logger.log(Level.FINER, "method = {0}", methods[i]);

                        if (methodName.equals(methods[i].getName()) &&
                            methods[i].getParameterTypes().length == 1) {

                            logger.finer("Method found");

                            meth = methods[i];
                        } // end of if
                    } // end of for
                } catch (Exception e) {
                    logger.log(Level.SEVERE,
                               "Fails to get method to add listener: {0}",
                               e.getMessage());

                } // end of catch

                categories.put(category, meth);
            } else {
                meth = categories.get(category);
            } // end of else

            logger.log(Level.FINER, "method = {0}", meth);
        } // end of sync

        if (meth == null) {
            logger.log(Level.WARNING, 
                       "Cannot find method {0} for class: {1}",
                       new Object[] { category.getAddMethodName(), className });
            
            return false;
        } // end of if
        
        try {
            meth.invoke(object, new Object[] { listener });
        } catch (Exception e) {
            logger.log(Level.SEVERE,
                       "Fails to add listener", e);
            
            logger.log(Level.SEVERE, "method = {0}", meth);
        } // end of catch

        return true;
    } // end of addListener

    /**
     * Removes |listener| with |category| on |object|.
     *
     * @param object Target object
     * @param category Listener category
     * @param listener Listener to be removed
     * @param options Binding options
     * @see BindingListenerCategory
     */
    protected static boolean removeListener(final Object object, final BindingListenerCategory category, final Object listener, final BindingOptionMap options) {

        final Logger logger = getLogger(options);
        
        logger.log(Level.FINER,
                   "object = {0}, category = {1}, listener = {2}",
                   new Object[] { object, category, listener });

        final Class objectClass = object.getClass();
        final String className = objectClass.getName();

        logger.log(Level.FINER, "className = {0}", className);

        Method meth = null;

        synchronized(removeListeners) {
            HashMap<BindingListenerCategory,Method> categories = null;

            if (!removeListeners.containsKey(className)) {
                categories = new HashMap<BindingListenerCategory,Method>();

                removeListeners.put(className, categories);
            } else {
                categories = removeListeners.get(className);
            } // end of if

            if (!categories.containsKey(category)) {
                final String methodName = category.getRemoveMethodName();

                logger.log(Level.FINER,
                           "expected method name = {0}", methodName);

                try {
                    Method[] methods = objectClass.getMethods();

                    for (int i = 0; i < methods.length && 
                             meth == null; i++) {

                        logger.log(Level.FINER, "method = {0}", methods[i]);

                        if (methodName.equals(methods[i].getName()) &&
                            methods[i].getParameterTypes().length == 1) {

                            logger.finer("Method found");

                            meth = methods[i];
                        } // end of if
                    } // end of for
                } catch (Exception e) {
                    logger.log(Level.SEVERE,
                               "Fails to get method to remove listener: {0}",
                               e.getMessage());

                } // end of catch

                categories.put(category, meth);
            } else {
                meth = categories.get(category);
            } // end of else
        } // end of sync

        logger.log(Level.FINER, "method = {0}", meth);

        if (meth == null) {
            logger.log(Level.WARNING,
                       "Fails to get method {0} for class: {1}",
                       new Object[] { category.getRemoveMethodName(), className });

            return false;
        } // end of if

        try {
            meth.invoke(object, new Object[] { listener });
        } catch (Exception e) {
            logger.throwing("melasse.Binder", "removeListener",  e);

            logger.log(Level.SEVERE,
                       "Fails to remove listener: {0}", e.getMessage());

        } // end of catch

        return true;
    } // end of removeListener

    /**
     * Sets |value| for property on object.
     *
     * @param object Target object
     * @param property Name of property on target |object|
     * @param value New property value
     */
    protected static void setValue(final Object object,
                                   final String property,
                                   final Object value,
                                   final BindingOptionMap options) {

        final Logger logger = getLogger(options);

        logger.log(Level.FINER,
                   "object = {0}, property = {1}, value = {2}",
                   new Object[] { object, property, value });

        final Class objectClass = object.getClass();
        final String className = objectClass.getName();

        logger.log(Level.FINER, "class name = {0}", className);

        Method setter = null;

        synchronized(setters) {
            HashMap<String,Method> classSetters = null;

            if (!setters.containsKey(className)) {
                classSetters = new HashMap<String,Method>(1);

                setters.put(className, classSetters);

                logger.finer("Will load setters");

                try {
                    Method[] methods = objectClass.getMethods();

                    String name;
                    for (int i = 0; i < methods.length; i++) {
                        name = methods[i].getName();

                        logger.log(Level.FINER, "name = {0}", name);

                        if (name.startsWith("set") &&
                            methods[i].getParameterTypes().length == 1) {

                            name = Character.toLowerCase(name.charAt(3)) +
                                name.substring(4);

                            logger.log(Level.FINER,
                                       "setter property name = {0}", name);

                            classSetters.put(name, methods[i]);
                        } // end of if
                    } // end of for
                } catch (Exception e) {
                    logger.log(Level.SEVERE,
                               "Fails to get setter: {0}", e.getMessage());

                    return;
                } // end of catch
            } else {
                classSetters = setters.get(className);
            } // end of else

            logger.log(Level.FINER, "classSetters = {0}", classSetters);

            if (!classSetters.containsKey(property)) {
                logger.log(Level.WARNING, "Target object does not support setting value for property {0}: {1}", new Object[] { property, object });

                return;
            } // end of if

            setter = classSetters.get(property);
        } // end of sync

        logger.log(Level.FINER, "setter = {0}", setter);

        try {
            setter.invoke(object, new Object[] { value });
        } catch (Exception e) {
            Throwable c = null;

            if (!(e instanceof InvocationTargetException) ||
                (c = e.getCause()) == null) {

                c = e;
            } // end of if
                    
            logger.log(Level.SEVERE, "Fails to set property value", c);

            final String type = (value == null) ? "<nulltype>"
                : value.getClass().toString();

            logger.log(Level.SEVERE,
                       "target class = {0}, property = {1}, value = {2} ({3})",
                       new Object[] { className, property, value, type });

        } // end of catch
    } // end of setValue

    /**
     * Returns |value| for property on object.
     *
     * @param object Target object
     * @param property Name of property on target |object|
     * @return Value for |property| on target |object|
     */
    protected static Object getValue(final Object object,
                                     final String property) {

        final Logger logger = defaultLogger;

        logger.log(Level.FINER, "object = {0}, property = {1}",
                   new Object[] { object, property });

        final Class objectClass = object.getClass();
        final String className = objectClass.getName();

        logger.log(Level.FINER, "className = {0}", className);

        Method getter = null;

        synchronized(getters) {
            HashMap<String,Method> classGetters = null;

            if (!getters.containsKey(className)) {
                classGetters = new HashMap<String,Method>(1);

                getters.put(className, classGetters);

                try {
                    Method[] methods = objectClass.getMethods();

                    String name;
                    int plen;
                    for (int i = 0; i < methods.length; i++) {
                        name = methods[i].getName();
                        plen = methods[i].getParameterTypes().length;

                        logger.log(Level.FINER, "name = {0}, plen = {1}", 
                                   new Object[] { name, new Integer(plen) });

                        if (name.startsWith("get") && plen == 0) {
                            name = Character.toLowerCase(name.charAt(3)) +
                                name.substring(4);

                            logger.log(Level.FINER,
                                       "property name = {0}", name);

                            classGetters.put(name, methods[i]);
                        } // end of if

                        if (name.startsWith("is") && plen == 0) {
                            name = Character.toLowerCase(name.charAt(2)) +
                                name.substring(3);

                            logger.log(Level.FINER,
                                       "property name = {0}", name);

                            classGetters.put(name, methods[i]);
                        } // end of if
                    } // end of for
                } catch (Exception e) {
                    logger.log(Level.SEVERE,
                               "Fails to get getter: {0}",
                               e.getMessage());

                    return null;
                } // end of catch
            } else {
                classGetters = getters.get(className);
            } // end of else

            logger.log(Level.FINER, "classGetters = {0}", classGetters);

            if (!classGetters.containsKey(property)) {
                logger.log(Level.WARNING,
                           "Target object does not support getting value for property: {0}, target object = {1}", 
                           new Object[] { 
                               property, 
                               object.getClass().getName() +
                               '@' + System.identityHashCode(object)
                           });

                return null;
            } // end of if

            getter = classGetters.get(property);
        } // end of sync

        logger.log(Level.FINER, "getter = {0}", getter);

        try {
            return getter.invoke(object, new Object[0]);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Fails to get property", e);

            logger.log(Level.SEVERE,
                       "Fails to get property value: {0}, property = {1}",
                       new Object[] { e.getMessage(), property });

            return null;
        } // end of catch
    } // end of getValue

    // --- Inner classes ---

    /**
     * Object path.
     *
     * @author Cedric Chantepie 
     */
    protected static class ObjectPath {
        // --- Properties ---

        /**
         * Path start
         */
        public final ObjectPathElement start;

        /**
         * Path end
         */
        public final ObjectPathElement end;

        // --- Constructors ---

        /**
         * Bulk constructor.
         */
        public ObjectPath(final ObjectPathElement start,
                          final ObjectPathElement end) {

            this.start = start;
            this.end = end;
        } // end of <init>

        // --- 

        /**
         * {@inheritDoc}
         */
        public String toString() {
            if (start == null) {
                return "<invalid>";
            } // end of if

            // ---

            Object targetObject = start.getTargetObject();
            String className = targetObject.getClass().getName();
            final StringBuffer buff = new StringBuffer(className);

            buff.append('@').
                append(System.identityHashCode(targetObject)).
                append('.').append(start.getProperty());

            ObjectPathElement e = start;
            while ((e = e.getNext()) != null) {
                targetObject = e.getTargetObject();

                if (targetObject != null) {
                    className = targetObject.getClass().getName();
                    buff.append('[').append(className).
                        append('@').
                        append(System.identityHashCode(targetObject)).
                        append(']');

                } // end of if

                buff.append('.').append(e.getProperty());
            } // end of if

            return buff.toString();
        } // end of toString
    } // end of class ObjectPath

    /**
     * AND clause transformer. Returns true if and only if all input booleans
     * set are true.
     *
     * @author Cedric Chantepie 
     */
    private static class AndClauseTransformer 
        implements UnaryFunction<Boolean,Boolean> {

        // --- Properties ---

        /**
         * Aggregated property
         */
        private final String property;

        /**
         * Property group
         * @todo Weak reference
         * @todo Custom class
         */
        private final HashMap<String,Integer> group;

        /**
         * Last value
         */
        private Boolean lastValue = null;

        // --- Constructors ---

        /**
         * Bulk constructor.
         *
         * @param group Property group
         * @param property Property name
         */
        public AndClauseTransformer(final HashMap<String,Integer> group,
                                    final String property) {

            this.group = group;
            this.property = property;
        } // end of <init>
        
        // ---

        /**
         * {@inheritDoc}
         */
        public Boolean apply(final Boolean value) {
            synchronized(this.group) {
                Integer fnum = this.group.get(this.property);

                if ((this.lastValue == null && value == null) ||
                    (this.lastValue != null && this.lastValue.equals(value))) {

                    return (fnum.intValue() == 0);
                } // end of if

                // ---

                if (fnum == null) {
                    fnum = new Integer(0);
                } // end of if

                if (Boolean.FALSE.equals(value)) {
                    fnum = new Integer(fnum.intValue()+1);
                } else if (fnum > 0 && 
                           Boolean.FALSE.equals(this.lastValue)) { // true

                    fnum = new Integer(fnum.intValue()-1);
                } // end of else if

                this.group.put(this.property, fnum);
                this.lastValue = value;

                // true only is no false
                return (fnum.intValue() == 0);
            } // end of sync
        } // end of transform
    } // end class AndClauseTransformer
} // end of class Binder
