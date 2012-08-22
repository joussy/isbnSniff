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

    /**
     * The list of BookItem to complete after Engine Query processing
     */
    protected List<BookItem> bookItemList = new ArrayList<BookItem>();
    /**
     * 
     */
    protected String moduleName = "unknown";

    /**
     * Process Engine Query for a BookItem
     * @param nb
     * @throws IsbnModuleException
     */
    protected abstract void processQueryIsbn(BookItem nb) throws IsbnModuleException;

    /**
     * 
     * @throws IsbnModuleException
     */
    protected abstract void processQueryInitialize() throws IsbnModuleException;

    /**
     * 
     * @throws IsbnModuleException
     */
    protected abstract void processQueryTerminate() throws IsbnModuleException;
    
    /**
     * Configure the Engine module from Configuration file
     * @param sObj The ini section containing the keys/values
     * @throws ConfigurationParserException
     */
    protected abstract void setConfigurationSpecific(SubnodeConfiguration sObj) throws ConfigurationParserException;

    //SOON
    //protected abstract void setConfiguration();

    /**
     * add a bookItem to the list
     * @param book
     */
    public void addBookItem(BookItem book) {
        bookItemList.add(book);
    }

    /**
     * Fill the BookItem list with retrieved value from search engine query results
     * @throws IsbnModuleExceptionList
     */
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

    /**
     * Retrieve the ItemBook List loaded in the module
     * @return
     */
    public List<BookItem> getBookItemList() {
        return bookItemList;
    }

    /**
     * 
     * @param nb
     * @return
     */
    protected BookItem getBookItem(IsbnNumber nb) {
        for (BookItem book : bookItemList) {
            if (book.getIsbn().equals(nb)) {
                return book;
            }
        }
        return null;
    }

    /**
     * Get the name of the Search Engine
     * @return
     */
    public String getModuleName() {
        return moduleName;
    }

    /**
     * Configure module from INI configuration file keys/values
     * @param sObj The section containing the keys/values
     * @throws ConfigurationParserException
     */
    public void setConfiguration(SubnodeConfiguration sObj) throws ConfigurationParserException {
        try {
            setConfigurationSpecific(sObj);
        } catch (ConfigurationParserException ex) {
            ex.setSection(moduleName);
            throw ex;
        }
    }
}
