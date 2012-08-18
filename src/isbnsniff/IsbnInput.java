/*
 */
package isbnsniff;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jousse_s
 */
public abstract class IsbnInput extends IsbnIO {
    List<IsbnNumber> isbnList = new ArrayList();
    InputStream iStream = null;
    protected abstract void parseStream() throws IOException, IsbnFormatException;
    public IsbnInput(String name)
    {
        super(name);
    }
        
    public List<IsbnNumber> getIsbnList()
    {
        return isbnList;
    }
/*
    static protected String getInputString()
    {
        String read = null;
        try {
            InputStream in = null; // should be something else ...
            InputStreamReader is = new InputStreamReader(in);
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(is);
            read = br.readLine();
            while (read != null) {
                //System.out.println(read);
                sb.append(read);
                read = br.readLine();
            }
            return sb.toString();
        } catch (IOException ex) {
            Logger.getLogger(IsbnInput.class.getName()).log(Level.SEVERE, null, ex);
        }
        return read;
    }
     * 
     */
}
