/*
 */
package isbnsniff;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jousse_s
 */
public abstract class IsbnInput extends IsbnIO {
    List<IsbnNumber> isbnList = new ArrayList<IsbnNumber>();
    InputStream iStream = null;
    /**
     * Generate an ISBN list from Input stream
     * @throws IOException
     * @throws IsbnFormatException
     */
    protected abstract void parseStream() throws IOException, IsbnFormatException;
    /**
     * 
     * @param name
     */
    public IsbnInput(String name)
    {
        super(name);
    }
        
    /**
     * Get the ISBN list from parsed input.
     * @return
     */
    public List<IsbnNumber> getIsbnList()
    {
        return isbnList;
    }
}
