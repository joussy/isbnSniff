/*
 */
package isbnsniff;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

/**
 *
 * @author jousse_s
 */
public class CliInterpreter {

    public final static int INPUT_NO = 0;
    public final static int INPUT_CSV = 1;
    public final static int INPUT_XML = 2;
    public final static int INPUT_CLI = 3;
    public final static int OUTPUT_CSV = 4;
    public final static int OUTPUT_XML = 5;
    public final static int OUTPUT_CLI = 6;
    public final static int OUTPUT_BIBTEX = 7;
    
    private final static String OPT_OUTPUT_MODE = "o";
    private final static String OPT_OUTPUT_PATH = "oF";
    private final static String OPT_INPUT_MODE = "i";
    private final static String OPT_INPUT_PATH = "iF";
    private final static String OPT_CONF_PATH = "c";
    private final static String OPT_LIST_MODULES = "m";
    private final static String OPT_ISBN_SET = "l";
    private final static String OPT_HELP = "h";
    
    private final static String ERR_UNDEFINED_OPT = "Missing option: ";
    private final static String ERR_UNRECOGNIZED_OPT = "Unrecognized value for option: ";
    
    private final static String USAGE_NAME = "isbnsniff";
    private File outputFile = null;
    private File inputFile = null;
    private File configurationFile = null;
    private int inputMode = INPUT_NO;
    private int outputMode = 0;
    private CommandLine cmd = null;
    private Options options = null;
    private String isbnListString = null;
    //@todo Remove XML input and CSV output
    public CliInterpreter(String[] args, List<IsbnModule> moduleList) throws ParseException {
        try {
            initializeParser(args);
        } catch (Exception ex) {
            throw new ParseException(ex.getMessage());
        }
        if (cmd.hasOption(OPT_HELP))
        {
            printHelp();
        }
        else if (cmd.hasOption(OPT_LIST_MODULES))
        {
            inputMode = INPUT_NO;
            String toPrint = "Search engines supported: ";
            for (IsbnModule module : moduleList)
            {
                toPrint += module.getModuleName() + " ";
            }
            System.out.println(toPrint);
        }
        else
        {
            try {
                parseInputParams();
                parseOutputParams();
                getConfiguration();
            } catch (ParseException ex) {
                throw new ParseException(ex.getMessage());
            }
        }
    }
    
    private void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        String cliUsage = USAGE_NAME + " "
                + "[[-" + OPT_INPUT_MODE + " csv|xml -" + OPT_INPUT_PATH + " path] | -" + OPT_ISBN_SET + " isbnList] "
                + "[-" + OPT_OUTPUT_MODE + " csv|xml -" + OPT_OUTPUT_PATH + " path] "
                + " -" + OPT_CONF_PATH + " path" + System.getProperty("line.separator")
                + USAGE_NAME + " -" + OPT_LIST_MODULES + System.getProperty("line.separator")
                + USAGE_NAME + " -" + OPT_HELP;
        formatter.printHelp(cliUsage, options);
    }
    
    private void initializeParser(String[] args) throws Exception {
        options = new Options();
        options.addOption(OPT_CONF_PATH, true, "Set the configuration file");
        options.addOption(OPT_INPUT_MODE, true, "Get isbn from an external source. Possible values are: xml, csv");
        options.addOption(OPT_INPUT_PATH, true, "Specify the input filename");
        options.addOption(OPT_OUTPUT_MODE, true, "Extract Lookup results to an external file. Possible values are: xml, csv, bibtex");
        options.addOption(OPT_OUTPUT_PATH, true, "Specify the output filename");
        options.addOption(OPT_ISBN_SET, true, "Specify a list of ISBNs 10 or 13 separated by commas.");
        options.addOption(OPT_LIST_MODULES, false, "List the Search engines supported.");
        options.addOption(OPT_HELP, false, "Print this help message.");
        CommandLineParser parser = new PosixParser();
        try {
        cmd = parser.parse(options, args);
        } catch (MissingArgumentException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    private void parseInputParams() throws ParseException {
        if (cmd.hasOption(OPT_INPUT_MODE)) {
            if (cmd.getOptionValue(OPT_INPUT_MODE).equals("csv")) {
                inputMode = INPUT_CSV;
            } else if (cmd.getOptionValue(OPT_INPUT_MODE).equals("xml")) {
                inputMode = INPUT_XML;
            }
            else
                throw new ParseException(ERR_UNRECOGNIZED_OPT + cmd.getOptionValue(OPT_INPUT_MODE));
        }
        else {
            inputMode = INPUT_CLI;
        }
        if (inputMode == INPUT_CSV || inputMode == INPUT_XML) {
            if (!cmd.hasOption(OPT_INPUT_PATH))
                throw new ParseException(ERR_UNDEFINED_OPT + OPT_INPUT_PATH);
            else
                inputFile = new File(cmd.getOptionValue(OPT_INPUT_PATH));
        } else if (inputMode == INPUT_CLI) {
            if (!cmd.hasOption(OPT_ISBN_SET))
                throw new ParseException(ERR_UNDEFINED_OPT + OPT_ISBN_SET);
            else
                isbnListString = cmd.getOptionValue(OPT_ISBN_SET);
        }
    }

    private void parseOutputParams() throws ParseException {
        if (cmd.hasOption(OPT_OUTPUT_MODE)) {
            if (cmd.getOptionValue(OPT_OUTPUT_MODE).equals("csv")) {
                outputMode = OUTPUT_CSV;
            } else if (cmd.getOptionValue(OPT_OUTPUT_MODE).equals("xml")) {
                outputMode = OUTPUT_XML;
            } else if (cmd.getOptionValue(OPT_OUTPUT_MODE).equals("bibtex")) {
                outputMode = OUTPUT_BIBTEX;
            }
            else
                throw new ParseException(ERR_UNRECOGNIZED_OPT + cmd.getOptionValue(OPT_OUTPUT_MODE));
        }
        else {
             outputMode = OUTPUT_CLI;
        }
        if (outputMode == OUTPUT_CSV || outputMode == OUTPUT_XML
                || outputMode == OUTPUT_BIBTEX) {
            if (!cmd.hasOption(OPT_OUTPUT_PATH))
                throw new ParseException(ERR_UNDEFINED_OPT + OPT_OUTPUT_PATH);
            else
                outputFile = new File(cmd.getOptionValue(OPT_OUTPUT_PATH));
        }
    }
    
    private void getConfiguration() throws ParseException {
        if (!cmd.hasOption(OPT_CONF_PATH))
            throw new ParseException(ERR_UNDEFINED_OPT + OPT_CONF_PATH);
        else
            configurationFile = new File(cmd.getOptionValue(OPT_CONF_PATH));
    }

    public IsbnInput generateIsbnInput() {
        try {
            if (getInputMode() == INPUT_CSV) {
                return new IsbnInputCsv(getInputFile());
            } else if (getInputMode() == INPUT_CLI) {
                return new IsbnInputCsv(getIsbnListString());
            } else if (getInputMode() == INPUT_XML) {
                return null;
            } else {
                return null;
            }
        }
        catch (FileNotFoundException ex) {
            System.err.println("Error: Unable to open file " + ex.getMessage());
            return null;
        }
    }

    public IsbnOutput generateIsbnOutput() {
        if (getOutputMode() == OUTPUT_CLI) {
            return new IsbnOutputStandard();
        } else if (getOutputMode() == OUTPUT_XML) {
            return new IsbnOutputXml(getOutputFile());
        } else if (getOutputMode() == OUTPUT_BIBTEX) {
            return new IsbnOutputBibTeX(getOutputFile());
        } else if (getOutputMode() == OUTPUT_CSV) {
            return null;
        } else {
            return null;
        }
    }

    public String getIsbnListString() {
        return isbnListString;
    }

    public File getInputFile() {
        return inputFile;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public File getConfigurationFile() {
        return configurationFile;
    }
    
    public int getInputMode() {
        return inputMode;
    }
    
    public int getOutputMode() {
        return outputMode;
    }
}
