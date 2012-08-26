package melasse.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.HashMap;
import java.util.Locale;

import java.util.logging.Logger;
import java.util.logging.Level;

import java.lang.reflect.Method;
import java.lang.reflect.Field;

import java.text.MessageFormat;

import java.awt.event.KeyEvent;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;

/**
 * Application overall utility class (signal/introspection, i18n ...).
 *
 * @author Cedric Chantepie 
 */
public class AppUtils {
    // --- Shared ---

    /**
     * Fallback logger
     */
    private static final Logger fallbackLogger =
	Logger.getLogger(AppUtils.class.getName());

    /**
     * Properties accessors cache
     */
    private static final HashMap<PropertyKey,Method> properties;

    /**
     * Methods cache
     */
    private static final HashMap<String,Method> methods;

    static {
	properties = new HashMap<PropertyKey,Method>();
	methods = new HashMap<String,Method>();
    } // end of <static>

    // ---

    /**
     * Returns |application| specific logger.
     * (Application object should define <code>getLogger</code> method).
     *
     * @param application Application instance
     */
    public static Logger getLogger(Object application) {
	return (Logger) getProperty(application, "logger");
    } // end of getLogger

    /**
     * Sends invocation signal to |target|, with no argument.
     *
     * @param target Target instance (of application)
     * @param methodName Name of method
     * @return Invocation result, or null
     */
    public static Object invoke(Object target, String methodName) {
	return invoke(target, methodName, new Object[0]);
    } // end of invoke

    /**
     * Sends invocation signal to |target| with |arguments|.
     *
     * @param target Target instance (of application)
     * @param methodName Name of method
     * @param argument Invocation sole argument
     * @return Invocation result, or null
     */
    public static Object invoke(Object target,
				String methodName,
				Object argument) {

	return invoke(target, 
	       methodName,
	       new Object[] { argument });

    } // end of invoke

    /**
     * Sends invocation signal to |target| with |arguments|.
     *
     * @param target Target instance (of application)
     * @param methodName Name of method
     * @param arguments Invocation arguments
     * @return Invocation result, or null
     */
    public static Object invoke(Object target,
				String methodName,
				Object[] arguments) {

	Logger logger = null;

	try {
	    logger = getLogger(target);
	} catch (Exception e) {
	    logger = fallbackLogger;
	} // end of catch

	logger.log(Level.FINER, 
		   "target = {0}, method name = {1}, arguments = {2}",
		   new Object[] { target, methodName, arguments });

	if (target == null) {
	    throw new IllegalArgumentException("Invalid target: " + target);
	} // end of if

	if (methodName == null ||
	    methodName.trim().length() == 0) {

	    throw new IllegalArgumentException("Invalid method name: " +
					       methodName);

	} // end of if

	if (arguments == null) {
	    arguments = new Object[0];
	} // end of if

	// ---

	Class targetClass = target.getClass();
	Method method = null;

	logger.log(Level.FINER,
		   "target class = {0}", targetClass);

	synchronized(methods) {
	    String key = methodName + '/' + arguments.length;

	    logger.log(Level.FINER, "key = {0}", key);

	    if (methods.containsKey(key)) {
		logger.finer("Gets method from cache");

		method = methods.get(key);
	    } else {
		Method[] array = targetClass.getMethods();

		Class[] ptypes;
		boolean m = false;
		for (int i = 0; i < array.length && method == null; i++) {
		    logger.log(Level.FINER,
			       "analyzed method = {0}", array[i]);

		    if (!methodName.equals(array[i].getName())) {
			continue; // name does not match
		    } // end of if

		    if ((ptypes = array[i].
			 getParameterTypes()).length != arguments.length) {

			continue; // arguments number does not match
		    } // end of if

		    m = true;

		    for (int p = 0; p < arguments.length; p++) {
			if (arguments[p] == null) {
			    continue; // skip check on null value
			} // end of if

			if (!ptypes[p].
			    isAssignableFrom(arguments[p].getClass())) {

			    m = false;
			    break;
			} // end of if
		    } // end of if
		    
		    if (m) { // parameters match
			method = array[i];
		    } // end of if
		} // end of for

		if (method != null) { // caches methods
		    methods.put(key, method);
		} // end of if
	    } // end of else
	} // end of sync

	if (method == null) {
	    logger.log(Level.WARNING,
		       "Cannot invoke not existing method on {0}: {1}",
		       new Object[] { targetClass, methodName });

	    return null;
	} // end of if

	// ---

	logger.log(Level.FINER,
		   "Will invoke matching method: {0}", method);

	try {
	    return method.invoke(target, arguments);
	} catch (Exception e) {
	    logger.log(Level.SEVERE, 
		       "Fails to invoke method", e);

	    logger.log(Level.FINER, "method = {0}", method);

	    return null;
	} // end of catch
    } // end of invoke

    /**
     * Returns property value, if exists 
     * (otherwise a runtime exception is thrown).
     *
     * @param target Target instance (of application)
     * @param name Property name
     */
    public static Object getProperty(Object target,
				     String name) {

	if (target == null) {
	    throw new IllegalArgumentException("Invalid target: " + target);
	} // end of if

	if (name == null || name.trim().length() == 0) {
	    throw new IllegalArgumentException("Invalid property name: " +
					       name);

	} // end of if

	// --- 

	Class targetClass = target.getClass();
	PropertyKey key = 
	    new PropertyKey(targetClass,
			    name);

	Method getter = null;

	synchronized(properties) {
	    if (properties.containsKey(key)) {
		getter = properties.get(key);
	    } else {
		try {
		    String getterName = "get" + 
			Character.toUpperCase(name.charAt(0)) +
			name.substring(1);

		    getter = targetClass.
			getMethod(getterName,
				  new Class[0]);

		    properties.put(key, getter);
		} catch (Exception e) {
		    System.err.println("Fails to find getter method: " +
				       e.getMessage());

		} // end of catch

		if (getter == null) {
		    try {
			String getterName = "is" + 
			    Character.toUpperCase(name.charAt(0)) +
			    name.substring(1);

			getter = targetClass.
			    getMethod(getterName,
				      new Class[0]);

			properties.put(key, getter);
		    } catch (Exception e) {
			System.err.println("Fails to find getter method: " +
					   e.getMessage());

		    } // end of catch
		} // end of if
	    } // end of else
	} // end of synchronized

	// ---
		
	if (getter == null) {
	    throw new RuntimeException("Unsupported property: " + key);
	} // end of if

	try {
	    return getter.invoke(target, new Object[0]);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new RuntimeException("Fails to get property value: " + key);
	} // end of catch
    } // end of getProperty

    /**
     * Sets property |value|, if exists 
     * (otherwise a runtime exception is thrown).
     *
     * @param target Target instance (of application)
     * @param name Property name
     * @param value New property value
     */
    public static Object setProperty(Object target,
				     String name,
				     Object value) {

	if (target == null) {
	    throw new IllegalArgumentException("Invalid target: " + target);
	} // end of if

	if (name == null || name.trim().length() == 0) {
	    throw new IllegalArgumentException("Invalid property name: " +
					       name);

	} // end of if

	// --- 

	Class targetClass = target.getClass();
	PropertyKey key = 
	    new PropertyKey(targetClass,
			    name);

	Method setter = null;

	synchronized(properties) {
	    if (properties.containsKey(key)) {
		setter = properties.get(key);
	    } else {
		try {
		    String setterName = "set" + 
			Character.toUpperCase(name.charAt(0)) +
			name.substring(1);

		    Method[] methods = targetClass.getMethods();
		    for (int i = 0; i < methods.length &&
			     setter == null; i++) {

			if (setterName.equals(methods[i].getName())) {
			    setter = methods[i];
			} // end of if
		    } // end of for

		    properties.put(key, setter);
		} catch (Exception e) {
		    System.err.println("Fails to find setter method: " +
				       e.getMessage());

		    e.printStackTrace();
		} // end of catch
	    } // end of else
	} // end of synchronized

	// ---
		
	if (setter == null) {
	    throw new RuntimeException("Unsupported property: " + key);
	} // end of if

	try {
	    return setter.invoke(target, new Object[] { value });
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new RuntimeException("Fails to get property value: " + key);
	} // end of catch
    } // end of setProperty

    /**
     * Returns localized key (VK) from |key|.
     *
     * @param baseName Bundle base name
     * @param locale Bundle preferred locale
     * @param key Key for string bundle
     */
    public static int localizedKey(String baseName,
				   Locale locale,
				   String key) {

	String vk = localizedString(baseName, locale, key);

	int k = -1;

	if (vk == null) {
	    return k;
	} // end of if
	
	vk = vk.toUpperCase();

	try {
	    Field fld = KeyEvent.class.getDeclaredField("VK_" + vk);

	    return ((Integer) fld.get(null)).intValue();
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new RuntimeException("Fails to get localized key: " +
				       e.getMessage());

	} // end of catch
    } // end of localizedKey

    /**
     * Returns localized string for |key|.
     *
     * @param baseName Bundle base name
     * @param locale String locale
     * @param key Key for string bundle
     * @param parameters Localization parameters
     */
    public static String localizedString(String baseName,
					 Locale locale,
					 String key,
					 Object[] parameters) {

	ResourceBundle bundle = ResourceBundle.
	    getBundle(baseName, locale);

	try {
	    String message = bundle.getString(key);
	    
	    return MessageFormat.format(message, parameters);
	} catch (MissingResourceException e) {
	    e.printStackTrace();

	    return '?' + key + '?';
	} // end of catch
    } // end of localizedString

    /**
     * Returns localized string for |key|.
     *
     * @param baseName Bundle base name
     * @param locale String locale
     * @param key Key for string bundle
     */
    public static String localizedString(String baseName,
					 Locale locale,
					 String key) {

	ResourceBundle bundle = ResourceBundle.
	    getBundle(baseName, locale);
	
	try {
	    return bundle.getString(key);
	} catch (MissingResourceException e) {
	    e.printStackTrace();

	    return '?' + key + '?';
	} // end of catch
    } // end of localizedString
    
    // --- Inner classes ---

    /**
     * Property key.
     *
     * @author Cedric Chantepie 
     */
    private static final class PropertyKey {
	// --- Properties ---

	/**
	 * Owner class
	 */
	private Class targetClass = null;

	/**
	 * Property name
	 */
	private String name = null;

	// --- Constructors ---

	/**
	 * No-arg constructor.
	 */
	public PropertyKey() {
	} // end of <init>

	/**
	 * Bulk constructor.
	 *
	 * @param target Target class
	 * @param name Property name
	 * @see #setTargetClass
	 * @see #setPropertyName
	 */
	public PropertyKey(Class target,
			   String name) {

	    this.targetClass = target;
	    this.name = name;
	} // end of <init>

	// --- Property accessors ---

	/**
	 * Returns target class.
	 *
	 * @see #setTargetClass
	 */
	public Class getTargetClass() {
	    return this.targetClass;
	} // end of getTargetClass

	/**
	 * Sets |target| class.
	 *
	 * @param target Target class
	 * @see #getTargetClass
	 */
	public void setTargetClass(Class target) {
	    if (this.targetClass != null &&
		!this.targetClass.equals(target)) {

		throw new IllegalStateException("Target class already set");
	    } // end of if

	    this.targetClass = target;
	} // end of setTargetClass

	/**
	 * Returns property name.
	 *
	 * @see #setPropertyName
	 */
	public String getPropertyName() {
	    return this.name;
	} // end of getPropertyName

	/**
	 * Sets property |name|.
	 *
	 * @param name Property name
	 * @see #getPropertyName
	 */
	public void setPropertyName(String name) {
	    if (this.name != null &&
		!this.name.equals(name)) {

		throw new IllegalStateException("Property name already set");
	    } // end of if

	    this.name = name;
	} // end of setPropertyName

	// --- Object support ---

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
	    return new ToStringBuilder(this).
		append("targetClass", this.targetClass).
		append("propertyName", this.name).
		toString();

	} // end of toString

	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object o) {
	    if (o == null || !(o instanceof PropertyKey)) {
		return false;
	    } // end of if

	    PropertyKey other = (PropertyKey) o;

	    return new EqualsBuilder().
		append(this.targetClass, other.targetClass).
		append(this.name, other.name).
		isEquals();

	} // end of equals

	/**
	 * {@inheritDoc}
	 */
	public int hashCode() {
	    return new HashCodeBuilder(1, 9).
		append(this.targetClass).
		append(this.name).
		toHashCode();

	} // end of hashCode
    } // end of class PropertyKey
} // end of AppUtils
