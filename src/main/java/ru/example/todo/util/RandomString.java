package ru.example.todo.util;
/*
 * Date: 7/3/21
 * Time: 1:09 PM
 * */

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Random;

public final class RandomString {

    private final Random random;
    private final char[] symbols;
    private final char[] buf;

    public static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String LOWER = UPPER.toLowerCase(Locale.ROOT);
    public static final String DIGITS = "0123456789";
    public static final String ALPHANUM = UPPER + LOWER + DIGITS;

    public RandomString(int length) {
        if (length < 32) throw new IllegalArgumentException();
        this.random = new SecureRandom();
        this.symbols = ALPHANUM.toCharArray();
        this.buf = new char[length];
    }

    /**
     * Generate a random string.
     */
    public String nextString() {
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];
        return new String(buf);
    }

}
