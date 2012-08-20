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
            org.w3c.dom.Element titleElement = doc.createElement(BookItem.A_TITLE);
                titleElement.setTextContent(book.getTitle());
            bookElement.appendChild(titleElement);
            
            org.w3c.dom.Element nbPagesElement = doc.createElement(BookItem.A_NBPAGES);
            if (book.getNbPages() != null) {
                nbPagesElement.setTextContent(book.getNbPages().toString());
            }
            bookElement.appendChild(nbPagesElement);
            
            org.w3c.dom.Element lccElem = doc.createElement(BookItem.A_LCC);
            if (book.getLcc() != null) {
                lccElem.setTextContent(book.getLcc());
            }
            bookElement.appendChild(lccElem);

            org.w3c.dom.Element publisherElem = doc.createElement(BookItem.A_PUBLISHER);
            if (book.getPublisher() != null) {
                publisherElem.setTextContent(book.getPublisher());
            }
            bookElement.appendChild(publisherElem);

            org.w3c.dom.Element synopsisElem = doc.createElement("description");
            if (book.getSynopsis() != null) {
                synopsisElem.setTextContent(book.getSynopsis());
            }
            bookElement.appendChild(synopsisElem);

            org.w3c.dom.Element publicationDateElem = doc.createElement(BookItem.A_PUBLICATION_DATE);
            if (book.getPublicationDate() != null) {
                publicationDateElem.setTextContent(book.getPublicationDate().toString());
            }
            bookElement.appendChild(publicationDateElem);
            
            org.w3c.dom.Element authorsElem = doc.createElement(BookItem.A_AUTHORS);
            if (book.getAuthorList() != null)
            {
                for (String author : book.getAuthorList()) {
                    org.w3c.dom.Element authorElem = doc.createElement("author");
                    authorElem.setTextContent(author);
                    authorsElem.appendChild(authorElem);
                }
            }
            bookElement.appendChild(authorsElem);

            org.w3c.dom.Element categoriesElem = doc.createElement(BookItem.A_CATEGORIES);
            if (book.getCategoryList() != null)
            {
                for (String category : book.getCategoryList()) {
                    org.w3c.dom.Element categoryElem = doc.createElement("category");
                    categoryElem.setTextContent(category);
                    categoriesElem.appendChild(categoryElem);
                }
            }
            bookElement.appendChild(categoriesElem);
            
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
            if (ex.getException().getClass() == FileNotFoundException.class) {
                throw (FileNotFoundException) ex.getException();
            } else if (ex.getException().getClass() == IOException.class) {
                throw (IOException) ex.getException();
            } else {
                Logger.getLogger(IsbnOutputXml.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void setConfiguration(SubnodeConfiguration cNode) {
    }
}
