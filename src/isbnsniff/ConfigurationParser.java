/*
 */
package isbnsniff;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;
/**
 *
 * @author jousse_s
 */
public class ConfigurationParser {
    File configurationFile = null;
    HierarchicalINIConfiguration iniConf = null;
    public ConfigurationParser(File value)
    {
        configurationFile = value;
        try {
            iniConf = new HierarchicalINIConfiguration(configurationFile);
            iniConf.setListDelimiter(',');
        } catch (ConfigurationException ex) {
            Logger.getLogger(ConfigurationParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public HierarchicalINIConfiguration getIniConf()
    {
        return iniConf;
    }
    static public List<IsbnModule> getModuleListFromParam(HierarchicalConfiguration section,
            String keyName, List<IsbnModule> moduleList)
    {
        List<IsbnModule> retList = new ArrayList();
        for (String moduleName : section.getStringArray(keyName))
        {
            for (IsbnModule module : moduleList)
            {
                if (moduleName.equals(module.getModuleName()))
                {
                    if (!retList.contains(module) && module.isEnabled())
                        retList.add(module);
                    break;
                }
            }
        }
        return retList;
    }
}
