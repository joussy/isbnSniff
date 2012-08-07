/*
 */
package EngineBookShare;

import EngineBookShare.Bookshare.Book.Metadata;
import isbnsniff.BookItem;
import isbnsniff.IsbnModule;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author jousse_s
 */
public class ModuleBookshare extends IsbnModule {
    final static String MODULE_NAME = "Bookshare";
    private String accessKey;
    private Unmarshaller unmarshaller = null;
    public ModuleBookshare(String key)
    {
        moduleName = MODULE_NAME;
        accessKey = key;
        try {
            JAXBContext jc = JAXBContext.newInstance(ObjectFactory.class, Bookshare.class);
            unmarshaller = jc.createUnmarshaller();
        } catch (JAXBException ex) {
            Logger.getLogger(ModuleBookshare.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void setaccessKey(String value)
    {
        accessKey = value;
    }
    @Override
    protected void processQueryIsbn(BookItem book) {
        URL query = null;
        Bookshare bookshareXml = null;
        String path = "http://api.bookshare.org/book/isbn/"
                + book.getIsbn().getIsbn13()
                + "/format/xml?api_key=" + accessKey;
        try {
            query = new URL(path);
        } catch (MalformedURLException ex) {
            Logger.getLogger(ModuleBookshare.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            bookshareXml = (Bookshare)unmarshaller.unmarshal(query);
        } catch (JAXBException ex) {
            Logger.getLogger(ModuleBookshare.class.getName()).log(Level.SEVERE, null, ex);
        }
        processBookshareTree(bookshareXml, book);
    }
    @Override
    protected void processQueryInitialize() {
    }

    @Override
    protected void processQueryTerminate() {
    }

    private void processBookshareTree(Bookshare bookshareXml, BookItem book) {
        if (bookshareXml != null)
            if (bookshareXml.getBook() != null)
                if (bookshareXml.getBook().getMetadata() != null)
                {
                    Metadata d = bookshareXml.getBook().getMetadata();
                    book.setTitle(d.getTitle());
                }
    }
}
