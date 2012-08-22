/*
 */
package isbnsniff;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;

/**
 * Retrieve values specified in the Ini Configuration file using Apache Commons Configuration
 * @author jousse_s
 */
public class ConfigurationParser {

    private IsbnOutput isbnOutput = null;
    private List<IsbnModule> priorityList = null;
    private List<IsbnModule> moduleList = null;
    private Map<String, List<IsbnModule>> valuesPriority =
            new HashMap<String, List<IsbnModule>>();
    private HierarchicalINIConfiguration iniConf = null;
    private ConfigurationParserExceptionList confEx = new ConfigurationParserExceptionList();
    final private static String MODULE_PRIORITY = "module_priority";

    ConfigurationParser(File configurationFile, List<IsbnModule> moduleLst,
            IsbnOutput output) throws ConfigurationParserExceptionList {
        moduleList = moduleLst;
        isbnOutput = output;
        readConfiguration(configurationFile);
        if (!confEx.getErrorList().isEmpty()) {
            throw confEx;
        }
    }

    private void readConfiguration(File configurationFile) {
        try {
            iniConf = new HierarchicalINIConfiguration(configurationFile);
        } catch (ConfigurationException ex) {
            confEx.addError(new ConfigurationParserException(ex.getMessage()));
            return;
        }
        if (iniConf.isEmpty()) {
            confEx.addError(new ConfigurationParserException(
                    "File not found, or empty configuration file"));
            return;
        }
        iniConf.setListDelimiter(',');
        processGeneralSection();
        processValuesSection();
        processModuleSections();
    }

    /**
     * 
     * @return
     */
    public HierarchicalINIConfiguration getIniConf() {
        return iniConf;
    }

    private void processGeneralSection() {
        Iterator it = iniConf.getSection("general").getKeys();
        while (it.hasNext()) {
            String generalKey = (String) it.next();
            if (generalKey.equals(MODULE_PRIORITY)) {
                try {
                    priorityList = getModuleListFromParam(
                            iniConf.getSection("general"),
                            MODULE_PRIORITY, moduleList);
                    if (priorityList.size() < 1) {
                        confEx.addError(new ConfigurationParserException(
                                MODULE_PRIORITY + " argument must contains"
                                + " at least one search engine", "general"));                        
                    }
                } catch (NoSuchFieldException ex) {
                    confEx.addError(
                            new ConfigurationParserException(ex.getMessage()
                            + ": Unknown module for " + MODULE_PRIORITY
                            + " field", "general"));
                }
            } else {
                confEx.addError(new ConfigurationParserException(
                        generalKey + ": Unrecognized Key", "general"));
            }
        }
        if (!iniConf.getSection("general").containsKey(MODULE_PRIORITY)) {
            confEx.addError(new ConfigurationParserException(
                    MODULE_PRIORITY + " argument is undefined", "general"));
        }
    }

    private void processValuesSection() {
        HierarchicalConfiguration valuesSection = iniConf.getSection("values");
        Iterator it = valuesSection.getKeys();
        boolean found = false;
        while (it.hasNext()) {
            String key = (String) it.next();
            for (String bookKey : BookItem.KEY_LIST) {
                if (bookKey.equalsIgnoreCase(key)) {
                    try {
                        valuesPriority.put(key.toLowerCase(), getModuleListFromParam(
                                valuesSection, key.toLowerCase(), priorityList));
                    } catch (NoSuchFieldException ex) {
                        confEx.addError(
                                new ConfigurationParserException(ex.getMessage()
                                + ": A module specified for a value must be "
                                + "previously defined in " + MODULE_PRIORITY
                                + " field", "values"));
                    }
                    found = true;
                    break;
                }
            }
            if (!found) {
                confEx.addError(new ConfigurationParserException(
                        key + ": Unrecognized Key", "values"));
            }
            found = false;
        }
    }

    private void processModuleSections() {
        if (priorityList == null)
            return;
        for (IsbnModule module : priorityList) {
            SubnodeConfiguration sObj = iniConf.getSection(module.getModuleName().toLowerCase());
            try {
                module.setConfiguration(sObj);
            } catch (ConfigurationParserException ex) {
                ex.setSection(module.getModuleName().toLowerCase());
                confEx.addError(ex);
            }
        }
    }

    /**
     * Instanciate a new Search Engine depending on values specified in the configuration file
     * @return a new Search Engine
     */
    public SearchEngine generateSearchEngine() {
        return new SearchEngine(priorityList, valuesPriority);
    }

    /**
     * Parse values separated by commas from the Value Section
     * @param sObj The section who contains the values
     * @param keyList The keys that should be present in this section
     * @return A map of keys associated with their values (Ini Configuration format)
     * @throws ConfigurationParserException If an value not present in keyList has been matched
     */
    public static Map<String, String> getSpecificModuleValues(SubnodeConfiguration sObj,
            String[] keyList) throws ConfigurationParserException {
        Map<String, String> ret = new HashMap<String, String>();
        sObj.setThrowExceptionOnMissing(true);
        Iterator<String> it = sObj.getKeys();
        boolean found = false;
        while (it.hasNext()) {
            String cKey = (String) it.next();
            found = false;
            for (String key : keyList) {
                if (key.equalsIgnoreCase(cKey)) {
                    found = true;
                }
            }
            if (!found) {
                throw new ConfigurationParserException(cKey + ": Undefined Key");
            }
        }
        for (String key : keyList) {
            if (!sObj.containsKey(key)) {
                throw new ConfigurationParserException(key + ": Key is undefined");
            }
            ret.put(key, sObj.getString(key));
        }
        return ret;
    }

    static private List<IsbnModule> getModuleListFromParam(HierarchicalConfiguration section,
            String keyName, List<IsbnModule> moduleList) throws NoSuchFieldException {
        List<IsbnModule> retList = new ArrayList<IsbnModule>();
        boolean found = false;
        for (String moduleName : section.getStringArray(keyName)) {
            moduleName = moduleName.replaceAll(" ", "");
            if (moduleList != null)
            {
                for (IsbnModule module : moduleList) {
                    if (moduleName.equalsIgnoreCase(module.getModuleName())) {
                        found = true;
                        if (!retList.contains(module)) {
                            retList.add(module);
                        }
                        break;
                    }
                }
            }
            if (!found && moduleName.length() > 0) {
                throw new NoSuchFieldException(moduleName);
            }
            found = false;
        }
        return retList;
    }    
}
