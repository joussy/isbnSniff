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
    List<String> outputValueList = new ArrayList<String>();

    public abstract void writeOutput() throws FileNotFoundException, IOException;

    public IsbnOutput(String mName) {
        super(mName);
    }

    public void setBookList(List<BookItem> value) {
        bookList = value;
    }

    public void setOutputValueList(List<String> value) {
        outputValueList = value;
    }

    protected String getValue(String key, BookItem item) {
        if (outputValueList.contains(key)) {
            if (item.getValue(key) != null) {
                return item.getValue(key).toString();
            }
            else
                return "";
        }
        return null;
    }
}
