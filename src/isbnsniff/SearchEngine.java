/*
 */
package isbnsniff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;

/**
 *
 * @author jousse_s
 */
public class SearchEngine {

    private List<IsbnModule> moduleList = new ArrayList();
    private List<IsbnNumber> isbnList = new ArrayList();
    private List<IsbnModule> priorityList = new ArrayList();
    private List<BookItem> bookResult = new ArrayList();
    private Map<String, List<IsbnModule>> valuesPriority = new HashMap();
    private ConfigurationParser cParser = null;

    public SearchEngine(List<IsbnModule> pList,
            Map<String, List<IsbnModule>> vPriority) {
        priorityList = pList;
        valuesPriority = vPriority;
    }
    /*
    private void processGeneralConfiguration() {
    HierarchicalConfiguration generalSection = cParser.getIniConf().getSection("general");
    priorityList = ConfigurationParser.getModuleListFromParam(
    generalSection, "module_priority", moduleList);
    }
    
    private void processValuesConfiguration() {
    HierarchicalConfiguration valuesSection = cParser.getIniConf().getSection("values");
    Iterator it = valuesSection.getKeys();
    while (it.hasNext())
    {
    String key = (String) it.next();
    valuesPriority.put(key, ConfigurationParser.getModuleListFromParam(
    valuesSection, key, moduleList));
    }
    }
    
    private void processModuleConfiguration() {
    for (IsbnModule module : moduleList) {
    SubnodeConfiguration sObj = cParser.getIniConf().getSection(module.getModuleName());
    module.setConfiguration(sObj);
    }
    }
     */

    public void performSearch() {
        System.out.print("Processing: ");
        for (IsbnModule module : priorityList) {
            for (IsbnNumber isbn : isbnList) {
                module.addBookItem(new BookItem(isbn));
            }
            System.out.print(", " + module.getModuleName());
            try {
                module.processQuery();
            } catch (IsbnModuleException ex) {
                System.err.println(ex.getModuleName() + " Engine Error: " + ex.getMessage());
            }
        }
        System.out.println();
    }

    /*
    public void mergeResults() {
    for (IsbnNumber isbn : isbnList) {
    BookItem book = new BookItem(isbn);
    for (IsbnModule module : priorityList) {
    book.automaticMerge(module.getBookItem(isbn), valuesPriority);
    }
    bookResult.add(book);
    }
    }
     * 
     */
    public void mergeResults() {
        for (IsbnNumber isbn : isbnList) {
            BookItem book = new BookItem(isbn);
            for (IsbnModule module : priorityList) {
                book.automaticMerge(module.getBookItem(isbn), valuesPriority);
            }
            for (Entry<String, List<IsbnModule>> entry : valuesPriority.entrySet()) {
                book.setValue(entry.getKey(), null);
                for (IsbnModule module : entry.getValue()) {
                    if (priorityList.contains(module)) {
                        Object value = module.getBookItem(isbn).getValue(entry.getKey());
                        if (value != null) {
                            book.setValue(entry.getKey(), value);
                            break;
                        }
                    }
                }
            }
            bookResult.add(book);
        }
    }

    public void addIsbnModule(IsbnModule module) {
        moduleList.add(module);
    }
    /*
    private IsbnModule getIsbnModule(String moduleName) {
    for (IsbnModule module : moduleList) {
    if (module.getModuleName().equals(moduleName)) {
    return module;
    }
    }
    return null;
    }
     */

    public void setIsbnList(List<IsbnNumber> value) {
        for (IsbnNumber isbn : value) {
            if (!isbnList.contains(isbn)) {
                isbnList.add(isbn);
            }
        }
    }

    public void debugPrintValuesPriority() {
        for (Entry<String, List<IsbnModule>> entry : valuesPriority.entrySet()) {
            System.out.print("value=" + entry.getKey() + " Priority=");
            for (IsbnModule module : entry.getValue()) {
                System.out.print(module.getModuleName() + ", ");
            }
            System.out.println();
        }
    }

    private String pA(String value) {
        return value == null ? "" : value;
    }
    public void debugPrintModuleResults() {
        for (IsbnModule module : priorityList) {
            System.out.println("/--" + module.getModuleName());
            for (BookItem book : module.getBookItemList()) {
                System.out.print("Title=" + pA(book.getTitle())
                        + ", NbPages=" + book.getNbPages()
                        + ", Isbn=" + book.getIsbn().getIsbn13());
                if (book.getAuthorList() != null)
                    System.out.print(", Authors=" + book.getAuthorList().toString());
                System.out.println();
            }
            System.out.println("--/");
        }
    }

    public List<BookItem> getResults() {
        return bookResult;
    }
}
