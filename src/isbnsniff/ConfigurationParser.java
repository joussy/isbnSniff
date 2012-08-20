/*
 */
package isbnsniff;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
public class ConfigurationParser {

    private IsbnOutput isbnOutput = null;
    private List<IsbnModule> priorityList = null;
    private List<IsbnModule> moduleList = null;
    private List<String> valueListOutput = null;
    private Map<String, List<IsbnModule>> valuesPriority =
            new HashMap<String, List<IsbnModule>>();
    private HierarchicalINIConfiguration iniConf = null;
    private ConfigurationParserExceptionList confEx = new ConfigurationParserExceptionList();

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

    private List<IsbnModule> getModuleListFromParam(HierarchicalConfiguration section,
            String keyName, List<IsbnModule> moduleList) {
        List<IsbnModule> retList = new ArrayList<IsbnModule>();
        boolean found = false;
        for (String moduleName : section.getStringArray(keyName)) {
            for (IsbnModule module : moduleList) {
                if (moduleName.equalsIgnoreCase(module.getModuleName())) {
                    found = true;
                    if (!retList.contains(module)) {
                        retList.add(module);
                    }
                    break;
                }
            }
            if (!found) {
                confEx.addError(new ConfigurationParserException("This module does not exist"));
            }
            found = false;
        }
        return retList;
    }

    private void processGeneralSection() {
        if (!iniConf.getSection("general").containsKey("module_priority"))
        {
            confEx.addError(new ConfigurationParserException(
                    "module_priority argument undefined"));
        }
        priorityList = getModuleListFromParam(
                iniConf.getSection("general"), "module_priority", moduleList);
        if (!iniConf.getSection("general").containsKey("output_values"))
            valueListOutput = new ArrayList<String>(Arrays.asList(BookItem.KEY_LIST));
        else
            valueListOutput = getValueListFromParam(iniConf.getSection("general"),
            "output_values", Arrays.asList(BookItem.KEY_LIST));
        isbnOutput.setOutputValueList(valueListOutput);
    }

    private void processValuesSection() {
        HierarchicalConfiguration valuesSection = iniConf.getSection("values");
        Iterator it = valuesSection.getKeys();
        boolean found = false;
        while (it.hasNext()) {
            String key = (String) it.next();
            for (String bookKey : BookItem.KEY_LIST) {
                if (bookKey.equalsIgnoreCase(key)) {
                    valuesPriority.put(key.toLowerCase(), getModuleListFromParam(
                            valuesSection, key.toLowerCase(), moduleList));
                    found = true;
                    break;
                }
            }
            if (!found)
                confEx.addError(new ConfigurationParserException(key + ": Unrecognized Value"));
            found = false;
        }
    }

    private void processModuleSections() {
        for (IsbnModule module : moduleList) {
            SubnodeConfiguration sObj = iniConf.getSection(module.getModuleName());
            module.setConfiguration(sObj);
        }
    }

    public SearchEngine generateSearchEngine() {
        return new SearchEngine(priorityList, valuesPriority);
    }

    private List<String> getValueListFromParam(SubnodeConfiguration section, String keyName, List<String> valueList) {
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
}
