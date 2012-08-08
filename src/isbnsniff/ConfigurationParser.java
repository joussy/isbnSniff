/*
 */
package isbnsniff;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;
/**
 *
 * @author jousse_s
 */
public class ConfigurationParser {
    File configurationFile = null;
    public ConfigurationParser(File value)
    {
        configurationFile = value;
    }
    
    public void parseConfiguration(List<IsbnModule> moduleList)
    {
        try {
            HierarchicalINIConfiguration iniConfObj = new HierarchicalINIConfiguration(configurationFile);
            // Get Section names in ini file     
            for (IsbnModule module : moduleList)
            {
                SubnodeConfiguration sObj = iniConfObj.getSection(module.getModuleName());
                module.setConfiguration(sObj);
            }
        } catch (ConfigurationException ex) {
            Logger.getLogger(ConfigurationParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
