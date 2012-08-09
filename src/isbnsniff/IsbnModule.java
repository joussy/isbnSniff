/*
 */
package isbnsniff;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.configuration.SubnodeConfiguration;

/**
 *
 * @author jousse_s
 */
public abstract class IsbnModule {

    private List<BookItem> bookItemList = new ArrayList();
    protected String moduleName = "unknown";
    boolean enabled = false;

    protected abstract void processQueryIsbn(BookItem nb);

    protected abstract void processQueryInitialize();

    protected abstract void processQueryTerminate();
    
    protected abstract void setConfigurationSpecific(SubnodeConfiguration sObj);

    //SOON
    //protected abstract void setConfiguration();

    public void addBookItem(BookItem book) {
        bookItemList.add(book);
    }

    public void processQuery() {
        processQueryInitialize();
        for (BookItem book : bookItemList) {
            processQueryIsbn(book);
        }
        processQueryTerminate();
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setConfiguration(SubnodeConfiguration sObj) {
        try {
            enabled = sObj.getBoolean("enable", enabled);
        } catch (ConversionException e) {
            //ERROR PARSING ERROR CHECK
            //Logger.getLogger(IsbnModule.class.getName()).log(Level.SEVERE, null, e);            
        }
        setConfigurationSpecific(sObj);
    }
}
