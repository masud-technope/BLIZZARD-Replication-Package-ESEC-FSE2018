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

import java.nio.ByteBuffer;

public class IPCPackage {

    public static final String LIBRARY_NAME = "ipc_010";

    public static native void initializeNative();

    public static native void setValue(byte[] buf);

    public static native ByteBuffer createBuffer(int size);

    private static boolean ourPackageInitialized;

    public static void initializePackage() {
        if (ourPackageInitialized)
            return;
        else
            basicInitializePackage();
    }

    private static synchronized void basicInitializePackage() {
        if (ourPackageInitialized)
            return;
        System.loadLibrary(LIBRARY_NAME);
        initializeNative();
        ourPackageInitialized = true;
    }
}
