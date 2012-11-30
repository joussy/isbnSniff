/*
 */
package isbnsniff;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.List;
import org.apache.commons.configuration.SubnodeConfiguration;

/**
 *
 * @author jousse_s
 */
final public class IsbnOutputCsv extends IsbnOutput {

    final static private String mName = "csv"; // Output module name
    private File filename;
    String csvSeparator;

    /**
     * Print the Engine results on the standard output
     */
    public IsbnOutputCsv(File value) {
        super(mName);
        filename = value;
        csvSeparator = ",";
    }
    
    /**
     * 
     */
    @Override
    public void writeOutput() throws IOException {
        int i = 0;
        String out = new String();
        out = "isbn13" + csvSeparator
                + "isbn10" + csvSeparator
                + BookItem.A_TITLE + csvSeparator
                + BookItem.A_AUTHORS + csvSeparator
                + BookItem.A_CATEGORIES + csvSeparator
                + BookItem.A_NBPAGES + csvSeparator
                + BookItem.A_PUBLICATION_DATE + csvSeparator
                + BookItem.A_SYNOPSIS + csvSeparator
                + System.getProperty("line.separator");
        for (BookItem book : bookList) {
            out += "\"" + book.getIsbn().getIsbn13() + "\"" + csvSeparator;
            out += "\"" + book.getIsbn().getIsbn10() + "\"" + csvSeparator;
            out += "\"" + book.getTitle() + "\"" + csvSeparator;
            out += "\"";
            if (book.getAuthorList() != null)
            {
                i = 0;
                for (String author : book.getAuthorList()) {
                    out += (i++ > 0 ? ", " : "") + author;
                }
            }
            out += "\"" + csvSeparator;
            out += "\"";
            if (book.getCategoryList() != null)
            {
                i = 0;
                for (String category : book.getCategoryList()) {
                    out += (i++ > 0 ? ", " : "") + category;
                }
            }
            out += "\"" + csvSeparator;
            out += "\"" + book.getNbPages() + "\"" + csvSeparator;
            out += "\"" + DateFormat.getDateInstance(DateFormat.MEDIUM).
                format(book.getPublicationDate())+ "\"" + csvSeparator;
            out += "\"" + book.getSynopsis() + "\"" + csvSeparator;
            out += System.getProperty("line.separator");
        }
        BufferedWriter output = new BufferedWriter(new FileWriter(filename));
        output.write(out);
        output.close();
    }
    
    /**
     * 
     * @param cNode
     */
    @Override
    public void setConfiguration(SubnodeConfiguration cNode) {
    }
}
