/*
 */
package isbnsniff;

import java.util.logging.Level;

/**
 *
 * @author jousse_s
 */
public class IsbnModuleException extends Exception {

    /**
     * Jaxb processing error
     */
    final public static Integer ERR_JAXB = 1;
    /**
     * URL generated in wrong
     */
    final public static int ERR_URL = 2;
    /**
     * The webservice loaded does not work correctly
     */
    final public static int ERR_WEBSERVICE = 3;
    /**
     * The XML/JSON format sent back by the Engine server has an unexpected format structure
     */
    final public static int ERR_UNEXPECTED_FORMAT = 4;
    
    final private static String MSG_ERR_JAXB = "Unmarshaller";
    final private static String MSG_ERR_URL = "URL";
    final private static String MSG_ERR_WEBSERVICE = "Webservice";
    final private static String MSG_ERR_UNEXPECTED_FORMAT = "Response Structure";

    int errType = 0;
    private String msg = null;
    private IsbnNumber isbn = null;
    private final Level level;
    private String moduleName;

    /**
     * Search Engine Exception
     * @param type Type of error message
     * @param message Details the error
     * @param lev Error level associated to the error
     */
    public IsbnModuleException(int type, String message, Level lev) {
        errType = type;
        msg = message;
        level = lev;
    }

    @Override
    public String getMessage() {
        String err = "[";
        if (errType == ERR_JAXB)
            err += MSG_ERR_JAXB;
        else if (errType == ERR_URL)
            err += MSG_ERR_URL;
        else if (errType == ERR_WEBSERVICE)
            err += MSG_ERR_WEBSERVICE;
        else if (errType == ERR_UNEXPECTED_FORMAT)
            err += MSG_ERR_UNEXPECTED_FORMAT;
        err += "] ";
        if (msg != null)
            err += msg;
        return err;
    }

    /**
     * Specify the ISBN associated with the error.
     * @param value
     */
    public void setIsbn(IsbnNumber value) {
        isbn = value;
    }
    
    /**
     * Get the ISBN associated with the error message, null otherwise
     * @return An ISBN
     */
    public IsbnNumber getIsbnNumber() {
        return isbn;
        
    }
    
    /**
     * Specify the name of module who has generated an error
     * @param value
     */
    public void setModuleName(String value) {
        moduleName = value;
    }
    
    /**
     * Get the module Name associated with the error message
     * @return
     */
    public String getModuleName() {
        return moduleName;
    }
    /**
     * Return the Error Level of the message
     * @return
     */
    public Level getErrorLevel() {
        return level;
    }
}
