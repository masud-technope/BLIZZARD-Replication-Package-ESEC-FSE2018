/**
 * $RCSfile$
 * $Revision$
 * $Date$
 *
 */
package org.jivesoftware.smack.util;

/**
 * <p>Encodes and decodes to and from Base64 notation.</p>
 * This code was obtained from <a href="http://iharder.net/base64">http://iharder.net/base64</a></p>
 *
 *
 * @author Robert Harder
 * @author rob@iharder.net
 * @version 2.2.1
 */
public class Base64 {

    /* ********  P U B L I C   F I E L D S  ******** */
    /** No options specified. Value is zero. */
    public static final int NO_OPTIONS = 0;

    /** Specify encoding. */
    public static final int ENCODE = 1;

    /** Specify decoding. */
    public static final int DECODE = 0;

    /** Specify that data should be gzip-compressed. */
    public static final int GZIP = 2;

    /** Don't break lines when encoding (violates strict Base64 specification) */
    public static final int DONT_BREAK_LINES = 8;

    /**
	 * Encode using Base64-like encoding that is URL- and Filename-safe as described
	 * in Section 4 of RFC3548:
	 * <a href="http://www.faqs.org/rfcs/rfc3548.html">http://www.faqs.org/rfcs/rfc3548.html</a>.
	 * It is important to note that data encoded this way is <em>not</em> officially valid Base64,
	 * or at the very least should not be called Base64 without also specifying that is
	 * was encoded using the URL- and Filename-safe dialect.
	 */
    public static final int URL_SAFE = 16;

    /**
	  * Encode using the special "ordered" dialect of Base64 described here:
	  * <a href="http://www.faqs.org/qa/rfcc-1940.html">http://www.faqs.org/qa/rfcc-1940.html</a>.
	  */
    public static final int ORDERED = 32;

    /* ********  P R I V A T E   F I E L D S  ******** */
    /** Maximum line length (76) of Base64 output. */
    private static final int MAX_LINE_LENGTH = 76;

    /** The equals sign (=) as a byte. */
    private static final byte EQUALS_SIGN = (byte) '=';

    /** The new line character (\n) as a byte. */
    private static final byte NEW_LINE = (byte) '\n';

    /** Preferred encoding. */
    private static final String PREFERRED_ENCODING = "UTF-8";

    // I think I end up not using the BAD_ENCODING indicator.
    //private final static byte BAD_ENCODING    = -9; // Indicates error in encoding
    // Indicates white space in encoding
    private static final byte WHITE_SPACE_ENC = -5;

    // Indicates equals sign in encoding
    private static final byte EQUALS_SIGN_ENC = -1;

    /* ********  S T A N D A R D   B A S E 6 4   A L P H A B E T  ******** */
    /** The 64 valid Base64 values. */
    //private final static byte[] ALPHABET;
    /* Host platform me be something funny like EBCDIC, so we hardcode these values. */
    private static final byte[] _STANDARD_ALPHABET = { (byte) 'A', (byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F', (byte) 'G', (byte) 'H', (byte) 'I', (byte) 'J', (byte) 'K', (byte) 'L', (byte) 'M', (byte) 'N', (byte) 'O', (byte) 'P', (byte) 'Q', (byte) 'R', (byte) 'S', (byte) 'T', (byte) 'U', (byte) 'V', (byte) 'W', (byte) 'X', (byte) 'Y', (byte) 'Z', (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f', (byte) 'g', (byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k', (byte) 'l', (byte) 'm', (byte) 'n', (byte) 'o', (byte) 'p', (byte) 'q', (byte) 'r', (byte) 's', (byte) 't', (byte) 'u', (byte) 'v', (byte) 'w', (byte) 'x', (byte) 'y', (byte) 'z', (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5', (byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) '+', (byte) '/' };

    /**
     * Translates a Base64 value to either its 6-bit reconstruction value
     * or a negative number indicating some other meaning.
     **/
    private static final byte[] _STANDARD_DECODABET = { // Decimal  0 -  8
    -9, // Decimal  0 -  8
    -9, // Decimal  0 -  8
    -9, // Decimal  0 -  8
    -9, // Decimal  0 -  8
    -9, // Decimal  0 -  8
    -9, // Decimal  0 -  8
    -9, // Decimal  0 -  8
    -9, // Decimal  0 -  8
    -9, // Whitespace: Tab and Linefeed
    -5, // Whitespace: Tab and Linefeed
    -5, // Decimal 11 - 12
    -9, // Decimal 11 - 12
    -9, // Whitespace: Carriage Return
    -5, // Decimal 14 - 26
    -9, // Decimal 14 - 26
    -9, // Decimal 14 - 26
    -9, // Decimal 14 - 26
    -9, // Decimal 14 - 26
    -9, // Decimal 14 - 26
    -9, // Decimal 14 - 26
    -9, // Decimal 14 - 26
    -9, // Decimal 14 - 26
    -9, // Decimal 14 - 26
    -9, // Decimal 14 - 26
    -9, // Decimal 14 - 26
    -9, // Decimal 14 - 26
    -9, // Decimal 27 - 31
    -9, // Decimal 27 - 31
    -9, // Decimal 27 - 31
    -9, // Decimal 27 - 31
    -9, // Decimal 27 - 31
    -9, // Whitespace: Space
    -5, // Decimal 33 - 42
    -9, // Decimal 33 - 42
    -9, // Decimal 33 - 42
    -9, // Decimal 33 - 42
    -9, // Decimal 33 - 42
    -9, // Decimal 33 - 42
    -9, // Decimal 33 - 42
    -9, // Decimal 33 - 42
    -9, // Decimal 33 - 42
    -9, // Decimal 33 - 42
    -9, // Plus sign at decimal 43
    62, // Decimal 44 - 46
    -9, // Decimal 44 - 46
    -9, // Decimal 44 - 46
    -9, // Slash at decimal 47
    63, // Numbers zero through nine
    52, // Numbers zero through nine
    53, // Numbers zero through nine
    54, // Numbers zero through nine
    55, // Numbers zero through nine
    56, // Numbers zero through nine
    57, // Numbers zero through nine
    58, // Numbers zero through nine
    59, // Numbers zero through nine
    60, // Numbers zero through nine
    61, // Decimal 58 - 60
    -9, // Decimal 58 - 60
    -9, // Decimal 58 - 60
    -9, // Equals sign at decimal 61
    -1, // Decimal 62 - 64
    -9, // Decimal 62 - 64
    -9, // Decimal 62 - 64
    -9, // Letters 'A' through 'N'
    0, // Letters 'A' through 'N'
    1, // Letters 'A' through 'N'
    2, // Letters 'A' through 'N'
    3, // Letters 'A' through 'N'
    4, // Letters 'A' through 'N'
    5, // Letters 'A' through 'N'
    6, // Letters 'A' through 'N'
    7, // Letters 'A' through 'N'
    8, // Letters 'A' through 'N'
    9, // Letters 'A' through 'N'
    10, // Letters 'A' through 'N'
    11, // Letters 'A' through 'N'
    12, // Letters 'A' through 'N'
    13, // Letters 'O' through 'Z'
    14, // Letters 'O' through 'Z'
    15, // Letters 'O' through 'Z'
    16, // Letters 'O' through 'Z'
    17, // Letters 'O' through 'Z'
    18, // Letters 'O' through 'Z'
    19, // Letters 'O' through 'Z'
    20, // Letters 'O' through 'Z'
    21, // Letters 'O' through 'Z'
    22, // Letters 'O' through 'Z'
    23, // Letters 'O' through 'Z'
    24, // Letters 'O' through 'Z'
    25, // Decimal 91 - 96
    -9, // Decimal 91 - 96
    -9, // Decimal 91 - 96
    -9, // Decimal 91 - 96
    -9, // Decimal 91 - 96
    -9, // Decimal 91 - 96
    -9, // Letters 'a' through 'm'
    26, // Letters 'a' through 'm'
    27, // Letters 'a' through 'm'
    28, // Letters 'a' through 'm'
    29, // Letters 'a' through 'm'
    30, // Letters 'a' through 'm'
    31, // Letters 'a' through 'm'
    32, // Letters 'a' through 'm'
    33, // Letters 'a' through 'm'
    34, // Letters 'a' through 'm'
    35, // Letters 'a' through 'm'
    36, // Letters 'a' through 'm'
    37, // Letters 'a' through 'm'
    38, // Letters 'n' through 'z'
    39, // Letters 'n' through 'z'
    40, // Letters 'n' through 'z'
    41, // Letters 'n' through 'z'
    42, // Letters 'n' through 'z'
    43, // Letters 'n' through 'z'
    44, // Letters 'n' through 'z'
    45, // Letters 'n' through 'z'
    46, // Letters 'n' through 'z'
    47, // Letters 'n' through 'z'
    48, // Letters 'n' through 'z'
    49, // Letters 'n' through 'z'
    50, // Letters 'n' through 'z'
    51, // Decimal 123 - 126
    -9, // Decimal 123 - 126
    -9, // Decimal 123 - 126
    -9, // Decimal 123 - 126
    -9 };

    /* ********  U R L   S A F E   B A S E 6 4   A L P H A B E T  ******** */
    /**
	 * Used in the URL- and Filename-safe dialect described in Section 4 of RFC3548:
	 * <a href="http://www.faqs.org/rfcs/rfc3548.html">http://www.faqs.org/rfcs/rfc3548.html</a>.
	 * Notice that the last two bytes become "hyphen" and "underscore" instead of "plus" and "slash."
	 */
    private static final byte[] _URL_SAFE_ALPHABET = { (byte) 'A', (byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F', (byte) 'G', (byte) 'H', (byte) 'I', (byte) 'J', (byte) 'K', (byte) 'L', (byte) 'M', (byte) 'N', (byte) 'O', (byte) 'P', (byte) 'Q', (byte) 'R', (byte) 'S', (byte) 'T', (byte) 'U', (byte) 'V', (byte) 'W', (byte) 'X', (byte) 'Y', (byte) 'Z', (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f', (byte) 'g', (byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k', (byte) 'l', (byte) 'm', (byte) 'n', (byte) 'o', (byte) 'p', (byte) 'q', (byte) 'r', (byte) 's', (byte) 't', (byte) 'u', (byte) 'v', (byte) 'w', (byte) 'x', (byte) 'y', (byte) 'z', (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5', (byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) '-', (byte) '_' };

    /**
	 * Used in decoding URL- and Filename-safe dialects of Base64.
	 */
    private static final byte[] _URL_SAFE_DECODABET = { // Decimal  0 -  8
    -9, // Decimal  0 -  8
    -9, // Decimal  0 -  8
    -9, // Decimal  0 -  8
    -9, // Decimal  0 -  8
    -9, // Decimal  0 -  8
    -9, // Decimal  0 -  8
    -9, // Decimal  0 -  8
    -9, // Decimal  0 -  8
    -9, // Whitespace: Tab and Linefeed
    -5, // Whitespace: Tab and Linefeed
    -5, // Decimal 11 - 12
    -9, // Decimal 11 - 12
    -9, // Whitespace: Carriage Return
    -5, // Decimal 14 - 26
    -9, // Decimal 14 - 26
    -9, // Decimal 14 - 26
    -9, // Decimal 14 - 26
    -9, // Decimal 14 - 26
    -9, // Decimal 14 - 26
    -9, // Decimal 14 - 26
    -9, // Decimal 14 - 26
    -9, // Decimal 14 - 26
    -9, // Decimal 14 - 26
    -9, // Decimal 14 - 26
    -9, // Decimal 14 - 26
    -9, // Decimal 14 - 26
    -9, // Decimal 27 - 31
    -9, // Decimal 27 - 31
    -9, // Decimal 27 - 31
    -9, // Decimal 27 - 31
    -9, // Decimal 27 - 31
    -9, // Whitespace: Space
    -5, // Decimal 33 - 42
    -9, // Decimal 33 - 42
    -9, // Decimal 33 - 42
    -9, // Decimal 33 - 42
    -9, // Decimal 33 - 42
    -9, // Decimal 33 - 42
    -9, // Decimal 33 - 42
    -9, // Decimal 33 - 42
    -9, // Decimal 33 - 42
    -9, // Decimal 33 - 42
    -9, // Plus sign at decimal 43
    -9, // Decimal 44
    -9, // Minus sign at decimal 45
    62, // Decimal 46
    -9, // Slash at decimal 47
    -9, // Numbers zero through nine
    52, // Numbers zero through nine
    53, // Numbers zero through nine
    54, // Numbers zero through nine
    55, // Numbers zero through nine
    56, // Numbers zero through nine
    57, // Numbers zero through nine
    58, // Numbers zero through nine
    59, // Numbers zero through nine
    60, // Numbers zero through nine
    61, // Decimal 58 - 60
    -9, // Decimal 58 - 60
    -9, // Decimal 58 - 60
    -9, // Equals sign at decimal 61
    -1, // Decimal 62 - 64
    -9, // Decimal 62 - 64
    -9, // Decimal 62 - 64
    -9, // Letters 'A' through 'N'
    0, // Letters 'A' through 'N'
    1, // Letters 'A' through 'N'
    2, // Letters 'A' through 'N'
    3, // Letters 'A' through 'N'
    4, // Letters 'A' through 'N'
    5, // Letters 'A' through 'N'
    6, // Letters 'A' through 'N'
    7, // Letters 'A' through 'N'
    8, // Letters 'A' through 'N'
    9, // Letters 'A' through 'N'
    10, // Letters 'A' through 'N'
    11, // Letters 'A' through 'N'
    12, // Letters 'A' through 'N'
    13, // Letters 'O' through 'Z'
    14, // Letters 'O' through 'Z'
    15, // Letters 'O' through 'Z'
    16, // Letters 'O' through 'Z'
    17, // Letters 'O' through 'Z'
    18, // Letters 'O' through 'Z'
    19, // Letters 'O' through 'Z'
    20, // Letters 'O' through 'Z'
    21, // Letters 'O' through 'Z'
    22, // Letters 'O' through 'Z'
    23, // Letters 'O' through 'Z'
    24, // Letters 'O' through 'Z'
    25, // Decimal 91 - 94
    -9, // Decimal 91 - 94
    -9, // Decimal 91 - 94
    -9, // Decimal 91 - 94
    -9, // Underscore at decimal 95
    63, // Decimal 96
    -9, // Letters 'a' through 'm'
    26, // Letters 'a' through 'm'
    27, // Letters 'a' through 'm'
    28, // Letters 'a' through 'm'
    29, // Letters 'a' through 'm'
    30, // Letters 'a' through 'm'
    31, // Letters 'a' through 'm'
    32, // Letters 'a' through 'm'
    33, // Letters 'a' through 'm'
    34, // Letters 'a' through 'm'
    35, // Letters 'a' through 'm'
    36, // Letters 'a' through 'm'
    37, // Letters 'a' through 'm'
    38, // Letters 'n' through 'z'
    39, // Letters 'n' through 'z'
    40, // Letters 'n' through 'z'
    41, // Letters 'n' through 'z'
    42, // Letters 'n' through 'z'
    43, // Letters 'n' through 'z'
    44, // Letters 'n' through 'z'
    45, // Letters 'n' through 'z'
    46, // Letters 'n' through 'z'
    47, // Letters 'n' through 'z'
    48, // Letters 'n' through 'z'
    49, // Letters 'n' through 'z'
    50, // Letters 'n' through 'z'
    51, // Decimal 123 - 126
    -9, // Decimal 123 - 126
    -9, // Decimal 123 - 126
    -9, // Decimal 123 - 126
    -9 };

    /* ********  O R D E R E D   B A S E 6 4   A L P H A B E T  ******** */
    /**
	 * I don't get the point of this technique, but it is described here:
	 * <a href="http://www.faqs.org/qa/rfcc-1940.html">http://www.faqs.org/qa/rfcc-1940.html</a>.
	 */
    private static final byte[] _ORDERED_ALPHABET = { (byte) '-', (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5', (byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) 'A', (byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F', (byte) 'G', (byte) 'H', (byte) 'I', (byte) 'J', (byte) 'K', (byte) 'L', (byte) 'M', (byte) 'N', (byte) 'O', (byte) 'P', (byte) 'Q', (byte) 'R', (byte) 'S', (byte) 'T', (byte) 'U', (byte) 'V', (byte) 'W', (byte) 'X', (byte) 'Y', (byte) 'Z', (byte) '_', (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f', (byte) 'g', (byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k', (byte) 'l', (byte) 'm', (byte) 'n', (byte) 'o', (byte) 'p', (byte) 'q', (byte) 'r', (byte) 's', (byte) 't', (byte) 'u', (byte) 'v', (byte) 'w', (byte) 'x', (byte) 'y', (byte) 'z' };

    /**
	 * Used in decoding the "ordered" dialect of Base64.
	 */
    private static final byte[] _ORDERED_DECODABET = { // Decimal  0 -  8
    -9, // Decimal  0 -  8
    -9, // Decimal  0 -  8
    -9, // Decimal  0 -  8
    -9, // Decimal  0 -  8
    -9, // Decimal  0 -  8
    -9, // Decimal  0 -  8
    -9, // Decimal  0 -  8
    -9, // Decimal  0 -  8
    -9, // Whitespace: Tab and Linefeed
    -5, // Whitespace: Tab and Linefeed
    -5, // Decimal 11 - 12
    -9, // Decimal 11 - 12
    -9, // Whitespace: Carriage Return
    -5, // Decimal 14 - 26
    -9, // Decimal 14 - 26
    -9, // Decimal 14 - 26
    -9, // Decimal 14 - 26
    -9, // Decimal 14 - 26
    -9, // Decimal 14 - 26
    -9, // Decimal 14 - 26
    -9, // Decimal 14 - 26
    -9, // Decimal 14 - 26
    -9, // Decimal 14 - 26
    -9, // Decimal 14 - 26
    -9, // Decimal 14 - 26
    -9, // Decimal 14 - 26
    -9, // Decimal 27 - 31
    -9, // Decimal 27 - 31
    -9, // Decimal 27 - 31
    -9, // Decimal 27 - 31
    -9, // Decimal 27 - 31
    -9, // Whitespace: Space
    -5, // Decimal 33 - 42
    -9, // Decimal 33 - 42
    -9, // Decimal 33 - 42
    -9, // Decimal 33 - 42
    -9, // Decimal 33 - 42
    -9, // Decimal 33 - 42
    -9, // Decimal 33 - 42
    -9, // Decimal 33 - 42
    -9, // Decimal 33 - 42
    -9, // Decimal 33 - 42
    -9, // Plus sign at decimal 43
    -9, // Decimal 44
    -9, // Minus sign at decimal 45
    0, // Decimal 46
    -9, // Slash at decimal 47
    -9, // Numbers zero through nine
    1, // Numbers zero through nine
    2, // Numbers zero through nine
    3, // Numbers zero through nine
    4, // Numbers zero through nine
    5, // Numbers zero through nine
    6, // Numbers zero through nine
    7, // Numbers zero through nine
    8, // Numbers zero through nine
    9, // Numbers zero through nine
    10, // Decimal 58 - 60
    -9, // Decimal 58 - 60
    -9, // Decimal 58 - 60
    -9, // Equals sign at decimal 61
    -1, // Decimal 62 - 64
    -9, // Decimal 62 - 64
    -9, // Decimal 62 - 64
    -9, // Letters 'A' through 'M'
    11, // Letters 'A' through 'M'
    12, // Letters 'A' through 'M'
    13, // Letters 'A' through 'M'
    14, // Letters 'A' through 'M'
    15, // Letters 'A' through 'M'
    16, // Letters 'A' through 'M'
    17, // Letters 'A' through 'M'
    18, // Letters 'A' through 'M'
    19, // Letters 'A' through 'M'
    20, // Letters 'A' through 'M'
    21, // Letters 'A' through 'M'
    22, // Letters 'A' through 'M'
    23, // Letters 'N' through 'Z'
    24, // Letters 'N' through 'Z'
    25, // Letters 'N' through 'Z'
    26, // Letters 'N' through 'Z'
    27, // Letters 'N' through 'Z'
    28, // Letters 'N' through 'Z'
    29, // Letters 'N' through 'Z'
    30, // Letters 'N' through 'Z'
    31, // Letters 'N' through 'Z'
    32, // Letters 'N' through 'Z'
    33, // Letters 'N' through 'Z'
    34, // Letters 'N' through 'Z'
    35, // Letters 'N' through 'Z'
    36, // Decimal 91 - 94
    -9, // Decimal 91 - 94
    -9, // Decimal 91 - 94
    -9, // Decimal 91 - 94
    -9, // Underscore at decimal 95
    37, // Decimal 96
    -9, // Letters 'a' through 'm'
    38, // Letters 'a' through 'm'
    39, // Letters 'a' through 'm'
    40, // Letters 'a' through 'm'
    41, // Letters 'a' through 'm'
    42, // Letters 'a' through 'm'
    43, // Letters 'a' through 'm'
    44, // Letters 'a' through 'm'
    45, // Letters 'a' through 'm'
    46, // Letters 'a' through 'm'
    47, // Letters 'a' through 'm'
    48, // Letters 'a' through 'm'
    49, // Letters 'a' through 'm'
    50, // Letters 'n' through 'z'
    51, // Letters 'n' through 'z'
    52, // Letters 'n' through 'z'
    53, // Letters 'n' through 'z'
    54, // Letters 'n' through 'z'
    55, // Letters 'n' through 'z'
    56, // Letters 'n' through 'z'
    57, // Letters 'n' through 'z'
    58, // Letters 'n' through 'z'
    59, // Letters 'n' through 'z'
    60, // Letters 'n' through 'z'
    61, // Letters 'n' through 'z'
    62, // Letters 'n' through 'z'
    63, // Decimal 123 - 126
    -9, // Decimal 123 - 126
    -9, // Decimal 123 - 126
    -9, // Decimal 123 - 126
    -9 };

    /* ********  D E T E R M I N E   W H I C H   A L H A B E T  ******** */
    /**
	 * Returns one of the _SOMETHING_ALPHABET byte arrays depending on
	 * the options specified.
	 * It's possible, though silly, to specify ORDERED and URLSAFE
	 * in which case one of them will be picked, though there is
	 * no guarantee as to which one will be picked.
	 */
    private static final byte[] getAlphabet(int options) {
        if ((options & URL_SAFE) == URL_SAFE)
            return _URL_SAFE_ALPHABET;
        else if ((options & ORDERED) == ORDERED)
            return _ORDERED_ALPHABET;
        else
            return _STANDARD_ALPHABET;
    }

    // end getAlphabet
    /**
	 * Returns one of the _SOMETHING_DECODABET byte arrays depending on
	 * the options specified.
	 * It's possible, though silly, to specify ORDERED and URL_SAFE
	 * in which case one of them will be picked, though there is
	 * no guarantee as to which one will be picked.
	 */
    private static final byte[] getDecodabet(int options) {
        if ((options & URL_SAFE) == URL_SAFE)
            return _URL_SAFE_DECODABET;
        else if ((options & ORDERED) == ORDERED)
            return _ORDERED_DECODABET;
        else
            return _STANDARD_DECODABET;
    }

    /** Defeats instantiation. */
    private  Base64() {
    }

    /**
     * Prints command line usage.
     *
     * @param msg A message to include with usage info.
     */
    private static final void usage(String msg) {
        System.err.println(msg);
        System.err.println("Usage: java Base64 -e|-d inputfile outputfile");
    }

    // end usage
    /* ********  E N C O D I N G   M E T H O D S  ******** */
    /**
     * Encodes up to the first three bytes of array <var>threeBytes</var>
     * and returns a four-byte array in Base64 notation.
     * The actual number of significant bytes in your array is
     * given by <var>numSigBytes</var>.
     * The array <var>threeBytes</var> needs only be as big as
     * <var>numSigBytes</var>.
     * Code can reuse a byte array by passing a four-byte array as <var>b4</var>.
     *
     * @param b4 A reusable byte array to reduce array instantiation
     * @param threeBytes the array to convert
     * @param numSigBytes the number of significant bytes in your array
     * @return four byte array in Base64 notation.
     * @since 1.5.1
     */
    private static byte[] encode3to4(byte[] b4, byte[] threeBytes, int numSigBytes, int options) {
        encode3to4(threeBytes, 0, numSigBytes, b4, 0, options);
        return b4;
    }

    // end encode3to4
    /**
     * <p>Encodes up to three bytes of the array <var>source</var>
     * and writes the resulting four Base64 bytes to <var>destination</var>.
     * The source and destination arrays can be manipulated
     * anywhere along their length by specifying
     * <var>srcOffset</var> and <var>destOffset</var>.
     * This method does not check to make sure your arrays
     * are large enough to accomodate <var>srcOffset</var> + 3 for
     * the <var>source</var> array or <var>destOffset</var> + 4 for
     * the <var>destination</var> array.
     * The actual number of significant bytes in your array is
     * given by <var>numSigBytes</var>.</p>
	 * <p>This is the lowest level of the encoding methods with
	 * all possible parameters.</p>
     *
     * @param source the array to convert
     * @param srcOffset the index where conversion begins
     * @param numSigBytes the number of significant bytes in your array
     * @param destination the array to hold the conversion
     * @param destOffset the index where output will be put
     * @return the <var>destination</var> array
     * @since 1.3
     */
    private static byte[] encode3to4(byte[] source, int srcOffset, int numSigBytes, byte[] destination, int destOffset, int options) {
        byte[] ALPHABET = getAlphabet(options);
        //           1         2         3
        // 01234567890123456789012345678901 Bit position
        // --------000000001111111122222222 Array position from threeBytes
        // --------|    ||    ||    ||    | Six bit groups to index ALPHABET
        //          >>18  >>12  >> 6  >> 0  Right shift necessary
        //                0x3f  0x3f  0x3f  Additional AND
        // Create buffer with zero-padding if there are only one or two
        // significant bytes passed in the array.
        // We have to shift left 24 in order to flush out the 1's that appear
        // when Java treats a value as negative that is cast from a byte to an int.
        int inBuff = (numSigBytes > 0 ? ((source[srcOffset] << 24) >>> 8) : 0) | (numSigBytes > 1 ? ((source[srcOffset + 1] << 24) >>> 16) : 0) | (numSigBytes > 2 ? ((source[srcOffset + 2] << 24) >>> 24) : 0);
        switch(numSigBytes) {
            case 3:
                destination[destOffset] = ALPHABET[(inBuff >>> 18)];
                destination[destOffset + 1] = ALPHABET[(inBuff >>> 12) & 0x3f];
                destination[destOffset + 2] = ALPHABET[(inBuff >>> 6) & 0x3f];
                destination[destOffset + 3] = ALPHABET[(inBuff) & 0x3f];
                return destination;
            case 2:
                destination[destOffset] = ALPHABET[(inBuff >>> 18)];
                destination[destOffset + 1] = ALPHABET[(inBuff >>> 12) & 0x3f];
                destination[destOffset + 2] = ALPHABET[(inBuff >>> 6) & 0x3f];
                destination[destOffset + 3] = EQUALS_SIGN;
                return destination;
            case 1:
                destination[destOffset] = ALPHABET[(inBuff >>> 18)];
                destination[destOffset + 1] = ALPHABET[(inBuff >>> 12) & 0x3f];
                destination[destOffset + 2] = EQUALS_SIGN;
                destination[destOffset + 3] = EQUALS_SIGN;
                return destination;
            default:
                return destination;
        }
    // end switch
    }

    // end encode3to4
    /**
     * Serializes an object and returns the Base64-encoded
     * version of that serialized object. If the object
     * cannot be serialized or there is another error,
     * the method will return <tt>null</tt>.
     * The object is not GZip-compressed before being encoded.
     *
     * @param serializableObject The object to encode
     * @return The Base64-encoded object
     * @since 1.4
     */
    public static String encodeObject(java.io.Serializable serializableObject) {
        return encodeObject(serializableObject, NO_OPTIONS);
    }

    // end encodeObject
    /**
     * Serializes an object and returns the Base64-encoded
     * version of that serialized object. If the object
     * cannot be serialized or there is another error,
     * the method will return <tt>null</tt>.
     * <p>
     * Valid options:<pre>
     *   GZIP: gzip-compresses object before encoding it.
     *   DONT_BREAK_LINES: don't break lines at 76 characters
     *     <i>Note: Technically, this makes your encoding non-compliant.</i>
     * </pre>
     * <p>
     * Example: <code>encodeObject( myObj, Base64.GZIP )</code> or
     * <p>
     * Example: <code>encodeObject( myObj, Base64.GZIP | Base64.DONT_BREAK_LINES )</code>
     *
     * @param serializableObject The object to encode
     * @param options Specified options
     * @return The Base64-encoded object
     * @see Base64#GZIP
     * @see Base64#DONT_BREAK_LINES
     * @since 2.0
     */
    public static String encodeObject(java.io.Serializable serializableObject, int options) {
        // Streams
        java.io.ByteArrayOutputStream baos = null;
        java.io.OutputStream b64os = null;
        java.io.ObjectOutputStream oos = null;
        java.util.zip.GZIPOutputStream gzos = null;
        // Isolate options
        int gzip = (options & GZIP);
        int dontBreakLines = (options & DONT_BREAK_LINES);
        try {
            // ObjectOutputStream -> (GZIP) -> Base64 -> ByteArrayOutputStream
            baos = new java.io.ByteArrayOutputStream();
            b64os = new Base64.OutputStream(baos, ENCODE | options);
            // GZip?
            if (gzip == GZIP) {
                gzos = new java.util.zip.GZIPOutputStream(b64os);
                oos = new java.io.ObjectOutputStream(gzos);
            } else
                // end if: gzip
                oos = new java.io.ObjectOutputStream(b64os);
            oos.writeObject(serializableObject);
        }// end try
         catch (java.io.IOException e) {
            e.printStackTrace();
            return null;
        } finally // end catch
        {
            try {
                oos.close();
            } catch (Exception e) {
            }
            try {
                gzos.close();
            } catch (Exception e) {
            }
            try {
                b64os.close();
            } catch (Exception e) {
            }
            try {
                baos.close();
            } catch (Exception e) {
            }
        }
        // Return value according to relevant encoding.
        try {
            return new String(baos.toByteArray(), PREFERRED_ENCODING);
        }// end try
         catch (java.io.UnsupportedEncodingException uue) {
            return new String(baos.toByteArray());
        }
    // end catch
    }

    // end encode
    /**
     * Encodes a byte array into Base64 notation.
     * Does not GZip-compress data.
     *
     * @param source The data to convert
     * @since 1.4
     */
    public static String encodeBytes(byte[] source) {
        return encodeBytes(source, 0, source.length, NO_OPTIONS);
    }

    // end encodeBytes
    /**
     * Encodes a byte array into Base64 notation.
     * <p>
     * Valid options:<pre>
     *   GZIP: gzip-compresses object before encoding it.
     *   DONT_BREAK_LINES: don't break lines at 76 characters
     *     <i>Note: Technically, this makes your encoding non-compliant.</i>
     * </pre>
     * <p>
     * Example: <code>encodeBytes( myData, Base64.GZIP )</code> or
     * <p>
     * Example: <code>encodeBytes( myData, Base64.GZIP | Base64.DONT_BREAK_LINES )</code>
     *
     *
     * @param source The data to convert
     * @param options Specified options
     * @see Base64#GZIP
     * @see Base64#DONT_BREAK_LINES
     * @since 2.0
     */
    public static String encodeBytes(byte[] source, int options) {
        return encodeBytes(source, 0, source.length, options);
    }

    // end encodeBytes
    /**
     * Encodes a byte array into Base64 notation.
     * Does not GZip-compress data.
     *
     * @param source The data to convert
     * @param off Offset in array where conversion should begin
     * @param len Length of data to convert
     * @since 1.4
     */
    public static String encodeBytes(byte[] source, int off, int len) {
        return encodeBytes(source, off, len, NO_OPTIONS);
    }

    // end encodeBytes
    /**
     * Encodes a byte array into Base64 notation.
     * <p>
     * Valid options:<pre>
     *   GZIP: gzip-compresses object before encoding it.
     *   DONT_BREAK_LINES: don't break lines at 76 characters
     *     <i>Note: Technically, this makes your encoding non-compliant.</i>
     * </pre>
     * <p>
     * Example: <code>encodeBytes( myData, Base64.GZIP )</code> or
     * <p>
     * Example: <code>encodeBytes( myData, Base64.GZIP | Base64.DONT_BREAK_LINES )</code>
     *
     *
     * @param source The data to convert
     * @param off Offset in array where conversion should begin
     * @param len Length of data to convert
     * @param options Specified options; alphabet type is pulled from this (standard, url-safe, ordered)
     * @see Base64#GZIP
     * @see Base64#DONT_BREAK_LINES
     * @since 2.0
     */
    public static String encodeBytes(byte[] source, int off, int len, int options) {
        // Isolate options
        int dontBreakLines = (options & DONT_BREAK_LINES);
        int gzip = (options & GZIP);
        // Compress?
        if (gzip == GZIP) {
            java.io.ByteArrayOutputStream baos = null;
            java.util.zip.GZIPOutputStream gzos = null;
            Base64.OutputStream b64os = null;
            try {
                // GZip -> Base64 -> ByteArray
                baos = new java.io.ByteArrayOutputStream();
                b64os = new Base64.OutputStream(baos, ENCODE | options);
                gzos = new java.util.zip.GZIPOutputStream(b64os);
                gzos.write(source, off, len);
                gzos.close();
            }// end try
             catch (java.io.IOException e) {
                e.printStackTrace();
                return null;
            } finally // end catch
            {
                try {
                    gzos.close();
                } catch (Exception e) {
                }
                try {
                    b64os.close();
                } catch (Exception e) {
                }
                try {
                    baos.close();
                } catch (Exception e) {
                }
            }
            // Return value according to relevant encoding.
            try {
                return new String(baos.toByteArray(), PREFERRED_ENCODING);
            }// end try
             catch (java.io.UnsupportedEncodingException uue) {
                return new String(baos.toByteArray());
            }
        // end catch
        } else // end if: compress
        // Else, don't compress. Better not to use streams at all then.
        {
            // Convert option to boolean in way that code likes it.
            boolean breakLines = dontBreakLines == 0;
            int len43 = len * 4 / 3;
            byte[] outBuff = new byte[// Main 4:3
            (len43) + // Account for padding
            ((len % 3) > 0 ? 4 : 0) + // New lines
            (breakLines ? (len43 / MAX_LINE_LENGTH) : 0)];
            int d = 0;
            int e = 0;
            int len2 = len - 2;
            int lineLength = 0;
            for (; d < len2; d += 3, e += 4) {
                encode3to4(source, d + off, 3, outBuff, e, options);
                lineLength += 4;
                if (breakLines && lineLength == MAX_LINE_LENGTH) {
                    outBuff[e + 4] = NEW_LINE;
                    e++;
                    lineLength = 0;
                }
            // end if: end of line
            }
            if (d < len) {
                encode3to4(source, d + off, len - d, outBuff, e, options);
                e += 4;
            }
            // Return value according to relevant encoding.
            try {
                return new String(outBuff, 0, e, PREFERRED_ENCODING);
            }// end try
             catch (java.io.UnsupportedEncodingException uue) {
                return new String(outBuff, 0, e);
            }
        // end catch
        }
    // end else: don't compress
    }

    // end encodeBytes
    /* ********  D E C O D I N G   M E T H O D S  ******** */
    /**
     * Decodes four bytes from array <var>source</var>
     * and writes the resulting bytes (up to three of them)
     * to <var>destination</var>.
     * The source and destination arrays can be manipulated
     * anywhere along their length by specifying
     * <var>srcOffset</var> and <var>destOffset</var>.
     * This method does not check to make sure your arrays
     * are large enough to accomodate <var>srcOffset</var> + 4 for
     * the <var>source</var> array or <var>destOffset</var> + 3 for
     * the <var>destination</var> array.
     * This method returns the actual number of bytes that
     * were converted from the Base64 encoding.
	 * <p>This is the lowest level of the decoding methods with
	 * all possible parameters.</p>
     *
     *
     * @param source the array to convert
     * @param srcOffset the index where conversion begins
     * @param destination the array to hold the conversion
     * @param destOffset the index where output will be put
	 * @param options alphabet type is pulled from this (standard, url-safe, ordered)
     * @return the number of decoded bytes converted
     * @since 1.3
     */
    private static int decode4to3(byte[] source, int srcOffset, byte[] destination, int destOffset, int options) {
        byte[] DECODABET = getDecodabet(options);
        // Example: Dk==
        if (source[srcOffset + 2] == EQUALS_SIGN) {
            // Two ways to do the same thing. Don't know which way I like best.
            //int outBuff =   ( ( DECODABET[ source[ srcOffset    ] ] << 24 ) >>>  6 )
            //              | ( ( DECODABET[ source[ srcOffset + 1] ] << 24 ) >>> 12 );
            int outBuff = ((DECODABET[source[srcOffset]] & 0xFF) << 18) | ((DECODABET[source[srcOffset + 1]] & 0xFF) << 12);
            destination[destOffset] = (byte) (outBuff >>> 16);
            return 1;
        } else // Example: DkL=
        if (source[srcOffset + 3] == EQUALS_SIGN) {
            // Two ways to do the same thing. Don't know which way I like best.
            //int outBuff =   ( ( DECODABET[ source[ srcOffset     ] ] << 24 ) >>>  6 )
            //              | ( ( DECODABET[ source[ srcOffset + 1 ] ] << 24 ) >>> 12 )
            //              | ( ( DECODABET[ source[ srcOffset + 2 ] ] << 24 ) >>> 18 );
            int outBuff = ((DECODABET[source[srcOffset]] & 0xFF) << 18) | ((DECODABET[source[srcOffset + 1]] & 0xFF) << 12) | ((DECODABET[source[srcOffset + 2]] & 0xFF) << 6);
            destination[destOffset] = (byte) (outBuff >>> 16);
            destination[destOffset + 1] = (byte) (outBuff >>> 8);
            return 2;
        } else // Example: DkLE
        {
            try {
                // Two ways to do the same thing. Don't know which way I like best.
                //int outBuff =   ( ( DECODABET[ source[ srcOffset     ] ] << 24 ) >>>  6 )
                //              | ( ( DECODABET[ source[ srcOffset + 1 ] ] << 24 ) >>> 12 )
                //              | ( ( DECODABET[ source[ srcOffset + 2 ] ] << 24 ) >>> 18 )
                //              | ( ( DECODABET[ source[ srcOffset + 3 ] ] << 24 ) >>> 24 );
                int outBuff = ((DECODABET[source[srcOffset]] & 0xFF) << 18) | ((DECODABET[source[srcOffset + 1]] & 0xFF) << 12) | ((DECODABET[source[srcOffset + 2]] & 0xFF) << 6) | ((DECODABET[source[srcOffset + 3]] & 0xFF));
                destination[destOffset] = (byte) (outBuff >> 16);
                destination[destOffset + 1] = (byte) (outBuff >> 8);
                destination[destOffset + 2] = (byte) (outBuff);
                return 3;
            } catch (Exception e) {
                System.out.println("" + source[srcOffset] + ": " + (DECODABET[source[srcOffset]]));
                System.out.println("" + source[srcOffset + 1] + ": " + (DECODABET[source[srcOffset + 1]]));
                System.out.println("" + source[srcOffset + 2] + ": " + (DECODABET[source[srcOffset + 2]]));
                System.out.println("" + source[srcOffset + 3] + ": " + (DECODABET[source[srcOffset + 3]]));
                return -1;
            }
        // end catch
        }
    }

    // end decodeToBytes
    /**
     * Very low-level access to decoding ASCII characters in
     * the form of a byte array. Does not support automatically
     * gunzipping or any other "fancy" features.
     *
     * @param source The Base64 encoded data
     * @param off    The offset of where to begin decoding
     * @param len    The length of characters to decode
     * @return decoded data
     * @since 1.3
     */
    public static byte[] decode(byte[] source, int off, int len, int options) {
        byte[] DECODABET = getDecodabet(options);
        int len34 = len * 3 / 4;
        // Upper limit on size of output
        byte[] outBuff = new byte[len34];
        int outBuffPosn = 0;
        byte[] b4 = new byte[4];
        int b4Posn = 0;
        int i = 0;
        byte sbiCrop = 0;
        byte sbiDecode = 0;
        for (i = off; i < off + len; i++) {
            // Only the low seven bits
            sbiCrop = (byte) (source[i] & 0x7f);
            sbiDecode = DECODABET[sbiCrop];
            if (// White space, Equals sign or better
            sbiDecode >= WHITE_SPACE_ENC) {
                if (sbiDecode >= EQUALS_SIGN_ENC) {
                    b4[b4Posn++] = sbiCrop;
                    if (b4Posn > 3) {
                        outBuffPosn += decode4to3(b4, 0, outBuff, outBuffPosn, options);
                        b4Posn = 0;
                        // If that was the equals sign, break out of 'for' loop
                        if (sbiCrop == EQUALS_SIGN)
                            break;
                    }
                // end if: quartet built
                }
            // end if: equals sign or better
            } else // end if: white space, equals sign or better
            {
                System.err.println("Bad Base64 input character at " + i + ": " + source[i] + "(decimal)");
                return null;
            }
        // end else:
        }
        // each input character
        byte[] out = new byte[outBuffPosn];
        System.arraycopy(outBuff, 0, out, 0, outBuffPosn);
        return out;
    }

    // end decode
    /**
     * Decodes data from Base64 notation, automatically
     * detecting gzip-compressed data and decompressing it.
     *
     * @param s the string to decode
     * @return the decoded data
     * @since 1.4
     */
    public static byte[] decode(String s) {
        return decode(s, NO_OPTIONS);
    }

    /**
     * Decodes data from Base64 notation, automatically
     * detecting gzip-compressed data and decompressing it.
     *
     * @param s the string to decode
	 * @param options encode options such as URL_SAFE
     * @return the decoded data
     * @since 1.4
     */
    public static byte[] decode(String s, int options) {
        byte[] bytes;
        try {
            bytes = s.getBytes(PREFERRED_ENCODING);
        }// end try
         catch (java.io.UnsupportedEncodingException uee) {
            bytes = s.getBytes();
        }
        // end catch
        //</change>
        // Decode
        bytes = decode(bytes, 0, bytes.length, options);
        // GZIP Magic Two-Byte Number: 0x8b1f (35615)
        if (bytes != null && bytes.length >= 4) {
            int head = ((int) bytes[0] & 0xff) | ((bytes[1] << 8) & 0xff00);
            if (java.util.zip.GZIPInputStream.GZIP_MAGIC == head) {
                java.io.ByteArrayInputStream bais = null;
                java.util.zip.GZIPInputStream gzis = null;
                java.io.ByteArrayOutputStream baos = null;
                byte[] buffer = new byte[2048];
                int length = 0;
                try {
                    baos = new java.io.ByteArrayOutputStream();
                    bais = new java.io.ByteArrayInputStream(bytes);
                    gzis = new java.util.zip.GZIPInputStream(bais);
                    while ((length = gzis.read(buffer)) >= 0) {
                        baos.write(buffer, 0, length);
                    }
                    // end while: reading input
                    // No error? Get new bytes.
                    bytes = baos.toByteArray();
                }// end try
                 catch (java.io.IOException e) {
                } finally // end catch
                {
                    try {
                        baos.close();
                    } catch (Exception e) {
                    }
                    try {
                        gzis.close();
                    } catch (Exception e) {
                    }
                    try {
                        bais.close();
                    } catch (Exception e) {
                    }
                }
            // end finally
            }
        // end if: gzipped
        }
        return bytes;
    }

    // end decode
    /**
     * Attempts to decode Base64 data and deserialize a Java
     * Object within. Returns <tt>null</tt> if there was an error.
     *
     * @param encodedObject The Base64 data to decode
     * @return The decoded and deserialized object
     * @since 1.5
     */
    public static Object decodeToObject(String encodedObject) {
        // Decode and gunzip if necessary
        byte[] objBytes = decode(encodedObject);
        java.io.ByteArrayInputStream bais = null;
        java.io.ObjectInputStream ois = null;
        Object obj = null;
        try {
            bais = new java.io.ByteArrayInputStream(objBytes);
            ois = new java.io.ObjectInputStream(bais);
            obj = ois.readObject();
        }// end try
         catch (java.io.IOException e) {
            e.printStackTrace();
            obj = null;
        }// end catch
         catch (java.lang.ClassNotFoundException e) {
            e.printStackTrace();
            obj = null;
        } finally // end catch
        {
            try {
                bais.close();
            } catch (Exception e) {
            }
            try {
                ois.close();
            } catch (Exception e) {
            }
        }
        return obj;
    }

    // end decodeObject
    /**
     * Convenience method for encoding data to a file.
     *
     * @param dataToEncode byte array of data to encode in base64 form
     * @param filename Filename for saving encoded data
     * @return <tt>true</tt> if successful, <tt>false</tt> otherwise
     *
     * @since 2.1
     */
    public static boolean encodeToFile(byte[] dataToEncode, String filename) {
        boolean success = false;
        Base64.OutputStream bos = null;
        try {
            bos = new Base64.OutputStream(new java.io.FileOutputStream(filename), Base64.ENCODE);
            bos.write(dataToEncode);
            success = true;
        }// end try
         catch (java.io.IOException e) {
            success = false;
        } finally // end catch: IOException
        {
            try {
                bos.close();
            } catch (Exception e) {
            }
        }
        return success;
    }

    // end encodeToFile
    /**
     * Convenience method for decoding data to a file.
     *
     * @param dataToDecode Base64-encoded data as a string
     * @param filename Filename for saving decoded data
     * @return <tt>true</tt> if successful, <tt>false</tt> otherwise
     *
     * @since 2.1
     */
    public static boolean decodeToFile(String dataToDecode, String filename) {
        boolean success = false;
        Base64.OutputStream bos = null;
        try {
            bos = new Base64.OutputStream(new java.io.FileOutputStream(filename), Base64.DECODE);
            bos.write(dataToDecode.getBytes(PREFERRED_ENCODING));
            success = true;
        }// end try
         catch (java.io.IOException e) {
            success = false;
        } finally // end catch: IOException
        {
            try {
                bos.close();
            } catch (Exception e) {
            }
        }
        return success;
    }

    // end decodeToFile
    /**
     * Convenience method for reading a base64-encoded
     * file and decoding it.
     *
     * @param filename Filename for reading encoded data
     * @return decoded byte array or null if unsuccessful
     *
     * @since 2.1
     */
    public static byte[] decodeFromFile(String filename) {
        byte[] decodedData = null;
        Base64.InputStream bis = null;
        try {
            // Set up some useful variables
            java.io.File file = new java.io.File(filename);
            byte[] buffer = null;
            int length = 0;
            int numBytes = 0;
            // Check for size of file
            if (file.length() > Integer.MAX_VALUE) {
                System.err.println("File is too big for this convenience method (" + file.length() + " bytes).");
                return null;
            }
            // end if: file too big for int index
            buffer = new byte[(int) file.length()];
            // Open a stream
            bis = new Base64.InputStream(new java.io.BufferedInputStream(new java.io.FileInputStream(file)), Base64.DECODE);
            // Read until done
            while ((numBytes = bis.read(buffer, length, 4096)) >= 0) length += numBytes;
            // Save in a variable to return
            decodedData = new byte[length];
            System.arraycopy(buffer, 0, decodedData, 0, length);
        }// end try
         catch (java.io.IOException e) {
            System.err.println("Error decoding from file " + filename);
        } finally // end catch: IOException
        {
            try {
                bis.close();
            } catch (Exception e) {
            }
        }
        return decodedData;
    }

    // end decodeFromFile
    /**
     * Convenience method for reading a binary file
     * and base64-encoding it.
     *
     * @param filename Filename for reading binary data
     * @return base64-encoded string or null if unsuccessful
     *
     * @since 2.1
     */
    public static String encodeFromFile(String filename) {
        String encodedData = null;
        Base64.InputStream bis = null;
        try {
            // Set up some useful variables
            java.io.File file = new java.io.File(filename);
            // Need max() for math on small files (v2.2.1)
            byte[] buffer = new byte[Math.max((int) (file.length() * 1.4), 40)];
            int length = 0;
            int numBytes = 0;
            // Open a stream
            bis = new Base64.InputStream(new java.io.BufferedInputStream(new java.io.FileInputStream(file)), Base64.ENCODE);
            // Read until done
            while ((numBytes = bis.read(buffer, length, 4096)) >= 0) length += numBytes;
            // Save in a variable to return
            encodedData = new String(buffer, 0, length, Base64.PREFERRED_ENCODING);
        }// end try
         catch (java.io.IOException e) {
            System.err.println("Error encoding from file " + filename);
        } finally // end catch: IOException
        {
            try {
                bis.close();
            } catch (Exception e) {
            }
        }
        return encodedData;
    }

    // end encodeFromFile
    /**
     * Reads <tt>infile</tt> and encodes it to <tt>outfile</tt>.
     *
     * @param infile Input file
     * @param outfile Output file
     * @since 2.2
     */
    public static void encodeFileToFile(String infile, String outfile) {
        String encoded = Base64.encodeFromFile(infile);
        java.io.OutputStream out = null;
        try {
            out = new java.io.BufferedOutputStream(new java.io.FileOutputStream(outfile));
            // Strict, 7-bit output.
            out.write(encoded.getBytes("US-ASCII"));
        }// end try
         catch (java.io.IOException ex) {
            ex.printStackTrace();
        } finally // end catch
        {
            try {
                out.close();
            } catch (Exception ex) {
            }
        }
    // end finally
    }

    // end encodeFileToFile
    /**
     * Reads <tt>infile</tt> and decodes it to <tt>outfile</tt>.
     *
     * @param infile Input file
     * @param outfile Output file
     * @since 2.2
     */
    public static void decodeFileToFile(String infile, String outfile) {
        byte[] decoded = Base64.decodeFromFile(infile);
        java.io.OutputStream out = null;
        try {
            out = new java.io.BufferedOutputStream(new java.io.FileOutputStream(outfile));
            out.write(decoded);
        }// end try
         catch (java.io.IOException ex) {
            ex.printStackTrace();
        } finally // end catch
        {
            try {
                out.close();
            } catch (Exception ex) {
            }
        }
    // end finally
    }

    /**
     * A {@link Base64.InputStream} will read data from another
     * <tt>java.io.InputStream</tt>, given in the constructor,
     * and encode/decode to/from Base64 notation on the fly.
     *
     * @see Base64
     * @since 1.3
     */
    public static class InputStream extends java.io.FilterInputStream {

        // Encoding or decoding
        private boolean encode;

        // Current position in the buffer
        private int position;

        // Small buffer holding converted data
        private byte[] buffer;

        // Length of buffer (3 or 4)
        private int bufferLength;

        // Number of meaningful bytes in the buffer
        private int numSigBytes;

        private int lineLength;

        // Break lines at less than 80 characters
        private boolean breakLines;

        // Record options used to create the stream.
        private int options;

        // Local copies to avoid extra method calls
        private byte[] alphabet;

        // Local copies to avoid extra method calls
        private byte[] decodabet;

        /**
         * Constructs a {@link Base64.InputStream} in DECODE mode.
         *
         * @param in the <tt>java.io.InputStream</tt> from which to read data.
         * @since 1.3
         */
        public  InputStream(java.io.InputStream in) {
            this(in, DECODE);
        }

        /**
         * Constructs a {@link Base64.InputStream} in
         * either ENCODE or DECODE mode.
         * <p>
         * Valid options:<pre>
         *   ENCODE or DECODE: Encode or Decode as data is read.
         *   DONT_BREAK_LINES: don't break lines at 76 characters
         *     (only meaningful when encoding)
         *     <i>Note: Technically, this makes your encoding non-compliant.</i>
         * </pre>
         * <p>
         * Example: <code>new Base64.InputStream( in, Base64.DECODE )</code>
         *
         *
         * @param in the <tt>java.io.InputStream</tt> from which to read data.
         * @param options Specified options
         * @see Base64#ENCODE
         * @see Base64#DECODE
         * @see Base64#DONT_BREAK_LINES
         * @since 2.0
         */
        public  InputStream(java.io.InputStream in, int options) {
            super(in);
            this.breakLines = (options & DONT_BREAK_LINES) != DONT_BREAK_LINES;
            this.encode = (options & ENCODE) == ENCODE;
            this.bufferLength = encode ? 4 : 3;
            this.buffer = new byte[bufferLength];
            this.position = -1;
            this.lineLength = 0;
            // Record for later, mostly to determine which alphabet to use
            this.options = options;
            this.alphabet = getAlphabet(options);
            this.decodabet = getDecodabet(options);
        }

        // end constructor
        /**
         * Reads enough of the input stream to convert
         * to/from Base64 and returns the next byte.
         *
         * @return next byte
         * @since 1.3
         */
        public int read() throws java.io.IOException {
            // Do we need to get data?
            if (position < 0) {
                if (encode) {
                    byte[] b3 = new byte[3];
                    int numBinaryBytes = 0;
                    for (int i = 0; i < 3; i++) {
                        try {
                            int b = in.read();
                            // If end of stream, b is -1.
                            if (b >= 0) {
                                b3[i] = (byte) b;
                                numBinaryBytes++;
                            }
                        // end if: not end of stream
                        }// end try: read
                         catch (java.io.IOException e) {
                            if (i == 0)
                                throw e;
                        }
                    // end catch
                    }
                    if (numBinaryBytes > 0) {
                        encode3to4(b3, 0, numBinaryBytes, buffer, 0, options);
                        position = 0;
                        numSigBytes = 4;
                    } else // end if: got data
                    {
                        return -1;
                    }
                // end else
                } else // end if: encoding
                // Else decoding
                {
                    byte[] b4 = new byte[4];
                    int i = 0;
                    for (i = 0; i < 4; i++) {
                        // Read four "meaningful" bytes:
                        int b = 0;
                        do {
                            b = in.read();
                        } while (b >= 0 && decodabet[b & 0x7f] <= WHITE_SPACE_ENC);
                        if (b < 0)
                            // Reads a -1 if end of stream
                            break;
                        b4[i] = (byte) b;
                    }
                    if (i == 4) {
                        numSigBytes = decode4to3(b4, 0, buffer, 0, options);
                        position = 0;
                    } else // end if: got four characters
                    if (i == 0) {
                        return -1;
                    } else // end else if: also padded correctly
                    {
                        // Must have broken out from above.
                        throw new java.io.IOException("Improperly padded Base64 input.");
                    }
                // end
                }
            // end else: decode
            }
            // Got data?
            if (position >= 0) {
                // End of relevant data?
                if (/*!encode &&*/
                position >= numSigBytes)
                    return -1;
                if (encode && breakLines && lineLength >= MAX_LINE_LENGTH) {
                    lineLength = 0;
                    return '\n';
                } else // end if
                {
                    // This isn't important when decoding
                    lineLength++;
                    // but throwing an extra "if" seems
                    // just as wasteful.
                    int b = buffer[position++];
                    if (position >= bufferLength)
                        position = -1;
                    // This is how you "cast" a byte that's
                    return b & 0xFF;
                // intended to be unsigned.
                }
            // end else
            } else // end if: position >= 0
            // Else error
            {
                // When JDK1.4 is more accepted, use an assertion here.
                throw new java.io.IOException("Error in Base64 code reading stream.");
            }
        // end else
        }

        // end read
        /**
         * Calls {@link #read()} repeatedly until the end of stream
         * is reached or <var>len</var> bytes are read.
         * Returns number of bytes read into array or -1 if
         * end of stream is encountered.
         *
         * @param dest array to hold values
         * @param off offset for array
         * @param len max number of bytes to read into array
         * @return bytes read into array or -1 if end of stream is encountered.
         * @since 1.3
         */
        public int read(byte[] dest, int off, int len) throws java.io.IOException {
            int i;
            int b;
            for (i = 0; i < len; i++) {
                b = read();
                if (b >= 0)
                    dest[off + i] = (byte) b;
                else if (i == 0)
                    return -1;
                else
                    // Out of 'for' loop
                    break;
            }
            // end for: each byte read
            return i;
        }
        // end read
    }

    /**
     * A {@link Base64.OutputStream} will write data to another
     * <tt>java.io.OutputStream</tt>, given in the constructor,
     * and encode/decode to/from Base64 notation on the fly.
     *
     * @see Base64
     * @since 1.3
     */
    public static class OutputStream extends java.io.FilterOutputStream {

        private boolean encode;

        private int position;

        private byte[] buffer;

        private int bufferLength;

        private int lineLength;

        private boolean breakLines;

        // Scratch used in a few places
        private byte[] b4;

        private boolean suspendEncoding;

        // Record for later
        private int options;

        // Local copies to avoid extra method calls
        private byte[] alphabet;

        // Local copies to avoid extra method calls
        private byte[] decodabet;

        /**
         * Constructs a {@link Base64.OutputStream} in ENCODE mode.
         *
         * @param out the <tt>java.io.OutputStream</tt> to which data will be written.
         * @since 1.3
         */
        public  OutputStream(java.io.OutputStream out) {
            this(out, ENCODE);
        }

        /**
         * Constructs a {@link Base64.OutputStream} in
         * either ENCODE or DECODE mode.
         * <p>
         * Valid options:<pre>
         *   ENCODE or DECODE: Encode or Decode as data is read.
         *   DONT_BREAK_LINES: don't break lines at 76 characters
         *     (only meaningful when encoding)
         *     <i>Note: Technically, this makes your encoding non-compliant.</i>
         * </pre>
         * <p>
         * Example: <code>new Base64.OutputStream( out, Base64.ENCODE )</code>
         *
         * @param out the <tt>java.io.OutputStream</tt> to which data will be written.
         * @param options Specified options.
         * @see Base64#ENCODE
         * @see Base64#DECODE
         * @see Base64#DONT_BREAK_LINES
         * @since 1.3
         */
        public  OutputStream(java.io.OutputStream out, int options) {
            super(out);
            this.breakLines = (options & DONT_BREAK_LINES) != DONT_BREAK_LINES;
            this.encode = (options & ENCODE) == ENCODE;
            this.bufferLength = encode ? 3 : 4;
            this.buffer = new byte[bufferLength];
            this.position = 0;
            this.lineLength = 0;
            this.suspendEncoding = false;
            this.b4 = new byte[4];
            this.options = options;
            this.alphabet = getAlphabet(options);
            this.decodabet = getDecodabet(options);
        }

        // end constructor
        /**
         * Writes the byte to the output stream after
         * converting to/from Base64 notation.
         * When encoding, bytes are buffered three
         * at a time before the output stream actually
         * gets a write() call.
         * When decoding, bytes are buffered four
         * at a time.
         *
         * @param theByte the byte to write
         * @since 1.3
         */
        public void write(int theByte) throws java.io.IOException {
            // Encoding suspended?
            if (suspendEncoding) {
                super.out.write(theByte);
                return;
            }
            // Encode?
            if (encode) {
                buffer[position++] = (byte) theByte;
                if (// Enough to encode.
                position >= bufferLength) {
                    out.write(encode3to4(b4, buffer, bufferLength, options));
                    lineLength += 4;
                    if (breakLines && lineLength >= MAX_LINE_LENGTH) {
                        out.write(NEW_LINE);
                        lineLength = 0;
                    }
                    // end if: end of line
                    position = 0;
                }
            // end if: enough to output
            } else // end if: encoding
            // Else, Decoding
            {
                // Meaningful Base64 character?
                if (decodabet[theByte & 0x7f] > WHITE_SPACE_ENC) {
                    buffer[position++] = (byte) theByte;
                    if (// Enough to output.
                    position >= bufferLength) {
                        int len = Base64.decode4to3(buffer, 0, b4, 0, options);
                        out.write(b4, 0, len);
                        //out.write( Base64.decode4to3( buffer ) );
                        position = 0;
                    }
                // end if: enough to output
                } else // end if: meaningful base64 character
                if (decodabet[theByte & 0x7f] != WHITE_SPACE_ENC) {
                    throw new java.io.IOException("Invalid character in Base64 data.");
                }
            // end else: not white space either
            }
        // end else: decoding
        }

        // end write
        /**
         * Calls {@link #write(int)} repeatedly until <var>len</var>
         * bytes are written.
         *
         * @param theBytes array from which to read bytes
         * @param off offset for array
         * @param len max number of bytes to read into array
         * @since 1.3
         */
        public void write(byte[] theBytes, int off, int len) throws java.io.IOException {
            // Encoding suspended?
            if (suspendEncoding) {
                super.out.write(theBytes, off, len);
                return;
            }
            for (int i = 0; i < len; i++) {
                write(theBytes[off + i]);
            }
        // end for: each byte written
        }

        // end write
        /**
         * Method added by PHIL. [Thanks, PHIL. -Rob]
         * This pads the buffer without closing the stream.
         */
        public void flushBase64() throws java.io.IOException {
            if (position > 0) {
                if (encode) {
                    out.write(encode3to4(b4, buffer, position, options));
                    position = 0;
                } else // end if: encoding
                {
                    throw new java.io.IOException("Base64 input not properly padded.");
                }
            // end else: decoding
            }
        // end if: buffer partially full
        }

        // end flush
        /**
         * Flushes and closes (I think, in the superclass) the stream.
         *
         * @since 1.3
         */
        public void close() throws java.io.IOException {
            // 1. Ensure that pending characters are written
            flushBase64();
            // 2. Actually close the stream
            // Base class both flushes and closes.
            super.close();
            buffer = null;
            out = null;
        }

        // end close
        /**
         * Suspends encoding of the stream.
         * May be helpful if you need to embed a piece of
         * base640-encoded data in a stream.
         *
         * @since 1.5.1
         */
        public void suspendEncoding() throws java.io.IOException {
            flushBase64();
            this.suspendEncoding = true;
        }

        // end suspendEncoding
        /**
         * Resumes encoding of the stream.
         * May be helpful if you need to embed a piece of
         * base640-encoded data in a stream.
         *
         * @since 1.5.1
         */
        public void resumeEncoding() {
            this.suspendEncoding = false;
        }
        // end resumeEncoding
    }
    // end inner class OutputStream
}
// end class Base64
