/*
 */
package isbnsniff;

import java.text.DateFormat;
import java.util.List;
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
        int i = 0;
        String out = new String();
        for (BookItem book : bookList) {
            out = "Isbn13: " + book.getIsbn().getIsbn13() + System.getProperty("line.separator");
            if (book.getTitle() != null) {
                out += BookItem.A_TITLE + ": " + book.getTitle() + System.getProperty("line.separator");
            }
            if (book.getNbPages() != null) {
                out += BookItem.A_NBPAGES + ": " + book.getNbPages() + System.getProperty("line.separator");
            }
            if (book.getLcc() != null) {
                out += BookItem.A_LCC + ": " + book.getLcc() + System.getProperty("line.separator");
            }
            if (book.getAuthorList() != null) {
                out += BookItem.A_AUTHORS + ": ";
                i = 0;
                for (String author : book.getAuthorList()) {
                    out += (i++ > 0 ? ", " : "") + author;
                }
                out += System.getProperty("line.separator");
            }
            if (book.getCategoryList() != null) {
                out += BookItem.A_CATEGORIES + ": ";
                i = 0;
                for (String author : book.getCategoryList()) {
                    out += (i++ > 0 ? ", " : "") + author + ", ";
                }
                out += System.getProperty("line.separator");                
            }
            if (book.getPublicationDate() != null)
            {
                out += BookItem.A_PUBLICATION_DATE + ": " +
                         DateFormat.getDateInstance(DateFormat.MEDIUM).
                        format(book.getPublicationDate());
                out += System.getProperty("line.separator");
            }
            if (book.getPublisher() != null)
            {
                out += BookItem.A_PUBLISHER + ": " + book.getPublisher() + System.getProperty("line.separator");
            }
            System.out.print(out);
            System.out.println("---------------------------");
        }
    }
    
    @Override
    public void setConfiguration(SubnodeConfiguration cNode) {
    }
}
