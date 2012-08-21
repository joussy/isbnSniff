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
 *
 * @author jousse_s
 */
//@todo unkown key should throw an exception
//@todo no engine = ?
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
                                + " at least one search engine"));                        
                    }
                } catch (NoSuchFieldException ex) {
                    confEx.addError(
                            new ConfigurationParserException(ex.getMessage()
                            + ": Unknown module for " + MODULE_PRIORITY
                            + " field"));
                }
            } else {
                confEx.addError(new ConfigurationParserException(
                        generalKey + ": Unrecognized Key"));
            }
        }
        if (!iniConf.getSection("general").containsKey(MODULE_PRIORITY)) {
            confEx.addError(new ConfigurationParserException(
                    MODULE_PRIORITY + " argument is undefined"));
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
                                + " field"));
                    }
                    found = true;
                    break;
                }
            }
            if (!found) {
                confEx.addError(new ConfigurationParserException(key + ": Unrecognized Key"));
            }
            found = false;
        }
    }

    private void processModuleSections() {
        for (IsbnModule module : moduleList) {
            SubnodeConfiguration sObj = iniConf.getSection(module.getModuleName());
            try {
                module.setConfiguration(sObj);
            } catch (ConfigurationParserException ex) {
                confEx.addError(new ConfigurationParserException(ex.getMessage()));
            }
        }
    }

    public SearchEngine generateSearchEngine() {
        return new SearchEngine(priorityList, valuesPriority);
    }

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
    
    /*
     * private List<String> getValueListFromParam(SubnodeConfiguration section, String keyName, List<String> valueList) {
    List<String> valueListRet = new ArrayList<String>();
    boolean found = false;
    for (String valueConf : section.getStringArray(keyName)) {
    for (String valueDef : valueList) {
    if (valueConf.equalsIgnoreCase(valueDef)) {
    found = true;
    if (!valueListRet.contains(valueConf)) {
    valueListRet.add(valueConf.toLowerCase());
    }
    break;
    }
    }
    if (!found) {
    confEx.addError(new ConfigurationParserException("This value does not exist"));
    }
    found = false;
    }
    return valueListRet;
    }
     */
}
