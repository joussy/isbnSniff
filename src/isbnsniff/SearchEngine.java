/*
 */
package isbnsniff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author jousse_s
 */
public class SearchEngine {

    private List<IsbnModule> moduleList = new ArrayList<IsbnModule>();
    private List<IsbnNumber> isbnList = new ArrayList<IsbnNumber>();
    private List<IsbnModule> priorityList = new ArrayList<IsbnModule>();
    private List<BookItem> bookResult = new ArrayList<BookItem>();
    private Map<String, List<IsbnModule>> valuesPriority =
            new HashMap<String, List<IsbnModule>>();
    private ConfigurationParser cParser = null;

    /**
     * Call the ISBN Engine modules from an ISBN list and merge the results
     * @param pList The list of the engines who will perform the search (Ordered by priority)
     * @param vPriority The Map of values/Module list
     */
    public SearchEngine(List<IsbnModule> pList,
            Map<String, List<IsbnModule>> vPriority) {
        priorityList = pList;
        valuesPriority = vPriority;
    }

    /**
     * Call all the loaded search engine
     */
    public void performSearch() {
        System.out.println("Processing ISBNs: ");
        for (IsbnModule module : priorityList) {
            for (IsbnNumber isbn : isbnList) {
                module.addBookItem(new BookItem(isbn));
            }
            System.out.print("=>" + module.getModuleName() + " ... ");
            IsbnModuleExceptionList exList = null;
            try {
                module.processQuery();
            } catch (IsbnModuleExceptionList ex) {
                exList = ex;
            }
            if (exList == null) {
                System.out.println("OK");
            }
            else {
                int i = 0;
                String err = "";
                for (IsbnModuleException ex : exList.getErrorList()) {
                    err += (i++ > 0 ? System.getProperty("line.separator") : "");
                    err += ex.getMessage();
                    if (ex.getIsbnNumber() != null) {
                        err += " (ISBN: " + ex.getIsbnNumber().getIsbn13() + ")";
                    }
                }
                System.out.println(i + " issues:");
                System.err.println(err);
            }
        }
    }
    //@todo empty string should be considered as null
    /**
     * Merge results from each search engine. If an engine does not know the value for a field, the following engine result is called
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

    /**
     * Specify the list of ISBN to send to all the loaded search engines
     * @param value
     */
    public void setIsbnList(List<IsbnNumber> value) {
        for (IsbnNumber isbn : value) {
            if (!isbnList.contains(isbn)) {
                isbnList.add(isbn);
            }
        }
    }

    /**
     * 
     */
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
    /**
     * Print a debug for each module answers
     */
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

    /**
     * Return the BookItem list generated from search engine results
     * @return
     */
    public List<BookItem> getResults() {
        return bookResult;
    }
}
