/*
 * @(#)Filer.java	1.1 04/01/26
 *
 * Copyright (c) 2004, Sun Microsystems, Inc.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Sun Microsystems, Inc. nor the names of
 *       its contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.sun.mirror.apt;

import java.io.*;

public interface Filer {

    /**
     * Creates a new source file and returns a writer for it.
     * The file's name and path (relative to the root of all newly created
     * source files) is based on the type to be declared in that file.
     * If more than one type is being declared, the name of the principal
     * top-level type (the public one, for example) should be used.
     *
     * <p> The {@linkplain java.nio.charset.Charset charset} used to
     * encode the file is determined by the implementation.
     * An annotation processing tool may have an <tt>-encoding</tt>
     * flag or the like for specifying this.  It will typically use
     * the platform's default encoding if none is specified.
     *
     * @param name  canonical (fully qualified) name of the principal type
     *		being declared in this file
     * @return a writer for the new file
     * @throws IOException if the file cannot be created
     */
    PrintWriter createSourceFile(String name) throws IOException;

    /**
     * Creates a new class file, and returns a stream for writing to it.
     * The file's name and path (relative to the root of all newly created
     * class files) is based on the name of the type being written.
     *
     * @param name canonical (fully qualified) name of the type being written
     * @return a stream for writing to the new file
     * @throws IOException if the file cannot be created
     */
    OutputStream createClassFile(String name) throws IOException;

    /**
     * Creates a new text file, and returns a writer for it.
     * The file is located along with either the
     * newly created source or newly created binary files.  It may be
     * named relative to some package (as are source and binary files),
     * and from there by an arbitrary pathname.  In a loose sense, the
     * pathname of the new file will be the concatenation of
     * <tt>loc</tt>, <tt>pkg</tt>, and <tt>relPath</tt>.
     *
     * <p> A {@linkplain java.nio.charset.Charset charset} for
     * encoding the file may be provided.  If none is given, the
     * charset used to encode source files
     * (see {@link #createSourceFile(String)}) will be used.
     *
     * @param loc location of the new file
     * @param pkg package relative to which the file should be named,
     *		or the empty string if none
     * @param relPath final pathname components of the file
     * @param charsetName the name of the charset to use, or null if none
     *		is being explicitly specified
     * @return a writer for the new file
     * @throws IOException if the file cannot be created
     */
    PrintWriter createTextFile(Location loc, String pkg, File relPath, String charsetName) throws IOException;

    /**
     * Creates a new binary file, and returns a stream for writing to it.
     * The file is located along with either the
     * newly created source or newly created binary files.  It may be
     * named relative to some package (as are source and binary files),
     * and from there by an arbitrary pathname.  In a loose sense, the
     * pathname of the new file will be the concatenation of
     * <tt>loc</tt>, <tt>pkg</tt>, and <tt>relPath</tt>.
     *
     * @param loc location of the new file
     * @param pkg package relative to which the file should be named,
     *		or the empty string if none
     * @param relPath final pathname components of the file
     * @return a stream for writing to the new file
     * @throws IOException if the file cannot be created
     */
    OutputStream createBinaryFile(Location loc, String pkg, File relPath) throws IOException;

    /**
     * Locations (subtrees within the file system) where new files are created.
     */
    enum Location implements  {

        /** The location of new source files. */
        SOURCE_TREE() {
        }
        , /** The location of new class files. */
        CLASS_TREE() {
        }
        ;
    }
}
