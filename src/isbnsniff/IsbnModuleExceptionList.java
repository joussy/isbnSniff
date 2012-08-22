/*
 */
package isbnsniff;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jousse_s
 */
public class IsbnModuleExceptionList extends Exception {
    
    private List<IsbnModuleException> messageList =
            new ArrayList<IsbnModuleException>();
    private String moduleName;

    /**
     * List of ModuleException thrown by a Search Engine module
     * @param value
     */
    public IsbnModuleExceptionList(String value) {
        moduleName = value;
    }

    /**
     * Add an error message
     * @param ex
     */
    public void addError(IsbnModuleException ex) {
        messageList.add(ex);
    }

    /**
     * Get the list of error messages thrown by an ISBN module
     * @return
     */
    public List<IsbnModuleException> getErrorList() {
        return messageList;
    }

    /**
     * Get the name of the ISBN module
     * @return
     */
    public String getModuleName() {
        return moduleName;
    }
}
