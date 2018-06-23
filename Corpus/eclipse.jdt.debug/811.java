/*******************************************************************************
 * Copyright (c) 2000, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdi.internal;

import java.io.DataInputStream;
import java.io.IOException;
import org.eclipse.jdi.internal.jdwp.JdwpCommandPacket;
import org.eclipse.jdi.internal.jdwp.JdwpID;
import org.eclipse.jdi.internal.jdwp.JdwpReplyPacket;
import org.eclipse.jdi.internal.jdwp.JdwpStringID;
import com.sun.jdi.ObjectCollectedException;
import com.sun.jdi.StringReference;

/**
 * this class implements the corresponding interfaces declared by the JDI
 * specification. See the com.sun.jdi package for more information.
 * 
 */
public class StringReferenceImpl extends ObjectReferenceImpl implements StringReference {

    /** JDWP Tag. */
    public static final byte tag = JdwpID.STRING_TAG;

    /**
	 * Creates new StringReferenceImpl.
	 */
    public  StringReferenceImpl(VirtualMachineImpl vmImpl, JdwpStringID stringID) {
        //$NON-NLS-1$
        super("StringReference", vmImpl, stringID);
    }

    /**
	 * @returns Value tag.
	 */
    @Override
    public byte getTag() {
        return tag;
    }

    /**
	 * @returns Returns the StringReference as a String.
	 */
    @Override
    public String value() {
        // Note that this information should not be cached.
        initJdwpRequest();
        try {
            JdwpReplyPacket replyPacket = requestVM(JdwpCommandPacket.SR_VALUE, this);
            defaultReplyErrorHandler(replyPacket.errorCode());
            DataInputStream replyData = replyPacket.dataInStream();
            //$NON-NLS-1$
            String result = readString("value", replyData);
            return result;
        } catch (IOException e) {
            defaultIOExceptionHandler(e);
            return null;
        } finally {
            handledJdwpRequest();
        }
    }

    /**
	 * @return Reads JDWP representation and returns new instance.
	 */
    public static StringReferenceImpl read(MirrorImpl target, DataInputStream in) throws IOException {
        VirtualMachineImpl vmImpl = target.virtualMachineImpl();
        JdwpStringID ID = new JdwpStringID(vmImpl);
        ID.read(in);
        if (target.fVerboseWriter != null)
            //$NON-NLS-1$
            target.fVerboseWriter.println("stringReference", ID.value());
        if (ID.isNull())
            return null;
        StringReferenceImpl mirror = new StringReferenceImpl(vmImpl, ID);
        return mirror;
    }

    /**
	 * @return Returns description of Mirror object.
	 */
    @Override
    public String toString() {
        try {
            //$NON-NLS-1$ //$NON-NLS-2$
            return "\"" + value() + "\"";
        } catch (ObjectCollectedException e) {
            return JDIMessages.StringReferenceImpl__Garbage_Collected__StringReference__3 + idString();
        } catch (Exception e) {
            return fDescription;
        }
    }
}
