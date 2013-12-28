package melasse;

import java.util.Locale;

import java.util.logging.Logger;
import java.util.logging.Level;

import java.text.NumberFormat;

import java.awt.Dimension;
import java.awt.Component;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JSpinner;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jdesktop.layout.GroupLayout;

import melasse.swing.SpinnerNumberModel;

import melasse.util.AppUtils;

/**
 * Convert panel.
 *
 * @author Cedric Chantepie ()
 */
public final class ConvertPanel extends JPanel {
    // --- Properties ---

    /**
     * Owning application
     */
    private Object application = null;

    /**
     * Application logger
     */
    private Logger logger = null;

    /**
     * Byte spinner model
     */
    private SpinnerNumberModel byteModel = null;

    /**
     * Kbyte label
     */
    private JLabel kbyteText = null;

    /**
     * Comment label
     */
    private JLabel commentText = null;

    // --- Constructors ---

    /**
     * Bulk constructor.
     *
     * @param application Owning application
     */
    protected ConvertPanel(Object application) {
	super();

	if (application == null) {
	    throw new IllegalArgumentException("Owner not specified");
	} // end of if

	// ---

	this.application = application;
	this.logger = AppUtils.getLogger(this.application);
    } // end of <init>

    // ---

    /**
     * Sets up UI.
     */
    protected void setUpUI() {
	logger.fine("Will set up convert panel");

	Locale locale = (Locale) AppUtils.
	    getProperty(this.application, "locale");

	logger.log(Level.FINER,
		   "locale = {0}", locale);

	// prepare labels
	String sizeTitle = AppUtils.
	    localizedString("melasse.ConvertPanel",
			    locale,
			    "size.title");

	String byteStr = AppUtils.
	    localizedString("melasse.ConvertPanel",
			    locale,
			    "byte.label");

	String kbyteStr = AppUtils.
	    localizedString("melasse.ConvertPanel",
			    locale,
			    "kbyte.label");

	JLabel byteLabel = new JLabel(byteStr);
	JLabel kbyteLabel = new JLabel(kbyteStr);

	// byte spinner
	this.byteModel = new SpinnerNumberModel();

	this.byteModel.setMinimum(new Integer(0));
	this.byteModel.setValue(new Integer(0));
	this.byteModel.setStepSize(new Integer(100));

	JSpinner byteSpinner = new JSpinner(this.byteModel);
	Dimension size = byteSpinner.getPreferredSize();

	size.setSize(75, size.getHeight());

	byteSpinner.setPreferredSize(size);

	// Size sub-panel
	JPanel sizePanel = new JPanel();
	this.kbyteText = new JLabel("", JLabel.CENTER);
	this.commentText = new JLabel();

	size = sizePanel.getPreferredSize();

	size.setSize(225, size.getHeight());

	sizePanel.setPreferredSize(size);

	// sets layout
	GroupLayout slayout = new GroupLayout(sizePanel);

	sizePanel.setLayout(slayout);

	slayout.setAutocreateGaps(true);
	slayout.setAutocreateContainerGaps(true);

	// lays out
	GroupLayout.Group svgroup = 
	    slayout.createSequentialGroup().
	    add(slayout.createParallelGroup(GroupLayout.LEADING).
		add(byteLabel,
		    GroupLayout.PREFERRED_SIZE,
		    GroupLayout.DEFAULT_SIZE,
		    GroupLayout.PREFERRED_SIZE).
		add(byteSpinner,
		    GroupLayout.PREFERRED_SIZE,
		    GroupLayout.DEFAULT_SIZE,
		    GroupLayout.PREFERRED_SIZE)).
	    add(slayout.createParallelGroup(GroupLayout.LEADING).
		add(kbyteLabel,
		    GroupLayout.PREFERRED_SIZE,
		    GroupLayout.DEFAULT_SIZE,
		    GroupLayout.PREFERRED_SIZE).
		add(this.kbyteText,
		    GroupLayout.PREFERRED_SIZE,
		    GroupLayout.DEFAULT_SIZE,
		    GroupLayout.PREFERRED_SIZE)).
	    add(this.commentText,
		GroupLayout.PREFERRED_SIZE,
		GroupLayout.DEFAULT_SIZE,
		GroupLayout.PREFERRED_SIZE);

	GroupLayout.Group shgroup = 
	    slayout.createParallelGroup(GroupLayout.LEADING).
	    add(slayout.createSequentialGroup().
		add(byteLabel,
		    0,
		    GroupLayout.DEFAULT_SIZE,
		    Short.MAX_VALUE).
		add(byteSpinner,
		    GroupLayout.PREFERRED_SIZE,
		    GroupLayout.DEFAULT_SIZE,
		    GroupLayout.PREFERRED_SIZE)).
	    add(slayout.createSequentialGroup().
		add(kbyteLabel,
		    0,
		    GroupLayout.DEFAULT_SIZE,
		    Short.MAX_VALUE).
		add(this.kbyteText,
		    GroupLayout.PREFERRED_SIZE,
		    GroupLayout.DEFAULT_SIZE,
		    GroupLayout.PREFERRED_SIZE)).
	    add(this.commentText,
		0,
		GroupLayout.DEFAULT_SIZE,
		Short.MAX_VALUE);
	
	slayout.setVerticalGroup(svgroup);
	slayout.setHorizontalGroup(shgroup);

	slayout.linkSize(new Component[] {
	    byteLabel, kbyteLabel
	}, GroupLayout.HORIZONTAL);

	sizePanel.setBorder(BorderFactory.
			    createTitledBorder(sizeTitle));

	// explanation sub-panel
	ExplanationPanel explain = 
	    new ExplanationPanel(this.application);
	
	explain.setTitleKey("convert.title");
	explain.setBodyKey("convert.body");
	explain.setCodeKey("convert.code");

	explain.setUp();

	// sets layout
	GroupLayout layout = new GroupLayout(this);

	this.setLayout(layout);

	layout.setAutocreateGaps(true);
	layout.setAutocreateContainerGaps(true);

	// lays out
	GroupLayout.Group vgroup = 
	    layout.createParallelGroup(GroupLayout.LEADING).
	    add(sizePanel,
		GroupLayout.PREFERRED_SIZE,
		GroupLayout.DEFAULT_SIZE,
		Short.MAX_VALUE).
	    add(explain,
		0,
		GroupLayout.DEFAULT_SIZE,
		Short.MAX_VALUE);

	GroupLayout.Group hgroup = 
	    layout.createSequentialGroup().
	    add(sizePanel,
		GroupLayout.PREFERRED_SIZE,
		GroupLayout.DEFAULT_SIZE,
		GroupLayout.PREFERRED_SIZE).
	    add(explain,
		0,
		GroupLayout.DEFAULT_SIZE,
		Short.MAX_VALUE);
	
	layout.setVerticalGroup(vgroup);
	layout.setHorizontalGroup(hgroup);
    } // end of setUpUI

    /**
     * Sets up bindings.
     */
    protected void setUpBindings() {
	Locale locale = (Locale) AppUtils.
	    getProperty(this.application, "locale");

	String lessComment = AppUtils.
	    localizedString("melasse.ConvertPanel",
			    locale,
			    "lessThan20Kb");

	String greaterOrEqualComment = AppUtils.
	    localizedString("melasse.ConvertPanel",
			    locale,
			    "greaterOrEqual20Kb");

	Binder.bind("value", this.byteModel,
		    "text", this.kbyteText,
		    new BindingOptionMap().
		    add(BindingKey.INPUT_TRANSFORMER,
			new ByteToKByteTransformer(locale)));

	Binder.bind("value", this.byteModel,
		    "text", this.commentText,
		    new BindingOptionMap().
		    add(BindingKey.INPUT_TRANSFORMER,
			new IntegerToCommentTransformer(20*1024,
							lessComment,
							greaterOrEqualComment)));

	Binder.bind("value", this.byteModel,
		    "foreground", this.commentText,
		    new BindingOptionMap().
		    add(BindingKey.INPUT_TRANSFORMER,
			new IntegerToColorTransformer(20*1024,
						      Color.GREEN,
						      Color.RED)));

    } // end of setUpBindings

    // --- Inner classes ---

    /**
     * Transforms an byte integer to kbyte integer
     *
     * @author Cedric Chantepie ()
     */
    protected class ByteToKByteTransformer 
	implements ValueTransformer<Integer> {
	
	// --- Properties ---

	/**
	 * Decimal format
	 */
	private NumberFormat format = null;

	// --- Constructors ---

	/**
	 * Bulk constructor.
	 *
	 * @param locale Locale for number format
	 */
	private ByteToKByteTransformer(Locale locale) {
	    if (locale == null) {
		throw new IllegalArgumentException("No locale specified");
	    } // end of if

	    this.format = NumberFormat.getNumberInstance(locale);
	} // end of <init>

	// ---

	/**
	 * Transforms byte |value| to a kbyte one (divided by 1024).
	 *
	 * @param value Byte integer value
	 * @return KByte float value as string
	 */
	public String transform(Integer value) {
	    if (value == null) {
		return "0";
	    } // end of if

	    float f = value.intValue() / 1024f;

	    return this.format.format(f);
	} // end of transform
    } // end of class ByteToKByteTransformer

    /**
     * Transforms an integer to a comment, according a given limit.
     *
     * @author Cedric Chantepie ()
     */
    protected class IntegerToCommentTransformer 
	implements ValueTransformer<Integer> {
	
	// --- Properties ---

	/**
	 * Integer limit
	 */
	private int limit = -1;

	/**
	 * Less comment
	 */
	private String lessComment = null;

	/**
	 * Greater than of equal comment
	 */
	private String greaterOrEqualComment = null;

	// --- Constructors ---

	/**
	 * Bulk constructor.
	 *
	 * @param limit
	 */
	private IntegerToCommentTransformer(int limit,
					    String lessComment,
					    String greaterOrEqualComment) {

	    this.limit = limit;
	    this.lessComment = lessComment;
	    this.greaterOrEqualComment = greaterOrEqualComment;
	} // end of <init>

	// ---

	/**
	 * If value is less than limit, return less comment,
	 * else return greater or equal comment.
	 *
	 * @param value Integer value
	 * @return Comment according given limit
	 */
	public String transform(Integer value) {
	    if (value == null) {
		return "";
	    } // end of if

	    if (value.intValue() < this.limit) {
		return this.lessComment;
	    } // end of if

	    return this.greaterOrEqualComment;
	} // end of transform
    } // end of class IntegerToCommentTransformer

    /**
     * Transforms an integer to a color, according a given limit.
     *
     * @author Cedric Chantepie ()
     */
    protected class IntegerToColorTransformer 
	implements ValueTransformer<Integer> {
	
	// --- Properties ---

	/**
	 * Integer limit
	 */
	private int limit = -1;

	/**
	 * Less color
	 */
	private Color lessColor = null;

	/**
	 * Greater than of equal color
	 */
	private Color greaterOrEqualColor = null;

	// --- Constructors ---

	/**
	 * Bulk constructor.
	 *
	 * @param limit
	 */
	private IntegerToColorTransformer(int limit,
					  Color lessColor,
					  Color greaterOrEqualColor) {

	    this.limit = limit;
	    this.lessColor = lessColor;
	    this.greaterOrEqualColor = greaterOrEqualColor;
	} // end of <init>

	// ---

	/**
	 * If value is less than limit, return less color,
	 * else return greater or equal color.
	 *
	 * @param value Integer value
	 * @return Color according given limit
	 */
	public Color transform(Integer value) {
	    if (value == null || 
		value.intValue() < this.limit) {

		return this.lessColor;
	    } // end of if

	    return this.greaterOrEqualColor;
	} // end of transform
    } // end of class IntegerToColorTransformer
} // end of class ConvertPanel
