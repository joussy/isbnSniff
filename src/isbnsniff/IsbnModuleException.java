/*
 */
package isbnsniff;

/**
 *
 * @author jousse_s
 */
public class IsbnModuleException extends Exception {
    final public static int ERR_JAXB = 1;
    final public static int ERR_URL = 2;
    final public static int ERR_WEBSERVICE = 3;
    final public static int ERR_UNEXPECTED_FORMAT = 4;
    int errType = 0;
    private String msg = null;
    private String moduleName = null;
    public IsbnModuleException(int type, String message)
    {
        errType = type;
        msg = message;
    }
    @Override
    public String getMessage() {
        return msg;
    }
    public void setModuleName(String name) {
        moduleName = name;
    }
    public String getModuleName() {
        return moduleName;
    }
}
