package edu.monash.fit4039.keepmybalance;

import java.text.CharacterIterator;
import java.text.DecimalFormat;

/**
 * Created by nathan on 26/5/17.
 */

public class Validation {
    //return true when the string is not null or "" or only space
    public static boolean isNullEmptyBlank(String string) {
        return isNull(string) || isBlank(string);
    }

    //return true when the string is not null
    public static boolean isNull(String string) {
        return string == null;
    }

    //return true when the string is not only space or ""
    public static boolean isBlank(String string) {
        return string.trim().isEmpty();
    }

    //left two decimal of a number
    public static double leftTwoDecimal(double number) {
        DecimalFormat df = new DecimalFormat("#0.00");
        return Double.valueOf(df.format(number));
    }

    //return true when the string only contains number or character
    public static boolean isNumericOrChar(String string) {
        return string.matches("[0-9A-Za-z]*");
    }

    //return true when the string only contains number or character and contains at least one upper case, one lower case and one number
    public static boolean isLowerUpperAndNumeric(String string) {
        return isNumericOrChar(string) && containsLowerCase(string) && containsUpperCase(string) && containsNumber(string);
    }

    //return true when the string contains at least one upper case
    public static boolean containsUpperCase(String string) {
        for (int i = 0; i < string.length(); i++) {
            if (Character.isUpperCase(string.charAt(i)))
                return true;
        }
        return false;
    }

    //return true when the string contains at least one lower case
    public static boolean containsLowerCase(String string) {
        for (int i = 0; i < string.length(); i++) {
            if (Character.isLowerCase(string.charAt(i)))
                return true;
        }
        return false;
    }

    //return true when the string contains at least one number
    public static boolean containsNumber(String string) {
        for (int i = 0; i < string.length(); i++) {
            if (Character.isDigit(string.charAt(i)))
                return true;
        }
        return false;
    }
}
