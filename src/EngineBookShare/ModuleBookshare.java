/*
 */
package EngineBookShare;

import EngineBookShare.Bookshare.Book.Metadata;
import isbnsniff.BookItem;
import isbnsniff.ConfigurationParser;
import isbnsniff.ConfigurationParserException;
import isbnsniff.IsbnModule;
import isbnsniff.IsbnModuleException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
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
    final private static String K_API_KEY = "api_key";
    final private static String[] K_LIST = {K_API_KEY};

    private String accessKey;
    private Unmarshaller unmarshaller = null;
    /**
     * Implementation of Bookshare module, using XML POJO
     * @throws IsbnModuleException
     */
    public ModuleBookshare() throws IsbnModuleException
    {
        moduleName = MODULE_NAME;
        try {
            JAXBContext jc = JAXBContext.newInstance(ObjectFactory.class, Bookshare.class);
            unmarshaller = jc.createUnmarshaller();
        } catch (JAXBException ex) {
            throw new IsbnModuleException(IsbnModuleException.ERR_JAXB, ex.getMessage(), Level.SEVERE);
        }
    }
    /**
     * 
     * @param book
     * @throws IsbnModuleException
     */
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
            throw new IsbnModuleException(IsbnModuleException.ERR_URL, ex.getMessage(), Level.WARNING);
        }
        try {
            bookshareXml = (Bookshare)unmarshaller.unmarshal(query);
        } catch (JAXBException ex) {
            throw new IsbnModuleException(IsbnModuleException.ERR_JAXB, ex.getMessage(), Level.SEVERE);
        }
        processBookshareTree(bookshareXml, book);
    }
    /**
     * 
     */
    @Override
    protected void processQueryInitialize() {
    }

    /**
     * 
     */
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
                    /*
                    if (d.getPublishDate() != null) {
                        try {
                            book.setPublicationDate(new SimpleDateFormat("MMddyyyy").parse(d.getPublishDate()));
                        } catch (ParseException ex) {
                        }
                    }
                     * 
                     */
                    book.setPublisher(d.getPublisher());
                    if (d.getCompleteSynopsis() != null)
                        book.setSynopsis(d.getCompleteSynopsis());
                    else
                        book.setSynopsis(d.getBriefSynopsis());
                    if (d.getAuthor() != null) {
                        for (String author : d.getAuthor())
                            book.addAuthor(author);
                    }
                    if (d.getCategory() != null) {
                        for (String category : d.getCategory())
                            book.addCategory(category);
                    }
                }
    }

    /**
     * 
     * @param sObj
     * @throws ConfigurationParserException
     */
    @Override
    protected void setConfigurationSpecific(SubnodeConfiguration sObj)
            throws ConfigurationParserException {
        Map<String, String> valueList
                = ConfigurationParser.getSpecificModuleValues(sObj, K_LIST);
        accessKey = valueList.get(K_API_KEY);
    }
}
