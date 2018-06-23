/*******************************************************************************
 * Copyright (c) Robert Harder
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Robert Harder <rob@iharder.net> - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.protocol.msn.internal.encode;

/**
 * <p>This file is distributed under the 
 * <a href="http://www.opensource.org/licenses/eclipse-1.0.php">Eclipse Public License</a>.
 * A warm "Thank You" goes out to the open source community for all their contributions
 * to the common good.</p>
 * 
 * <p>Encodes and decodes to and from Base64 notation.</p>
 * <p>Homepage: <a href="http://iharder.net/base64">http://iharder.net/base64</a>.</p>
 *
 * <p>The <tt>options</tt> parameter, which appears in a few places, is used to pass 
 * several pieces of information to the encoder. In the "higher level" methods such as 
 * encodeBytes( bytes, options ) the options parameter can be used to indicate such 
 * things as first gzipping the bytes before encoding them, not inserting linefeeds 
 * (though that breaks strict Base64 compatibility), and encoding using the URL-safe 
 * and Ordered dialects.</p>
 *
 * <p>The constants defined in Base64 can be OR-ed together to combine options, so you 
 * might make a call like this:</p>
 *
 * <code>String encoded = Base64.encodeBytes( mybytes, Base64.GZIP | Base64.DONT_BREAK_LINES );</code>
 *
 * <p>to compress the data before encoding it and then making the output have no newline characters.</p>
 *
 *
 * <p>
 * Change Log:
 * </p>
 * <ul>
 *  <li>v2.2.1 - Fixed bug using URL_SAFE and ORDERED encodings. Fixed bug
 *   when using very small files (~< 40 bytes).</li>
 *  <li>v2.2 - Added some helper methods for encoding/decoding directly from
 *   one file to the next. Also added a main() method to support command line
 *   encoding/decoding from one file to the next. Also added these Base64 dialects:
 *   <ol>
 *   <li>The default is RFC3548 format.</li>
 *   <li>Calling Base64.setFormat(Base64.BASE64_FORMAT.URLSAFE_FORMAT) generates
 *   URL and file name friendly format as described in Section 4 of RFC3548.
 *   http://www.faqs.org/rfcs/rfc3548.html</li>
 *   <li>Calling Base64.setFormat(Base64.BASE64_FORMAT.ORDERED_FORMAT) generates
 *   URL and file name friendly format that preserves lexical ordering as described
 *   in http://www.faqs.org/qa/rfcc-1940.html</li>
 *   </ol>
 *   Special thanks to Jim Kellerman at <a href="http://www.powerset.com/">http://www.powerset.com/</a>
 *   for contributing the new Base64 dialects.
 *  </li>
 * 
 *  <li>v2.1 - Cleaned up javadoc comments and unused variables and methods. Added
 *   some convenience methods for reading and writing to and from files.</li>
 *  <li>v2.0.2 - Now specifies UTF-8 encoding in places where the code fails on systems
 *   with other encodings (like EBCDIC).</li>
 *  <li>v2.0.1 - Fixed an error when decoding a single byte, that is, when the
 *   encoded data was a single byte.</li>
 *  <li>v2.0 - I got rid of methods that used booleans to set options. 
 *   Now everything is more consolidated and cleaner. The code now detects
 *   when data that's being decoded is gzip-compressed and will decompress it
 *   automatically. Generally things are cleaner. You'll probably have to
 *   change some method calls that you were making to support the new
 *   options format (<tt>int</tt>s that you "OR" together).</li>
 *  <li>v1.5.1 - Fixed bug when decompressing and decoding to a             
 *   byte[] using <tt>decode( String s, boolean gzipCompressed )</tt>.      
 *   Added the ability to "suspend" encoding in the Output Stream so        
 *   you can turn on and off the encoding if you need to embed base64       
 *   data in an otherwise "normal" stream (like an XML file).</li>  
 *  <li>v1.5 - Output stream pases on flush() command but doesn't do anything itself.
 *      This helps when using GZIP streams.
 *      Added the ability to GZip-compress objects before encoding them.</li>
 *  <li>v1.4 - Added helper methods to read/write files.</li>
 *  <li>v1.3.6 - Fixed OutputStream.flush() so that 'position' is reset.</li>
 *  <li>v1.3.5 - Added flag to turn on and off line breaks. Fixed bug in input stream
 *      where last buffer being read, if not completely full, was not returned.</li>
 *  <li>v1.3.4 - Fixed when "improperly padded stream" error was thrown at the wrong time.</li>
 *  <li>v1.3.3 - Fixed I/O streams which were totally messed up.</li>
 * </ul>
 *
 * <p>
 * Please visit <a href="http://iharder.net/base64">http://iharder.net/base64</a>
 * periodically to check for updates or to contribute improvements.
 * </p>
 *
 * @author Robert Harder
 * @author rob@iharder.net
 * @version 2.2.1
 */
public class Base64 {

    /* ********  P R I V A T E   F I E L D S  ******** */
    /** Maximum line length (76) of Base64 output. */
    private static final int MAX_LINE_LENGTH = 76;

    /** The equals sign (=) as a byte. */
    private static final byte EQUALS_SIGN = (byte) '=';

    /** The new line character (\n) as a byte. */
    private static final byte NEW_LINE = (byte) '\n';

    /* ********  S T A N D A R D   B A S E 6 4   A L P H A B E T  ******** */
    /** The 64 valid Base64 values. */
    //private final static byte[] ALPHABET;
    /* Host platform me be something funny like EBCDIC, so we hardcode these values. */
    private static final byte[] _STANDARD_ALPHABET = { (byte) 'A', (byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F', (byte) 'G', (byte) 'H', (byte) 'I', (byte) 'J', (byte) 'K', (byte) 'L', (byte) 'M', (byte) 'N', (byte) 'O', (byte) 'P', (byte) 'Q', (byte) 'R', (byte) 'S', (byte) 'T', (byte) 'U', (byte) 'V', (byte) 'W', (byte) 'X', (byte) 'Y', (byte) 'Z', (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f', (byte) 'g', (byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k', (byte) 'l', (byte) 'm', (byte) 'n', (byte) 'o', (byte) 'p', (byte) 'q', (byte) 'r', (byte) 's', (byte) 't', (byte) 'u', (byte) 'v', (byte) 'w', (byte) 'x', (byte) 'y', (byte) 'z', (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5', (byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) '+', (byte) '/' };

    /** Defeats instantiation. */
    private  Base64() {
    // nothing to do
    }

    /* ********  E N C O D I N G   M E T H O D S  ******** */
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
    private static byte[] encode3to4(byte[] source, int srcOffset, int numSigBytes, byte[] destination, int destOffset) {
        final byte[] ALPHABET = _STANDARD_ALPHABET;
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
        final int inBuff = (numSigBytes > 0 ? ((source[srcOffset] << 24) >>> 8) : 0) | (numSigBytes > 1 ? ((source[srcOffset + 1] << 24) >>> 16) : 0) | (numSigBytes > 2 ? ((source[srcOffset + 2] << 24) >>> 24) : 0);
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
	 * Encodes a byte array into Base64 notation.
	 * Does not GZip-compress data.
	 *
	 * @param source The data to convert
	 * @return encoded bytes.
	 * @since 1.4
	 */
    public static String encodeBytes(byte[] source) {
        return encodeBytes(source, 0, source.length);
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
	 * @return encoded bytes.
	 * @since 2.0
	 */
    public static String encodeBytes(byte[] source, int off, int len) {
        final int len43 = len * 4 / 3;
        final byte[] outBuff = new byte[// Main 4:3
        (len43) + // Account for padding
        ((len % 3) > 0 ? 4 : 0)];
        // New lines      
        int d = 0;
        int e = 0;
        final int len2 = len - 2;
        int lineLength = 0;
        for (; d < len2; d += 3, e += 4) {
            encode3to4(source, d + off, 3, outBuff, e);
            lineLength += 4;
            if (lineLength == MAX_LINE_LENGTH) {
                outBuff[e + 4] = NEW_LINE;
                e++;
                lineLength = 0;
            }
        // end if: end of line
        }
        if (d < len) {
            encode3to4(source, d + off, len - d, outBuff, e);
            e += 4;
        }
        return new String(outBuff, 0, e);
    }
    // end encodeBytes
}
// end class Base64
