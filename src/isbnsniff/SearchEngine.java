/*
 */
package isbnsniff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

    public SearchEngine(ConfigurationParser value) {
        cParser = value;
    }

    public void processConfiguration() {
        processModuleConfiguration();
        processGeneralConfiguration();
        processValuesConfiguration();
    }

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

    public void performSearch() {
        System.out.print("Processing: ");
        for (IsbnModule module : priorityList) {
            for (IsbnNumber isbn : isbnList) {
                module.addBookItem(new BookItem(isbn));
            }
            System.out.print(", " + module.getModuleName());
            module.processQuery();
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
                for (IsbnModule module : entry.getValue())
                {
                    Object value = module.getBookItem(isbn).getValue(entry.getKey());
                    if (value != null)
                    {
                        book.setValue(entry.getKey(), value);
                        break;
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

    public void addIsbn(IsbnNumber isbn) {
        if (!isbnList.contains(isbn)) {
            isbnList.add(isbn);
        }
    }
    public void setIsbnList(List<IsbnNumber> value)
    {
        for (IsbnNumber isbn : value)
        {
            addIsbn(isbn);
        }
    }

    public void printResults() {
        System.out.println("/--MergedResults");
        for (BookItem book : bookResult) {
            System.out.println("Title=" + book.getTitle()
                    + ", NbPages=" + book.getNbPages()
                    + ", Isbn=" + book.getIsbn().getIsbn13());
        }
    }

    public void printValuesPriority() {
        for (Entry<String, List<IsbnModule>> entry : valuesPriority.entrySet())
        {
            System.out.print("value=" + entry.getKey()+ " Priority=");
            for (IsbnModule module : entry.getValue())
            {
                System.out.print(module.getModuleName() + ", ");
            }
            System.out.println();
        }
    }
    public void printModuleResults() {
        for (IsbnModule module : priorityList) {
            System.out.println("/--" + module.getModuleName());
            for (BookItem book : module.getBookItemList()) {
                System.out.println("Title=" + book.getTitle()
                        + ", NbPages=" + book.getNbPages()
                        + ", Isbn=" + book.getIsbn().getIsbn13());
            }
            System.out.println("--/");
        }
    }

    private void processModuleConfiguration() {
        for (IsbnModule module : moduleList) {
            SubnodeConfiguration sObj = cParser.getIniConf().getSection(module.getModuleName());
            module.setConfiguration(sObj);
        }
    }
}
