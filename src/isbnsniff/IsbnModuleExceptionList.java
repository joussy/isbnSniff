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

    public IsbnModuleExceptionList(String value) {
        moduleName = value;
    }

    public void addError(IsbnModuleException ex) {
        messageList.add(ex);
    }

    public List<IsbnModuleException> getErrorList() {
        return messageList;
    }

    public String getModuleName() {
        return moduleName;
    }
}
