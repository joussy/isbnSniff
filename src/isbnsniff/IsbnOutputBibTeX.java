/*
 */
package isbnsniff;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.lang.StringEscapeUtils;

/**
 *
 * @author jousse_s
 */
public class IsbnOutputBibTeX extends IsbnOutput {
    private File filename;
    final static private String mName = "bibtex"; // Output module name

    /**
     * Generate a .bib file from a BookItem List
     * @param value
     */
    public IsbnOutputBibTeX(File value) {
        super(mName);
       filename = value;
    }
    
    private Map<String, String> getEntry(BookItem book) {
        Format formatter = new SimpleDateFormat("yyyy");
        Map<String, String> entry = new HashMap<String, String>();
        entry.put("isbn", book.getIsbn().getIsbn13());
        entry.put("title", book.getTitle());
        if (book.getPublicationDate() != null) {
            entry.put("year", formatter.format(book.getPublicationDate()));
        }
        entry.put("publisher", book.getPublisher());
        if (book.getAuthorList() != null) {
            String authors = new String();
            for (int j = 0; j < book.getAuthorList().size(); j++) {
                if (j > 0) {
                    authors += " and ";
                }
                authors += book.getAuthorList().get(j);
            }
            entry.put("author", authors);
        }
        return entry;
    }
    
    /**
     * Write the BookItem list in the BibTeX format
     * @throws FileNotFoundException
     * @throws IOException
     */
    @Override
    public void writeOutput() throws FileNotFoundException, IOException {
        String endl = System.getProperty("line.separator");
        String out = new String();
        for (int i = 0; i < bookList.size(); i++) {
            out += "@Book{isbnsniff-" + i + "," + endl;
            for (Entry<String, String> entry : getEntry(bookList.get(i)).entrySet()) {
                out += " " + entry.getKey() + " = \""
                        + StringEscapeUtils.escapeJavaScript(entry.getValue())
                        + "\"," + endl;
            }
            out += "}" + endl + endl;
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
