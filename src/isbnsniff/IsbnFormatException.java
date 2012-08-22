/*
 */
package isbnsniff;

/**
 *
 * @author jousse_s
 */
public class IsbnFormatException extends Exception {

    /**
     * The check digit at the and of the ISBN is incorrect
     */
    final public static int ERR_CHECK_DIGIT = 1;
    /**
     * The format of the ISBN does not match neither 10 or 13 version
     */
    final public static int ERR_FORMAT = 2;
    int errNum = 0;
    String isbn = null;

    /**
     * Exception 
     * @param num
     * @param isbnValue
     */
    public IsbnFormatException(int num, String isbnValue) {
        errNum = num;
        isbn = isbnValue;
    }

    @Override
    public String getMessage() {
        if (errNum == ERR_CHECK_DIGIT) {
            return "Incorrect Check Digit";
        } else if (errNum == ERR_FORMAT) {
            return "Unrecognized ISBN Format";
        } else {
            return "Undefined Exception";
        }
    }

    /**
     * 
     * @return
     */
    public String getIsbn() {
        return isbn;
    }
}
