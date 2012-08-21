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

    public SearchEngine(List<IsbnModule> pList,
            Map<String, List<IsbnModule>> vPriority) {
        priorityList = pList;
        valuesPriority = vPriority;
    }

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
