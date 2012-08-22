/*
 */
package isbnsniff;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author jousse_s
 */
public class IsbnNumber {

    private Integer ean = 0;
    private Integer body = 0;
    private Integer checkDigit = 0;
    /**
     * Define an empty ISBN
     */
    public IsbnNumber()
    {
    }
    /**
     * Define an ISBN from a String
     * @param number
     * @throws IsbnFormatException If the ISBN parsed is invalid
     */
    public IsbnNumber(String number) throws IsbnFormatException
    {
        setIsbnNumber(number);
    }
    /**
     * Check the format and the check digit of a given ISBN
     * @param orgNumber
     * @throws IsbnFormatException
     */
    public final void setIsbnNumber(String orgNumber) throws IsbnFormatException
    {
        if (orgNumber == null)
            return;
        String number = orgNumber.replaceAll("[^0-9Xx]", "");
        Pattern p = Pattern.compile("([0-9]{3})([0-9]{9})([0-9]{1})");
        Matcher m = p.matcher(number);
        if (m.find())
        {
            ean = Integer.parseInt(m.group(1));
            body = Integer.parseInt(m.group(2));
            checkDigit = Integer.parseInt(m.group(3));
            if (getIsbnCheckDigit(false) != checkDigit)
                throw new IsbnFormatException(
                        IsbnFormatException.ERR_CHECK_DIGIT, orgNumber);
        }
        else
        {
            p = Pattern.compile("([0-9]{9})([0-9]{1}|x|X)");
            m = p.matcher(number);
            if (m.find())
            {
                ean = 978;
                body = Integer.parseInt(m.group(1));
                if (m.group(2).matches("[0-9]{1}"))
                    checkDigit = Integer.parseInt(m.group(2));
                else
                    checkDigit = 10;
                if (getIsbnCheckDigit(true) != checkDigit)
                throw new IsbnFormatException(
                        IsbnFormatException.ERR_CHECK_DIGIT, orgNumber);
            }
            else
                throw new IsbnFormatException(
                        IsbnFormatException.ERR_FORMAT, orgNumber);
        }
    }
 @Override
    public boolean equals(Object obj)
    {
        if (this.getClass() == obj.getClass())
        {
            return this.body.equals(((IsbnNumber)obj).body);
        }
        return false;
    }
 /**
  * Get the ISBN 13 digits version of the number
  * @return
  */
 public String getIsbn13()
    {
        return String.format("%03d", ean) + String.format("%09d", body) +
                getIsbnCheckDigit(false).toString();
    }
    /**
     * Get the ISBN 10 digits version of the number
     * @return
     */
    public String getIsbn10()
    {        
        return String.format("%09d", body) +
                (getIsbnCheckDigit(true) > 9 ? "X" : String.format("%01d", getIsbnCheckDigit(true)));
    }
    /**
     * Check Digit 10/13 format calculation
     * @param isbn10Mode True for 10 digits, false for 13 digits
     * @return the check digit
     */
    public Integer getIsbnCheckDigit(boolean isbn10Mode)
    {
        int sum = 0;
        int mod = 10;
        if (isbn10Mode)
        {
            for (int isbnType = 9; isbnType > 0; isbnType--)
            {
                sum += isbnType * ((body % mod) / (mod / 10));
                //System.out.print((body % mod) / (mod / 10) + " x " + isbnType + " + ");
                mod *= 10;
            }
            return sum % 11;
        }
        else
        {
            int n = 3;
            for (int isbnType = 9; isbnType > 0; isbnType--)
            {
                sum += ((body % mod) / (mod / 10)) * n;
                //System.out.print((body % mod) / (mod / 10) + " x " + n + " + ");
                mod *= 10;
                n = n == 3 ? 1 : 3;
            }
            mod = 10;
            for (int isbnType = 3; isbnType > 0; isbnType--)
            {
                sum += ((ean % mod) / (mod / 10)) * n;
                //System.out.print((ean % mod) / (mod / 10) + " x " + n + " + ");
                mod *= 10;
                n = n == 3 ? 1 : 3;
            }
            if (sum % 10 == 0)
                return 0;
            else
                return 10 - (sum % 10);
        }
    }
}
