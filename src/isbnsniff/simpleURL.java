/*
 */
package isbnsniff;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jousse_s
 */
public class simpleURL {
    String host = new String();
    String path = new String();
    Map<String, String> paramMap = new HashMap();
    int port = 80;
    //String host = new String();
    public simpleURL(String value)
    {
        host = value;
    }
    public void setPath(String value)
    {
        path = value;
    }
    public void setPort(int value)
    {
        port = value;
    }
    public void addParameter(String key, String value)
    {
        paramMap.put(key, value);
    }
    public void getURL()
    {
        //URL u = new URL("http", host, port, host) 
    }
}
