/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.presence.im;

import java.util.Map;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.presence.IIMMessage;

/**
 * Chat message. This is the message received when another user sends a
 * chat message to us.
 */
public interface IChatMessage extends IIMMessage {

    /**
	 * Get thread ID for this message. If thread IDs are not supported,
	 * <code>null</code> will be returned.
	 * 
	 * @return ID that identifies thread for this message. If threads are not
	 *         supported by provider, will return <code>null</code>.
	 */
    public ID getThreadID();

    /**
	 * Get subject for this message. If subjects are not supported, null will be
	 * returned.
	 * 
	 * @return String that is the subject of this message. If subjects are not
	 *         supported by provider, will return null.
	 */
    public String getSubject();

    /**
	 * Get the message body for this message. Will not be null.
	 * 
	 * @return String content/body of this message. Will not be null.
	 */
    public String getBody();

    /**
	 * Get type for this message. Will not be null. Defaults to Type.CHAT.
	 * 
	 * @return Type associated with this message. Defaults to Type.CHAT.
	 */
    public Type getType();

    /**
	 * Get properties associated with this chat message
	 * 
	 * @return Map of properties. Will not be null.
	 */
    public Map getProperties();

    public static class Type {

        //$NON-NLS-1$
        private static final String CHAT_NAME = "chat";

        //$NON-NLS-1$
        private static final String SYSTEM_NAME = "system";

        //$NON-NLS-1$
        private static final String ERROR_NAME = "error";

        private final transient String name;

        // instances
        protected  Type(String name) {
            this.name = name;
        }

        public static Type fromString(String itemType) {
            if (itemType == null)
                return null;
            if (itemType.equals(CHAT_NAME)) {
                return CHAT;
            } else if (itemType.equals(SYSTEM_NAME)) {
                return SYSTEM;
            } else if (itemType.equals(ERROR_NAME)) {
                return ERROR;
            } else
                return null;
        }

        public static final Type CHAT = new Type(CHAT_NAME);

        public static final Type SYSTEM = new Type(SYSTEM_NAME);

        public static final Type ERROR = new Type(ERROR_NAME);

        public String toString() {
            return name;
        }

        // This is to make sure that subclasses don't screw up these methods
        public final boolean equals(Object that) {
            return super.equals(that);
        }

        public final int hashCode() {
            return super.hashCode();
        }
    }
}
