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
 * Command Interpreter using Apache Commons CLI
 * @author jousse_s
 */
public class CliInterpreter {

    /**
     * No Input Mode
     */
    public final static int INPUT_NO = 0;
    /**
     * CSV input mode
     */
    public final static int INPUT_CSV = 1;
    /**
     * Command line input mode
     */
    public final static int INPUT_CLI = 2;
    /**
     * XML output mode
     */
    public final static int OUTPUT_XML = 3;
    /**
     * Standard output mode
     */
    public final static int OUTPUT_CLI = 4;
    /**
     * BibTeX output mode
     */
    public final static int OUTPUT_BIBTEX = 5;
    /**
     * CSV output mode
     */
    public final static int OUTPUT_CSV = 6;
    
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
    /**
     * Initialize Command Line interpreter
     * @param args The argv String Array from Java Main class
     * @param moduleList The list of module loaded in the core
     * @throws ParseException
     */
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
            toPrint = "Values supported: ";
            for (String key : BookItem.KEY_LIST) {
                toPrint += key + " ";
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
        String header = "       " + USAGE_NAME + " -" + OPT_LIST_MODULES + System.getProperty("line.separator");
        String cliUsage = USAGE_NAME + " "
                + " -" + OPT_CONF_PATH + " <path> "
                + "[[-" + OPT_INPUT_MODE + " <arg> -" + OPT_INPUT_PATH + " <arg>] | -" + OPT_ISBN_SET + " <arg>] "
                + "[-" + OPT_OUTPUT_MODE + " <arg> -" + OPT_OUTPUT_PATH + " <arg>] "
                + System.getProperty("line.separator");
        formatter.printHelp(100, cliUsage, header, options, "");
    }
    
    private void initializeParser(String[] args) throws Exception {
        options = new Options();
        options.addOption(OPT_CONF_PATH, true, "Set the configuration file");
        options.addOption(OPT_INPUT_MODE, true, "Get ISBN from an external source. Possible values are: csv");
        options.addOption(OPT_INPUT_PATH, true, "Specify the input filename");
        options.addOption(OPT_OUTPUT_MODE, true, "Extract Lookup results to an external file. Possible values are: xml, bibtex, csv");
        options.addOption(OPT_OUTPUT_PATH, true, "Specify the output filename");
        options.addOption(OPT_ISBN_SET, true, "Specify a list of ISBNs 10 or 13 separated by commas.");
        options.addOption(OPT_LIST_MODULES, false, "List the Search engines and output values. These values can be specified in the configuration file");
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
            } else
                throw new ParseException(ERR_UNRECOGNIZED_OPT + cmd.getOptionValue(OPT_INPUT_MODE));
        }
        else if (cmd.hasOption(OPT_ISBN_SET)){
            inputMode = INPUT_CLI;
        }
        else
            throw new ParseException("No input mode specified");
        if (inputMode == INPUT_CSV) {
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
            if (cmd.getOptionValue(OPT_OUTPUT_MODE).equals("xml")) {
                outputMode = OUTPUT_XML;
            } else if (cmd.getOptionValue(OPT_OUTPUT_MODE).equals("bibtex")) {
                outputMode = OUTPUT_BIBTEX;
            } else if (cmd.getOptionValue(OPT_OUTPUT_MODE).equals("csv")) {
                outputMode = OUTPUT_CSV;
            }
            else
                throw new ParseException(ERR_UNRECOGNIZED_OPT + cmd.getOptionValue(OPT_OUTPUT_MODE));
        }
        else {
             outputMode = OUTPUT_CLI;
        }
        if (outputMode == OUTPUT_XML || outputMode == OUTPUT_CSV || outputMode == OUTPUT_BIBTEX) {
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

    /**
     * Instantiate and configure a new Isbn Input depending on configuration file values
     * @return The IsbnInput instantiated and configurated, or null if no input has been specified
     */
    public IsbnInput generateIsbnInput() {
        try {
            if (getInputMode() == INPUT_CSV) {
                return new IsbnInputCsv(getInputFile());
            } else if (getInputMode() == INPUT_CLI) {
                return new IsbnInputCsv(getIsbnListString());
            } else {
                return null;
            }
        }
        catch (FileNotFoundException ex) {
            System.err.println("Error: Unable to open file " + ex.getMessage());
            return null;
        }
    }

    /**
     * Instantiate and configure a new Isbn Output depending on configuration file values
     * @return The IsbnOutput instantiated and configurated, or null if no input has been specified
     */
    public IsbnOutput generateIsbnOutput() {
        if (getOutputMode() == OUTPUT_CLI) {
            return new IsbnOutputStandard();
        } else if (getOutputMode() == OUTPUT_XML) {
            return new IsbnOutputXml(getOutputFile());
        } else if (getOutputMode() == OUTPUT_BIBTEX) {
            return new IsbnOutputBibTeX(getOutputFile());
        } else if (getOutputMode() == OUTPUT_CSV) {
            return new IsbnOutputCsv(getOutputFile());
        } else {
            return null;
        }
    }

    /**
     * Retrieve the List of ISBNs as a String separated by commas, or null if no -l option has been specified
     * @return The -l value, or null if the argument has been specified
     */
    public String getIsbnListString() {
        return isbnListString;
    }

    /**
     * Retrieve the input File specified by the command line
     * @return the -iF value, or null if the argument has been specified
     */
    public File getInputFile() {
        return inputFile;
    }

    /**
     * Retrieve the output File specified by the command line
     * @return the -oF value, or null if the argument has been specified
     */
    public File getOutputFile() {
        return outputFile;
    }

    /**
     * Retrieve the configuration File specified by the command line
     * @return The -c value, or null if the argument has been specified
     */
    public File getConfigurationFile() {
        return configurationFile;
    }
    
    /**
     * Gets the actual input mode specified by the command line
     * @return a int value matching with an input mode
     */
    public int getInputMode() {
        return inputMode;
    }
    
    /**
     * Gets the actual output mode specified by the command line
     * @return a int value matching with the output mode
     */
    public int getOutputMode() {
        return outputMode;
    }
}
