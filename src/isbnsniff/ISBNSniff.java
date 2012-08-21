package isbnsniff;

import EngineOpenLibrary.ModuleOpenLibrary;
import EngineLibraryThing.ModuleLibraryThing;
import EngineGoogleBooks.ModuleGoogleBooks;
import EngineBookShare.ModuleBookshare;
import EngineAmazon.ModuleAmazon;
import EngineIsbnDb.ModuleIsbndb;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author jousse_s
 */
public class ISBNSniff {

    public static void main(String[] args) {
        
        List<IsbnModule> moduleList = new ArrayList<IsbnModule>();
        try {
            moduleList.add(new ModuleIsbndb());
            moduleList.add(new ModuleGoogleBooks());
            moduleList.add(new ModuleAmazon());
            moduleList.add(new ModuleLibraryThing());
            moduleList.add(new ModuleBookshare());
            moduleList.add(new ModuleOpenLibrary());
        } catch (IsbnModuleException ex) {
            System.err.println("Fatal Error: " + ex.getModuleName() + " Engine initialization has failed: " + ex.getMessage());
            System.err.println("Please de-activate this module.");
            System.out.println("Aborting ...");
            return;
        }
        CliInterpreter cli;
        try {
            cli = new CliInterpreter(args, moduleList);
        } catch (ParseException ex) {
            System.out.println("Command Line Error, " + ex.getMessage());
            System.out.println("Aborting ...");
            return;
        }
        IsbnInput input = cli.generateIsbnInput();
        IsbnOutput output = cli.generateIsbnOutput();
        if (input == null || output == null) {
            return;
        }
        try {
            input.parseStream();
        } catch (IOException ex) {
            System.err.println("Output Error: " + ex.getMessage());
        } catch (IsbnFormatException ex) {
            System.out.println(input.getIOName() + ": " + ex.getMessage()
                    + "(Isbn:" + ex.getIsbn() + ")");
            System.out.println("Aborting ...");
            return;
        }
        ConfigurationParser cfgParser = null;
        try {
            cfgParser = new ConfigurationParser(
                    cli.getConfigurationFile(), moduleList, output);
        } catch (ConfigurationParserExceptionList ex) {
            for (ConfigurationParserException i : ex.getErrorList())
            {
                System.out.println("Configuration File Error: "
                        + i.getMessage());
            }
            System.out.println("Aborting ...");
            return;
        }
        SearchEngine sEngine = cfgParser.generateSearchEngine();
        for (IsbnNumber nb : input.getIsbnList()) {
            //System.out.println("ISBN=" + nb.getIsbn13());
        }
        sEngine.setIsbnList(input.getIsbnList());
        sEngine.performSearch();
        sEngine.debugPrintModuleResults();
        sEngine.mergeResults();
        output.setBookList(sEngine.getResults());
        try {
            output.writeOutput();
        } catch (FileNotFoundException ex) {
            System.err.println("Output Error: " + ex.getMessage());
        } catch (IOException ex) {
            System.err.println("Output Error: " + ex.getMessage());
        }

    }
}
