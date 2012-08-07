/*
 */

package EngineIsbnDb;


import EngineIsbnDb.ISBNdb.BookList;
import EngineIsbnDb.ISBNdb.BookList.BookData;
import isbnsniff.BookItem;
import isbnsniff.IsbnModule;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;


/**
 *
 * @author jousse_s
 */
/*
 *             "http://www.librarything.com/services/rest/1.1/?method=librarything.ck.getwork&isbn="
                + book.getIsbn().getIsbn13() + "&apikey=" + accessKey);

 */
public class ModuleIsbndb extends IsbnModule {
    final static String MODULE_NAME = "IsbnDb";
    private String accessKey;
    private Unmarshaller unmarshaller = null;
    public ModuleIsbndb(String key)
    {
        moduleName = MODULE_NAME;
        accessKey = key;
        try {
            JAXBContext jc = JAXBContext.newInstance(ObjectFactory.class, ISBNdb.class);
            unmarshaller = jc.createUnmarshaller();
        } catch (JAXBException ex) {
            Logger.getLogger(ISBNdb.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void setaccessKey(String value)
    {
        accessKey = value;
    }
    @Override
    protected void processQueryIsbn(BookItem book) {
        URL query = null;
        ISBNdb isbndbXml = null;
        String path = "http://isbndb.com/api/books.xml?&results=details&results=authors&results=texts&index1=isbn&value1="
                + book.getIsbn().getIsbn13() + "&access_key=" + accessKey;
        try {
            query = new URL(path);
        } catch (MalformedURLException ex) {
            Logger.getLogger(ModuleIsbndb.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            isbndbXml = (ISBNdb)unmarshaller.unmarshal(query);
        } catch (JAXBException ex) {
            Logger.getLogger(ModuleIsbndb.class.getName()).log(Level.SEVERE, null, ex);
        }
        processIsbndbTree(isbndbXml, book);
    }
    @Override
    protected void processQueryInitialize() {
    }

    @Override
    protected void processQueryTerminate() {
    }

    private void processIsbndbTree(ISBNdb isbndbXml, BookItem book) {
        if (isbndbXml != null)
            if (isbndbXml.getBookList() != null)
                if (isbndbXml.getBookList().getBookData() != null)
                {
                    BookData bookXml = isbndbXml.getBookList().getBookData();
                    book.setTitle(bookXml.getTitle());
                    processDetailsPhysicalDescription(book,
                            bookXml.getDetails().getPhysicalDescriptionText());
                }
    }
    private void processDetailsPhysicalDescription(BookItem book, String attributeValue)
    {
        Pattern p = Pattern.compile("([0-9]+) (p\\.|pages)");
        Matcher m = p.matcher(attributeValue);
        if (m.find()) {
            book.setNbPages(Integer.parseInt(m.group(1)));
        }
    }
}
