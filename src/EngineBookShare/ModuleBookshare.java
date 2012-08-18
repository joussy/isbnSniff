/*
 */
package EngineBookShare;

import EngineBookShare.Bookshare.Book.Metadata;
import isbnsniff.BookItem;
import isbnsniff.IsbnModule;
import isbnsniff.IsbnModuleException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.UnmarshalException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.apache.commons.configuration.SubnodeConfiguration;

/**
 *
 * @author jousse_s
 */
public class ModuleBookshare extends IsbnModule {
    final static String MODULE_NAME = "Bookshare";
    private String accessKey;
    private Unmarshaller unmarshaller = null;
    public ModuleBookshare() throws IsbnModuleException
    {
        moduleName = MODULE_NAME;
        try {
            JAXBContext jc = JAXBContext.newInstance(ObjectFactory.class, Bookshare.class);
            unmarshaller = jc.createUnmarshaller();
        } catch (JAXBException ex) {
            throw new IsbnModuleException(IsbnModuleException.ERR_JAXB, ex.getMessage());
        }
    }
    @Override
    protected void processQueryIsbn(BookItem book) throws IsbnModuleException {
        URL query = null;
        Bookshare bookshareXml = null;
        String path = "http://api.bookshare.org/book/isbn/"
                + book.getIsbn().getIsbn13()
                + "/format/xml?api_key=" + accessKey;
        try {
            query = new URL(path);
        } catch (MalformedURLException ex) {
            throw new IsbnModuleException(IsbnModuleException.ERR_URL, ex.getMessage());
        }
        try {
            bookshareXml = (Bookshare)unmarshaller.unmarshal(query);
        } catch (JAXBException ex) {
            throw new IsbnModuleException(IsbnModuleException.ERR_JAXB, ex.getMessage());
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

    @Override
    protected void setConfigurationSpecific(SubnodeConfiguration sObj) {
        accessKey = sObj.getString("api_key", "undefined");
    }
}
