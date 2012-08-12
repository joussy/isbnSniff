/*
 */
package isbnsniff;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.lang.model.element.Element;
import javax.swing.text.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 *
 * @author jousse_s
 */
public class IsbnOutputXml extends IsbnOutput {
    File oFilename;
    DocumentBuilder docBuilder = null;

    public IsbnOutputXml(File value) {
        super();
        oFilename = value;
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(IsbnOutputXml.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void writeOutput() {
        org.w3c.dom.Document doc = docBuilder.newDocument();
        org.w3c.dom.Element rootElement = doc.createElement("isbnsniff");
        doc.appendChild(rootElement);
        for (BookItem book : bookList) {
            org.w3c.dom.Element bookElement = doc.createElement("book");
            bookElement.setAttribute("isbn13", book.getIsbn().getIsbn13());
            bookElement.setAttribute("isbn10", book.getIsbn().getIsbn10());
            if (book.getTitle() != null) {
                org.w3c.dom.Element titleElement = doc.createElement("title");
                titleElement.setTextContent(book.getTitle());
                bookElement.appendChild(titleElement);
            }
            if (book.getNbPages() != null) {
                org.w3c.dom.Element nbPagesElement = doc.createElement("nbpages");
                nbPagesElement.setTextContent(book.getNbPages().toString());
                bookElement.appendChild(nbPagesElement);
            }
            rootElement.appendChild(bookElement);
            writeDomToFile(doc);
        }
    }
    
    private void writeDomToFile(org.w3c.dom.Document doc)
    {
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
            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);
        } catch (TransformerException ex) {
            Logger.getLogger(IsbnOutputXml.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
}
