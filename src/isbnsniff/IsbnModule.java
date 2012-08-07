/*
 */
package isbnsniff;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jousse_s
 */
public abstract class IsbnModule {
        private List<BookItem> bookItemList = new ArrayList();
        protected String moduleName = "unknown";
        protected abstract void processQueryIsbn(BookItem nb);
        protected abstract void processQueryInitialize();
        protected abstract void processQueryTerminate();

        public void addBookItem(BookItem book)
        {
            bookItemList.add(book);
        }
        public void processQuery()
        {
            processQueryInitialize();
            for (BookItem book : bookItemList)
            {
                processQueryIsbn(book);
            }
            processQueryTerminate();
        }
        public List<BookItem> getBookItemList()
        {
            return bookItemList;
        }
        protected BookItem getBookItem(IsbnNumber nb)
        {
            for (BookItem book : bookItemList)
            {
                if (book.getIsbn().equals(nb))
                    return book;
            }
            return null;
        }
        public String getModuleName()
        {
            return moduleName;
        }
}
