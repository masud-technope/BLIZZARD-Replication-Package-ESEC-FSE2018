/*******************************************************************************
 * Copyright (c) 2009  Clark N. Hobbie
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Clark N. Hobbie - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.ipc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * An internal class that provides utility methods common to IPC classes.
 * 
 * @author Clark N. Hobbie
 */
public class Utils {

    public static long toMilliseconds(long seconds, long nanos) {
        long result = seconds * 1000;
        result += nanos / 1000000;
        long temp = nanos % 1000000;
        if (temp > 0)
            result++;
        return result;
    }

    public static long toNanoseconds(long sec, long nanos) {
        long result = nanos;
        result = result + 1000000000 * sec;
        return result;
    }

    public static void closeNoExceptions(Reader reader) {
        if (null == reader)
            return;
        try {
            reader.close();
        } catch (IOException e) {
            ;
        }
    }

    public static void closeNoExceptions(BufferedReader reader) {
        if (null == reader)
            return;
        try {
            reader.close();
        } catch (IOException e) {
            ;
        }
    }

    public static void closeNoExceptions(InputStream istream) {
        if (null == istream)
            return;
        try {
            istream.close();
        } catch (IOException e) {
            ;
        }
    }

    public static String readFile(File file) throws IOException {
        FileReader reader = null;
        try {
            reader = new FileReader(file);
            StringWriter sw = new StringWriter(1024);
            for (int c = reader.read(); c != -1; c = reader.read()) {
                sw.write(c);
            }
            return sw.toString();
        } catch (FileNotFoundException e) {
            return null;
        } finally {
            Utils.closeNoExceptions(reader);
        }
    }

    public static String readFile(String name) throws IOException {
        File file = new File(name);
        return readFile(file);
    }

    public static void writeFile(File file, String contents) throws IOException {
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            writer.write(contents);
        } finally {
            Utils.closeNoExceptions(writer);
        }
    }

    public static void writeFile(String name, String contents) throws IOException {
        File file = new File(name);
        writeFile(file, contents);
    }

    public static void closeNoExceptions(FileWriter writer) {
        if (null == writer)
            return;
        try {
            writer.close();
        } catch (IOException e) {
            ;
        }
    }

    /**
	 * Find the enum tag that matches a string without regards to case.
	 * <P>
	 * This method is intended to be used like so:
	 * </P>
	 * <P>
	 * <CODE>
	 * <PRE>
	 * enum Foo {
	 *     One,Two,Three,Four;
	 *     public static Foo toValueIgnoreCase(String s) {
	 *         return (Foo) Utils.toValueIgnoreCase(Foo.values(), s);
	 *     }
	 * }
	 * </PRE>
	 * </CODE>
	 * </P>
	 * 
	 * @param values
	 *        The enum tag values.
	 * @param s
	 *        The string we are trying to convert.
	 * @return The matching enum tag.
	 * @throws RuntimeException
	 *         If there is no match.
	 */
    public static Object toValueIgnoreCase(Object[] values, String s) {
        Object result = matchIgnoreCase(values, s);
        if (null == result) {
            StringWriter sw = new StringWriter();
            PrintWriter out = new PrintWriter(sw);
            out.println(s + " does not match any tag name.");
            out.println("Available tag names are:");
            for (Object o : values) {
                String value = o.toString();
                out.println(value);
            }
            out.close();
            String msg = sw.toString();
            throw new RuntimeException(msg);
        }
        return result;
    }

    public static Object matchIgnoreCase(Object[] values, String s) {
        for (Object tag : values) {
            if (s.equalsIgnoreCase(tag.toString()))
                return tag;
        }
        return null;
    }

    public static void closeNoExceptions(OutputStream ostream) {
        try {
            ostream.close();
        } catch (IOException e) {
            ;
        }
    }

    public static void fillArrayWith(byte[] dest, byte[] pattern) {
        int count = 0;
        while (count < dest.length) {
            for (int i = 0; i < pattern.length; i++) {
                dest[count] = pattern[i];
                count++;
            }
        }
    }

    /**
	 * Create a byte array that is populated with a particular string.
	 * <P>
	 * Useful when initializing shared memory.
	 * </P>
	 * 
	 * @param size
	 *        The size of the returned array.
	 * @param pattern
	 *        The pattern to use to populate the array.
	 * @return The populated array.
	 */
    public static byte[] createAndFillWith(int size, String pattern) {
        byte[] buffer = new byte[size];
        byte[] pat = pattern.getBytes();
        fillArrayWith(buffer, pat);
        return buffer;
    }

    @SuppressWarnings("unchecked")
    public static void listProperties() {
        Collection col = System.getProperties().keySet();
        List<String> list = new ArrayList(col);
        Collections.sort(list);
        for (String key : list) {
            String value = System.getProperty(key);
            System.out.println(key + " = " + value);
        }
    }

    /**
	 * Read or create a file.
	 * <P>
	 * This method opens the named text file and returns its contents, assuming the file
	 * exists.
	 * </P>
	 * <P>
	 * If the file does not exist, then it creates it and writes the supplied string into
	 * it. This operation happens in an "atomic" fashion, so that another thread or
	 * process that if another process or thread attempts to write the same file at the
	 * same time, only one of them will succeed.
	 * </P>
	 * 
	 * @param file
	 *        The file to read or create.
	 * @param contents
	 *        If the file needs to be created, the contents of the file.
	 * @throws IOException
	 *         If a problem exists while trying to read or write the file.
	 * @return The contents of the file. If the file does not exist, this will simply
	 *         return the supplied string.
	 */
    public static String readOrCreate(File file, String contents) throws IOException {
        //
        if (file.exists()) {
            return readFile(file);
        } else //
        // otherwise, write the supplied string to the file using an "atomic"
        // operation
        //
        {
            //
            // create a temp file and write the contents to it.
            //
            File parent = file.getParentFile();
            File temp = File.createTempFile("tempfile", null, parent);
            Utils.writeFile(temp, contents);
            //
            if (temp.renameTo(file)) {
                return contents;
            } else //
            // otherwise, someone created the file before we did. Return its
            // contents
            //
            {
                return readFile(file);
            }
        }
    }

    public static String readOrCreate(String name, String contents) throws IOException {
        File file = new File(name);
        return readOrCreate(file, contents);
    }

    public static void printEnv() {
        Properties props = System.getProperties();
        List<String> list = new ArrayList<String>();
        for (Object o : props.keySet()) {
            list.add((String) o);
        }
        for (String name : list) {
            String value = props.getProperty(name);
            System.out.println(name + " = " + value);
        }
        System.out.flush();
    }
}
