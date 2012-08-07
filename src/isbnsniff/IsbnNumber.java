/*
 */
package isbnsniff;

import java.math.BigInteger;
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
    public IsbnNumber()
    {
    }
    public IsbnNumber(String number)
    {
        setIsbnNumber(number);
    }
    public final void setIsbnNumber(String number)
    {
        if (number == null)
            return;
        Pattern p = Pattern.compile("([0-9]{3})([0-9]{9})([0-9]{1})");
        Matcher m = p.matcher(number);
        if (m.find())
        {
            ean = Integer.parseInt(m.group(1));
            body = Integer.parseInt(m.group(2));
            checkDigit = Integer.parseInt(m.group(3));
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
            }
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
    public String getIsbn13()
    {
        return String.format("%03d", ean) + String.format("%09d", body) +
                getIsbnCheckDigit(false).toString();
    }
    public String getIsbn10()
    {        
        return String.format("%09d", body) +
                (getIsbnCheckDigit(true) > 9 ? "X" : String.format("%01d", getIsbnCheckDigit(true)));
    }
    /*
     * isbn10Mode : True for 10 digits, false for 13 digits
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
            return 10 - (sum % 10);
        }
    }
}
