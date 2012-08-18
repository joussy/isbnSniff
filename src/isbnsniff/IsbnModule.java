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

    private List<BookItem> bookItemList = new ArrayList();
    protected String moduleName = "unknown";

    protected abstract void processQueryIsbn(BookItem nb) throws IsbnModuleException;

    protected abstract void processQueryInitialize() throws IsbnModuleException;

    protected abstract void processQueryTerminate() throws IsbnModuleException;
    
    protected abstract void setConfigurationSpecific(SubnodeConfiguration sObj);

    //SOON
    //protected abstract void setConfiguration();

    public void addBookItem(BookItem book) {
        bookItemList.add(book);
    }

    public void processQuery() throws IsbnModuleException {
        try {
            processQueryInitialize();
            for (BookItem book : bookItemList) {
                processQueryIsbn(book);
            }
            processQueryTerminate();
        } catch (IsbnModuleException ex) {
            ex.setModuleName(moduleName);
            throw ex;
        }
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

    public void setConfiguration(SubnodeConfiguration sObj) {
        setConfigurationSpecific(sObj);
    }
}
