/*
 */
package isbnsniff;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jousse_s
 */
public class BookItem {
    IsbnNumber isbn;
    Integer weight;
    List<String> authorList = new ArrayList();
    List<String> categoryList = new ArrayList();
    String title;
    Integer pages;
    String publisher;
    String languageCode;
    String lccNumber;
    
    public BookItem(IsbnNumber value)
    {
        isbn = value;
    }
    public void addAuthor(String author)
    {
        authorList.add(author);
    }
    public void setNbPages(Integer value)
    {
        pages = value;
    }
    public void setTitle(String value)
    {
        title = value;
    }
    public String getTitle()
    {
        return title;
    }
    public Integer getNbPages()
    {
        return pages;
    }
    public IsbnNumber getIsbn()
    {
        return isbn;
    }
}
