/*
 */
package isbnsniff;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jousse_s
 */
public abstract class IsbnOutput extends IsbnIO {

    List<BookItem> bookList = new ArrayList<BookItem>();

    /**
     * Write the BookItem list in a specified format
     * @throws FileNotFoundException
     * @throws IOException
     */
    public abstract void writeOutput() throws FileNotFoundException, IOException;

    /**
     * Generate a new Output
     * @param mName The name of the output Module
     */
    public IsbnOutput(String mName) {
        super(mName);
    }

    /**
     * Specify the BookList to write
     * @param value
     */
    public void setBookList(List<BookItem> value) {
        bookList = value;
    }
}
