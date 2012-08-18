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
    abstract public void setConfiguration(SubnodeConfiguration cNode);
    public IsbnIO(String name)
    {
        ioName = name;
    }
    
    public String getIOName()
    {
        return ioName;
    }
}
