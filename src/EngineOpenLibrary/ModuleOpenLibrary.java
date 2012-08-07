/*
 */
package EngineOpenLibrary;

import com.fasterxml.jackson.databind.*;
import com.meterware.httpunit.*;
import isbnsniff.BookItem;
import isbnsniff.IsbnModule;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.SAXException;

/**
 *
 * @author jousse_s
 */
public class ModuleOpenLibrary extends IsbnModule {
    final static String MODULE_NAME = "OpenLibrary";
    private String accessKey;
    private WebConversation wc = null;
    public ModuleOpenLibrary(String key)
    {
        moduleName = MODULE_NAME;
        HttpUnitOptions.setScriptingEnabled(false);
        accessKey = key;
    }
    public void setaccessKey(String value)
    {
        accessKey = value;
    }
    
    @Override
    protected void processQueryIsbn(BookItem book)
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OpenLibraryJson olj = null;
        URL jsonQ = null;
        try {
            jsonQ = new URL("http://openlibrary.org/api/books.json?format=json&jscmd=data&bibkeys=ISBN:"
                + book.getIsbn().getIsbn13());
        } catch (MalformedURLException ex) {
            Logger.getLogger(ModuleOpenLibrary.class.getName()).log(Level.SEVERE, null, ex);
        }
        try
        {
            olj = mapper.readValue(jsonQ, OpenLibraryJson.class);
        }
        catch (JsonMappingException ex) {
            Logger.getLogger(ModuleOpenLibrary.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex) {
            Logger.getLogger(ModuleOpenLibrary.class.getName()).log(Level.SEVERE, null, ex);
        }
        processJSON(book, olj);
    }
    @Override
    protected void processQueryInitialize() {
        wc = new WebConversation();
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
}
