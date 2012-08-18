/*
 */
package EngineOpenLibrary;

import com.fasterxml.jackson.databind.*;
import isbnsniff.BookItem;
import isbnsniff.IsbnModule;
import isbnsniff.IsbnModuleException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.commons.configuration.SubnodeConfiguration;

/**
 *
 * @author jousse_s
 */
public class ModuleOpenLibrary extends IsbnModule {
    final static String MODULE_NAME = "OpenLibrary";
    public ModuleOpenLibrary()
    {
        moduleName = MODULE_NAME;
    }
    @Override
    protected void processQueryIsbn(BookItem book) throws IsbnModuleException
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OpenLibraryJson olj = null;
        URL jsonQ = null;
        try {
            jsonQ = new URL("http://openlibrary.org/api/books.json?format=json&jscmd=data&bibkeys=ISBN:"
                + book.getIsbn().getIsbn13());
        } catch (MalformedURLException ex) {
            throw new IsbnModuleException(IsbnModuleException.ERR_URL, ex.getMessage());
        }
        try
        {
            olj = mapper.readValue(jsonQ, OpenLibraryJson.class);
        }
        catch (JsonMappingException ex) {
            throw new IsbnModuleException(IsbnModuleException.ERR_JAXB, ex.getMessage());
        }
        catch (IOException ex) {
            throw new IsbnModuleException(IsbnModuleException.ERR_JAXB, ex.getMessage());
        }
        processJSON(book, olj);
    }
    @Override
    protected void processQueryInitialize() {
    }
    @Override
    protected void processQueryTerminate() {
    }

    private void processJSON(BookItem book, OpenLibraryJson olj) {
        if (olj == null)
            return;
        if (olj.getISBN() == null)
            return;
        book.setTitle(olj.getISBN().getTitle());
        book.setNbPages(olj.getISBN().getNumber_of_pages().intValue());
    }

    @Override
    protected void setConfigurationSpecific(SubnodeConfiguration sObj) {
    }
}
