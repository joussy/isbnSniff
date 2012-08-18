/*
 */
package EngineIsbnDb;

import EngineIsbnDb.ISBNdb.BookList.BookData;
import isbnsniff.BookItem;
import isbnsniff.IsbnModule;
import isbnsniff.IsbnModuleException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private String accessKey;
    private Unmarshaller unmarshaller = null;

    public ModuleIsbndb() throws IsbnModuleException {
        moduleName = MODULE_NAME;
        try {
            JAXBContext jc = JAXBContext.newInstance(ObjectFactory.class, ISBNdb.class);
            unmarshaller = jc.createUnmarshaller();
        } catch (JAXBException ex) {
            throw new IsbnModuleException(IsbnModuleException.ERR_JAXB, ex.getMessage());
        }
    }

    @Override
    protected void processQueryIsbn(BookItem book) throws IsbnModuleException {
        URL query = null;
        ISBNdb isbndbXml = null;
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
            throw new IsbnModuleException(IsbnModuleException.ERR_URL, ex.getMessage());
        }
        try {
            isbndbXml = (ISBNdb) unmarshaller.unmarshal(query);
        } catch (JAXBException ex) {
            throw new IsbnModuleException(IsbnModuleException.ERR_JAXB, ex.getMessage());
        }
        processIsbndbTree(isbndbXml, book);
    }

    @Override
    protected void processQueryInitialize() {
    }

    @Override
    protected void processQueryTerminate() {
    }

    private void processIsbndbTree(ISBNdb isbndbXml, BookItem book) throws IsbnModuleException {
        if (isbndbXml == null) {
            throw new IsbnModuleException(IsbnModuleException.ERR_UNEXPECTED_FORMAT, "No BookList");
        }
        if (isbndbXml.getBookList() == null)
            throw new IsbnModuleException(IsbnModuleException.ERR_UNEXPECTED_FORMAT, isbndbXml.getErrorMessage());
        if (isbndbXml.getBookList().getBookData() != null) {
            BookData bookXml = isbndbXml.getBookList().getBookData();
            book.setTitle(bookXml.getTitle());
            processDetailsPhysicalDescription(book,
                    bookXml.getDetails().getPhysicalDescriptionText());
        }
    }

    private void processDetailsPhysicalDescription(BookItem book, String attributeValue) {
        Pattern p = Pattern.compile("([0-9]+) (p\\.|pages)");
        Matcher m = p.matcher(attributeValue);
        if (m.find()) {
            book.setNbPages(Integer.parseInt(m.group(1)));
        }
    }

    @Override
    protected void setConfigurationSpecific(SubnodeConfiguration sObj) {
        accessKey = sObj.getString("api_key", "undefined");
    }
}
