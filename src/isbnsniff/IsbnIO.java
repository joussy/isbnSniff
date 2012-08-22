/*
 */
package isbnsniff;

import org.apache.commons.configuration.SubnodeConfiguration;

/**
 *
 * @author jousse_s
 */
public abstract class IsbnIO {
    String ioName = null;
    /**
     * Configure the IO with the values from the configuration file
     * @param cNode INI Node containing configuration keys/values
     */
    abstract public void setConfiguration(SubnodeConfiguration cNode);
    /**
     * 
     * @param name The name of the I/O Module
     */
    public IsbnIO(String name)
    {
        ioName = name;
    }
    
    /**
     * Get the name of the IO module
     * @return
     */
    public String getIOName()
    {
        return ioName;
    }
}
