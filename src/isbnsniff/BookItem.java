/*
 */
package isbnsniff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author jousse_s
 */
public class BookItem {
    final public String A_NBPAGES = "nbpages";
    final public String A_TITLE = "title";
    final public String A_AUTHOR = "authors";
    final public String A_PUBLISHER = "publisher";
    final public String A_PUBLICATION_DATE = "publication_date";
    final public String A_DESCRIPTION = "description";
    final public String A_DIMENSIONS = "dimensions";
    final public String A_LANGUAGE = "language";
    final public String A_CATEGORIES = "categories";
    IsbnNumber isbn;
    Integer weight;
    List<String> authorList = new ArrayList();
    List<String> categoryList = new ArrayList();
    String title;
    String publisher;
    String languageCode;
    String lccNumber;
    Map<String, Object> attributeList = new HashMap();
    
    public BookItem(IsbnNumber value)
    {
        isbn = value;
    }
    public Object getValue(String key)
    {
        return attributeList.get(key);
    }
    public void setValue(String key, Object value)
    {
        attributeList.put(key, value);
    }
    public void addAuthor(String author)
    {
        authorList.add(author);
    }
    public void setNbPages(Integer value)
    {
        attributeList.put(A_NBPAGES, value);
    }
    public void setTitle(String value)
    {
        attributeList.put(A_TITLE, value);
    }
    public String getTitle()
    {
        return (String) attributeList.get(A_TITLE);
    }
    public Integer getNbPages()
    {
        return (Integer) attributeList.get(A_NBPAGES);
    }
    public IsbnNumber getIsbn()
    {
        return isbn;
    }

    public void automaticMerge(BookItem bookItem, Map<String, List<IsbnModule>> valuesPriority) {
        for (Entry<String, Object> entry : bookItem.attributeList.entrySet())
        {
            if (attributeList.get((String) entry.getKey()) == null)
            {
                attributeList.put((String) entry.getKey(), entry.getValue());
            }
        }
    }
}
