/*
 */
package isbnsniff;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jousse_s
 */
public abstract class IsbnOutput {
    List<BookItem> bookList = new ArrayList();
    public abstract void writeOutput();
    public IsbnOutput()
    {
    }
    public void setBookList(List<BookItem> value)
    {
        bookList = value;
    }
}
