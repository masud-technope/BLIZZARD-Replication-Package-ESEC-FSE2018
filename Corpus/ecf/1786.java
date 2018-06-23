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
package org.eclipse.ecf.presence;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Map;
import org.eclipse.core.runtime.IAdaptable;

/**
 * Presence information for a user. 
 * 
 */
public interface IPresence extends IAdaptable, Serializable {

    /**
	 * Get the presence mode for this presence
	 * 
	 * @return Mode the mode information for this presence. Will not return
	 *         <code>null</code>.
	 */
    public Mode getMode();

    /**
	 * Get properties for this presence
	 * 
	 * @return Map of properties for this presence information. Will not return
	 *         <code>null</code>.
	 */
    public Map getProperties();

    /**
	 * Get status information for this presence information.
	 * 
	 * @return String status information for this presence info. May return
	 *         <code>null</code>.
	 */
    public String getStatus();

    /**
	 * Get presence type information for this presence.
	 * 
	 * @return Type the type of presence. Will not return <code>null</code>.
	 */
    public Type getType();

    /**
	 * Get picture data for this presence
	 * 
	 * @return byte [] image data. Empty array will be returned if not picture
	 *         data.
	 */
    public byte[] getPictureData();

    /**
	 * A type-safe enum class to represent the presence type information
	 * 
	 */
    public static class Type implements Serializable {

        private static final long serialVersionUID = 3546921402750743089L;

        //$NON-NLS-1$
        private static final String AVAILABLE_NAME = "available";

        //$NON-NLS-1$
        private static final String ERROR_NAME = "error";

        //$NON-NLS-1$
        private static final String SUBSCRIBE_NAME = "subscribe";

        //$NON-NLS-1$
        private static final String SUBSCRIBED_NAME = "subscribed";

        //$NON-NLS-1$
        private static final String UNAVAILABLE_NAME = "unavailable";

        //$NON-NLS-1$
        private static final String UNSUBSCRIBE_NAME = "unsubscribe";

        //$NON-NLS-1$
        private static final String UNSUBSCRIBED_NAME = "unsubscribed";

        //$NON-NLS-1$
        private static final String UNKWOWN_NAME = "unknown";

        private final transient String name;

        // instances
        protected  Type(String name) {
            this.name = name;
        }

        public static Type fromString(String presenceType) {
            if (presenceType == null)
                return null;
            if (presenceType.equals(AVAILABLE_NAME)) {
                return AVAILABLE;
            } else if (presenceType.equals(ERROR_NAME)) {
                return ERROR;
            } else if (presenceType.equals(SUBSCRIBE_NAME)) {
                return SUBSCRIBE;
            } else if (presenceType.equals(SUBSCRIBED_NAME)) {
                return SUBSCRIBED;
            } else if (presenceType.equals(UNAVAILABLE_NAME)) {
                return UNAVAILABLE;
            } else if (presenceType.equals(UNSUBSCRIBE_NAME)) {
                return UNSUBSCRIBE;
            } else if (presenceType.equals(UNSUBSCRIBED_NAME)) {
                return UNSUBSCRIBED;
            } else if (presenceType.equals(UNKWOWN_NAME)) {
                return UNKNOWN;
            } else
                return null;
        }

        public static final Type AVAILABLE = new Type(AVAILABLE_NAME);

        public static final Type ERROR = new Type(ERROR_NAME);

        public static final Type SUBSCRIBE = new Type(SUBSCRIBE_NAME);

        public static final Type SUBSCRIBED = new Type(SUBSCRIBED_NAME);

        public static final Type UNAVAILABLE = new Type(UNAVAILABLE_NAME);

        public static final Type UNSUBSCRIBE = new Type(UNSUBSCRIBE_NAME);

        public static final Type UNSUBSCRIBED = new Type(UNSUBSCRIBED_NAME);

        public static final Type UNKNOWN = new Type(UNKWOWN_NAME);

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

        // For serialization
        private static int nextOrdinal = 0;

        private final int ordinal = nextOrdinal++;

        private static final Type[] VALUES = { AVAILABLE, ERROR, SUBSCRIBE, SUBSCRIBED, UNAVAILABLE, UNSUBSCRIBE, UNSUBSCRIBED, UNKNOWN };

        /**
		 * @return Object
		 * @throws ObjectStreamException not thrown by this implementation.
		 */
        Object readResolve() throws ObjectStreamException {
            return VALUES[ordinal];
        }
    }

    /**
	 * A type-safe enum class to represent the presence mode information
	 * 
	 */
    public static class Mode implements Serializable {

        private static final long serialVersionUID = 3834588811853640499L;

        //$NON-NLS-1$
        private static final String AVAILABLE_NAME = "available";

        //$NON-NLS-1$
        private static final String AWAY_NAME = "away";

        //$NON-NLS-1$
        private static final String CHAT_NAME = "chat";

        //$NON-NLS-1$
        private static final String DND_NAME = "do not disturb";

        //$NON-NLS-1$
        private static final String EXTENDED_AWAY_NAME = "extended away";

        //$NON-NLS-1$
        private static final String INVISIBLE_NAME = "unsubscribed";

        private final transient String name;

        // instances
        protected  Mode(String name) {
            this.name = name;
        }

        public static Mode fromString(String presenceMode) {
            if (presenceMode == null)
                return null;
            if (presenceMode.equals(AVAILABLE_NAME)) {
                return AVAILABLE;
            } else if (presenceMode.equals(AWAY_NAME)) {
                return AWAY;
            } else if (presenceMode.equals(CHAT_NAME)) {
                return CHAT;
            } else if (presenceMode.equals(DND_NAME)) {
                return DND;
            } else if (presenceMode.equals(EXTENDED_AWAY_NAME)) {
                return EXTENDED_AWAY;
            } else if (presenceMode.equals(INVISIBLE_NAME)) {
                return INVISIBLE;
            } else
                return null;
        }

        public static final Mode AVAILABLE = new Mode(AVAILABLE_NAME);

        public static final Mode AWAY = new Mode(AWAY_NAME);

        public static final Mode CHAT = new Mode(CHAT_NAME);

        public static final Mode DND = new Mode(DND_NAME);

        public static final Mode EXTENDED_AWAY = new Mode(EXTENDED_AWAY_NAME);

        public static final Mode INVISIBLE = new Mode(INVISIBLE_NAME);

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

        // For serialization
        private static int nextOrdinal = 0;

        private final int ordinal = nextOrdinal++;

        private static final Mode[] VALUES = { AVAILABLE, AWAY, CHAT, DND, EXTENDED_AWAY, INVISIBLE };

        Object readResolve() throws ObjectStreamException {
            return VALUES[ordinal];
        }
    }
}
