/*
 */
package EngineIsbnDb;

import EngineIsbnDb.ISBNdbBooks.BookList.BookData;
import EngineIsbnDb.ISBNdbBooks.BookList.BookData.Authors.Person;
import isbnsniff.BookItem;
import isbnsniff.ConfigurationParser;
import isbnsniff.ConfigurationParserException;
import isbnsniff.IsbnModule;
import isbnsniff.IsbnModuleException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.apache.commons.configuration.SubnodeConfiguration;

/**
 *
 * @author jousse_s
 */
public class ModuleIsbndb extends IsbnModule {

    final static String MODULE_NAME = "IsbnDb";
    final private static String K_API_KEY = "api_key";
    final private static String[] K_LIST = {K_API_KEY};
    
    private String accessKey;
    private Unmarshaller unmarshallerBooks = null;
    private Unmarshaller unmarshallerPublishers = null;

    public ModuleIsbndb() throws IsbnModuleException {
        moduleName = MODULE_NAME;
        try {
            JAXBContext jcBooks = JAXBContext.newInstance(ObjectFactoryBooks.class, ISBNdbBooks.class);
            JAXBContext jcPublishers = JAXBContext.newInstance(ObjectFactoryPublishers.class, ISBNdbPublishers.class);
            unmarshallerBooks = jcBooks.createUnmarshaller();
            unmarshallerPublishers = jcPublishers.createUnmarshaller();
        } catch (JAXBException ex) {
            throw new IsbnModuleException(IsbnModuleException.ERR_JAXB, ex.getMessage(), Level.SEVERE);
        }
    }

    @Override
    protected void processQueryIsbn(BookItem book) throws IsbnModuleException {
        URL query = null;
        ISBNdbBooks isbndbXml = null;
        String path = "http://isbndb.com/api/books.xml?"
                + "results=details"
                + "&results=authors"
                + "&results=texts"
                + "&index1=isbn"
                + "&value1=" + book.getIsbn().getIsbn13()
                + "&access_key=" + accessKey;
        try {
            query = new URL(path);
        } catch (MalformedURLException ex) {
            throw new IsbnModuleException(IsbnModuleException.ERR_URL, ex.getMessage(), Level.WARNING);
        }
        try {
            isbndbXml = (ISBNdbBooks) unmarshallerBooks.unmarshal(query);
        } catch (JAXBException ex) {
            throw new IsbnModuleException(IsbnModuleException.ERR_JAXB, ex.getMessage(), Level.SEVERE);
        }
        processIsbndbTree(isbndbXml, book);
    }

    @Override
    protected void processQueryInitialize() {
    }

    @Override
    protected void processQueryTerminate() {
    }

    private void processIsbndbTree(ISBNdbBooks isbndbXml, BookItem book) throws IsbnModuleException {
        if (isbndbXml == null) {
            throw new IsbnModuleException(IsbnModuleException.ERR_UNEXPECTED_FORMAT, "No BookList", Level.INFO);
        }
        if (isbndbXml.getBookList() == null)
            throw new IsbnModuleException(IsbnModuleException.ERR_UNEXPECTED_FORMAT, isbndbXml.getErrorMessage(), Level.INFO);
        if (isbndbXml.getBookList().getBookData() != null) {
            BookData bookXml = isbndbXml.getBookList().getBookData();
            book.setSynopsis(bookXml.getSummary());
            book.setLongTitle(bookXml.getTitleLong());
            book.setTitle(bookXml.getTitle());
            processAuthors(book, bookXml);
            processDetails(book, bookXml);
            if (bookXml.getPublisherText() != null && bookXml.getPublisherText().getPublisherId() != null) {
                book.setPublisher(processPublisherLookup(book,
                        bookXml.getPublisherText().getPublisherId()));
            }
        }
    }

    private void processDetails(BookItem book, BookData bookXml) {
        if (bookXml.getDetails() != null) {
            //lcc
            book.setLcc(bookXml.getDetails().getLccNumber());
            //nb pages
            Pattern p = Pattern.compile("([0-9]+) (p\\.|pages)");
            Matcher m = p.matcher(bookXml.getDetails().getPhysicalDescriptionText());
            if (m.find()) {
                book.setNbPages(Integer.parseInt(m.group(1)));
            }
            //Publication date
            String publicationDate = null;
            p = Pattern.compile("[0-9]{4}");
            m = p.matcher(bookXml.getDetails().getEditionInfo());
            if (m.find()) {
                publicationDate = m.group();
            } else {
                m = p.matcher(bookXml.getPublisherText().getValue());
                if (m.find()) {
                    publicationDate = m.group();
                }
            }
            if (publicationDate != null) {
                try {
                    book.setPublicationDate(new SimpleDateFormat("yyyy").parse(m.group()));
                } catch (ParseException ex) {
                }
            }
        }
    }
    
    private void processAuthors(BookItem book, BookData bookXml) {
        if (bookXml.getAuthors() != null) {
            for (Person person : bookXml.getAuthors().getPerson()) {
                if (person.getValue() != null) {
                    Pattern p = Pattern.compile("(.*), (.*)");
                    Matcher m = p.matcher(person.getValue());
                    if (m.find()) {
                        book.addAuthor(m.group(2) + " " + m.group(1));
                    }
                    else
                        book.addAuthor(person.getValue());
                }
            }
        }
    }
    
    @Override
    protected void setConfigurationSpecific(SubnodeConfiguration sObj)
            throws ConfigurationParserException {
        Map<String, String> valueList
                = ConfigurationParser.getSpecificModuleValues(sObj, K_LIST);
        accessKey = valueList.get(K_API_KEY);
    }
    
    private String processPublisherLookup(BookItem book, String publisherId) {
        URL query = null;
        ISBNdbPublishers isbndbXml = null;
        String path = "http://isbndb.com/api/publishers.xml?"
                + "&index1=publisher_id"
                + "&value1=" + publisherId
                + "&access_key=" + accessKey;
        try {
            query = new URL(path);
        } catch (MalformedURLException ex) {
            return null;
        }
        try {
            isbndbXml = (ISBNdbPublishers) unmarshallerPublishers.unmarshal(query);
        } catch (JAXBException ex) {
            return null;
        }
        if (isbndbXml.getPublisherList() != null)
            if (isbndbXml.getPublisherList().getPublisherData() != null)
                return isbndbXml.getPublisherList().getPublisherData().getName();
        return null;
    }
}
