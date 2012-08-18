/*
 */
package isbnsniff;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.csv.*;
import org.apache.commons.csv.CSVParser;

/**
 *
 * @author jousse_s
 */
final public class IsbnInputCsv extends IsbnInput {
    final static private String mName = "csv";
    Reader reader = null;
    public IsbnInputCsv(File file) throws FileNotFoundException
    {
        super(mName);
        reader = new FileReader(file);
    }
    
    public IsbnInputCsv(String value)
    {
        super(mName);
        reader = new StringReader(value);
    }

    @Override
    public void parseStream() throws IOException, IsbnFormatException
    {
        //CSVParser parser = new CSVParser(reader, CSVStrategy.EXCEL_STRATEGY);
        CSVParser parser = new CSVParser(reader, new CSVStrategy(',','"','#', true, true, true));
        String[][] data = null;
        data = parser.getAllValues();
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

    @Override
    public void setConfiguration(SubnodeConfiguration cNode) {
    }
}
