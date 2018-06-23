/****************************************************************************
 * Copyright (c) 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.ui.screencapture;

import java.io.*;
import java.util.zip.*;

/**
 *
 */
public class ScreenCaptureUtil {

    public static byte[] uncompress(byte[] source) {
        final ZipInputStream ins = new ZipInputStream(new ByteArrayInputStream(source));
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int read = 0;
        final byte[] buf = new byte[16192];
        try {
            ins.getNextEntry();
            while ((read = ins.read(buf)) > 0) {
                bos.write(buf, 0, read);
            }
            bos.flush();
            ins.close();
        } catch (final IOException e) {
        }
        return bos.toByteArray();
    }

    public static byte[] compress(byte[] source) throws IOException {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final ZipOutputStream zos = new ZipOutputStream(bos);
        final ByteArrayInputStream bis = new ByteArrayInputStream(source);
        int read = 0;
        final byte[] buf = new byte[16192];
        //$NON-NLS-1$
        zos.putNextEntry(new ZipEntry("bytes"));
        while ((read = bis.read(buf)) != -1) {
            zos.write(buf, 0, read);
        }
        zos.finish();
        zos.flush();
        return bos.toByteArray();
    }
}
