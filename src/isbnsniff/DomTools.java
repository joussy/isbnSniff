/*
 */
package isbnsniff;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author jousse_s
 */
public class DomTools {

    public static List<Node> getNodeList(Node parent, String nodeName) {
        List<Node> ret = new ArrayList();
        return ret;
        /*
        NodeList nodeList = parent.getElementsByTagName("BookData");
        //NodeList l = domTree.getChildNodes();
        for (int i = 0; i < bookDataList.getLength(); i++)
        {
        processDOMBookData(bookDataList.item(i));
        }
         */
    }

    public static String getChildNodeValue(Node parent, String nodeName) {
        Node t;
        try {
            t = getChildNode(parent, nodeName);
        } catch (Exception ex) {
            return null;
        }
        if (t.getFirstChild().getNodeType() == Node.TEXT_NODE) {
            return t.getFirstChild().getNodeValue();
        }
        return null;
    }

    public static Node getChildNode(Node parent, String nodeName) throws Exception {
        NodeList nodeList = parent.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                //System.out.println("DEBUG=" + nodeList.item(i).getNodeName());
                if (nodeList.item(i).getNodeName().equalsIgnoreCase(nodeName)) {
                    return nodeList.item(i);
                }
            }
        }
        throw new Exception("No XML Data for " + nodeName);
    }

    public static String getChildNodeAttribute(Node parent, String nodeName, String attributeName) {
        Node childNode = null;
        try {
            childNode = getChildNode(parent, nodeName);
        } catch (Exception ex) {
            return null;
        }
        return getNodeAttribute(childNode, attributeName);
    }

    public static String getNodeAttribute(Node node, String attribute) {
        if (node.getAttributes() != null) {
            if (node.getAttributes().getNamedItem(attribute) != null) {
                return node.getAttributes().getNamedItem(attribute).getNodeValue();
            }
        }
        return null;
    }
    //public static setChildNode(Node node, String name, String value)
}
