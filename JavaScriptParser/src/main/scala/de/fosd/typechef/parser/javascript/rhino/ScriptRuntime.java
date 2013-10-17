package de.fosd.typechef.parser.javascript.rhino;


public class ScriptRuntime {
    public static final double
            NaN = Double.longBitsToDouble(0x7ff8000000000000L);
    /*
 * Helper function for toNumber, parseInt, and TokenStream.getToken.
 */
    static double stringToNumber(String s, int start, int radix) {
        char digitMax = '9';
        char lowerCaseBound = 'a';
        char upperCaseBound = 'A';
        int len = s.length();
        if (radix < 10) {
            digitMax = (char) ('0' + radix - 1);
        }
        if (radix > 10) {
            lowerCaseBound = (char) ('a' + radix - 10);
            upperCaseBound = (char) ('A' + radix - 10);
        }
        int end;
        double sum = 0.0;
        for (end=start; end < len; end++) {
            char c = s.charAt(end);
            int newDigit;
            if ('0' <= c && c <= digitMax)
                newDigit = c - '0';
            else if ('a' <= c && c < lowerCaseBound)
                newDigit = c - 'a' + 10;
            else if ('A' <= c && c < upperCaseBound)
                newDigit = c - 'A' + 10;
            else
                break;
            sum = sum*radix + newDigit;
        }
        if (start == end) {
            return NaN;
        }
        if (sum >= 9007199254740992.0) {
            if (radix == 10) {
                /* If we're accumulating a decimal number and the number
                 * is >= 2^53, then the result from the repeated multiply-add
                 * above may be inaccurate.  Call Java to get the correct
                 * answer.
                 */
                try {
                    return Double.parseDouble(s.substring(start, end));
                } catch (NumberFormatException nfe) {
                    return NaN;
                }
            } else if (radix == 2 || radix == 4 || radix == 8 ||
                    radix == 16 || radix == 32)
            {
                /* The number may also be inaccurate for one of these bases.
                 * This happens if the addition in value*radix + digit causes
                 * a round-down to an even least significant mantissa bit
                 * when the first dropped bit is a one.  If any of the
                 * following digits in the number (which haven't been added
                 * in yet) are nonzero then the correct action would have
                 * been to round up instead of down.  An example of this
                 * occurs when reading the number 0x1000000000000081, which
                 * rounds to 0x1000000000000000 instead of 0x1000000000000100.
                 */
                int bitShiftInChar = 1;
                int digit = 0;

                final int SKIP_LEADING_ZEROS = 0;
                final int FIRST_EXACT_53_BITS = 1;
                final int AFTER_BIT_53         = 2;
                final int ZEROS_AFTER_54 = 3;
                final int MIXED_AFTER_54 = 4;

                int state = SKIP_LEADING_ZEROS;
                int exactBitsLimit = 53;
                double factor = 0.0;
                boolean bit53 = false;
                // bit54 is the 54th bit (the first dropped from the mantissa)
                boolean bit54 = false;

                for (;;) {
                    if (bitShiftInChar == 1) {
                        if (start == end)
                            break;
                        digit = s.charAt(start++);
                        if ('0' <= digit && digit <= '9')
                            digit -= '0';
                        else if ('a' <= digit && digit <= 'z')
                            digit -= 'a' - 10;
                        else
                            digit -= 'A' - 10;
                        bitShiftInChar = radix;
                    }
                    bitShiftInChar >>= 1;
                    boolean bit = (digit & bitShiftInChar) != 0;

                    switch (state) {
                        case SKIP_LEADING_ZEROS:
                            if (bit) {
                                --exactBitsLimit;
                                sum = 1.0;
                                state = FIRST_EXACT_53_BITS;
                            }
                            break;
                        case FIRST_EXACT_53_BITS:
                            sum *= 2.0;
                            if (bit)
                                sum += 1.0;
                            --exactBitsLimit;
                            if (exactBitsLimit == 0) {
                                bit53 = bit;
                                state = AFTER_BIT_53;
                            }
                            break;
                        case AFTER_BIT_53:
                            bit54 = bit;
                            factor = 2.0;
                            state = ZEROS_AFTER_54;
                            break;
                        case ZEROS_AFTER_54:
                            if (bit) {
                                state = MIXED_AFTER_54;
                            }
                            // fallthrough
                        case MIXED_AFTER_54:
                            factor *= 2;
                            break;
                    }
                }
                switch (state) {
                    case SKIP_LEADING_ZEROS:
                        sum = 0.0;
                        break;
                    case FIRST_EXACT_53_BITS:
                    case AFTER_BIT_53:
                        // do nothing
                        break;
                    case ZEROS_AFTER_54:
                        // x1.1 -> x1 + 1 (round up)
                        // x0.1 -> x0 (round down)
                        if (bit54 & bit53)
                            sum += 1.0;
                        sum *= factor;
                        break;
                    case MIXED_AFTER_54:
                        // x.100...1.. -> x + 1 (round up)
                        // x.0anything -> x (round down)
                        if (bit54)
                            sum += 1.0;
                        sum *= factor;
                        break;
                }
            }
            /* We don't worry about inaccurate numbers for any other base. */
        }
        return sum;
    }

    // It is public so NativeRegExp can access it.
    public static boolean isJSLineTerminator(int c)
    {
        // Optimization for faster check for eol character:
        // they do not have 0xDFD0 bits set
        if ((c & 0xDFD0) != 0) {
            return false;
        }
        return c == '\n' || c == '\r' || c == 0x2028 || c == 0x2029;
    }
}
