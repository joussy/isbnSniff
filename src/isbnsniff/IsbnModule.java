/*
 */
package isbnsniff;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.configuration.SubnodeConfiguration;

/**
 *
 * @author jousse_s
 */
public abstract class IsbnModule {

    protected List<BookItem> bookItemList = new ArrayList<BookItem>();
    protected String moduleName = "unknown";

    protected abstract void processQueryIsbn(BookItem nb) throws IsbnModuleException;

    protected abstract void processQueryInitialize() throws IsbnModuleException;

    protected abstract void processQueryTerminate() throws IsbnModuleException;
    
    protected abstract void setConfigurationSpecific(SubnodeConfiguration sObj) throws ConfigurationParserException;

    //SOON
    //protected abstract void setConfiguration();

    public void addBookItem(BookItem book) {
        bookItemList.add(book);
    }

    public void processQuery() throws IsbnModuleExceptionList {
        IsbnModuleExceptionList exList = new IsbnModuleExceptionList(moduleName);
        try {
            processQueryInitialize();
            for (BookItem book : bookItemList) {
                try {
                    processQueryIsbn(book);
                } catch (IsbnModuleException ex) {
                    ex.setIsbn(book.getIsbn());
                    exList.addError(ex);
                }
            }
            processQueryTerminate();
        } catch (IsbnModuleException ex) {
            exList.addError(ex);
        }
        if (exList.getErrorList().size() > 0)
            throw exList;
    }

    public List<BookItem> getBookItemList() {
        return bookItemList;
    }

    protected BookItem getBookItem(IsbnNumber nb) {
        for (BookItem book : bookItemList) {
            if (book.getIsbn().equals(nb)) {
                return book;
            }
        }
        return null;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setConfiguration(SubnodeConfiguration sObj) throws ConfigurationParserException {
        setConfigurationSpecific(sObj);
    }
}
