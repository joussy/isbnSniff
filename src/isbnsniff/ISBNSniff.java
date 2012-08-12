/*
 */
package isbnsniff;
import EngineOpenLibrary.ModuleOpenLibrary;
import EngineLibraryThing.ModuleLibraryThing;
import EngineGoogleBooks.ModuleGoogleBooks;
import EngineBookShare.ModuleBookshare;
import EngineAmazon.ModuleAmazon;
import EngineIsbnDb.ModuleIsbndb;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jousse_s
 */

/*
    public static final String KEY = "e7aaceb52f476adcb08072a98e2a02ad";
    public static final String ISBN = "0061031321";
    public void connectionXML() {
        HttpUnitOptions.setScriptingEnabled(false);
        WebConversation wc = new WebConversation();
        WebRequest req = new GetMethodWebRequest(
            "http://www.librarything.com/services/rest/1.1/?method=librarything.ck.getwork&isbn="
            + ISBN + "&apikey=" + KEY);
        WebResponse resp;
        try {
            resp = wc.getResponse(req);
            System.out.println(resp.getText());
            //resp.getDOM();
        } catch (IOException ex) {
            Logger.getLogger(ISBNSniff.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(ISBNSniff.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
 */

public class ISBNSniff {
    public static void main(String[] args) {
        ConfigurationParser configurationParser =
                new ConfigurationParser(new File("src/isbnsniff/conf.ini"));
        IsbnInput in = null;
        try {
            in = new IsbnInputCsv(new FileInputStream(new File("src/isbnsniff/example.csv")));
            in.parseStream();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ISBNSniff.class.getName()).log(Level.SEVERE, null, ex);
        }
        SearchEngine sEngine = new SearchEngine(configurationParser);

        sEngine.addIsbnModule(new ModuleIsbndb());
        sEngine.addIsbnModule(new ModuleGoogleBooks());
        sEngine.addIsbnModule(new ModuleAmazon());
        sEngine.addIsbnModule(new ModuleLibraryThing());
        sEngine.addIsbnModule(new ModuleBookshare());
        sEngine.addIsbnModule(new ModuleOpenLibrary());

        sEngine.setIsbnList(in.getIsbnList());
//        sEngine.addIsbn(new IsbnNumber("9781934356005"));//Erlang
//        sEngine.addIsbn(new IsbnNumber("9780061031328"));//Terry pratchet
//        sEngine.addIsbn(new IsbnNumber("9780590353403"));//Harry potter
//        sEngine.addIsbn(new IsbnNumber("9781459235908")); //Her Better Half
//        sEngine.addIsbn(new IsbnNumber("9780373881093")); //Her Better Half
        
        sEngine.processConfiguration();
        sEngine.printValuesPriority();
        sEngine.performSearch();
        sEngine.mergeResults();
        sEngine.printModuleResults();
        sEngine.printResults();
        
    }
}
