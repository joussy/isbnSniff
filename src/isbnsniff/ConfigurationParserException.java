/*
 */
package isbnsniff;

/**
 *
 * @author jousse_s
 */
public class ConfigurationParserException extends Exception {
    public static int ERR_UNDEFINED_KEY = 1;
    public static int ERR_UNKNOWN_MODULE = 2;
    public ConfigurationParserException(String message) {
        super(message);
    }
}
