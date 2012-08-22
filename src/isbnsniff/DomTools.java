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

    /**
     * Return a list of XML nodes corresponding to a specified node name
     * @param parent Node where the search is perform
     * @param nodeName The name of the node targeted
     * @return
     */
    public static List<Node> getNodeList(Node parent, String nodeName) {
        List<Node> ret = new ArrayList<Node>();
        return ret;
    }

    /**
     * Return the value of an XML Node
     * @param parent Parent XML Node hosting the node
     * @param nodeName The name of the node targeted
     * @return The value of the targeted XML node
     */
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

    /**
     * Return an XML node corresponding to a specified node name
     * @param parent Node where the search is perform
     * @param nodeName The name of the node targeted
     * @return The targeted XML node
     * @throws Exception
     */
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

    /**
     * Get an attribute value contained in an XML Node
     * @param parent The Parent XML Node containing the targeted attribute
     * @param nodeName The name of node containing the targeted attribute
     * @param attributeName The attribute name
     * @return The value of the attribute, or null if the attribute does not exists
     */
    public static String getChildNodeAttribute(Node parent, String nodeName, String attributeName) {
        Node childNode = null;
        try {
            childNode = getChildNode(parent, nodeName);
        } catch (Exception ex) {
            return null;
        }
        return getNodeAttribute(childNode, attributeName);
    }

    /**
     * Get an attribute value contained in an XML Node
     * @param Node containing the targeted attribute
     * @param attributeName The attribute name
     * @return The value of the attribute, or null if the attribute does not exists
     */
    public static String getNodeAttribute(Node node, String attribute) {
        if (node.getAttributes() != null) {
            if (node.getAttributes().getNamedItem(attribute) != null) {
                return node.getAttributes().getNamedItem(attribute).getNodeValue();
            }
        }
        return null;
    }
}
