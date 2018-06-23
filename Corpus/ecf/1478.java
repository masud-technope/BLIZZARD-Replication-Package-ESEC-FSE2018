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
package org.eclipse.ecf.provider.generic;

import java.io.Serializable;
import org.eclipse.ecf.core.identity.ID;

public class ContainerMessage implements Serializable {

    private static final long serialVersionUID = 3257009847533975857L;

    private ID fromContainerID;

    private ID toContainerID;

    private long sequence;

    private Serializable data;

    /**
	 * @return Returns the data.
	 */
    public Serializable getData() {
        return data;
    }

    /**
	 * @param data
	 *            The data to set.
	 */
    public void setData(Serializable data) {
        this.data = data;
    }

    /**
	 * @return Returns the fromContainerID.
	 */
    public ID getFromContainerID() {
        return fromContainerID;
    }

    /**
	 * @param fromContainerID
	 *            The fromContainerID to set.
	 */
    public void setFromContainerID(ID fromContainerID) {
        this.fromContainerID = fromContainerID;
    }

    /**
	 * @return Returns the sequence.
	 */
    public long getSequence() {
        return sequence;
    }

    /**
	 * @param sequence
	 *            The sequence to set.
	 */
    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    /**
	 * @return Returns the toContainerID.
	 */
    public ID getToContainerID() {
        return toContainerID;
    }

    /**
	 * @param toContainerID
	 *            The toContainerID to set.
	 */
    public void setToContainerID(ID toContainerID) {
        this.toContainerID = toContainerID;
    }

    public static ContainerMessage createViewChangeMessage(ID from, ID to, long seq, ID ids[], boolean add, Serializable data) {
        return new ContainerMessage(from, to, seq, new ViewChangeMessage(ids, add, data));
    }

    public static ContainerMessage createJoinGroupMessage(ID from, ID to, long seq, Serializable data) {
        return new ContainerMessage(from, to, seq, new JoinGroupMessage(data));
    }

    public static ContainerMessage createLeaveGroupMessage(ID from, ID to, long seq, Serializable data) {
        return new ContainerMessage(from, to, seq, new LeaveGroupMessage(data));
    }

    public static ContainerMessage createSharedObjectCreateMessage(ID from, ID to, long seq, Serializable data) {
        return new ContainerMessage(from, to, seq, new CreateMessage(data));
    }

    public static ContainerMessage createSharedObjectCreateResponseMessage(ID from, ID to, long contSeq, ID soID, Throwable e, long sequence) {
        return new ContainerMessage(from, to, contSeq, new CreateResponseMessage(soID, e, sequence));
    }

    public static ContainerMessage createSharedObjectMessage(ID from, ID to, long seq, ID fromSharedObject, Serializable data) {
        return new ContainerMessage(from, to, seq, new SharedObjectMessage(fromSharedObject, data));
    }

    public static ContainerMessage createSharedObjectDisposeMessage(ID from, ID to, long seq, ID sharedObjectID) {
        return new ContainerMessage(from, to, seq, new SharedObjectDisposeMessage(sharedObjectID));
    }

    protected  ContainerMessage(ID from, ID to, long seq, Serializable data) {
        this.fromContainerID = from;
        this.toContainerID = to;
        this.sequence = seq;
        this.data = data;
    }

    public String toString() {
        //$NON-NLS-1$
        final StringBuffer sb = new StringBuffer("ContainerMessage[");
        //$NON-NLS-1$
        sb.append(fromContainerID).append(";").append(toContainerID).append(//$NON-NLS-1$
        ";");
        //$NON-NLS-1$ //$NON-NLS-2$
        sb.append(sequence).append(";").append(data).append("]");
        return sb.toString();
    }

    public static final class ViewChangeMessage implements Serializable {

        private static final long serialVersionUID = 3256999977782882869L;

        private final ID changeIDs[];

        private final boolean add;

        private final Serializable data;

         ViewChangeMessage(ID id[], boolean a, Serializable data) {
            this.changeIDs = id;
            this.add = a;
            this.data = data;
        }

        protected String printChangeIDs() {
            if (changeIDs == null)
                //$NON-NLS-1$
                return "null";
            final StringBuffer buf = new StringBuffer();
            for (int i = 0; i < changeIDs.length; i++) {
                buf.append(changeIDs[i]);
                if (i != (changeIDs.length - 1))
                    //$NON-NLS-1$
                    buf.append(",");
            }
            return buf.toString();
        }

        public String toString() {
            //$NON-NLS-1$
            final StringBuffer sb = new StringBuffer("ViewChangeMessage[");
            //$NON-NLS-1$ //$NON-NLS-2$
            sb.append(printChangeIDs()).append(";").append(add).append(";").append(//$NON-NLS-1$
            data).append(//$NON-NLS-1$
            "]");
            return sb.toString();
        }

        /**
		 * @return Returns the add.
		 */
        public boolean isAdd() {
            return add;
        }

        /**
		 * @return Returns the changeIDs.
		 */
        public ID[] getChangeIDs() {
            return changeIDs;
        }

        /**
		 * @return Returns the data.
		 */
        public Serializable getData() {
            return data;
        }
    }

    public static final class CreateMessage implements Serializable {

        private static final long serialVersionUID = 3257849874417595703L;

        Serializable data;

         CreateMessage(Serializable data) {
            this.data = data;
        }

        public Serializable getData() {
            return data;
        }

        public String toString() {
            //$NON-NLS-1$
            final StringBuffer sb = new StringBuffer("CreateMessage[");
            //$NON-NLS-1$
            sb.append(data).append("]");
            return sb.toString();
        }
    }

    public static final class CreateResponseMessage implements Serializable {

        private static final long serialVersionUID = 3762531213570554166L;

        private final ID sharedObjectID;

        private final Throwable exception;

        private final long sequence;

        public  CreateResponseMessage(ID objID, Throwable except, long sequence) {
            this.sharedObjectID = objID;
            this.exception = except;
            this.sequence = sequence;
        }

        public String toString() {
            //$NON-NLS-1$
            final StringBuffer sb = new StringBuffer("CreateResponseMessage[");
            //$NON-NLS-1$ //$NON-NLS-2$
            sb.append(sharedObjectID).append(";").append(exception).append(";").append(//$NON-NLS-1$
            sequence).append(//$NON-NLS-1$
            "]");
            return sb.toString();
        }

        /**
		 * @return Returns the exception.
		 */
        public Throwable getException() {
            return exception;
        }

        /**
		 * @return Returns the sequence.
		 */
        public long getSequence() {
            return sequence;
        }

        /**
		 * @return Returns the sharedObjectID.
		 */
        public ID getSharedObjectID() {
            return sharedObjectID;
        }
    }

    public static final class SharedObjectMessage implements Serializable {

        private static final long serialVersionUID = 3257281448531867441L;

        private final Serializable data;

        private final ID fromSharedObjectID;

         SharedObjectMessage(ID fromSharedObject, Serializable data) {
            this.fromSharedObjectID = fromSharedObject;
            this.data = data;
        }

        public String toString() {
            //$NON-NLS-1$
            final StringBuffer sb = new StringBuffer("SharedObjectMessage[");
            //$NON-NLS-1$ //$NON-NLS-2$
            sb.append(fromSharedObjectID).append(";").append(data).append("]");
            return sb.toString();
        }

        public Serializable getData() {
            return data;
        }

        /**
		 * @return Returns the fromSharedObjectID.
		 */
        public ID getFromSharedObjectID() {
            return fromSharedObjectID;
        }
    }

    public static final class SharedObjectDisposeMessage implements Serializable {

        private static final long serialVersionUID = 3905241221474498104L;

        private final ID sharedObjectID;

         SharedObjectDisposeMessage(ID objID) {
            this.sharedObjectID = objID;
        }

        public String toString() {
            //$NON-NLS-1$
            final StringBuffer sb = new StringBuffer("SharedObjectDisposeMessage[");
            //$NON-NLS-1$
            sb.append(sharedObjectID).append("]");
            return sb.toString();
        }

        public ID getSharedObjectID() {
            return sharedObjectID;
        }
    }

    public static final class JoinGroupMessage implements Serializable {

        private static final long serialVersionUID = 3257564022885855287L;

        private final Serializable data;

        public  JoinGroupMessage(Serializable data) {
            this.data = data;
        }

        public Serializable getData() {
            return data;
        }

        public String toString() {
            //$NON-NLS-1$
            final StringBuffer sb = new StringBuffer("JoinGroupMessage[");
            //$NON-NLS-1$
            sb.append(data).append("]");
            return sb.toString();
        }
    }

    public static final class LeaveGroupMessage implements Serializable {

        private static final long serialVersionUID = 3258128072350972213L;

        private final Serializable data;

        public  LeaveGroupMessage(Serializable data) {
            this.data = data;
        }

        public Serializable getData() {
            return data;
        }

        public String toString() {
            //$NON-NLS-1$
            final StringBuffer sb = new StringBuffer("LeaveGroupMessage[");
            //$NON-NLS-1$
            sb.append(data).append("]");
            return sb.toString();
        }
    }
}
