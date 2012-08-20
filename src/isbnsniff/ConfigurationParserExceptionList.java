/*
 */
package isbnsniff;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jousse_s
 */
public class ConfigurationParserExceptionList extends Exception {

    private List<ConfigurationParserException> messageList =
            new ArrayList<ConfigurationParserException>();
    public void addError(ConfigurationParserException ex) {
        messageList.add(ex);
    }
    public List<ConfigurationParserException> getErrorList() {
        return messageList;
    }
}
