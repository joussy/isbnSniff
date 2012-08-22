/*
 */
package isbnsniff;

import java.util.ArrayList;
import java.util.List;

/**
 * List of ParserException found during Configuration file interpretation
 * @author jousse_s
 */
public class ConfigurationParserExceptionList extends Exception {

    private List<ConfigurationParserException> messageList =
            new ArrayList<ConfigurationParserException>();
    /**
     * Add a new ConfigurationParserException error
     * @param ex
     */
    public void addError(ConfigurationParserException ex) {
        messageList.add(ex);
    }
    /**
     * get the list of exceptions
     * @return A list of ConfigurationParserException
     */
    public List<ConfigurationParserException> getErrorList() {
        return messageList;
    }
}
