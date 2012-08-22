/*
 */
package isbnsniff;

/**
 * Configuration Interpreter Exception
 * @author jousse_s
 */
public class ConfigurationParserException extends Exception {
    /**
     * A expected key has not been defined in the configuration file
     */
    public static int ERR_UNDEFINED_KEY = 1;
    /**
     * A module specified in the configuration file is actually not loaded in the core
     */
    public static int ERR_UNKNOWN_MODULE = 2;
    private String section = null;
    private String msg = null;

    /**
     * 
     * @param message The error message
     */
    public ConfigurationParserException(String message) {
        msg = message;
    }
    /**
     * 
     * @param message The error message
     * @param value The section associated with the error message
     */
    public ConfigurationParserException(String message, String value) {
        msg = message;
        section = value;
    }
    
    /**
     * Get the section associated with the error
     * @return
     */
    public String getSection() {
        return section;
    }
    
    /**
     * Set the section associated with the error
     * @param value
     */
    public void setSection(String value) {
        section = value;
    }
    
    @Override
    public String getMessage() {
        if (section != null)
            return msg + " (" + section + " section)";
        return msg;
    }
}
