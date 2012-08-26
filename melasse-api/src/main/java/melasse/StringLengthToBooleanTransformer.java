package melasse;

/**
 * Transforms a string length to a boolean value.
 * Returns false if null or empty.
 *
 * @author Cedric Chantepie 
 */
public class StringLengthToBooleanTransformer implements ValueTransformer<String> {
    // --- Shared ---

    /**
     * Non trimming transformer
     */
    private static StringLengthToBooleanTransformer nonTrimmingInstance = null;

    /**
     * Trimming transformer
     */
    private static StringLengthToBooleanTransformer trimmingInstance = null;

    // --- Properties ---

    /**
     * Trim string value before test, or not
     */
    private boolean trimBefore = false;

    // --- Constructors ---

    /**
     * Bulk constructor.
     *
     * @param trimBefore Indicates whether should trim string before 
     * transformation
     */
    private StringLengthToBooleanTransformer(boolean trimBefore) {
	this.trimBefore = trimBefore;
    } // end of <init>

    /**
     * Returns trimming instance.
     */
    public static synchronized ValueTransformer getTrimmingInstance() {
	if (trimmingInstance == null) {
	    trimmingInstance = new StringLengthToBooleanTransformer(true);
	} // end of if

	return trimmingInstance;
    } // end of getTrimmingInstance

    /**
     * Returns non trimming instance.
     */
    public static synchronized ValueTransformer getNonTrimmingInstance() {
	if (nonTrimmingInstance == null) {
	    nonTrimmingInstance = new StringLengthToBooleanTransformer(false);
	} // end of if

	return nonTrimmingInstance;
    } // end of getNonTrimmingInstance

    // ---

    /**
     * Transforms a given string |value| and returns a boolean one.
     * If string is null or empty, return false, true if not.
     *
     * @param value String value
     * @return Boolean value
     */
    public Boolean transform(String value) {
	if (value == null) {
	    return false;
	} // end of if

	if (trimBefore) {
	    value = value.trim();
	} // end of if

	return (value.length() > 0);
    } // end of transform
} // end of class StringLengthToBooleanTransformer
