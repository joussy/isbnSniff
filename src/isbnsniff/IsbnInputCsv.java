/*
 */
package isbnsniff;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.axis.utils.IOUtils;
import org.apache.commons.csv.*;
import org.apache.commons.csv.CSVParser;

/**
 *
 * @author jousse_s
 */
public class IsbnInputCsv extends IsbnInput {
    public IsbnInputCsv(InputStream s)
    {
        super(s);
    }
    @Override
    public void parseStream()
    {
        StringReader reader = new StringReader(getInputString());
        //CSVParser parser = new CSVParser(reader, CSVStrategy.EXCEL_STRATEGY);
        CSVParser parser = new CSVParser(reader, new CSVStrategy(',','"','#', true, true, true));
        String[][] data = null;
        try {
            data = parser.getAllValues();
        } catch (IOException ex) {
            Logger.getLogger(IsbnInputCsv.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (String[] line : data) {
            for (int i = 0; i < line.length; i++) {
                if (line[i].length() > 0)
                {
                    isbnList.add(new IsbnNumber(line[i]));
                    //System.out.println("value " + i + "=" + line[i]);
                }
            }
        }
    }
}
