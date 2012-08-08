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
import java.util.ArrayList;
import java.util.List;

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
    public static final String KEY_ISBNDB = "M3J2QAT3";
    public static final String KEY_GOOGLE_BOOKS = "AIzaSyDqSWIheWKftCfXxWvf_hyyFDsAzuvYGm0";
    public static final String KEY_AMAZON_ASSOCIATED = "joussybuffout-20";
    public static final String KEY_AMAZON_SECRET = "VZJOAxxbGBXIiRanJeYJUySwifBqZdGTfxrdFyu1";
    public static final String KEY_AMAZON_AWS = "AKIAILAAYXVGXRWSBF7A";
    public static final String KEY_LIBRARYTHING = "69dccf2d7d299190bee974902fe2b259";
    public static final String KEY_BOOKSHARE = "xaqugvnv2xepcbcqh5449wxk";
    public static void main(String[] args) {

        
        List<IsbnModule> moduleList = new ArrayList();
        
        ModuleIsbndb modI = new ModuleIsbndb();
        moduleList.add(modI);
        
        ModuleGoogleBooks modG = new ModuleGoogleBooks();
        moduleList.add(modG);
        
        ModuleAmazon modA = new ModuleAmazon();
        moduleList.add(modA);
        
        ModuleLibraryThing modL = new ModuleLibraryThing();
        moduleList.add(modL);
        
        ModuleBookshare modB = new ModuleBookshare();
        moduleList.add(modB);

        ModuleOpenLibrary modO = new ModuleOpenLibrary();
        moduleList.add(modO);

        ConfigurationParser configurationParser =
                new ConfigurationParser(new File("src/isbnsniff/conf.ini"));
        configurationParser.parseConfiguration(moduleList);

        for (IsbnModule module : moduleList)
        {
//            module.addBookItem(new BookItem(new IsbnNumber("9781934356005")));//Erlang
//            module.addBookItem(new BookItem(new IsbnNumber("9780061031328")));//Terry pratchet
            module.addBookItem(new BookItem(new IsbnNumber("9780590353403")));//Harry potter
//            module.addBookItem(new BookItem(new IsbnNumber("9781459235908"))); //Her Better Half
//            module.addBookItem(new BookItem(new IsbnNumber("9780061031308"))); // Does not exist
//            module.addBookItem(new BookItem(new IsbnNumber("0525951849"))); 
//            module.addBookItem(new BookItem(new IsbnNumber("0233964444"))); 
//            module.addBookItem(new BookItem(new IsbnNumber("0028638360")));
            
            module.processQuery();
            System.out.println("/--" + module.getModuleName());
            System.out.println("enable=" + module.isEnabled());
            for (BookItem book : module.getBookItemList())
            {
                System.out.println("Title=" + book.getTitle()
                        + ", NbPages=" + book.getNbPages()
                        + ", Isbn=" + book.getIsbn().getIsbn13());
            }
            System.out.println("--/");
        }
    }
}
