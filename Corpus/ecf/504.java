/****************************************************************************
 * Copyright (c) 2008 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.telephony.call.dtmf;

import java.io.ObjectStreamException;
import java.io.Serializable;
import org.eclipse.ecf.telephony.call.CallException;

/**
 * ICallSession adapter for sending DTMF codes during a call session.
 */
public interface IDTMFCallSessionAdapter {

    public static class DTMF implements Serializable {

        private static final long serialVersionUID = -4664398793204446391L;

        private static final char TYPE_0_CHAR = '0';

        private static final char TYPE_1_CHAR = '1';

        private static final char TYPE_2_CHAR = '2';

        private static final char TYPE_3_CHAR = '3';

        private static final char TYPE_4_CHAR = '4';

        private static final char TYPE_5_CHAR = '5';

        private static final char TYPE_6_CHAR = '6';

        private static final char TYPE_7_CHAR = '7';

        private static final char TYPE_8_CHAR = '8';

        private static final char TYPE_9_CHAR = '9';

        private static final char TYPE_ASTERISK_CHAR = '*';

        private static final char TYPE_SHARP_CHAR = '#';

        private final transient char type;

        protected  DTMF(char c) {
            this.type = c;
        }

        public static final DTMF TYPE_0 = new DTMF(TYPE_0_CHAR);

        public static final DTMF TYPE_1 = new DTMF(TYPE_1_CHAR);

        public static final DTMF TYPE_2 = new DTMF(TYPE_2_CHAR);

        public static final DTMF TYPE_3 = new DTMF(TYPE_3_CHAR);

        public static final DTMF TYPE_4 = new DTMF(TYPE_4_CHAR);

        public static final DTMF TYPE_5 = new DTMF(TYPE_5_CHAR);

        public static final DTMF TYPE_6 = new DTMF(TYPE_6_CHAR);

        public static final DTMF TYPE_7 = new DTMF(TYPE_7_CHAR);

        public static final DTMF TYPE_8 = new DTMF(TYPE_8_CHAR);

        public static final DTMF TYPE_9 = new DTMF(TYPE_9_CHAR);

        public static final DTMF TYPE_SHARP = new DTMF(TYPE_SHARP_CHAR);

        public static final DTMF TYPE_ASTERISK = new DTMF(TYPE_ASTERISK_CHAR);

        public static DTMF fromChar(char c) {
            return new DTMF(c);
        }

        public char toChar() {
            return type;
        }

        // This is to make sure that subclasses don't screw up these methods
        public final boolean equals(Object that) {
            return super.equals(that);
        }

        public final int hashCode() {
            return super.hashCode();
        }

        // For serialization
        private static int nextOrdinal = 0;

        private final int ordinal = nextOrdinal++;

        private static final DTMF[] VALUES = { TYPE_0, TYPE_1, TYPE_2, TYPE_3, TYPE_4, TYPE_5, TYPE_6, TYPE_7, TYPE_8, TYPE_9, TYPE_SHARP, TYPE_ASTERISK };

        /**
		 * @return Object
		 * @throws ObjectStreamException not thrown by this implementation.
		 */
        Object readResolve() throws ObjectStreamException {
            return VALUES[ordinal];
        }
    }

    /**
	 * Send DTMF to receiver.
	 * 
	 * @param command the DTMF command to send.  Must not be <code>null</code>.
	 * @throws CallException if problem with sending (e.g. no longer connected).
	 */
    public void sendDTMF(DTMF command) throws CallException;
}
