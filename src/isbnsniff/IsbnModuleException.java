/*
 */
package isbnsniff;

import java.util.logging.Level;

/**
 *
 * @author jousse_s
 */
public class IsbnModuleException extends Exception {

    final public static Integer ERR_JAXB = 1;
    final public static int ERR_URL = 2;
    final public static int ERR_WEBSERVICE = 3;
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

    public void setIsbn(IsbnNumber value) {
        isbn = value;
    }
    
    public IsbnNumber getIsbnNumber() {
        return isbn;
        
    }
    
    public void setModuleName(String value) {
        moduleName = value;
    }
    
    public String getModuleName() {
        return moduleName;
    }
    public Level getErrorLevel() {
        return level;
    }
}
