package ru.example.todoapp.util;
/*
 * Date: 7/3/21
 * Time: 1:09 PM
 * */

import java.util.Locale;
import java.util.Random;

public final class RandomStringGenerator {

    private static final int LENGTH = 64;
    private static final char[] buf = new char[LENGTH];

    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = UPPER.toLowerCase(Locale.ROOT);
    private static final String DIGITS = "0123456789";
    private static final String ALPHANUM = UPPER + LOWER + DIGITS;

    private static final char[] symbols = ALPHANUM.toCharArray();

    /**
     * Generate a random string.
     */
    public static String nextString() {
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[new Random().nextInt(symbols.length)];
        return new String(buf);
    }

}
