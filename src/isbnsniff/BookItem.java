/*
 */
package isbnsniff;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author jousse_s
 */
public class BookItem {

    final public static String A_NBPAGES = "nbpages";
    final public static String A_TITLE = "title";
    final public static String A_LONG_TITLE = "long_title";
    final public static String A_AUTHORS = "authors";
    final public static String A_PUBLISHER = "publisher";
    final public static String A_PUBLICATION_DATE = "publication_date";
    final public static String A_DESCRIPTION = "description";
    final public static String A_DIMENSIONS = "dimensions";
    final public static String A_LANGUAGE = "language";
    final public static String A_CATEGORIES = "categories";
    final public static String A_SYNOPSIS = "synopsis";
    final public static String A_LCC = "lcc";
    final public static String[] KEY_LIST = {A_NBPAGES, A_TITLE, A_LONG_TITLE,
        A_AUTHORS,
        A_PUBLISHER, A_PUBLICATION_DATE, A_DESCRIPTION, A_DIMENSIONS,
        A_LANGUAGE, A_CATEGORIES, A_SYNOPSIS, A_LCC};
    IsbnNumber isbn;
    Integer weight;
    List<String> authorList = null;
    List<String> categoryList = null;
    Map<String, Object> attributeList = new HashMap<String, Object>();

    public BookItem(IsbnNumber value) {
        isbn = value;
    }

    public Object getValue(String key) {
        return attributeList.get(key);
    }

    public void setValue(String key, Object value) {
        attributeList.put(key, value);
    }

    public void addAuthor(String author) {
        if (authorList == null)
        {
            authorList = new ArrayList<String>();
            attributeList.put(A_AUTHORS, authorList);
        }
        authorList.add(author);
    }

    public void setNbPages(Integer value) {
        attributeList.put(A_NBPAGES, value);
    }

    public void setLongTitle(String value) {
        attributeList.put(A_LONG_TITLE, value);
    }

    public void setTitle(String value) {
        attributeList.put(A_TITLE, value);
    }
    
    public void setPublicationDate(Date value) {
        attributeList.put(A_PUBLICATION_DATE, value);
    }

    public void setPublisher(String value) {
        attributeList.put(A_PUBLISHER, value);
    }
    
    public void setSynopsis(String value) {
        attributeList.put(A_SYNOPSIS, value);
    }
    
    public void setLcc(String value) {
        attributeList.put(A_LCC, value);
    }

    public void addCategory(String value) {
        if (categoryList == null)
        {
            categoryList = new ArrayList<String>();
            attributeList.put(A_CATEGORIES, categoryList);
        }
        categoryList.add(value);
    }
    
    public String getTitle() {
        return (String) attributeList.get(A_TITLE);
    }

    public String getLongTitle() {
        return (String) attributeList.get(A_LONG_TITLE);
    }

    public String getLcc() {
        return (String) attributeList.get(A_LCC);
    }

    public Integer getNbPages() {
        return (Integer) attributeList.get(A_NBPAGES);
    }

    public IsbnNumber getIsbn() {
        return isbn;
    }

    public List<String> getAuthorList() {
        return (List<String>) attributeList.get(A_AUTHORS);
    }

    public List<String> getCategoryList() {
        return (List<String>) attributeList.get(A_CATEGORIES);
    }
    
    public Date getPublicationDate() {
        return (Date) attributeList.get(A_PUBLICATION_DATE);
    }
    
    public String getPublisher() {
        return (String) attributeList.get(A_PUBLISHER);
    }

    public String getSynopsis() {
        return (String) attributeList.get(A_SYNOPSIS);
    }

    public void automaticMerge(BookItem bookItem, Map<String, List<IsbnModule>> valuesPriority) {
        for (Entry<String, Object> entry : bookItem.attributeList.entrySet()) {
            if (attributeList.get((String) entry.getKey()) == null) {
                attributeList.put((String) entry.getKey(), entry.getValue());
            }
        }
    }
}
