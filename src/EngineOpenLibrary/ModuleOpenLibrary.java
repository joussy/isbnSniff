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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import org.apache.commons.configuration.SubnodeConfiguration;

/**
 *
 * @author jousse_s
 */
public class ModuleOpenLibrary extends IsbnModule {
    final static String MODULE_NAME = "OpenLibrary";
    /**
     * Implementation of OpenLibrary Engine using Json Pojo and Jackson unmarshaller
     */
    public ModuleOpenLibrary()
    {
        moduleName = MODULE_NAME;
    }
    /**
     * Send a query for each bookItem and retrieve the result using Jackson unmarshaller
     * @param book
     * @throws IsbnModuleException
     */
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
            throw new IsbnModuleException(IsbnModuleException.ERR_URL, ex.getMessage(), Level.WARNING);
        }
        try
        {
            olj = mapper.readValue(jsonQ, OpenLibraryJson.class);
        }
        catch (JsonMappingException ex) {
            throw new IsbnModuleException(IsbnModuleException.ERR_JAXB, ex.getMessage(), Level.SEVERE);
        }
        catch (IOException ex) {
            throw new IsbnModuleException(IsbnModuleException.ERR_JAXB, ex.getMessage(), Level.WARNING);
        }
        processJSON(book, olj);
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

    private void processJSON(BookItem book, OpenLibraryJson olj) {
        if (olj == null)
            return;
        if (olj.getISBN() == null)
            return;
        book.setTitle(olj.getISBN().getTitle());
        if (olj.getISBN().getNumber_of_pages() != null)
            book.setNbPages(olj.getISBN().getNumber_of_pages().intValue());
        //LinkedHashMap t = (LinkedHashMap) olj.getISBN().getAuthors();
        if (olj.getISBN().getAuthors() != null)
        {
            for (LinkedHashMap entry : (List<LinkedHashMap>) olj.getISBN().getAuthors()) {
                book.addAuthor((String) entry.get("name"));
            }
        }
        if (olj.getISBN().getPublish_date() != null) {
            //DateFormat format = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
            Date publicationDate = null;
            try {
                //Date date = format.parse(olj.getISBN().getPublish_date());                
                //book.setPublicationDate(date);
                publicationDate = new SimpleDateFormat("MMMM dd, yyyy").parse(olj.getISBN().getPublish_date());

            } catch (ParseException ex) {
                try {
                    publicationDate = new SimpleDateFormat("yyyy").parse(olj.getISBN().getPublish_date());
                } catch (ParseException ex1) {
                }
            }
            book.setPublicationDate(publicationDate);
        }
        if (olj.getISBN().getPublishers() != null)
        {
            for (LinkedHashMap entry : (List<LinkedHashMap>) olj.getISBN().getPublishers()) {
                book.setPublisher((String) entry.get("name"));
                break;
            }
        }
    }

    /**
     * 
     * @param sObj
     */
    @Override
    protected void setConfigurationSpecific(SubnodeConfiguration sObj) {
    }
}
