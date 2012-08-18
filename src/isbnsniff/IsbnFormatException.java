/*
 */
package isbnsniff;

/**
 *
 * @author jousse_s
 */
public class IsbnFormatException extends Exception {

    final public static int ERR_CHECK_DIGIT = 1;
    final public static int ERR_FORMAT = 2;
    int errNum = 0;
    String isbn = null;

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

    public String getIsbn() {
        return isbn;
    }
}
