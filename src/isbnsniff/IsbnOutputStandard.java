/*
 */
package isbnsniff;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.configuration.SubnodeConfiguration;

/**
 *
 * @author jousse_s
 */
final public class IsbnOutputStandard extends IsbnOutput {

    final static private String mName = "standard"; // Output module name

    public IsbnOutputStandard() {
        super(mName);
    }
    
    @Override
    public void writeOutput() {
        System.out.println("/--IsbnOutputStandard");
        String value;
        String out = new String();
        for (BookItem book : bookList) {
            out = "Isbn13: " + book.getIsbn().getIsbn13() + System.getProperty("line.separator");
            if ((value = getValue(BookItem.A_TITLE, book)) != null) {
                out += BookItem.A_TITLE + ": " + value + System.getProperty("line.separator");
            }
            if ((value = getValue(BookItem.A_NBPAGES, book)) != null) {
                out += BookItem.A_NBPAGES + ": " + value + System.getProperty("line.separator");
            }
            System.out.print(out);
        }
    }
    @Override
    public void setConfiguration(SubnodeConfiguration cNode) {
    }
}
