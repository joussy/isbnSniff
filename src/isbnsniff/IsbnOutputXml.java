/*
 */
package isbnsniff;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.ErrorListener;
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
final public class IsbnOutputXml extends IsbnOutput {

    final static private String mName = "xml"; // Output module name
    File oFilename;
    DocumentBuilder docBuilder = null;

    public IsbnOutputXml(File value) {
        super(mName);
        oFilename = value;
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(IsbnOutputXml.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void writeOutput() throws FileNotFoundException, IOException {
        org.w3c.dom.Document doc = docBuilder.newDocument();
        org.w3c.dom.Element rootElement = doc.createElement("isbnsniff");
        doc.appendChild(rootElement);
        String value;
        for (BookItem book : bookList) {
            org.w3c.dom.Element bookElement = doc.createElement("book");
            bookElement.setAttribute("isbn13", book.getIsbn().getIsbn13());
            bookElement.setAttribute("isbn10", book.getIsbn().getIsbn10());
            if ((value = getValue(BookItem.A_TITLE, book)) != null) {
                org.w3c.dom.Element titleElement = doc.createElement(BookItem.A_TITLE);
                titleElement.setTextContent(value);
                bookElement.appendChild(titleElement);
            }
            if ((value = getValue(BookItem.A_NBPAGES, book)) != null) {
                org.w3c.dom.Element nbPagesElement = doc.createElement(BookItem.A_NBPAGES);
                nbPagesElement.setTextContent(value);
                bookElement.appendChild(nbPagesElement);
            }
            rootElement.appendChild(bookElement);
            writeDomToFile(doc);
        }
    }

    private void writeDomToFile(org.w3c.dom.Document doc) throws FileNotFoundException, IOException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(IsbnOutputXml.class.getName()).log(Level.SEVERE, null, ex);
        }
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(oFilename);
        try {
            transformer.transform(source, result);
        } catch (TransformerException ex) {
            if (ex.getException().getClass() == FileNotFoundException.class)
                throw (FileNotFoundException) ex.getException();
            else if (ex.getException().getClass() == IOException.class)
                throw (IOException) ex.getException();
            else
               Logger.getLogger(IsbnOutputXml.class.getName()).log(Level.SEVERE, null, ex); 
        }
    }

    @Override
    public void setConfiguration(SubnodeConfiguration cNode) {
    }
}
